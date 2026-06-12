package com.autoaccounting.domain.usecase

import com.autoaccounting.domain.model.Budget
import com.autoaccounting.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(): Flow<List<Budget>> {
        return budgetRepository.getActiveBudgets()
    }
}
