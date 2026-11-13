package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.enums.UserRole
import com.example.data.model.User
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSwitchSheet(
    currentUser: User?,
    onSelectRole: (String?) -> Unit,
    onOpenAuth: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Switch User Persona",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = HavenNavy
            )
            Text(
                text = "Experience PROJECT X — GLOBAL PROPERTY GROUP from each authenticated role or as a public visitor.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // 1. Visitor Option
            PersonaItem(
                title = "Public Visitor (Signed Out)",
                subtitle = "Browse published listings, search & filter publicly without account.",
                icon = Icons.Default.Visibility,
                iconBg = Color(0xFF94A3B8),
                isSelected = currentUser == null,
                onClick = {
                    onSelectRole(null)
                    onDismiss()
                },
                testTag = "persona_visitor"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Customer Option
            PersonaItem(
                title = "Customer: Chidi Okonkwo",
                subtitle = "Save favourites, book private inspections, submit property enquiries.",
                icon = Icons.Default.Person,
                iconBg = Color(0xFF0284C7),
                isSelected = currentUser?.role == UserRole.CUSTOMER.value,
                onClick = {
                    onSelectRole("user_customer_1")
                    onDismiss()
                },
                testTag = "persona_customer"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Agent Option
            PersonaItem(
                title = "Licensed Agent: Amina Bello",
                subtitle = "PROJECT X Prime Realty. Manage listings, review inspection requests & enquiries.",
                icon = Icons.Default.BusinessCenter,
                iconBg = HavenGold,
                isSelected = currentUser?.role == UserRole.AGENT.value,
                onClick = {
                    onSelectRole("agent_amina_1")
                    onDismiss()
                },
                testTag = "persona_agent"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Property Owner & Landlord Option
            PersonaItem(
                title = "Property Owner: Tariq Al-Mansoor",
                subtitle = "Al-Mansoor International Assets. Multi-unit portfolio, rental yields & tenancies.",
                icon = Icons.Default.HomeWork,
                iconBg = Color(0xFF0D9488),
                isSelected = currentUser?.id == "owner_tariq_1" || currentUser?.isOwner == true,
                onClick = {
                    onSelectRole("owner_tariq_1")
                    onDismiss()
                },
                testTag = "persona_owner"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Admin Option
            PersonaItem(
                title = "Platform Director: Victor Nnakwe",
                subtitle = "PROJECT X Executive Board. Listing approval queue, verify agents, cloud sync.",
                icon = Icons.Default.AdminPanelSettings,
                iconBg = Color(0xFFDC2626),
                isSelected = currentUser?.role == UserRole.ADMIN.value,
                onClick = {
                    onSelectRole("admin_victor_1")
                    onDismiss()
                },
                testTag = "persona_admin"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Or Login/Register as New Customer/Agent
            androidx.compose.material3.OutlinedButton(
                onClick = {
                    onDismiss()
                    onOpenAuth()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("switch_sheet_auth_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = HavenNavy,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log In or Register New Account",
                    color = HavenNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PersonaItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFFF1F5F9) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) HavenNavy else Color(0xFFE2E8F0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = HavenNavy
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(HavenNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = HavenGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
