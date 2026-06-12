package com.autoaccounting.feature.statistics

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.autoaccounting.ui.theme.BalanceBlue
import com.autoaccounting.ui.theme.CardWhite
import com.autoaccounting.ui.theme.EntertainmentYellow
import com.autoaccounting.ui.theme.ExpenseRed
import com.autoaccounting.ui.theme.ExpenseRedLight
import com.autoaccounting.ui.theme.OtherPurple
import com.autoaccounting.ui.theme.PrimaryGreen
import com.autoaccounting.ui.theme.PrimaryGreenLight
import com.autoaccounting.ui.theme.TextPrimary
import com.autoaccounting.ui.theme.TextSecondary

private val categoryColors = listOf(
    PrimaryGreen, ExpenseRed, BalanceBlue, EntertainmentYellow, OtherPurple
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "统计",
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
                SummaryCards(
                    income = uiState.monthlySummary.income,
                    expense = uiState.monthlySummary.expense,
                    balance = uiState.monthlySummary.balance
                )
                Spacer(modifier = Modifier.height(20.dp))
                CategoryRankingHeader()
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (uiState.categorySummary.isEmpty()) {
                item {
                    EmptyCategoryCard()
                }
            } else {
                items(uiState.categorySummary.mapIndexed { index, summary ->
                    Triple(
                        summary.categoryName ?: "未分类",
                        summary.total,
                        categoryColors[index % categoryColors.size]
                    )
                }) { (name, amount, color) ->
                    CategoryRankingItem(
                        name = name,
                        amount = amount,
                        color = color,
                        maxAmount = uiState.monthlySummary.expense
                    )
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
private fun SummaryCards(
    income: Double,
    expense: Double,
    balance: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            title = "收入",
            amount = income,
            color = PrimaryGreen,
            backgroundColor = PrimaryGreenLight,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "支出",
            amount = expense,
            color = ExpenseRed,
            backgroundColor = ExpenseRedLight,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "结余",
            amount = balance,
            color = BalanceBlue,
            backgroundColor = BalanceBlue.copy(alpha = 0.1f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¥ %,.0f".format(amount),
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (title == "支出") Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = if (title == "支出") "↓8.3%" else "↑12.5%",
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun CategoryRankingHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "支出分类",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CategoryRankingItem(
    name: String,
    amount: Double,
    color: Color,
    maxAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "¥ %,.0f".format(amount),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (maxAmount > 0) (amount / maxAmount).toFloat().coerceIn(0f, 1f) else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun EmptyCategoryCard() {
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
            Text("📊", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("暂无数据", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }
    }
}
