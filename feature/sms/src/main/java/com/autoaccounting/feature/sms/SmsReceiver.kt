package com.autoaccounting.feature.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages?.forEach { smsMessage ->
            val sender = smsMessage.displayOriginatingAddress ?: return@forEach
            val body = smsMessage.messageBody ?: return@forEach

            if (SmsParser.isBankSms(sender, body)) {
                val parsed = SmsParser.parse(body)
                if (parsed != null) {
                    // TODO: Save to pending queue for user confirmation
                }
            }
        }
    }
}
