package com.autoaccounting.feature.sms

import com.autoaccounting.domain.model.TransactionType

object SmsParser {

    private val BANK_KEYWORDS = setOf(
        "支出", "收入", "消费", "转账", "还款",
        "扣款", "入账", "到账", "付款", "收款",
        "交易", "支付", "刷卡", "取现"
    )

    private val BANK_SENDERS = setOf(
        "95588", "95533", "95566", "95555", "95568", "95599", "1069"
    )

    private val AMOUNT_PATTERNS = listOf(
        Regex("""(?:支出|消费|扣款|付款|转账)[：:\s]*(?:人民币)?[¥￥]?\s*(\d+\.?\d*)"""),
        Regex("""[¥￥]\s*(\d+\.?\d*)"""),
        Regex("""(\d+\.?\d*)\s*元"""),
    )

    private val MERCHANT_PATTERNS = listOf(
        Regex("""(?:在|于|商户)\s*(.+?)(?:消费|支出|付款|交易)"""),
        Regex("""(?:商户[：:]\s*)(.+?)(?:\s|$)"""),
        Regex("""(?:付款给|转账给|收款方)\s*(.+?)(?:\s|$)"""),
    )

    fun isBankSms(sender: String, body: String): Boolean {
        val senderMatch = BANK_SENDERS.any { sender.contains(it) }
        val contentMatch = BANK_KEYWORDS.any { body.contains(it) }
        return senderMatch || contentMatch
    }

    fun parse(body: String): ParsedTransaction? {
        val amount = extractAmount(body) ?: return null
        val merchant = extractMerchant(body)
        val type = determineType(body)

        return ParsedTransaction(
            amount = amount,
            merchant = merchant,
            type = type,
            originalMessage = body
        )
    }

    private fun extractAmount(body: String): Double? {
        for (pattern in AMOUNT_PATTERNS) {
            val match = pattern.find(body)
            if (match != null) {
                return match.groupValues[1].toDoubleOrNull()
            }
        }
        return null
    }

    private fun extractMerchant(body: String): String? {
        for (pattern in MERCHANT_PATTERNS) {
            val match = pattern.find(body)
            if (match != null) {
                return match.groupValues[1].trim()
            }
        }
        return null
    }

    private fun determineType(body: String): TransactionType {
        val expenseKeywords = listOf("支出", "消费", "扣款", "付款", "转出")
        val incomeKeywords = listOf("收入", "到账", "入账", "转入", "收款")

        return when {
            expenseKeywords.any { body.contains(it) } -> TransactionType.EXPENSE
            incomeKeywords.any { body.contains(it) } -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }
    }
}

data class ParsedTransaction(
    val amount: Double,
    val merchant: String?,
    val type: TransactionType,
    val originalMessage: String
)
