package com.autoaccounting.feature.sms

import com.autoaccounting.domain.model.TransactionType

object NotificationParser {

    private val PAYMENT_KEYWORDS = setOf(
        "支付成功", "付款成功", "消费成功", "转账成功",
        "收款成功", "到账", "红包", "退款"
    )

    fun isPaymentNotification(packageName: String, title: String, text: String): Boolean {
        return PAYMENT_KEYWORDS.any { text.contains(it) }
    }

    fun parse(packageName: String, title: String, text: String): ParsedTransaction? {
        return when (packageName) {
            "com.eg.android.AlipayGphone" -> parseAlipay(text)
            "com.tencent.mm" -> parseWechat(text)
            else -> parseGeneric(text)
        }
    }

    private fun parseAlipay(text: String): ParsedTransaction? {
        val amount = Regex("""(\d+\.?\d*)\s*元""").find(text)
            ?.groupValues?.get(1)?.toDoubleOrNull() ?: return null

        val merchant = Regex("""(?:给|向)\s*(.+?)(?:\s|$)""").find(text)
            ?.groupValues?.get(1)?.trim()

        return ParsedTransaction(
            amount = amount,
            merchant = merchant,
            type = if (text.contains("退款")) TransactionType.INCOME else TransactionType.EXPENSE,
            originalMessage = text
        )
    }

    private fun parseWechat(text: String): ParsedTransaction? {
        val amount = Regex("""(\d+\.?\d*)\s*元""").find(text)
            ?.groupValues?.get(1)?.toDoubleOrNull() ?: return null

        val isIncome = text.contains("收款") || text.contains("收入")

        return ParsedTransaction(
            amount = amount,
            merchant = null,
            type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
            originalMessage = text
        )
    }

    private fun parseGeneric(text: String): ParsedTransaction? {
        return SmsParser.parse(text)
    }
}
