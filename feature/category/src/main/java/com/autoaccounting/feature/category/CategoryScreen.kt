package com.autoaccounting.feature.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.autoaccounting.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Category?>(null) }
    var newCategoryName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("分类管理") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加分类")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("支出分类", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.categories.forEach { category ->
                    CategoryChip(
                        name = category.name,
                        color = Color(category.color),
                        isSystem = category.isSystem,
                        onLongClick = {
                            if (!category.isSystem) {
                                showDeleteDialog = category
                            }
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                newCategoryName = ""
            },
            title = { Text("添加分类") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("分类名称") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            viewModel.addCategory(
                                name = newCategoryName,
                                icon = "label",
                                color = 0xFF6B7280
                            )
                            newCategoryName = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("添加")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        newCategoryName = ""
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }

    showDeleteDialog?.let { category ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("删除分类") },
            text = { Text("确定要删除分类\"${category.name}\"吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(category)
                        showDeleteDialog = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryChip(
    name: String,
    color: Color,
    @Suppress("UNUSED_PARAMETER") isSystem: Boolean,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier.combinedClickable(
            onClick = { },
            onLongClick = onLongClick
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    name.first().toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
