package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.enums.EnquiryStatus
import com.example.data.enums.InspectionStatus
import com.example.data.enums.PropertyStatus
import com.example.data.enums.UserRole
import com.example.data.local.HavenDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.Enquiry
import com.example.data.model.Favourite
import com.example.data.model.Inspection
import com.example.data.model.Property
import com.example.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class HavenRepository(private val context: Context) {
    private val database = HavenDatabase.getDatabase(context)
    private val propertyDao = database.propertyDao()
    private val userDao = database.userDao()
    private val enquiryDao = database.enquiryDao()
    private val inspectionDao = database.inspectionDao()
    private val favouriteDao = database.favouriteDao()
    private val chatMessageDao = database.chatMessageDao()

    private val firestoreDb: FirebaseFirestore by lazy {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            if (dbId.isNotBlank()) {
                FirebaseFirestore.getInstance(dbId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (e: Exception) {
            Log.w("HavenRepository", "Fallback to default Firestore: ${e.message}")
            FirebaseFirestore.getInstance()
        }
    }

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentCountryFilter = MutableStateFlow("ALL")
    val currentCountryFilter: StateFlow<String> = _currentCountryFilter.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
            // Set default initial persona to Customer
            val defaultUser = userDao.getUserById("user_customer_1")
            _currentUser.value = defaultUser
            listenToWebsiteProperties()
        }
    }

    fun setCountryFilter(countryCode: String) {
        _currentCountryFilter.value = countryCode
    }

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    suspend fun switchPersona(userId: String) {
        val user = userDao.getUserById(userId)
        _currentUser.value = user
    }

    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        role: String,
        agencyName: String? = null,
        country: String = "NG"
    ): User {
        val currency = when (country) {
            "NG" -> "NGN"
            "US" -> "USD"
            "GB" -> "GBP"
            "AE" -> "AED"
            else -> "NGN"
        }
        val prefix = if (role.equals("agent", ignoreCase = true)) "agent_" else "user_"
        val newUserId = "$prefix${UUID.randomUUID().toString().take(8)}"
        val newUser = User(
            id = newUserId,
            name = name,
            email = email,
            phone = phone,
            role = role.lowercase(),
            agencyName = agencyName,
            isVerified = !role.equals("agent", ignoreCase = true), // agents start unverified until admin approves
            preferredCountry = country,
            preferredCurrency = currency
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser

        // Dispatch Welcome message to user: "whenever we sign in with your email address they should send you a message that you have join"
        sendWelcomeNotification(newUser, isNewRegistration = true)

        // Sync user profile to Firestore
        syncUserToFirestore(newUser)

        return newUser
    }

    suspend fun loginUser(email: String): User? {
        val user = userDao.getUserByEmail(email)
        if (user != null) {
            _currentUser.value = user
            // Dispatch in-app notification & chat message acknowledging sign in
            sendWelcomeNotification(user, isNewRegistration = false)
        }
        return user
    }

    private suspend fun sendWelcomeNotification(user: User, isNewRegistration: Boolean) {
        val welcomeGreeting = if (isNewRegistration) {
            "Welcome to PROJECT X — GLOBAL PROPERTY GROUP! Your private client account (${user.email}) has been successfully created. Explore prime global estates across residential, commercial, and multi-unit developments."
        } else {
            "Welcome back to PROJECT X, ${user.name}! You are authenticated with ${user.email}. All verified residences and confidential broker channels are accessible."
        }

        // 1. Deliver as real-time in-app advisory message from PROJECT X Client Advisory
        val welcomeMsg = ChatMessage(
            id = "welcome_${UUID.randomUUID().toString().take(8)}",
            senderId = "admin_victor_1",
            senderName = "PROJECT X Client Advisory",
            recipientId = user.id,
            recipientName = user.name,
            propertyId = null,
            propertyTitle = "Client Access & Security",
            message = welcomeGreeting,
            timestamp = System.currentTimeMillis()
        )
        chatMessageDao.insertMessage(welcomeMsg)

        // 2. Persist notification to Firestore user notification collection
        try {
            val notifData = hashMapOf(
                "userId" to user.id,
                "title" to if (isNewRegistration) "Welcome to PROJECT X" else "Client Sign In Verified",
                "message" to welcomeGreeting,
                "type" to if (isNewRegistration) "welcome_registration" else "welcome_login",
                "email" to user.email,
                "read" to false,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestoreDb.collection("users")
                .document(user.id)
                .collection("notifications")
                .add(notifData)
        } catch (e: Exception) {
            Log.e("HavenRepository", "Error syncing welcome notification to Firestore: ${e.message}")
        }
    }

    private fun syncUserToFirestore(user: User) {
        try {
            val userData = hashMapOf(
                "email" to user.email,
                "name" to user.name,
                "phone" to user.phone,
                "role" to user.role,
                "agencyName" to (user.agencyName ?: ""),
                "preferredCountry" to user.preferredCountry,
                "isVerified" to user.isVerified,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestoreDb.collection("users")
                .document(user.id)
                .set(userData)
        } catch (e: Exception) {
            Log.e("HavenRepository", "Error syncing user to Firestore: ${e.message}")
        }
    }

    // Properties
    fun getPublishedProperties(): Flow<List<Property>> = propertyDao.getPublishedProperties()
    fun getAllProperties(): Flow<List<Property>> = propertyDao.getAllProperties()
    fun getPropertiesByAgent(agentId: String): Flow<List<Property>> = propertyDao.getPropertiesByAgent(agentId)
    fun getPendingReviewProperties(): Flow<List<Property>> = propertyDao.getPendingReviewProperties()
    fun observeProperty(id: String): Flow<Property?> = propertyDao.observePropertyById(id)
    suspend fun getProperty(id: String): Property? = propertyDao.getPropertyById(id)

    suspend fun createProperty(property: Property) {
        propertyDao.insertProperty(property)
        syncPropertyToFirestore(property)
    }

    suspend fun updateProperty(property: Property) {
        propertyDao.updateProperty(property)
        syncPropertyToFirestore(property)
    }

    suspend fun updatePropertyStatus(propertyId: String, status: String) {
        propertyDao.updateStatus(propertyId, status)
        try {
            firestoreDb.collection("properties").document(propertyId).update("status", status)
        } catch (e: Exception) {
            Log.w("HavenRepository", "Firestore status update skipped: ${e.message}")
        }
    }

    suspend fun rejectProperty(propertyId: String, reason: String) {
        propertyDao.rejectProperty(propertyId, reason)
        try {
            firestoreDb.collection("properties").document(propertyId).update(
                mapOf(
                    "status" to "rejected",
                    "rejectionReason" to reason
                )
            )
        } catch (e: Exception) {
            Log.w("HavenRepository", "Firestore reject update skipped: ${e.message}")
        }
    }

    suspend fun verifyProperty(propertyId: String, verified: Boolean) {
        val status = if (verified) "verified" else "unverified"
        propertyDao.updateVerificationStatus(propertyId, status)
        try {
            firestoreDb.collection("properties").document(propertyId).update("isVerified", verified)
        } catch (e: Exception) {
            Log.w("HavenRepository", "Firestore verify update skipped: ${e.message}")
        }
    }

    private fun syncPropertyToFirestore(prop: Property) {
        try {
            val propData = hashMapOf(
                "title" to prop.title,
                "description" to prop.description,
                "price" to prop.originalPrice,
                "formattedPrice" to prop.formattedPrice,
                "currency" to prop.currency,
                "countryCode" to prop.countryCode,
                "state" to prop.state,
                "city" to prop.city,
                "neighbourhood" to prop.neighbourhood,
                "luxuryCategory" to prop.luxuryCategory,
                "propertyType" to prop.propertyType,
                "listingType" to prop.listingType,
                "status" to prop.status,
                "bedrooms" to prop.bedrooms,
                "bathrooms" to prop.bathrooms,
                "sizeSqFt" to prop.sizeSqFt,
                "isVerified" to prop.isVerified,
                "titleDocument" to prop.titleDocument,
                "images" to prop.images,
                "videoUrl" to (prop.videoUrl ?: ""),
                "ownerId" to prop.ownerId,
                "agentId" to prop.agentId,
                "agentName" to prop.agentName,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestoreDb.collection("properties")
                .document(prop.id)
                .set(propData)
        } catch (e: Exception) {
            Log.e("HavenRepository", "Error syncing property to Firestore: ${e.message}")
        }
    }

    fun listenToWebsiteProperties() {
        try {
            firestoreDb.collection("properties").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("HavenRepository", "Firestore properties listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    CoroutineScope(Dispatchers.IO).launch {
                        for (doc in snapshot.documents) {
                            val id = doc.id
                            val title = doc.getString("title") ?: continue
                            val desc = doc.getString("description") ?: ""
                            val price = doc.getDouble("price") ?: 0.0
                            val currency = doc.getString("currency") ?: "NGN"
                            val countryCode = doc.getString("countryCode") ?: "NG"
                            val state = doc.getString("state") ?: ""
                            val city = doc.getString("city") ?: ""
                            val neighbourhood = doc.getString("neighbourhood") ?: ""
                            val luxuryCat = doc.getString("luxuryCategory") ?: "Waterfront Sanctuary"
                            val propType = doc.getString("propertyType") ?: "Detached Villa"
                            val listType = doc.getString("listingType") ?: "sale"
                            val status = doc.getString("status") ?: "published"
                            val bedrooms = doc.getLong("bedrooms")?.toInt() ?: 0
                            val bathrooms = doc.getLong("bathrooms")?.toInt() ?: 0
                            val sizeSqFt = doc.getLong("sizeSqFt")?.toInt() ?: 0
                            val isVerified = doc.getBoolean("isVerified") ?: true
                            val titleDoc = doc.getString("titleDocument") ?: "Certificate of Occupancy"
                            val images = (doc.get("images") as? List<*>)?.mapNotNull { it?.toString() } ?: listOf("img_haven_waterfront")
                            val videoUrl = doc.getString("videoUrl")
                            val ownerId = doc.getString("ownerId") ?: ""
                            val agentId = doc.getString("agentId") ?: ""
                            val agentName = doc.getString("agentName") ?: "PROJECT X Advisor"

                            val prop = Property(
                                id = id,
                                title = title,
                                description = desc,
                                originalPrice = price,
                                currency = currency,
                                countryCode = countryCode,
                                state = state,
                                city = city,
                                neighbourhood = neighbourhood,
                                luxuryCategory = luxuryCat,
                                propertyType = propType,
                                listingType = listType,
                                status = status,
                                bedrooms = bedrooms,
                                bathrooms = bathrooms,
                                sizeSqFt = sizeSqFt,
                                verificationStatus = if (isVerified) "verified" else "unverified",
                                titleDocument = titleDoc,
                                images = images,
                                videoUrl = videoUrl,
                                ownerId = ownerId,
                                agentId = agentId,
                                agentName = agentName
                            )
                            propertyDao.insertProperty(prop)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("HavenRepository", "Error setting up properties listener: ${e.message}")
        }
    }

    suspend fun syncPropertiesFromWebsite(onComplete: (Int) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                firestoreDb.collection("properties").get().addOnSuccessListener { snapshot ->
                    if (snapshot != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            var count = 0
                            for (doc in snapshot.documents) {
                                val id = doc.id
                                val title = doc.getString("title") ?: continue
                                val desc = doc.getString("description") ?: ""
                                val price = doc.getDouble("price") ?: 0.0
                                val currency = doc.getString("currency") ?: "NGN"
                                val countryCode = doc.getString("countryCode") ?: "NG"
                                val state = doc.getString("state") ?: ""
                                val city = doc.getString("city") ?: ""
                                val neighbourhood = doc.getString("neighbourhood") ?: ""
                                val luxuryCat = doc.getString("luxuryCategory") ?: "Waterfront Sanctuary"
                                val propType = doc.getString("propertyType") ?: "Detached Villa"
                                val listType = doc.getString("listingType") ?: "sale"
                                val status = doc.getString("status") ?: "published"
                                val bedrooms = doc.getLong("bedrooms")?.toInt() ?: 0
                                val bathrooms = doc.getLong("bathrooms")?.toInt() ?: 0
                                val sizeSqFt = doc.getLong("sizeSqFt")?.toInt() ?: 0
                                val isVerified = doc.getBoolean("isVerified") ?: true
                                val titleDoc = doc.getString("titleDocument") ?: "Certificate of Occupancy"
                                val images = (doc.get("images") as? List<*>)?.mapNotNull { it?.toString() } ?: listOf("img_haven_waterfront")
                                val videoUrl = doc.getString("videoUrl")
                                val ownerId = doc.getString("ownerId") ?: ""
                                val agentId = doc.getString("agentId") ?: ""
                                val agentName = doc.getString("agentName") ?: "PROJECT X Advisor"

                                val prop = Property(
                                    id = id,
                                    title = title,
                                    description = desc,
                                    originalPrice = price,
                                    currency = currency,
                                    countryCode = countryCode,
                                    state = state,
                                    city = city,
                                    neighbourhood = neighbourhood,
                                    luxuryCategory = luxuryCat,
                                    propertyType = propType,
                                    listingType = listType,
                                    status = status,
                                    bedrooms = bedrooms,
                                    bathrooms = bathrooms,
                                    sizeSqFt = sizeSqFt,
                                    verificationStatus = if (isVerified) "verified" else "unverified",
                                    titleDocument = titleDoc,
                                    images = images,
                                    videoUrl = videoUrl,
                                    ownerId = ownerId,
                                    agentId = agentId,
                                    agentName = agentName
                                )
                                propertyDao.insertProperty(prop)
                                count++
                            }
                            onComplete(count)
                        }
                    } else {
                        onComplete(0)
                    }
                }.addOnFailureListener {
                    onComplete(0)
                }
            } catch (e: Exception) {
                Log.w("HavenRepository", "Sync failed: ${e.message}")
                onComplete(0)
            }
        }
    }

    // Enquiries
    fun getAllEnquiries(): Flow<List<Enquiry>> = enquiryDao.getAllEnquiries()
    fun getEnquiriesForCustomer(customerId: String): Flow<List<Enquiry>> = enquiryDao.getEnquiriesByCustomer(customerId)
    fun getEnquiriesForAgent(agentId: String): Flow<List<Enquiry>> = enquiryDao.getEnquiriesByAgent(agentId)

    suspend fun sendEnquiry(
        propertyId: String,
        propertyTitle: String,
        agentId: String,
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        message: String
    ) {
        val customerId = _currentUser.value?.id ?: "guest_${UUID.randomUUID().toString().take(6)}"
        val enquiryId = "enq_${UUID.randomUUID().toString().take(8)}"
        val enquiry = Enquiry(
            id = enquiryId,
            propertyId = propertyId,
            propertyTitle = propertyTitle,
            customerId = customerId,
            customerName = customerName,
            customerEmail = customerEmail,
            customerPhone = customerPhone,
            agentId = agentId,
            message = message,
            status = EnquiryStatus.UNREAD.value
        )
        enquiryDao.insertEnquiry(enquiry)

        // Mirror enquiry to Firestore
        try {
            val enquiryData = hashMapOf(
                "propertyId" to propertyId,
                "propertyTitle" to propertyTitle,
                "customerId" to customerId,
                "customerName" to customerName,
                "customerEmail" to customerEmail,
                "customerPhone" to customerPhone,
                "agentId" to agentId,
                "message" to message,
                "status" to EnquiryStatus.UNREAD.value,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestoreDb.collection("enquiries").document(enquiryId).set(enquiryData)
        } catch (e: Exception) {
            Log.e("HavenRepository", "Error mirroring enquiry to Firestore: ${e.message}")
        }
    }

    suspend fun updateEnquiryStatus(enquiryId: String, status: String) {
        enquiryDao.updateEnquiryStatus(enquiryId, status)
        try {
            firestoreDb.collection("enquiries").document(enquiryId).update("status", status)
        } catch (e: Exception) {
            Log.w("HavenRepository", "Error updating enquiry in Firestore: ${e.message}")
        }
    }

    // Inspections
    fun getAllInspections(): Flow<List<Inspection>> = inspectionDao.getAllInspections()
    fun getInspectionsForCustomer(customerId: String): Flow<List<Inspection>> = inspectionDao.getInspectionsByCustomer(customerId)
    fun getInspectionsForAgent(agentId: String): Flow<List<Inspection>> = inspectionDao.getInspectionsByAgent(agentId)

    suspend fun bookInspection(
        propertyId: String,
        propertyTitle: String,
        propertyAddress: String,
        agentId: String,
        date: String,
        timeSlot: String,
        notes: String
    ) {
        val user = _currentUser.value
        val inspectionId = "insp_${UUID.randomUUID().toString().take(8)}"
        val inspection = Inspection(
            id = inspectionId,
            propertyId = propertyId,
            propertyTitle = propertyTitle,
            propertyAddress = propertyAddress,
            customerId = user?.id ?: "guest",
            customerName = user?.name ?: "Guest Applicant",
            customerPhone = user?.phone ?: "",
            agentId = agentId,
            date = date,
            timeSlot = timeSlot,
            notes = notes,
            status = InspectionStatus.PENDING.value
        )
        inspectionDao.insertInspection(inspection)

        // Mirror inspection to Firestore
        try {
            val inspectionData = hashMapOf(
                "propertyId" to propertyId,
                "propertyTitle" to propertyTitle,
                "customerId" to (user?.id ?: "guest"),
                "customerName" to (user?.name ?: "Guest Applicant"),
                "customerPhone" to (user?.phone ?: ""),
                "agentId" to agentId,
                "inspectionDate" to date,
                "timeSlot" to timeSlot,
                "notes" to notes,
                "status" to InspectionStatus.PENDING.value,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestoreDb.collection("inspections").document(inspectionId).set(inspectionData)
        } catch (e: Exception) {
            Log.e("HavenRepository", "Error mirroring inspection to Firestore: ${e.message}")
        }
    }

    suspend fun updateInspectionStatus(inspectionId: String, status: String) {
        inspectionDao.updateInspectionStatus(inspectionId, status)
        try {
            firestoreDb.collection("inspections").document(inspectionId).update("status", status)
        } catch (e: Exception) {
            Log.w("HavenRepository", "Error updating inspection in Firestore: ${e.message}")
        }
    }

    suspend fun rescheduleInspection(inspectionId: String, date: String, timeSlot: String) {
        inspectionDao.rescheduleInspection(inspectionId, date, timeSlot)
        try {
            firestoreDb.collection("inspections").document(inspectionId).update(
                mapOf(
                    "inspectionDate" to date,
                    "timeSlot" to timeSlot
                )
            )
        } catch (e: Exception) {
            Log.w("HavenRepository", "Error updating rescheduled inspection in Firestore: ${e.message}")
        }
    }

    // Favourites
    fun getFavouriteProperties(userId: String): Flow<List<Property>> = favouriteDao.getFavouriteProperties(userId)
    fun isFavourite(userId: String, propertyId: String): Flow<Boolean> = favouriteDao.isFavourite(userId, propertyId)

    suspend fun toggleFavourite(userId: String, propertyId: String, isCurrentlyFav: Boolean) {
        if (isCurrentlyFav) {
            favouriteDao.removeFavourite(userId, propertyId)
        } else {
            favouriteDao.addFavourite(
                Favourite(
                    id = "fav_${userId}_${propertyId}",
                    userId = userId,
                    propertyId = propertyId
                )
            )
        }
    }

    // Users
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    suspend fun setUserVerification(userId: String, isVerified: Boolean) {
        userDao.setVerification(userId, isVerified)
        try {
            firestoreDb.collection("users").document(userId).update("isVerified", isVerified)
        } catch (e: Exception) {
            Log.w("HavenRepository", "Error updating user verification in Firestore: ${e.message}")
        }
    }

    // Real-Time Chat & Community Messaging
    fun getMessagesBetween(userId1: String, userId2: String): Flow<List<ChatMessage>> =
        chatMessageDao.getMessagesBetween(userId1, userId2)

    fun getAllMessagesForUser(userId: String): Flow<List<ChatMessage>> =
        chatMessageDao.getAllMessagesForUser(userId)

    suspend fun sendMessage(
        senderId: String,
        senderName: String,
        recipientId: String,
        recipientName: String,
        text: String,
        propertyId: String? = null,
        propertyTitle: String? = null
    ) {
        val userMsg = ChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            senderId = senderId,
            senderName = senderName,
            recipientId = recipientId,
            recipientName = recipientName,
            propertyId = propertyId,
            propertyTitle = propertyTitle,
            message = text,
            timestamp = System.currentTimeMillis()
        )
        chatMessageDao.insertMessage(userMsg)

        // Instant automated agent reply simulation for responsive live feel
        if (recipientId.startsWith("agent_") || recipientId.startsWith("admin_")) {
            CoroutineScope(Dispatchers.IO).launch {
                kotlinx.coroutines.delay(1200)
                val replyText = when {
                    text.contains("title", ignoreCase = true) || text.contains("deed", ignoreCase = true) || text.contains("c of o", ignoreCase = true) ->
                        "Good day $senderName. The land title and deed has been authenticated with full Certificate of Occupancy. Would you like me to share the verified survey coordinates?"
                    text.contains("viewing", ignoreCase = true) || text.contains("inspect", ignoreCase = true) || text.contains("saturday", ignoreCase = true) ->
                        "Certainly! I can arrange an exclusive viewing. Please book a preferred time slot via the inspection calendar, or 10:30 AM works well."
                    text.contains("price", ignoreCase = true) || text.contains("offer", ignoreCase = true) || text.contains("discount", ignoreCase = true) ->
                        "Thank you for your enquiry. The vendor is open to reviewing structured offers from pre-qualified purchasers."
                    else ->
                        "Hello $senderName, thank you for contacting PROJECT X. I'm actively reviewing your message and will facilitate every detail for you."
                }
                val reply = ChatMessage(
                    id = "msg_${UUID.randomUUID().toString().take(8)}",
                    senderId = recipientId,
                    senderName = recipientName,
                    recipientId = senderId,
                    recipientName = senderName,
                    propertyId = propertyId,
                    propertyTitle = propertyTitle,
                    message = replyText,
                    timestamp = System.currentTimeMillis()
                )
                chatMessageDao.insertMessage(reply)
            }
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        if (propertyDao.getCount() > 0) return

        // 1. Initial Users
        val users = listOf(
            User(
                id = "user_customer_1",
                name = "Chidi Okonkwo",
                email = "chidi.okonkwo@havenbuyer.com",
                phone = "+234 803 123 4567",
                role = UserRole.CUSTOMER.value,
                isVerified = true,
                preferredCountry = "NG",
                preferredCurrency = "NGN"
            ),
            User(
                id = "agent_amina_1",
                name = "Amina Bello",
                email = "amina.bello@havenluxury.ng",
                phone = "+234 802 987 6543",
                role = UserRole.AGENT.value,
                agencyName = "Haven Prime Realty Group",
                isVerified = true,
                preferredCountry = "NG",
                preferredCurrency = "NGN"
            ),
            User(
                id = "agent_david_2",
                name = "David Thorne",
                email = "d.thorne@havenproperties.co.uk",
                phone = "+44 20 7946 0192",
                role = UserRole.AGENT.value,
                agencyName = "Thorne & Partners London",
                isVerified = true,
                preferredCountry = "GB",
                preferredCurrency = "GBP"
            ),
            User(
                id = "admin_victor_1",
                name = "Victor Nnakwe",
                email = "nnakwevictor90@gmail.com",
                phone = "07017331773",
                role = UserRole.ADMIN.value,
                agencyName = "PROJECT X Executive Board",
                isVerified = true,
                preferredCountry = "NG",
                preferredCurrency = "NGN"
            ),
            User(
                id = "owner_tariq_1",
                name = "Tariq Al-Mansoor",
                email = "tariq.almansoor@projectxgroup.ae",
                phone = "+971 4 312 8899",
                role = UserRole.OWNER.value,
                agencyName = "Al-Mansoor International Assets",
                isVerified = true,
                preferredCountry = "AE",
                preferredCurrency = "AED"
            )
        )
        userDao.insertUsers(users)

        // 2. Initial Luxury Properties with attributes for location (Nigeria, US, UK, UAE), price, and luxury category
        val properties = listOf(
            Property(
                id = "prop_ng_01",
                title = "The Grand Waterfront Villa",
                description = "An ultra-luxurious architectural marvel situated on the pristine shores of Banana Island. Features bespoke Italian marble flooring, double-height floor-to-ceiling glass facades, smart home climate and lighting control, a private yacht jetty, temperature-controlled infinity pool, cinema room, private elevator, and dedicated staff quarters with 24/7 uninterrupted power.",
                listingType = "sale",
                originalPrice = 1850000000.0,
                currency = "NGN",
                countryCode = "NG",
                state = "Lagos",
                city = "Lagos",
                lga = "Eti-Osa",
                neighbourhood = "Banana Island, Ikoyi",
                address = "14 Ocean Crest Way, Banana Island, Ikoyi, Lagos",
                bedrooms = 6,
                bathrooms = 7,
                toilets = 8,
                parking = 6,
                sizeSqFt = 9200,
                amenities = listOf("Waterfront View", "Private Yacht Jetty", "Infinity Pool", "Elevator", "Smart Home Automation", "24/7 Power", "Private Cinema", "Security Bunker", "Staff Quarters"),
                images = listOf("img_haven_waterfront", "haven_hero_banner", "haven_penthouse"),
                agentId = "agent_amina_1",
                agentName = "Amina Bello",
                agentPhone = "+234 802 987 6543",
                agentEmail = "amina.bello@havenluxury.ng",
                status = PropertyStatus.PUBLISHED.value,
                verificationStatus = "verified"
            ),
            Property(
                id = "prop_ng_02",
                title = "Maitama Diplomatic Presidential Palace",
                description = "Set on two expansive acres within the prestigious diplomatic enclave of Maitama, Abuja. Boasts bulletproof panoramic fenestration, Olympic-length heated swimming pool, an underground wine cellar, private executive boardroom, multi-car underground showroom, and state-of-the-art perimeter security.",
                listingType = "sale",
                originalPrice = 2400000000.0,
                currency = "NGN",
                countryCode = "NG",
                state = "FCT",
                city = "Abuja",
                lga = "Municipal",
                neighbourhood = "Maitama",
                address = "7 Aso Drive, Maitama District, Abuja",
                bedrooms = 7,
                bathrooms = 8,
                toilets = 9,
                parking = 10,
                sizeSqFt = 12500,
                amenities = listOf("Olympic Pool", "Bulletproof Glazing", "Executive Boardroom", "Helipad Access", "Wine Cellar", "24/7 Power", "CCTV Surveillance", "Lush Gardens"),
                images = listOf("img_haven_estate", "haven_hero_banner"),
                agentId = "agent_amina_1",
                agentName = "Amina Bello",
                agentPhone = "+234 802 987 6543",
                agentEmail = "amina.bello@havenluxury.ng",
                status = PropertyStatus.PUBLISHED.value,
                verificationStatus = "verified"
            ),
            Property(
                id = "prop_ng_03",
                title = "Eko Atlantic Sky-Penthouse",
                description = "Perched atop the crowning levels of Eko Atlantic City with uninterrupted 270-degree vistas of the Atlantic Ocean and Victoria Island coastline. Offers private wraparound terrace, heated infinity dip pool, private butler service, bespoke German kitchen, and private high-speed elevator.",
                listingType = "sale",
                originalPrice = 750000000.0,
                currency = "NGN",
                countryCode = "NG",
                state = "Lagos",
                city = "Lagos",
                lga = "Victoria Island",
                neighbourhood = "Eko Atlantic City",
                address = "Tower One, Pearl Boulevard, Eko Atlantic, Lagos",
                bedrooms = 4,
                bathrooms = 5,
                toilets = 5,
                parking = 3,
                sizeSqFt = 5800,
                amenities = listOf("Ocean View", "Private Sky Pool", "Concierge Service", "High Speed Elevator", "Smart Home Automation", "Gym & Spa", "24/7 Power"),
                images = listOf("haven_penthouse"),
                agentId = "agent_amina_1",
                agentName = "Amina Bello",
                agentPhone = "+234 802 987 6543",
                agentEmail = "amina.bello@havenluxury.ng",
                status = PropertyStatus.PUBLISHED.value,
                verificationStatus = "verified"
            ),
            Property(
                id = "prop_ng_04",
                title = "Lekki Phase 1 Smart Villa with Cinema",
                description = "Modern and sleek 5-bedroom detached residence in a serene, gated close in Lekki Phase 1. Features high-end finishes, fully integrated voice automation, private rooftop lounge, custom cinema with Dolby Atmos, and Olympic lap swimming pool.",
                listingType = "rent",
                originalPrice = 35000000.0,
                currency = "NGN",
                countryCode = "NG",
                state = "Lagos",
                city = "Lagos",
                lga = "Eti-Osa",
                neighbourhood = "Lekki Phase 1",
                address = "12 Admiralty Close, Lekki Phase 1, Lagos",
                bedrooms = 5,
                bathrooms = 5,
                toilets = 6,
                parking = 4,
                sizeSqFt = 4500,
                amenities = listOf("Cinema Room", "Swimming Pool", "Rooftop Lounge", "Smart Automation", "Fitted Kitchen", "24/7 Power"),
                images = listOf("img_haven_estate"),
                agentId = "agent_amina_1",
                agentName = "Amina Bello",
                agentPhone = "+234 802 987 6543",
                agentEmail = "amina.bello@havenluxury.ng",
                status = PropertyStatus.PUBLISHED.value,
                verificationStatus = "verified"
            ),
            Property(
                id = "prop_ng_05",
                title = "Asokoro Sunset Hilltop Residence",
                description = "Exclusive luxury serviced shortlet overlooking Abuja's lush landscape. Includes full housekeeping, dedicated private chef, infinity terrace, high-speed fiber internet, and 24/7 security detail.",
                listingType = "shortlet",
                originalPrice = 850000.0,
                currency = "NGN",
                countryCode = "NG",
                state = "FCT",
                city = "Abuja",
                lga = "Municipal",
                neighbourhood = "Asokoro",
                address = "19 Hill View Crescent, Asokoro, Abuja",
                bedrooms = 4,
                bathrooms = 4,
                toilets = 5,
                parking = 4,
                sizeSqFt = 4200,
                amenities = listOf("Private Chef", "Panoramic View", "Daily Housekeeping", "Infinity Terrace", "High Speed Wifi", "24/7 Power"),
                images = listOf("img_haven_waterfront"),
                agentId = "agent_amina_1",
                agentName = "Amina Bello",
                agentPhone = "+234 802 987 6543",
                agentEmail = "amina.bello@havenluxury.ng",
                status = PropertyStatus.PUBLISHED.value,
                verificationStatus = "verified"
            ),
            Property(
                id = "prop_ng_06",
                title = "Old GRA Colonial Garden Manor",
                description = "Stately colonial architectural manor on sprawling manicured grounds in Port Harcourt's historic Old GRA. Complete with swimming pool, industrial water filtration, independent transformer, and detached guest houses.",
                listingType = "sale",
                originalPrice = 850000000.0,
                currency = "NGN",
                countryCode = "NG",
                state = "Rivers",
                city = "Port Harcourt",
                lga = "Port Harcourt",
                neighbourhood = "Old GRA",
                address = "5 Forces Avenue, Old GRA, Port Harcourt",
                bedrooms = 5,
                bathrooms = 6,
                toilets = 7,
                parking = 8,
                sizeSqFt = 7800,
                amenities = listOf("Manicured Grounds", "Swimming Pool", "Guest House", "Dedicated Transformer", "Borehole & Filtration"),
                images = listOf("haven_penthouse"),
                agentId = "agent_amina_1",
                agentName = "Amina Bello",
                agentPhone = "+234 802 987 6543",
                agentEmail = "amina.bello@havenluxury.ng",
                status = PropertyStatus.PUBLISHED.value,
                verificationStatus = "verified"
            ),
            Property(
                id = "prop_ng_07",
                title = "Epe Lagoon Modern Eco-Estate",
                description = "Newly developed sustainable luxury lakefront compound featuring solar microgrid, private fishing pier, organic garden, and contemporary Scandinavian open-concept architecture. Awaiting final administrative verification review.",
                listingType = "sale",
                originalPrice = 280000000.0,
                currency = "NGN",
                countryCode = "NG",
                state = "Lagos",
                city = "Epe",
                lga = "Epe",
                neighbourhood = "Lagoon Front",
                address = "8 Marina Road, Epe Lagoon, Lagos",
                bedrooms = 4,
                bathrooms = 4,
                toilets = 5,
                parking = 4,
                sizeSqFt = 3900,
                amenities = listOf("Lagoon Front", "Solar Microgrid", "Private Pier", "Smart Climate"),
                images = listOf("haven_hero_banner"),
                agentId = "agent_amina_1",
                agentName = "Amina Bello",
                agentPhone = "+234 802 987 6543",
                agentEmail = "amina.bello@havenluxury.ng",
                status = PropertyStatus.PENDING_REVIEW.value,
                verificationStatus = "unverified"
            ),
            Property(
                id = "prop_uae_01",
                title = "Palm Jumeirah Signature Beachfront Villa",
                description = "Iconic ultra-luxury estate located on the Fronds of Palm Jumeirah, Dubai. Private pristine sandy beach, custom infinity pool with Dubai Marina skyline views, private spa, and Italian furnishings.",
                listingType = "sale",
                originalPrice = 42000000.0,
                currency = "AED",
                countryCode = "AE",
                state = "Dubai",
                city = "Dubai",
                lga = "Palm Jumeirah",
                neighbourhood = "Frond N, Palm Jumeirah",
                address = "Villa 22, Frond N, Palm Jumeirah, Dubai",
                bedrooms = 6,
                bathrooms = 7,
                toilets = 8,
                parking = 5,
                sizeSqFt = 11000,
                amenities = listOf("Private Beach", "Skyline View", "Infinity Pool", "Private Spa", "Smart Home", "Concierge"),
                images = listOf("img_haven_waterfront", "haven_hero_banner"),
                agentId = "agent_david_2",
                agentName = "David Thorne",
                agentPhone = "+44 20 7946 0192",
                agentEmail = "d.thorne@havenproperties.co.uk",
                status = PropertyStatus.PUBLISHED.value,
                verificationStatus = "verified"
            ),
            Property(
                id = "prop_uk_01",
                title = "Mayfair Belgravia Classical Townhouse",
                description = "A grade-II listed Georgian masterpiece in the heart of Mayfair, London. Preserved classical plaster moldings, passenger lift to all 5 levels, temperature-controlled wine room, rooftop entertaining terrace, and separate staff mews.",
                listingType = "sale",
                originalPrice = 14500000.0,
                currency = "GBP",
                countryCode = "GB",
                state = "Greater London",
                city = "London",
                lga = "Westminster",
                neighbourhood = "Mayfair",
                address = "18 Chesterfield Hill, Mayfair, London W1J 5BG",
                bedrooms = 5,
                bathrooms = 6,
                toilets = 7,
                parking = 2,
                sizeSqFt = 6800,
                amenities = listOf("Heritage Architecture", "Internal Passenger Lift", "Wine Cellar", "Rooftop Terrace", "Mews House"),
                images = listOf("haven_penthouse", "img_haven_estate"),
                agentId = "agent_david_2",
                agentName = "David Thorne",
                agentPhone = "+44 20 7946 0192",
                agentEmail = "d.thorne@havenproperties.co.uk",
                status = PropertyStatus.PUBLISHED.value,
                verificationStatus = "verified"
            ),
            Property(
                id = "prop_us_01",
                title = "Biscayne Bay Ultra-Modern Sanctuary",
                description = "Architectural triumph in Coral Gables / Miami with 120 feet of deepwater frontage on Biscayne Bay. Designed by world-renowned architects with cantilevering balconies, zero-edge pool, private dock for 100ft mega-yacht.",
                listingType = "sale",
                originalPrice = 12900000.0,
                currency = "USD",
                countryCode = "US",
                state = "Florida",
                city = "Miami",
                lga = "Miami-Dade",
                neighbourhood = "Coral Gables",
                address = "3400 Gables Boulevard, Coral Gables, FL 33134",
                bedrooms = 6,
                bathrooms = 7,
                toilets = 8,
                parking = 6,
                sizeSqFt = 9800,
                amenities = listOf("100ft Mega-Yacht Dock", "Zero-Edge Pool", "Cantilever Architecture", "Smart Glass", "Outdoor Summer Kitchen"),
                images = listOf("img_haven_estate", "haven_penthouse"),
                agentId = "agent_david_2",
                agentName = "David Thorne",
                agentPhone = "+44 20 7946 0192",
                agentEmail = "d.thorne@havenproperties.co.uk",
                status = PropertyStatus.PUBLISHED.value,
                verificationStatus = "verified"
            )
        )
        propertyDao.insertProperties(properties)

        // Sync initial luxury properties to Firestore
        properties.forEach { syncPropertyToFirestore(it) }

        // 3. Initial Enquiries
        val enquiries = listOf(
            Enquiry(
                id = "enq_01",
                propertyId = "prop_ng_01",
                propertyTitle = "The Grand Waterfront Villa",
                customerId = "user_customer_1",
                customerName = "Chidi Okonkwo",
                customerEmail = "chidi.okonkwo@havenbuyer.com",
                customerPhone = "+234 803 123 4567",
                agentId = "agent_amina_1",
                message = "Good day, I am interested in acquiring this waterfront villa on Banana Island for my family. Could you kindly provide the survey plan and schedule an in-person viewing?",
                status = EnquiryStatus.UNREAD.value,
                createdAt = System.currentTimeMillis() - 86400000
            ),
            Enquiry(
                id = "enq_02",
                propertyId = "prop_ng_03",
                propertyTitle = "Eko Atlantic Sky-Penthouse",
                customerId = "user_customer_1",
                customerName = "Chidi Okonkwo",
                customerEmail = "chidi.okonkwo@havenbuyer.com",
                customerPhone = "+234 803 123 4567",
                agentId = "agent_amina_1",
                message = "Inquiring regarding payment milestones and the facility management service charges for this penthouse.",
                status = EnquiryStatus.CONTACTED.value,
                createdAt = System.currentTimeMillis() - 172800000
            )
        )
        enquiries.forEach { enquiryDao.insertEnquiry(it) }

        // 4. Initial Inspections
        val inspections = listOf(
            Inspection(
                id = "insp_01",
                propertyId = "prop_ng_01",
                propertyTitle = "The Grand Waterfront Villa",
                propertyAddress = "14 Ocean Crest Way, Banana Island, Ikoyi, Lagos",
                customerId = "user_customer_1",
                customerName = "Chidi Okonkwo",
                customerPhone = "+234 803 123 4567",
                agentId = "agent_amina_1",
                date = "2026-11-15",
                timeSlot = "10:00 AM - 11:30 AM",
                notes = "Will be arriving with personal architect and legal representative.",
                status = InspectionStatus.CONFIRMED.value
            ),
            Inspection(
                id = "insp_02",
                propertyId = "prop_ng_02",
                propertyTitle = "Maitama Diplomatic Presidential Palace",
                propertyAddress = "7 Aso Drive, Maitama District, Abuja",
                customerId = "user_customer_1",
                customerName = "Chidi Okonkwo",
                customerPhone = "+234 803 123 4567",
                agentId = "agent_amina_1",
                date = "2026-11-20",
                timeSlot = "02:00 PM - 03:30 PM",
                notes = "Requested security clearance in advance.",
                status = InspectionStatus.PENDING.value
            )
        )
        inspections.forEach { inspectionDao.insertInspection(it) }

        // 5. Initial Favourites
        favouriteDao.addFavourite(
            Favourite(
                id = "fav_user_customer_1_prop_ng_01",
                userId = "user_customer_1",
                propertyId = "prop_ng_01"
            )
        )
        favouriteDao.addFavourite(
            Favourite(
                id = "fav_user_customer_1_prop_ng_03",
                userId = "user_customer_1",
                propertyId = "prop_ng_03"
            )
        )

        // 6. Initial Real-Time Chat Messages
        val initialMessages = listOf(
            ChatMessage(
                id = "msg_seed_1",
                senderId = "agent_amina_1",
                senderName = "Amina Bello",
                recipientId = "user_customer_1",
                recipientName = "Chidi Okonkwo",
                propertyId = "prop_ng_01",
                propertyTitle = "The Grand Waterfront Villa",
                message = "Good afternoon Mr. Okonkwo. The verified architectural plans and deed of conveyance for the Banana Island Waterfront estate are ready for your review.",
                timestamp = System.currentTimeMillis() - 7200000
            ),
            ChatMessage(
                id = "msg_seed_2",
                senderId = "user_customer_1",
                senderName = "Chidi Okonkwo",
                recipientId = "agent_amina_1",
                recipientName = "Amina Bello",
                propertyId = "prop_ng_01",
                propertyTitle = "The Grand Waterfront Villa",
                message = "Thank you Amina. I have inspected the specs. Does the private jetty support a 55ft motor yacht during low tide?",
                timestamp = System.currentTimeMillis() - 3600000
            ),
            ChatMessage(
                id = "msg_seed_3",
                senderId = "agent_amina_1",
                senderName = "Amina Bello",
                recipientId = "user_customer_1",
                recipientName = "Chidi Okonkwo",
                propertyId = "prop_ng_01",
                propertyTitle = "The Grand Waterfront Villa",
                message = "Yes, absolutely! The channel was dredged to 4.2 meters depth specifically for medium and large yachts.",
                timestamp = System.currentTimeMillis() - 1800000
            )
        )
        chatMessageDao.insertMessages(initialMessages)
    }
}
