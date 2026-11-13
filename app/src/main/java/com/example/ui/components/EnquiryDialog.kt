package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.User
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy

@Composable
fun EnquiryDialog(
    property: Property,
    currentUser: User?,
    onSend: (name: String, email: String, phone: String, message: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var message by remember {
        mutableStateOf("Hello, I am interested in '${property.title}'. Please share more details on purchase terms and availability.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Contact Listing Agent",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = HavenNavy
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Agent: ${property.agentName.ifBlank { "Haven Prime Advisor" }}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = HavenNavy
                )
                Text(
                    text = "Listing: ${property.title}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Full Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("enquiry_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("enquiry_email_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (with Country Code)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("enquiry_phone_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Your Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("enquiry_message_input"),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && message.isNotBlank()) {
                        onSend(name, email, phone, message)
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank() && message.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = HavenNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_enquiry_btn")
            ) {
                Text("Submit Enquiry", color = HavenGold, fontWeight = FontWeight.Bold)
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
