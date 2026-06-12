package com.autoaccounting.feature.addtransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.autoaccounting.domain.model.TransactionType
import com.autoaccounting.ui.theme.CardWhite
import com.autoaccounting.ui.theme.ExpenseRed
import com.autoaccounting.ui.theme.PageBackground
import com.autoaccounting.ui.theme.PrimaryGreen
import com.autoaccounting.ui.theme.TextPrimary
import com.autoaccounting.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "记一笔",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "返回",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {}) {
                        Text("")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AmountInputCard(
                amount = uiState.amount,
                onAmountChange = viewModel::updateAmount,
                selectedType = uiState.type,
                selectedCategoryName = uiState.categories.find { it.id == uiState.categoryId }?.name,
                hasError = uiState.amountError
            )

            Spacer(modifier = Modifier.height(16.dp))

            TypeToggle(
                selectedType = uiState.type,
                onTypeSelected = { viewModel.updateType(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "选择分类",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            CategoryGrid(
                categories = uiState.categories,
                selectedCategoryId = uiState.categoryId,
                onCategorySelected = { viewModel.updateCategory(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            val selectedAccountName = uiState.accounts.find { it.id == uiState.accountId }?.name ?: "现金"
            FormField(
                icon = "💰",
                label = "账户",
                value = selectedAccountName
            )

            Spacer(modifier = Modifier.height(12.dp))

            FormField(
                icon = "📅",
                label = "日期",
                value = uiState.date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
            )

            Spacer(modifier = Modifier.height(12.dp))

            FormField(
                icon = "✏️",
                label = "备注",
                value = uiState.note.ifEmpty { "添加备注..." },
                isPlaceholder = uiState.note.isEmpty(),
                onValueChange = viewModel::updateNote,
                isEditable = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveTransaction() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                enabled = uiState.isFormValid
            ) {
                Text(
                    text = "记一笔",
                    style = MaterialTheme.typography.titleMedium,
                    color = CardWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AmountInputCard(
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedType: TransactionType,
    selectedCategoryName: String?,
    hasError: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "输入金额",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            prefix = { Text("¥ ", style = MaterialTheme.typography.headlineLarge, color = TextPrimary) },
            placeholder = { Text("0.00", style = MaterialTheme.typography.headlineLarge, color = Color.LightGray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = hasError,
            textStyle = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (selectedCategoryName != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "🏷️ $selectedCategoryName · ${if (selectedType == TransactionType.EXPENSE) "支出" else "收入"}",
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryGreen,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGreen.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun TypeToggle(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedType == TransactionType.EXPENSE,
            onClick = { onTypeSelected(TransactionType.EXPENSE) },
            label = {
                Text(
                    "支出",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ExpenseRed,
                selectedLabelColor = CardWhite,
                containerColor = CardWhite,
                labelColor = TextSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        FilterChip(
            selected = selectedType == TransactionType.INCOME,
            onClick = { onTypeSelected(TransactionType.INCOME) },
            label = {
                Text(
                    "收入",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PrimaryGreen,
                selectedLabelColor = CardWhite,
                containerColor = CardWhite,
                labelColor = TextSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryGrid(
    categories: List<com.autoaccounting.domain.model.Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.take(8).forEach { category ->
            val isSelected = category.id == selectedCategoryId
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardWhite)
                    .clickable { onCategorySelected(category.id) }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) PrimaryGreen.copy(alpha = 0.1f)
                            else Color(0xFFF5F5F5)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.icon.firstOrNull()?.toString() ?: "📁",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) PrimaryGreen else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(PrimaryGreen)
                    )
                }
            }
        }
    }
}

@Composable
private fun FormField(
    icon: String,
    label: String,
    value: String,
    isPlaceholder: Boolean = false,
    onValueChange: ((String) -> Unit)? = null,
    isEditable: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.width(48.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        if (isEditable) {
            OutlinedTextField(
                value = if (isPlaceholder) "" else value,
                onValueChange = { onValueChange?.invoke(it) },
                placeholder = {
                    if (isPlaceholder) {
                        Text("添加备注...", color = TextSecondary)
                    }
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
