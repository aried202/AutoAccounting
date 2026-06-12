package com.autoaccounting.feature.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoaccounting.domain.model.Account
import com.autoaccounting.domain.model.Category
import com.autoaccounting.domain.model.TransactionType
import com.autoaccounting.domain.repository.AccountRepository
import com.autoaccounting.domain.repository.CategoryRepository
import com.autoaccounting.domain.usecase.AddTransactionUseCase
import com.autoaccounting.domain.usecase.ClassifyTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
    private val classifyTransactionUseCase: ClassifyTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadAccounts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            accountRepository.getActiveAccounts().collect { accounts ->
                _uiState.update { state ->
                    state.copy(
                        accounts = accounts,
                        accountId = state.accountId ?: accounts.firstOrNull()?.id
                    )
                }
            }
        }
    }

    fun updateType(type: TransactionType) {
        _uiState.update { it.copy(type = type) }
    }

    fun updateAmount(amount: String) {
        if (amount.isEmpty() || amount.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
            _uiState.update { it.copy(amount = amount, amountError = false) }
        }
    }

    fun updateCategory(categoryId: Long) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }

    fun updateAccount(accountId: Long) {
        _uiState.update { it.copy(accountId = accountId, accountError = false) }
    }

    fun updateDate(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
        if (note.length >= 2) {
            viewModelScope.launch {
                val suggested = classifyTransactionUseCase(note)
                suggested?.let { categoryId ->
                    _uiState.update { it.copy(categoryId = categoryId) }
                }
            }
        }
    }

    fun saveTransaction() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = true) }
            return
        }

        if (state.accountId == null) {
            _uiState.update { it.copy(accountError = true) }
            return
        }

        viewModelScope.launch {
            try {
                addTransactionUseCase(
                    amount = amount,
                    type = state.type,
                    categoryId = state.categoryId,
                    accountId = state.accountId,
                    note = state.note.ifBlank { null },
                    date = state.date.atStartOfDay()
                )
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val amountError: Boolean = false,
    val categories: List<Category> = emptyList(),
    val categoryId: Long? = null,
    val accounts: List<Account> = emptyList(),
    val accountId: Long? = null,
    val accountError: Boolean = false,
    val date: LocalDate = LocalDate.now(),
    val note: String = "",
    val isSaved: Boolean = false,
    val error: String? = null
) {
    val isFormValid: Boolean
        get() = amount.toDoubleOrNull() != null
                && amount.toDouble() > 0
                && accountId != null
}
