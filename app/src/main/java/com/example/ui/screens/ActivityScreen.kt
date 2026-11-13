package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.enums.EnquiryStatus
import com.example.data.enums.InspectionStatus
import com.example.data.model.Enquiry
import com.example.data.model.Inspection
import com.example.data.model.User
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy
import com.example.ui.theme.StatusSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityScreen(
    inspections: List<Inspection>,
    enquiries: List<Enquiry>,
    currentUser: User?,
    onUpdateInspectionStatus: (id: String, status: String) -> Unit,
    onUpdateEnquiryStatus: (id: String, status: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Activity & Communications",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = HavenNavy,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "Manage scheduled property inspections and advisor communications",
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: Inspections & Enquiries
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = HavenNavy,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = HavenGold,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Inspections (${inspections.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                },
                modifier = Modifier.testTag("tab_inspections")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Enquiries (${enquiries.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                },
                modifier = Modifier.testTag("tab_enquiries")
            )
        }

        if (selectedTab == 0) {
            // Inspections List
            if (inspections.isEmpty()) {
                EmptyActivityState(
                    title = "No Inspections Scheduled",
                    subtitle = "Book private on-site or virtual walk-throughs for any property on the marketplace."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(inspections, key = { it.id }) { insp ->
                        InspectionCard(
                            inspection = insp,
                            currentUser = currentUser,
                            onUpdateStatus = { status -> onUpdateInspectionStatus(insp.id, status) }
                        )
                    }
                }
            }
        } else {
            // Enquiries List
            if (enquiries.isEmpty()) {
                EmptyActivityState(
                    title = "No Inquiries Yet",
                    subtitle = "Questions or interest sent to listing agents will appear here."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(enquiries, key = { it.id }) { enq ->
                        EnquiryCard(
                            enquiry = enq,
                            currentUser = currentUser,
                            onUpdateStatus = { status -> onUpdateEnquiryStatus(enq.id, status) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectionCard(
    inspection: Inspection,
    currentUser: User?,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Property Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inspection.propertyTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = HavenNavy,
                    modifier = Modifier.weight(1f)
                )

                val statusObj = inspection.inspectionStatus
                val badgeBg = when (statusObj) {
                    InspectionStatus.CONFIRMED -> Color(0xFFDCFCE7)
                    InspectionStatus.PENDING -> Color(0xFFFEF3C7)
                    InspectionStatus.COMPLETED -> Color(0xFFE0F2FE)
                    InspectionStatus.CANCELLED -> Color(0xFFFEE2E2)
                }
                val badgeColor = when (statusObj) {
                    InspectionStatus.CONFIRMED -> Color(0xFF166534)
                    InspectionStatus.PENDING -> Color(0xFFB45309)
                    InspectionStatus.COMPLETED -> Color(0xFF0369A1)
                    InspectionStatus.CANCELLED -> Color(0xFF991B1B)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = statusObj.label.uppercase(),
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date & Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = HavenGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${inspection.date}  •  ${inspection.timeSlot}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
            }

            // Location
            if (inspection.propertyAddress.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = inspection.propertyAddress,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Notes / attendee
            if (inspection.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${inspection.notes}",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Actions for Agents or Admins
            val isAgentOrAdmin = currentUser?.isAgent == true || currentUser?.isAdmin == true
            if (isAgentOrAdmin && inspection.inspectionStatus == InspectionStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onUpdateStatus(InspectionStatus.CANCELLED.value) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Decline", color = Color(0xFFDC2626), fontSize = 12.sp)
                    }
                    Button(
                        onClick = { onUpdateStatus(InspectionStatus.CONFIRMED.value) },
                        modifier = Modifier.weight(1.3f),
                        colors = ButtonDefaults.buttonColors(containerColor = HavenNavy),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Confirm Inspection", color = HavenGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else if (inspection.inspectionStatus == InspectionStatus.CONFIRMED && isAgentOrAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onUpdateStatus(InspectionStatus.COMPLETED.value) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Mark Inspection Completed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else if (inspection.inspectionStatus == InspectionStatus.PENDING) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { onUpdateStatus(InspectionStatus.CANCELLED.value) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Request", color = Color(0xFFDC2626), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun EnquiryCard(
    enquiry: Enquiry,
    currentUser: User?,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = enquiry.propertyTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = HavenNavy,
                    modifier = Modifier.weight(1f)
                )

                val statusObj = enquiry.enquiryStatus
                val badgeBg = when (statusObj) {
                    EnquiryStatus.UNREAD -> Color(0xFFFEF3C7)
                    EnquiryStatus.CONTACTED -> Color(0xFFE0F2FE)
                    EnquiryStatus.RESOLVED -> Color(0xFFDCFCE7)
                }
                val badgeColor = when (statusObj) {
                    EnquiryStatus.UNREAD -> Color(0xFFB45309)
                    EnquiryStatus.CONTACTED -> Color(0xFF0369A1)
                    EnquiryStatus.RESOLVED -> Color(0xFF166534)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = statusObj.label.uppercase(),
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "From: ${enquiry.customerName} (${enquiry.customerEmail})",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            )

            if (enquiry.customerPhone.isNotBlank()) {
                Text(
                    text = "Phone: ${enquiry.customerPhone}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF8FAFC),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${enquiry.message}\"",
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(10.dp)
                )
            }

            // Quick Status Actions for Agent or Admin
            val isAgentOrAdmin = currentUser?.isAgent == true || currentUser?.isAdmin == true
            if (isAgentOrAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (enquiry.enquiryStatus != EnquiryStatus.CONTACTED) {
                        OutlinedButton(
                            onClick = { onUpdateStatus(EnquiryStatus.CONTACTED.value) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Mark Contacted", fontSize = 11.sp, color = HavenNavy)
                        }
                    }
                    if (enquiry.enquiryStatus != EnquiryStatus.RESOLVED) {
                        Button(
                            onClick = { onUpdateStatus(EnquiryStatus.RESOLVED.value) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Resolve", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyActivityState(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.EventNote,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = HavenNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
