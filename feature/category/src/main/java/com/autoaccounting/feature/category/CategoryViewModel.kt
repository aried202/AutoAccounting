package com.autoaccounting.feature.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoaccounting.domain.model.Category
import com.autoaccounting.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun addCategory(name: String, icon: String, color: Long) {
        viewModelScope.launch {
            categoryRepository.insert(
                Category(name = name, icon = icon, color = color)
            )
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.delete(category)
        }
    }
}

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false
)
