package com.autoaccounting.common.util

import java.text.DecimalFormat

object CurrencyUtils {
    private val formatter = DecimalFormat("#,##0.00")

    fun format(amount: Double): String = "¥ ${formatter.format(amount)}"
    fun formatPlain(amount: Double): String = formatter.format(amount)
}
