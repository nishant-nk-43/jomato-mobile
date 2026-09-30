package com.application.jomato.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.application.jomato.Prefs
import com.application.jomato.ui.theme.JomatoTheme

@Composable
fun PrivacyConsentDialog(
    onDismiss: () -> Unit,
    onCustomize: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = JomatoTheme.Background,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = JomatoTheme.Brand,
                    modifier = Modifier.size(44.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your Privacy Choices",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = JomatoTheme.BrandBlack
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "We have updated Jomato with stronger privacy protections:\n\n" +
                            "• Hardware Encryption: Sensitive credentials and saved addresses are now encrypted with AES-256-GCM in Android KeyStore.\n" +
                            "• Background Order Verification: Queries to Zomato for claimed rescue orders are OFF by default.\n" +
                            "• Third-Party Telemetry: App launch pings and order metric sharing are OFF by default.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = JomatoTheme.TextGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        Prefs.setPrivacyOnboardingCompleted(context, true)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JomatoTheme.Brand),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Continue with Private Defaults (All Off)", color = JomatoTheme.Background, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        Prefs.setPrivacyOnboardingCompleted(context, true)
                        onCustomize()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Customize Data Sharing Settings", color = JomatoTheme.BrandBlack)
                }
            }
        }
    }
}
