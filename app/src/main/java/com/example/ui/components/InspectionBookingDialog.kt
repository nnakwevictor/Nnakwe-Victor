package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Property
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy

@Composable
fun InspectionBookingDialog(
    property: Property,
    onConfirm: (date: String, timeSlot: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf("2026-11-18") }
    var selectedTime by remember { mutableStateOf("10:00 AM - 11:30 AM") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Book Private Inspection",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = HavenNavy
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = property.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HavenNavy
                )
                Text(
                    text = property.address.ifBlank { "${property.neighbourhood}, ${property.city}" },
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date selection
                Text(
                    text = "Preferred Date",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = HavenNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                val dates = listOf("2026-11-18", "2026-11-20", "2026-11-22", "2026-11-25")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    dates.forEach { d ->
                        val isSelected = selectedDate == d
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) HavenNavy else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable { selectedDate = d }
                        ) {
                            Text(
                                text = d.substring(5), // MM-DD
                                color = if (isSelected) HavenGold else Color(0xFF334155),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Time Slot
                Text(
                    text = "Time Window",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = HavenNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                val timeSlots = listOf(
                    "10:00 AM - 11:30 AM",
                    "02:00 PM - 03:30 PM",
                    "04:30 PM - 06:00 PM"
                )
                timeSlots.forEach { slot ->
                    val isSelected = selectedTime == slot
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) HavenNavy else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { selectedTime = slot }
                    ) {
                        Text(
                            text = slot,
                            color = if (isSelected) HavenGold else Color(0xFF334155),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special Requests / Attendees") },
                    placeholder = { Text("E.g., Coming with legal counsel / architect") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inspection_notes_input"),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedDate, selectedTime, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = HavenNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_booking_btn")
            ) {
                Text("Confirm Request", color = HavenGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel", color = HavenNavy)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}
