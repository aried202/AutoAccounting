package com.autoaccounting.feature.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("设置") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        context.startActivity(intent)
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("通知监听权限", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Text(
                        "开启后可自动识别支付宝、微信等支付通知",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clickable {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                        context.startActivity(intent)
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("短信权限", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Text(
                        "开启后可自动识别银行短信",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
