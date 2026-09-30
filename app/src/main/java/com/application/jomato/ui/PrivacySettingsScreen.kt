package com.application.jomato.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.application.jomato.Prefs
import com.application.jomato.entity.zomato.ZomatoManager
import com.application.jomato.ui.theme.JomatoTheme
import com.application.jomato.utils.FileLogger

@Composable
fun PrivacySettingsScreen(navController: NavController) {
    val context = LocalContext.current

    var appAnalytics by remember { mutableStateOf(Prefs.isAppAnalyticsEnabled(context)) }
    var orderTracking by remember { mutableStateOf(Prefs.isOrderTrackingConsentGranted(context)) }
    var orderTelemetry by remember { mutableStateOf(Prefs.isOrderTelemetryEnabled(context)) }

    AppScreen(
        title = "Privacy & Data Sharing",
        showBack = true,
        onBack = { navController.navigateUp() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Local Storage Security Card ───────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = JomatoTheme.SecondaryBg),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, JomatoTheme.Divider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = JomatoTheme.Success,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Local Storage Encrypted",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = JomatoTheme.BrandBlack
                        )
                        Text(
                            text = "Sensitive data (OAuth session tokens, user ID, saved addresses, GPS coordinates) is protected using hardware-backed AES-256-GCM encryption in Android KeyStore.",
                            style = MaterialTheme.typography.bodySmall,
                            color = JomatoTheme.TextGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "ACCOUNT QUERIES & CONSENT",
                style = MaterialTheme.typography.labelSmall,
                color = JomatoTheme.TextGray,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ── Background Order Queries Toggle ─────────────────────────
            PrivacyToggleCard(
                title = "Query Account for Claimed Orders",
                description = "When a Food Rescue deal is claimed in your area, Jomato queries Zomato's order summary API with your account to verify if you were the buyer and calculate savings. Keeping this OFF prevents background queries against your Zomato account.",
                checked = orderTracking,
                onCheckedChange = { checked ->
                    orderTracking = checked
                    Prefs.setOrderTrackingConsent(context, checked)
                    if (!checked && orderTelemetry) {
                        orderTelemetry = false
                        Prefs.setOrderTelemetryEnabled(context, false)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "THIRD-PARTY DATA SHARING",
                style = MaterialTheme.typography.labelSmall,
                color = JomatoTheme.TextGray,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ── App Open Telemetry Toggle ──────────────────────────────
            PrivacyToggleCard(
                title = "App Launch Analytics",
                description = "Shares anonymous install ID, app version, and Android OS version to the developer's server (logs.jomato-mobile.workers.dev) on startup for active user count.",
                checked = appAnalytics,
                onCheckedChange = { checked ->
                    appAnalytics = checked
                    Prefs.setAppAnalyticsEnabled(context, checked)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Order Savings Telemetry Toggle ──────────────────────────
            PrivacyToggleCard(
                title = "Share Order Savings Metrics",
                description = "Shares real Zomato Order ID, original cart value, and amount paid to the developer's server (zomato-food-rescue.jomato-mobile.workers.dev) when you claim a rescue order.",
                checked = orderTelemetry,
                enabled = orderTracking,
                onCheckedChange = { checked ->
                    orderTelemetry = checked
                    Prefs.setOrderTelemetryEnabled(context, checked)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Quick Disable All Button ────────────────────────────────
            OutlinedButton(
                onClick = {
                    appAnalytics = false
                    orderTracking = false
                    orderTelemetry = false
                    Prefs.setAppAnalyticsEnabled(context, false)
                    Prefs.setOrderTrackingConsent(context, false)
                    Prefs.setOrderTelemetryEnabled(context, false)
                    Toast.makeText(context, "All tracking & sharing disabled", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, JomatoTheme.Brand),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = JomatoTheme.Brand),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Rounded.VisibilityOff, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Disable All Telemetry & Queries", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Clear Local Logs & Claims Button ─────────────────────────
            OutlinedButton(
                onClick = {
                    FileLogger.clearLogs(context)
                    ZomatoManager.clearClaimedOrders(context)
                    Toast.makeText(context, "Local diagnostic logs & claim history cleared", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, JomatoTheme.Divider),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = JomatoTheme.BrandBlack),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Rounded.Delete, contentDescription = null, tint = JomatoTheme.TextGray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Diagnostic Logs & Cached Orders", color = JomatoTheme.BrandBlack)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PrivacyToggleCard(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = JomatoTheme.Background),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, JomatoTheme.Divider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) JomatoTheme.BrandBlack else JomatoTheme.TextGray,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = enabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = JomatoTheme.Background,
                        checkedTrackColor = JomatoTheme.Brand,
                        uncheckedThumbColor = JomatoTheme.TextGray,
                        uncheckedTrackColor = JomatoTheme.Divider
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = JomatoTheme.TextGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
