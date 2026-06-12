package com.autoaccounting.domain.usecase

import com.autoaccounting.domain.model.Budget
import com.autoaccounting.domain.repository.BudgetRepository
import javax.inject.Inject

class CreateBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget): Long {
        return budgetRepository.insert(budget)
    }
}
