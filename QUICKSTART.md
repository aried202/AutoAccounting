# AutoAccounting - 快速开始指南

## 1. 环境准备

### 安装 Android Studio

1. 访问 https://developer.android.com/studio
2. 下载最新版本 (Hedgehog 或更新)
3. 安装并启动 Android Studio
4. 完成初始设置向导

### 配置开发环境

```bash
# 验证 Java 版本 (Android Studio 自带 JDK 17)
java -version

# 验证 Gradle (项目自带 Gradle Wrapper)
./gradlew --version
```

---

## 2. 创建项目

### 方式一：使用 Android Studio (推荐)

1. File → New → New Project
2. 选择 "Empty Activity"
3. 配置:
   - Name: AutoAccounting
   - Package name: com.autoaccounting.app
   - Save location: D:\AutoAccounting
   - Language: Kotlin
   - Minimum SDK: API 26
   - Build configuration language: Kotlin DSL

### 方式二：命令行创建

```bash
# 使用 Android Studio 的项目模板
# 或手动创建项目结构 (参考 ARCHITECTURE.md)
```

---

## 3. 项目初始化步骤

### Step 1: 创建目录结构

```bash
# 在项目根目录执行
mkdir -p app/src/main/java/com/autoaccounting/app
mkdir -p core/common/src/main/java/com/autoaccounting/common
mkdir -p core/ui/src/main/java/com/autoaccounting/ui
mkdir -p domain/src/main/java/com/autoaccounting/domain
mkdir -p data/src/main/java/com/autoaccounting/data
mkdir -p feature/home/src/main/java/com/autoaccounting/feature/home
mkdir -p feature/addtransaction/src/main/java/com/autoaccounting/feature/addtransaction
mkdir -p feature/category/src/main/java/com/autoaccounting/feature/category
mkdir -p feature/sms/src/main/java/com/autoaccounting/feature/sms
```

### Step 2: 配置 Gradle

**settings.gradle.kts** (项目根目录):
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolution {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AutoAccounting"
include(":app")
include(":core:common")
include(":core:ui")
include(":domain")
include(":data")
include(":feature:home")
include(":feature:addtransaction")
include(":feature:category")
include(":feature:sms")
```

**gradle/libs.versions.toml**:
```toml
[versions]
agp = "8.2.2"
kotlin = "1.9.22"
compose-bom = "2024.02.00"
hilt = "2.50"
room = "2.6.1"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "1.9.22-1.0.17" }
```

### Step 3: 配置 app/build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.autoaccounting.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.autoaccounting.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation(libs.compose.bom)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
}
```

---

## 4. 第一个功能：手动记账

### 4.1 创建实体类

**domain/src/main/java/com/autoaccounting/domain/model/Transaction.kt**:
```kotlin
package com.autoaccounting.domain.model

import java.time.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val note: String? = null,
    val merchant: String? = null,
    val categoryId: Long? = null,
    val accountId: Long,
    val source: TransactionSource = TransactionSource.MANUAL,
    val originalMessage: String? = null,
    val transactionDate: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

enum class TransactionSource {
    MANUAL, SMS, NOTIFICATION
}
```

### 4.2 创建 Room 实体

**data/src/main/java/com/autoaccounting/data/local/entity/TransactionEntity.kt**:
```kotlin
package com.autoaccounting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String,  // INCOME, EXPENSE, TRANSFER
    val note: String? = null,
    val merchant: String? = null,
    val categoryId: Long? = null,
    val accountId: Long,
    val source: String = "MANUAL",
    val originalMessage: String? = null,
    val transactionDate: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 4.3 创建 DAO

**data/src/main/java/com/autoaccounting/data/local/dao/TransactionDao.kt**:
```kotlin
package com.autoaccounting.data.local.dao

import androidx.room.*
import com.autoaccounting.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long
    
    @Update
    suspend fun update(transaction: TransactionEntity)
    
    @Delete
    suspend fun delete(transaction: TransactionEntity)
    
    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?
}
```

### 4.4 创建 ViewModel

**feature/addtransaction/src/main/java/com/autoaccounting/feature/addtransaction/AddTransactionViewModel.kt**:
```kotlin
package com.autoaccounting.feature.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()
    
    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }
    
    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }
    
    fun saveTransaction() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()
        
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = true) }
            return
        }
        
        viewModelScope.launch {
            // TODO: 保存到数据库
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}

data class AddTransactionUiState(
    val amount: String = "",
    val amountError: Boolean = false,
    val note: String = "",
    val isSaved: Boolean = false
)
```

### 4.5 创建 UI

**feature/addtransaction/src/main/java/com/autoaccounting/feature/addtransaction/AddTransactionScreen.kt**:
```kotlin
package com.autoaccounting.feature.addtransaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("添加账单") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::updateAmount,
                label = { Text("金额") },
                prefix = { Text("¥ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = uiState.amountError,
                supportingText = if (uiState.amountError) {
                    { Text("请输入有效金额") }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::updateNote,
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.saveTransaction() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存")
            }
        }
    }
}
```

---

## 5. 运行应用

### 5.1 启动模拟器

1. Android Studio → Tools → Device Manager
2. Create Device → 选择 Pixel 7
3. 下载系统镜像 (Android 14)
4. 启动模拟器

### 5.2 运行应用

1. 点击 Android Studio 工具栏的 ▶️ 按钮
2. 选择目标设备 (模拟器或真机)
3. 等待编译和安装

### 5.3 命令行运行

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 安装到连接的设备
./gradlew installDebug

# 启动应用
adb shell am start -n com.autoaccounting.app/.MainActivity
```

---

## 6. 下一步

完成上述步骤后，你已经:

✅ 搭建了项目基础结构
✅ 配置了依赖 (Hilt, Room, Compose)
✅ 创建了第一个手动记账功能
✅ 可以运行应用

### 推荐继续开发:

1. **完善数据库**: 添加 Category 和 Account 实体
2. **首页列表**: 展示账单列表
3. **分类选择**: 实现分类选择器
4. **短信识别**: 实现自动识别功能

详细实现请参考 **ARCHITECTURE.md** 文档。

---

**遇到问题?**

- 检查 Android Studio 是否为最新版本
- 确保 Gradle Sync 成功
- 查看 Logcat 中的错误信息
- 参考官方文档: https://developer.android.com/jetpack/compose
