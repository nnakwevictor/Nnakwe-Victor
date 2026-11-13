package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.Property
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

@Composable
fun MortgageCalculatorDialog(
    property: Property,
    onDismiss: () -> Unit
) {
    val defaultRate = when (property.countryCode.uppercase()) {
        "NG" -> 16f
        "US" -> 6.8f
        "GB" -> 5.5f
        "AE" -> 4.5f
        else -> 10f
    }

    var downPaymentPercent by remember { mutableFloatStateOf(20f) }
    var loanTermYears by remember { mutableIntStateOf(20) }
    var interestRate by remember { mutableFloatStateOf(defaultRate) }

    val totalPrice = property.originalPrice
    val downPaymentAmount = totalPrice * (downPaymentPercent / 100.0)
    val principal = totalPrice - downPaymentAmount

    // Monthly repayment formula: M = P [ i(1 + i)^n ] / [ (1 + i)^n – 1]
    val monthlyInterestRate = (interestRate / 100.0) / 12.0
    val totalMonths = loanTermYears * 12
    val monthlyPayment = if (monthlyInterestRate > 0) {
        val factor = (1 + monthlyInterestRate).pow(totalMonths.toDouble())
        principal * (monthlyInterestRate * factor) / (factor - 1)
    } else {
        principal / totalMonths
    }

    val totalRepayment = monthlyPayment * totalMonths
    val totalInterest = totalRepayment - principal

    fun formatMoney(amount: Double): String {
        val numFormat = NumberFormat.getNumberInstance(Locale.US)
        val formatted = numFormat.format(amount.toLong())
        return when (property.currency.uppercase()) {
            "NGN" -> "₦$formatted"
            "USD" -> "$$formatted"
            "GBP" -> "£$formatted"
            "AED" -> "AED $formatted"
            else -> "${property.currency} $formatted"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Investment & Mortgage Calculator",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = HavenNavy
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = property.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HavenNavy
                )
                Text(
                    text = "Total Valuation: ${property.formattedPrice}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Monthly Estimate Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = HavenNavy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ESTIMATED MONTHLY REPAYMENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HavenGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatMoney(monthlyPayment),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "for $loanTermYears years at ${"%.1f".format(interestRate)}% p.a.",
                            fontSize = 11.sp,
                            color = Color(0xFFCAD5E2)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Down Payment Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Down Payment: ${downPaymentPercent.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HavenNavy)
                    Text(formatMoney(downPaymentAmount), fontSize = 12.sp, color = Color(0xFF475569))
                }
                Slider(
                    value = downPaymentPercent,
                    onValueChange = { downPaymentPercent = it },
                    valueRange = 10f..60f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = HavenGold, activeTrackColor = HavenNavy)
                )

                // Loan Term Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Loan Term: $loanTermYears Years", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HavenNavy)
                    Text("$totalMonths Months", fontSize = 12.sp, color = Color(0xFF475569))
                }
                Slider(
                    value = loanTermYears.toFloat(),
                    onValueChange = { loanTermYears = it.toInt() },
                    valueRange = 5f..30f,
                    steps = 4,
                    colors = SliderDefaults.colors(thumbColor = HavenGold, activeTrackColor = HavenNavy)
                )

                // Interest Rate Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Interest Rate: ${"%.1f".format(interestRate)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HavenNavy)
                    Text("Annual", fontSize = 12.sp, color = Color(0xFF475569))
                }
                Slider(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    valueRange = 3f..25f,
                    colors = SliderDefaults.colors(thumbColor = HavenGold, activeTrackColor = HavenNavy)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Breakdown Summary
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Principal Loan:", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(formatMoney(principal), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = HavenNavy)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Interest Cost:", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(formatMoney(totalInterest), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = HavenNavy)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = HavenNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("close_calc_btn")
            ) {
                Text("Done", color = HavenGold, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}
