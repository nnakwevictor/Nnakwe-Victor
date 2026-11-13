package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.enums.PropertyStatus
import com.example.data.enums.UserRole
import com.example.data.model.Property
import com.example.data.model.User
import com.example.data.repository.HavenRepository
import com.example.ui.components.AuthDialog
import com.example.ui.components.HavenTopBar
import com.example.ui.components.RoleSwitchSheet
import com.example.ui.screens.ActivityScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AgentDashboardScreen
import com.example.ui.screens.ChatThreadScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.CreateEditPropertyScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FavouritesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PropertyDetailScreen
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy
import com.example.ui.theme.HavenNavyDark
import com.example.ui.theme.HavenNavyLight
import kotlinx.coroutines.launch

enum class MainNavTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    EXPLORE("Explore", Icons.Default.Explore),
    SAVED("Saved", Icons.Default.Favorite),
    CONNECT("Network", Icons.Default.Groups),
    ACTIVITY("Activity", Icons.Default.EventNote),
    DASHBOARD("Hub", Icons.Default.BusinessCenter)
}

@Composable
fun HavenApp(
    repository: HavenRepository,
    onNavigateToLogin: (() -> Unit)? = null,
    initialTab: MainNavTab = MainNavTab.EXPLORE
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentUser by repository.currentUser.collectAsState()
    val selectedCountry by repository.currentCountryFilter.collectAsState()

    val allProperties by repository.getAllProperties().collectAsState(initial = emptyList())
    val publishedProperties by repository.getPublishedProperties().collectAsState(initial = emptyList())
    val pendingReviewProperties by repository.getPendingReviewProperties().collectAsState(initial = emptyList())

    val favouriteProperties by repository.getFavouriteProperties(currentUser?.id ?: "").collectAsState(initial = emptyList())
    val favouriteIds = remember(favouriteProperties) { favouriteProperties.map { it.id }.toSet() }

    val allEnquiries by repository.getAllEnquiries().collectAsState(initial = emptyList())
    val allInspections by repository.getAllInspections().collectAsState(initial = emptyList())
    val allUsers by repository.getAllUsers().collectAsState(initial = emptyList())

    // Role-filtered Activity records
    val relevantEnquiries = remember(allEnquiries, currentUser) {
        when {
            currentUser == null -> emptyList()
            currentUser?.isAdmin == true -> allEnquiries
            currentUser?.isAgent == true -> allEnquiries.filter { it.agentId == currentUser?.id }
            else -> allEnquiries.filter { it.customerId == currentUser?.id }
        }
    }
    val relevantInspections = remember(allInspections, currentUser) {
        when {
            currentUser == null -> emptyList()
            currentUser?.isAdmin == true -> allInspections
            currentUser?.isAgent == true -> allInspections.filter { it.agentId == currentUser?.id }
            else -> allInspections.filter { it.customerId == currentUser?.id }
        }
    }

    var selectedTab by remember { mutableStateOf(initialTab) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var isCreatingListing by remember { mutableStateOf(false) }
    var showRoleSwitchSheet by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var activeChatRecipient by remember { mutableStateOf<User?>(null) }
    var activeChatPropertyTitle by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (selectedProperty == null && !isCreatingListing && activeChatRecipient == null) {
                HavenTopBar(
                    currentUser = currentUser,
                    selectedCountry = selectedCountry,
                    onCountrySelected = { repository.setCountryFilter(it) },
                    onSwitchRoleClick = { showRoleSwitchSheet = true }
                )
            }
        },
        bottomBar = {
            if (selectedProperty == null && !isCreatingListing && activeChatRecipient == null) {
                Column(modifier = Modifier.fillMaxWidth().background(HavenNavyDark)) {
                    HorizontalDivider(color = HavenNavyLight.copy(alpha = 0.8f), thickness = 1.dp)
                    NavigationBar(
                        containerColor = HavenNavyDark,
                        tonalElevation = 0.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        val tabs = listOf(
                            MainNavTab.EXPLORE,
                            MainNavTab.SAVED,
                            MainNavTab.CONNECT,
                            MainNavTab.ACTIVITY,
                            MainNavTab.DASHBOARD
                        )

                        tabs.forEach { tab ->
                            val isSelected = selectedTab == tab
                            val tabLabel = if (tab == MainNavTab.DASHBOARD) {
                                when {
                                    currentUser?.isAdmin == true -> "Admin"
                                    currentUser?.isAgent == true -> "Agent"
                                    else -> "Account"
                                }
                            } else tab.label

                            val tabIcon = if (tab == MainNavTab.DASHBOARD) {
                                when {
                                    currentUser?.isAdmin == true -> Icons.Default.AdminPanelSettings
                                    currentUser?.isAgent == true -> Icons.Default.Apartment
                                    else -> Icons.Default.Person
                                }
                            } else tab.icon

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tabIcon,
                                        contentDescription = tabLabel
                                    )
                                },
                                label = {
                                    Text(
                                        text = tabLabel,
                                        fontFamily = com.example.ui.theme.PlusJakartaFontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = HavenGold,
                                    selectedTextColor = HavenGold,
                                    indicatorColor = HavenNavyLight,
                                    unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                    unselectedTextColor = Color.White.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // 1. 1-on-1 Real-time Chat Thread
                activeChatRecipient != null -> {
                    ChatThreadScreen(
                        currentUserId = currentUser?.id ?: "guest_applicant",
                        currentUserName = currentUser?.name ?: "Guest Applicant",
                        recipient = activeChatRecipient!!,
                        propertyTitle = activeChatPropertyTitle,
                        repository = repository,
                        onBack = {
                            activeChatRecipient = null
                            activeChatPropertyTitle = null
                        }
                    )
                }

                // 2. Property Detail view
                selectedProperty != null -> {
                    PropertyDetailScreen(
                        property = selectedProperty!!,
                        currentUser = currentUser,
                        isFavourite = favouriteIds.contains(selectedProperty!!.id),
                        onBack = { selectedProperty = null },
                        onToggleFavourite = { prop ->
                            val user = currentUser
                            if (user != null) {
                                coroutineScope.launch {
                                    repository.toggleFavourite(user.id, prop.id, favouriteIds.contains(prop.id))
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please select a profile to save favourites")
                                }
                            }
                        },
                        onBookInspection = { date, time, notes ->
                            coroutineScope.launch {
                                repository.bookInspection(
                                    propertyId = selectedProperty!!.id,
                                    propertyTitle = selectedProperty!!.title,
                                    propertyAddress = selectedProperty!!.address,
                                    agentId = selectedProperty!!.agentId,
                                    date = date,
                                    timeSlot = time,
                                    notes = notes
                                )
                                snackbarHostState.showSnackbar("Inspection requested for $date ($time)")
                            }
                        },
                        onSendEnquiry = { name, email, phone, msg ->
                            coroutineScope.launch {
                                repository.sendEnquiry(
                                    propertyId = selectedProperty!!.id,
                                    propertyTitle = selectedProperty!!.title,
                                    agentId = selectedProperty!!.agentId,
                                    customerName = name,
                                    customerEmail = email,
                                    customerPhone = phone,
                                    message = msg
                                )
                                snackbarHostState.showSnackbar("Enquiry sent to ${selectedProperty!!.agentName}")
                            }
                        },
                        onStartChat = { agentId, agentName ->
                            val targetUser = allUsers.find { it.id == agentId } ?: User(
                                id = agentId,
                                name = agentName,
                                email = "",
                                role = "agent",
                                isVerified = true
                            )
                            activeChatRecipient = targetUser
                            activeChatPropertyTitle = selectedProperty?.title
                        }
                    )
                }

                // 3. Create Listing screen
                isCreatingListing -> {
                    CreateEditPropertyScreen(
                        currentUser = currentUser ?: allUsers.first(),
                        onSaveProperty = { newProp ->
                            coroutineScope.launch {
                                repository.createProperty(newProp)
                                isCreatingListing = false
                                val msg = if (newProp.status == PropertyStatus.PENDING_REVIEW.value)
                                    "Submitted '${newProp.title}' for Admin review"
                                else "Saved '${newProp.title}' as Draft"
                                snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onCancel = { isCreatingListing = false }
                    )
                }

                // 3. Tab views
                else -> {
                    when (selectedTab) {
                        MainNavTab.EXPLORE -> {
                            ExploreScreen(
                                properties = allProperties,
                                favouriteIds = favouriteIds,
                                selectedCountry = selectedCountry,
                                onToggleFavourite = { prop ->
                                    val user = currentUser
                                    if (user != null) {
                                        coroutineScope.launch {
                                            repository.toggleFavourite(user.id, prop.id, favouriteIds.contains(prop.id))
                                        }
                                    } else {
                                        showRoleSwitchSheet = true
                                    }
                                },
                                onPropertyClick = { prop -> selectedProperty = prop }
                            )
                        }

                        MainNavTab.SAVED -> {
                            FavouritesScreen(
                                favouriteProperties = favouriteProperties,
                                currentUser = currentUser,
                                onPropertyClick = { prop -> selectedProperty = prop },
                                onToggleFavourite = { prop ->
                                    val user = currentUser
                                    if (user != null) {
                                        coroutineScope.launch {
                                            repository.toggleFavourite(user.id, prop.id, true)
                                        }
                                    }
                                },
                                onExploreClick = { selectedTab = MainNavTab.EXPLORE }
                            )
                        }

                        MainNavTab.CONNECT -> {
                            CommunityScreen(
                                users = allUsers,
                                currentUser = currentUser,
                                onStartChat = { target ->
                                    activeChatRecipient = target
                                    activeChatPropertyTitle = null
                                }
                            )
                        }

                        MainNavTab.ACTIVITY -> {
                            ActivityScreen(
                                inspections = relevantInspections,
                                enquiries = relevantEnquiries,
                                currentUser = currentUser,
                                onUpdateInspectionStatus = { id, status ->
                                    coroutineScope.launch {
                                        repository.updateInspectionStatus(id, status)
                                        snackbarHostState.showSnackbar("Inspection updated to $status")
                                    }
                                },
                                onUpdateEnquiryStatus = { id, status ->
                                    coroutineScope.launch {
                                        repository.updateEnquiryStatus(id, status)
                                        snackbarHostState.showSnackbar("Enquiry status updated to $status")
                                    }
                                }
                            )
                        }

                        MainNavTab.DASHBOARD -> {
                            when {
                                currentUser?.isAdmin == true -> {
                                    AdminDashboardScreen(
                                        allProperties = allProperties,
                                        pendingProperties = pendingReviewProperties,
                                        users = allUsers,
                                        onApproveProperty = { id ->
                                            coroutineScope.launch {
                                                repository.updatePropertyStatus(id, PropertyStatus.PUBLISHED.value)
                                                repository.verifyProperty(id, true)
                                                snackbarHostState.showSnackbar("Listing approved and published to PROJECT X Global Marketplace!")
                                            }
                                        },
                                        onRejectProperty = { id, reason ->
                                            coroutineScope.launch {
                                                repository.rejectProperty(id, reason)
                                                snackbarHostState.showSnackbar("Listing rejected with feedback sent to agent")
                                            }
                                        },
                                        onToggleAgentVerification = { userId, verified ->
                                            coroutineScope.launch {
                                                repository.setUserVerification(userId, verified)
                                                val statusText = if (verified) "verified" else "unverified"
                                                snackbarHostState.showSnackbar("Agent verification updated to $statusText")
                                            }
                                        },
                                        onPropertyClick = { prop -> selectedProperty = prop }
                                    )
                                }

                                else -> {
                                    val dashboardUser = if (currentUser != null && (currentUser?.isAgent == true || currentUser?.isOwner == true)) {
                                        currentUser!!
                                    } else {
                                        currentUser ?: User(
                                            id = "agent_amina_1",
                                            name = "Amina Bello",
                                            email = "amina.bello@havenluxury.ng",
                                            role = UserRole.AGENT.value,
                                            agencyName = "PROJECT X Prime Realty Group",
                                            isVerified = true
                                        )
                                    }
                                    val agentProps = if (currentUser != null && (currentUser?.isAgent == true || currentUser?.isOwner == true)) {
                                        allProperties.filter { it.agentId == currentUser?.id || it.ownerId == currentUser?.id }
                                    } else {
                                        allProperties
                                    }
                                    AgentDashboardScreen(
                                        agentUser = dashboardUser,
                                        myProperties = agentProps,
                                        myEnquiries = relevantEnquiries,
                                        myInspections = relevantInspections,
                                        onCreateListingClick = { isCreatingListing = true },
                                        onPropertyClick = { prop -> selectedProperty = prop },
                                        onUpdatePropertyStatus = { id, status ->
                                            coroutineScope.launch {
                                                repository.updatePropertyStatus(id, status)
                                                snackbarHostState.showSnackbar("Property status updated to $status")
                                            }
                                        },
                                        onSyncWebsiteClick = {
                                            coroutineScope.launch {
                                                repository.syncPropertiesFromWebsite { count ->
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Synchronized $count properties with Project X Cloud Backend")
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Persona Switcher Bottom Sheet
        if (showRoleSwitchSheet) {
            RoleSwitchSheet(
                currentUser = currentUser,
                onSelectRole = { userId ->
                    coroutineScope.launch {
                        if (userId == null) {
                            repository.setCurrentUser(null)
                        } else {
                            repository.switchPersona(userId)
                        }
                    }
                },
                onOpenAuth = {
                    showRoleSwitchSheet = false
                    if (onNavigateToLogin != null) {
                        onNavigateToLogin()
                    } else {
                        showAuthDialog = true
                    }
                },
                onDismiss = { showRoleSwitchSheet = false }
            )
        }

        // Login & Registration Auth Dialog
        if (showAuthDialog) {
            AuthDialog(
                onLogin = { email ->
                    coroutineScope.launch {
                        val loggedIn = repository.loginUser(email)
                        if (loggedIn != null) {
                            snackbarHostState.showSnackbar("Welcome back, ${loggedIn.name}!")
                        } else {
                            snackbarHostState.showSnackbar("No account found for $email. Please register.")
                        }
                    }
                },
                onRegister = { name, email, phone, role, agency, country ->
                    coroutineScope.launch {
                        val registered = repository.registerUser(
                            name = name,
                            email = email,
                            phone = phone,
                            role = role,
                            agencyName = agency,
                            country = country
                        )
                        val roleDisplay = if (registered.isAgent) "Agent Account (Pending Review)" else "Customer Account"
                        snackbarHostState.showSnackbar("Registered $roleDisplay for ${registered.name}!")
                    }
                },
                onDismiss = { showAuthDialog = false }
            )
        }
    }
}
