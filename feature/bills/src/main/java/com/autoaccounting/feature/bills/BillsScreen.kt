package com.autoaccounting.feature.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.autoaccounting.domain.model.Transaction
import com.autoaccounting.domain.model.TransactionType
import com.autoaccounting.ui.theme.CardWhite
import com.autoaccounting.ui.theme.ExpenseRed
import com.autoaccounting.ui.theme.ExpenseRedLight
import com.autoaccounting.ui.theme.PageBackground
import com.autoaccounting.ui.theme.PrimaryGreen
import com.autoaccounting.ui.theme.TextPrimary
import com.autoaccounting.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsScreen(
    viewModel: BillsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = PageBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "账单",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "2026年6月 ▼",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                ExpenseOverviewCard(expense = uiState.monthlySummary.expense)
                Spacer(modifier = Modifier.height(16.dp))
                SearchBar()
                Spacer(modifier = Modifier.height(16.dp))
            }

            val groupedTransactions = uiState.transactions.groupBy { it.transactionDate.toLocalDate() }

            if (groupedTransactions.isEmpty()) {
                item {
                    EmptyBillsCard()
                }
            } else {
                groupedTransactions.forEach { (date, transactions) ->
                    item {
                        DateGroupHeader(
                            date = date,
                            transactions = transactions
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(transactions) { transaction ->
                        BillTransactionItem(transaction = transaction)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ExpenseOverviewCard(expense: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "本月支出",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¥ %,.2f".format(expense),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "较上月 ↓ 8.3%",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryGreen
                )
            }
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ExpenseRedLight),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ExpenseRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📊", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Composable
private fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("搜索交易记录...", color = TextSecondary) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "搜索",
                tint = TextSecondary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = CardWhite,
            unfocusedContainerColor = CardWhite
        ),
        singleLine = true
    )
}

@Composable
private fun DateGroupHeader(
    date: LocalDate,
    transactions: List<Transaction>
) {
    val today = LocalDate.now()
    val dateLabel = when (date) {
        today -> "今天 · ${date.format(DateTimeFormatter.ofPattern("M月d日", Locale.CHINESE))}"
        today.minusDays(1) -> "昨天 · ${date.format(DateTimeFormatter.ofPattern("M月d日", Locale.CHINESE))}"
        else -> date.format(DateTimeFormatter.ofPattern("M月d日", Locale.CHINESE))
    }

    val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (totalIncome > 0) {
                Text(
                    text = "收入 ¥ %,.0f".format(totalIncome),
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryGreen
                )
            }
            if (totalExpense > 0) {
                Text(
                    text = "支出 ¥ %,.0f".format(totalExpense),
                    style = MaterialTheme.typography.bodySmall,
                    color = ExpenseRed
                )
            }
        }
    }
}

@Composable
private fun BillTransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val categoryColor = when (transaction.type) {
                TransactionType.EXPENSE -> ExpenseRed
                TransactionType.INCOME -> PrimaryGreen
                TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.note?.firstOrNull()?.toString()
                        ?: transaction.merchant?.firstOrNull()?.toString()
                        ?: "💰",
                    style = MaterialTheme.typography.titleMedium,
                    color = categoryColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.note ?: transaction.merchant ?: "未命名",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${transaction.merchant ?: ""} · ${transaction.transactionDate.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Text(
                text = when (transaction.type) {
                    TransactionType.EXPENSE -> "-¥ %.2f".format(transaction.amount)
                    TransactionType.INCOME -> "+¥ %.2f".format(transaction.amount)
                    TransactionType.TRANSFER -> "¥ %.2f".format(transaction.amount)
                },
                style = MaterialTheme.typography.titleMedium,
                color = when (transaction.type) {
                    TransactionType.EXPENSE -> ExpenseRed
                    TransactionType.INCOME -> PrimaryGreen
                    TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyBillsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📄", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("暂无账单记录", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }
    }
}
