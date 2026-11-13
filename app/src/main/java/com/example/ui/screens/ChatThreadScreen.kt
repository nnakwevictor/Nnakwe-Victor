package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.User
import com.example.data.repository.HavenRepository
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy
import com.example.ui.theme.StatusSuccess
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatThreadScreen(
    currentUserId: String,
    currentUserName: String,
    recipient: User,
    propertyId: String? = null,
    propertyTitle: String? = null,
    repository: HavenRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages by repository.getMessagesBetween(currentUserId, recipient.id)
        .collectAsState(initial = emptyList())

    var textInput by remember { mutableStateOf("") }

    // Scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Chat Header with Partner Info & Online Status
        Surface(
            color = HavenNavy,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(HavenGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = recipient.name.take(1),
                        fontWeight = FontWeight.Bold,
                        color = HavenNavy,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = recipient.name,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        if (recipient.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = HavenGold,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(StatusSuccess)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${recipient.agencyName ?: "Haven Network"} • Online",
                            color = Color(0xFFCAD5E2),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Optional Property Context Banner
        if (propertyTitle != null) {
            Surface(
                color = Color(0xFFF1F5F9),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Apartment,
                        contentDescription = null,
                        tint = HavenNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Regarding: $propertyTitle",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HavenNavy
                    )
                }
            }
        }

        // Message Thread List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == currentUserId
                MessageBubble(message = msg, isMe = isMe)
            }
        }

        // Quick Suggestions
        val quickSuggestions = listOf(
            "Is the Governor's Consent verified?",
            "Can we schedule an inspection this week?",
            "What are the estate service charges?",
            "Can I make a structured acquisition offer?"
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickSuggestions) { suggestion ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.clickable {
                        textInput = suggestion
                    }
                ) {
                    Text(
                        text = suggestion,
                        fontSize = 11.sp,
                        color = HavenNavy,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Message Input Row
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Message ${recipient.name}...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HavenGold,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = CircleShape,
                    color = if (textInput.isNotBlank()) HavenNavy else Color(0xFFE2E8F0),
                    modifier = Modifier.size(46.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                val msgText = textInput.trim()
                                textInput = ""
                                coroutineScope.launch {
                                    repository.sendMessage(
                                        senderId = currentUserId,
                                        senderName = currentUserName,
                                        recipientId = recipient.id,
                                        recipientName = recipient.name,
                                        text = msgText,
                                        propertyId = propertyId,
                                        propertyTitle = propertyTitle
                                    )
                                }
                            }
                        },
                        enabled = textInput.isNotBlank(),
                        modifier = Modifier.testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (textInput.isNotBlank()) HavenGold else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, isMe: Boolean) {
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 2.dp,
                    bottomEnd = if (isMe) 2.dp else 16.dp
                ),
                color = if (isMe) HavenNavy else Color.White,
                shadowElevation = 1.5.dp,
                border = if (!isMe) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
            ) {
                Text(
                    text = message.message,
                    color = if (isMe) Color.White else HavenNavy,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
            ) {
                Text(
                    text = formattedTime,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
                if (isMe) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Delivered",
                        tint = HavenGold,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
