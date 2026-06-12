package com.autoaccounting.feature.sms

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationListenerServiceImpl : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras

        if (packageName !in SUPPORTED_PACKAGES) return

        val title = extras.getString(Notification.EXTRA_TITLE) ?: return
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()

        val fullText = bigText ?: text

        if (NotificationParser.isPaymentNotification(packageName, title, fullText)) {
            val parsed = NotificationParser.parse(packageName, title, fullText)
            if (parsed != null) {
                scope.launch {
                    // TODO: Save to pending queue for user confirmation
                }
            }
        }
    }

    companion object {
        val SUPPORTED_PACKAGES = setOf(
            "com.eg.android.AlipayGphone",
            "com.tencent.mm",
            "com.icbc",
            "com.chinamworld.bocmbci",
            "com.ccb.start",
            "comcmb.pb"
        )
    }
}
