package com.autoaccounting.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.autoaccounting.ui.theme.IncomeGreen
import com.autoaccounting.ui.theme.PrimaryGreen
import com.autoaccounting.ui.theme.PrimaryGreenLight
import com.autoaccounting.ui.theme.TextPrimary
import com.autoaccounting.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    onAddTransaction: () -> Unit = {},
    onNavigateToBills: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                GreetingSection(username = uiState.username)
                Spacer(modifier = Modifier.height(20.dp))
                BalanceCard(
                    balance = uiState.monthlySummary.balance,
                    income = uiState.monthlySummary.income,
                    expense = uiState.monthlySummary.expense
                )
                Spacer(modifier = Modifier.height(12.dp))
                IncomeExpenseRow(
                    income = uiState.monthlySummary.income,
                    expense = uiState.monthlySummary.expense
                )
                Spacer(modifier = Modifier.height(20.dp))
                RecentTransactionsHeader(onViewAll = onNavigateToBills)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val allTransactions = uiState.todayTransactions + uiState.groupedByDate.values.flatten()
            val recentTransactions = allTransactions.take(5)

            if (recentTransactions.isEmpty()) {
                item {
                    EmptyTransactionCard()
                }
            } else {
                items(recentTransactions) { transaction ->
                    TransactionListItem(transaction = transaction)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GreetingSection(username: String) {
    Column {
        Text(
            text = "你好，$username 👋",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        Text(
            text = "掌控每一笔收支",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun BalanceCard(
    balance: Double,
    income: Double,
    expense: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "本月结余",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¥ %,.2f".format(balance),
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowUp,
                                contentDescription = null,
                                tint = CardWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("收入", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Text(
                        "¥ %,.2f".format(income),
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(ExpenseRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = CardWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("支出", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Text(
                        "¥ %,.2f".format(expense),
                        style = MaterialTheme.typography.titleMedium,
                        color = ExpenseRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun IncomeExpenseRow(
    income: Double,
    expense: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGreenLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = CardWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("本月收入", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(
                    "¥ %,.2f".format(income),
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ExpenseRedLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ExpenseRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = CardWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("本月支出", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(
                    "¥ %,.2f".format(expense),
                    style = MaterialTheme.typography.titleMedium,
                    color = ExpenseRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RecentTransactionsHeader(onViewAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "最近交易",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "查看全部",
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryGreen,
            modifier = Modifier.clickable { onViewAll() }
        )
    }
}

@Composable
private fun TransactionListItem(transaction: Transaction) {
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
                val dateText = if (transaction.transactionDate.toLocalDate() == LocalDate.now()) {
                    "今天 · ${transaction.merchant ?: ""}"
                } else if (transaction.transactionDate.toLocalDate() == LocalDate.now().minusDays(1)) {
                    "昨天 · ${transaction.merchant ?: ""}"
                } else {
                    "${transaction.transactionDate.format(DateTimeFormatter.ofPattern("M月d日", Locale.CHINESE))} · ${transaction.merchant ?: ""}"
                }
                Text(
                    text = dateText,
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
private fun EmptyTransactionCard() {
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
            Text(
                text = "📋",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "暂无交易记录",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }
    }
}
