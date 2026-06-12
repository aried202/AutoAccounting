# AutoAccounting - Android 自动记账应用架构设计

## 1. 项目概述

### 1.1 项目信息
- **项目名称**: AutoAccounting (智能记账)
- **包名**: `com.autoaccounting.app`
- **最低 SDK**: API 26 (Android 8.0)
- **目标 SDK**: API 34 (Android 14)
- **技术栈**: Kotlin + Jetpack Compose + Hilt + Room

### 1.2 核心功能矩阵

| 功能模块 | 优先级 | Phase |
|---------|--------|-------|
| 手动记账 | P0 | 1 |
| 分类管理 | P0 | 1 |
| 账单列表展示 | P0 | 1 |
| 短信自动识别 | P1 | 2 |
| 通知自动识别 | P1 | 2 |
| 智能分类推荐 | P1 | 3 |
| 数据统计图表 | P2 | 3 |
| 数据导出 | P3 | 4 |

---

## 2. 项目结构设计

### 2.1 模块划分

```
AutoAccounting/
├── app/                          # 应用入口模块
│   ├── src/main/
│   │   ├── java/com/autoaccounting/app/
│   │   │   ├── AutoAccountingApp.kt       # Application类
│   │   │   ├── MainActivity.kt            # 主Activity
│   │   │   └── di/                        # 全局DI配置
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── core/                         # 核心基础模块
│   ├── common/                   # 通用工具类
│   │   └── src/main/java/.../common/
│   │       ├── util/DateUtils.kt
│   │       ├── util/CurrencyUtils.kt
│   │       └── extension/StringExt.kt
│   ├── ui/                       # 通用UI组件
│   │   └── src/main/java/.../ui/
│   │       ├── component/BottomNavBar.kt
│   │       ├── component/LoadingIndicator.kt
│   │       ├── theme/Theme.kt
│   │       ├── theme/Color.kt
│   │       └── theme/Type.kt
│   └── designsystem/             # 设计系统
│       └── src/main/java/.../designsystem/
│           ├── token/AppColor.kt
│           └── token/AppDimen.kt
│
├── domain/                       # 领域层
│   └── src/main/java/.../domain/
│       ├── model/                # 领域模型
│       │   ├── Transaction.kt
│       │   ├── Category.kt
│       │   ├── Account.kt
│       │   └── TransactionSummary.kt
│       ├── repository/           # 仓库接口
│       │   ├── TransactionRepository.kt
│       │   ├── CategoryRepository.kt
│       │   └── AccountRepository.kt
│       └── usecase/              # 用例
│           ├── AddTransactionUseCase.kt
│           ├── GetTransactionsUseCase.kt
│           ├── ClassifyTransactionUseCase.kt
│           └── ParseSmsUseCase.kt
│
├── data/                         # 数据层
│   └── src/main/java/.../data/
│       ├── local/                # 本地数据源
│       │   ├── db/AppDatabase.kt
│       │   ├── dao/
│       │   │   ├── TransactionDao.kt
│       │   │   ├── CategoryDao.kt
│       │   │   └── AccountDao.kt
│       │   ├── entity/           # Room实体
│       │   │   ├── TransactionEntity.kt
│       │   │   ├── CategoryEntity.kt
│       │   │   └── AccountEntity.kt
│       │   └── converter/        # 类型转换器
│       │       └── Converters.kt
│       ├── repository/           # 仓库实现
│       │   ├── TransactionRepositoryImpl.kt
│       │   ├── CategoryRepositoryImpl.kt
│       │   └── AccountRepositoryImpl.kt
│       └── mapper/               # 数据映射
│           ├── TransactionMapper.kt
│           └── CategoryMapper.kt
│
├── feature/                      # 功能模块
│   ├── home/                     # 首页
│   │   └── src/main/java/.../home/
│   │       ├── HomeScreen.kt
│   │       └── HomeViewModel.kt
│   ├── addtransaction/           # 添加账单
│   │   └── src/main/java/.../addtransaction/
│   │       ├── AddTransactionScreen.kt
│   │       └── AddTransactionViewModel.kt
│   ├── category/                 # 分类管理
│   │   └── src/main/java/.../category/
│   │       ├── CategoryScreen.kt
│   │       └── CategoryViewModel.kt
│   ├── statistics/               # 统计
│   │   └── src/main/java/.../statistics/
│   │       ├── StatisticsScreen.kt
│   │       └── StatisticsViewModel.kt
│   ├── settings/                 # 设置
│   │   └── src/main/java/.../settings/
│   │       ├── SettingsScreen.kt
│   │       └── SettingsViewModel.kt
│   └── sms/                      # 短信识别
│       └── src/main/java/.../sms/
│           ├── SmsParser.kt
│           ├── NotificationParser.kt
│           └── SmsReceiver.kt
│
├── buildSrc/                     # 构建配置
│   └── src/main/kotlin/
│       ├── Dependencies.kt       # 依赖版本管理
│       └── AndroidConfig.kt      # Android配置
│
├── gradle/
│   └── libs.versions.toml        # Version Catalog
└── settings.gradle.kts
```

### 2.2 模块依赖关系

```
app ──────────────────────────────────────────────────────────────►
  │                                                               │
  ├──► feature/home ──► domain ◄── feature/addtransaction         │
  │         │              ▲              │                        │
  │         ▼              │              ▼                        │
  │    core/ui         domain ◄── feature/category                │
  │         │              ▲              │                        │
  │         ▼              │              ▼                        │
  │    core/common     data ◄── feature/sms                       │
  │                        │                                      │
  │                        ▼                                      │
  │                    Room DB                                     │
  │                                                               │
  └──► feature/settings ──► domain ──► data ──► Room DB          │
```

---

## 3. 数据库设计

### 3.1 ER 图

```
┌─────────────────────┐      ┌─────────────────────┐
│     Category        │      │      Account         │
├─────────────────────┤      ├─────────────────────┤
│ id (PK, Long)       │      │ id (PK, Long)       │
│ name (String)       │◄─────│ name (String)       │
│ icon (String)       │      │ type (Enum)         │
│ color (Long)        │      │ balance (Double)    │
│ parentId (Long?)    │      │ currency (String)   │
│ sortOrder (Int)     │      │ isActive (Boolean)  │
│ isSystem (Boolean)  │      │ createdAt (Long)    │
│ createdAt (Long)    │      └─────────┬───────────┘
└─────────┬───────────┘                │
          │                            │
          │  1:N                       │  1:N
          ▼                            ▼
┌─────────────────────────────────────────────────┐
│                 Transaction                      │
├─────────────────────────────────────────────────┤
│ id (PK, Long)                                   │
│ amount (Double)                                 │
│ type (Enum: INCOME/EXPENSE/TRANSFER)            │
│ note (String?)                                  │
│ merchant (String?)                              │
│ categoryId (FK → Category)                      │
│ accountId (FK → Account)                        │
│ toAccountId (FK → Account?, 仅转账用)           │
│ source (Enum: MANUAL/SMS/NOTIFICATION)          │
│ originalMessage (String?)                       │
│ transactionDate (Long)                          │
│ createdAt (Long)                                │
│ updatedAt (Long)                                │
│ isSynced (Boolean)                              │
└─────────────────────────────────────────────────┘
```

### 3.2 Room 实体定义

```kotlin
// ===== TransactionEntity.kt =====
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("categoryId"),
        Index("accountId"),
        Index("transactionDate"),
        Index("type")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val amount: Double,
    
    @ColumnInfo(name = "type")
    val type: TransactionType,  // INCOME, EXPENSE, TRANSFER
    
    val note: String? = null,
    
    val merchant: String? = null,
    
    val categoryId: Long? = null,
    
    val accountId: Long,
    
    @ColumnInfo(name = "toAccountId")
    val toAccountId: Long? = null,  // 仅转账类型使用
    
    @ColumnInfo(name = "source")
    val source: TransactionSource = TransactionSource.MANUAL,
    
    @ColumnInfo(name = "originalMessage")
    val originalMessage: String? = null,
    
    @ColumnInfo(name = "transactionDate")
    val transactionDate: Long,
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val updatedAt: Long = System.currentTimeMillis(),
    
    val isSynced: Boolean = false
)

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

enum class TransactionSource {
    MANUAL, SMS, NOTIFICATION
}
```

```kotlin
// ===== CategoryEntity.kt =====
@Entity(
    tableName = "categories",
    indices = [Index("parentId")]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    val icon: String,  // Material Icon name
    
    val color: Long,   // ARGB color value
    
    @ColumnInfo(name = "parentId")
    val parentId: Long? = null,
    
    @ColumnInfo(name = "sortOrder")
    val sortOrder: Int = 0,
    
    @ColumnInfo(name = "isSystem")
    val isSystem: Boolean = false,  // 系统预设不可删除
    
    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis()
)
```

```kotlin
// ===== AccountEntity.kt =====
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    @ColumnInfo(name = "type")
    val type: AccountType,  // CASH, BANK_CARD, CREDIT_CARD, ALIPAY, WECHAT, OTHER
    
    val balance: Double = 0.0,
    
    val currency: String = "CNY",
    
    @ColumnInfo(name = "isActive")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis()
)

enum class AccountType {
    CASH, BANK_CARD, CREDIT_CARD, ALIPAY, WECHAT, OTHER
}
```

### 3.3 DAO 设计

```kotlin
// ===== TransactionDao.kt =====
@Dao
interface TransactionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>): List<Long>
    
    @Update
    suspend fun update(transaction: TransactionEntity)
    
    @Delete
    suspend fun delete(transaction: TransactionEntity)
    
    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE transactionDate BETWEEN :startDate AND :endDate 
        ORDER BY transactionDate DESC
    """)
    fun getTransactionsByDateRange(
        startDate: Long, 
        endDate: Long
    ): Flow<List<TransactionEntity>>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE categoryId = :categoryId 
        ORDER BY transactionDate DESC
    """)
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE accountId = :accountId 
        ORDER BY transactionDate DESC
    """)
    fun getTransactionsByAccount(accountId: Long): Flow<List<TransactionEntity>>
    
    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE type = :type AND transactionDate BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalByTypeAndDateRange(
        type: TransactionType, 
        startDate: Long, 
        endDate: Long
    ): Double?
    
    @Query("""
        SELECT categoryId, SUM(amount) as total 
        FROM transactions 
        WHERE type = 'EXPENSE' AND transactionDate BETWEEN :startDate AND :endDate
        GROUP BY categoryId
    """)
    suspend fun getExpenseSummaryByCategory(
        startDate: Long, 
        endDate: Long
    ): List<CategorySummary>
    
    @Query("""
        SELECT * FROM transactions 
        WHERE source != 'MANUAL' AND originalMessage LIKE '%' || :keyword || '%'
        LIMIT 1
    """)
    suspend fun findDuplicateByMessage(keyword: String): TransactionEntity?
}

data class CategorySummary(
    val categoryId: Long?,
    val total: Double
)
```

```kotlin
// ===== CategoryDao.kt =====
@Dao
interface CategoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)
    
    @Update
    suspend fun update(category: CategoryEntity)
    
    @Delete
    suspend fun delete(category: CategoryEntity)
    
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories WHERE parentId IS NULL ORDER BY sortOrder ASC")
    fun getRootCategories(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories WHERE parentId = :parentId ORDER BY sortOrder ASC")
    fun getSubCategories(parentId: Long): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?
    
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :keyword || '%'")
    suspend fun searchByName(keyword: String): List<CategoryEntity>
    
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}
```

```kotlin
// ===== AccountDao.kt =====
@Dao
interface AccountDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long
    
    @Update
    suspend fun update(account: AccountEntity)
    
    @Delete
    suspend fun delete(account: AccountEntity)
    
    @Query("SELECT * FROM accounts WHERE isActive = 1 ORDER BY createdAt ASC")
    fun getActiveAccounts(): Flow<List<AccountEntity>>
    
    @Query("SELECT * FROM accounts ORDER BY createdAt ASC")
    fun getAllAccounts(): Flow<List<AccountEntity>>
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?
    
    @Query("UPDATE accounts SET balance = balance + :amount WHERE id = :accountId")
    suspend fun updateBalance(accountId: Long, amount: Double)
}
```

### 3.4 数据库配置

```kotlin
// ===== AppDatabase.kt =====
@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    
    companion object {
        const val DATABASE_NAME = "auto_accounting.db"
    }
}
```

```kotlin
// ===== Converters.kt =====
class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name
    
    @TypeConverter
    fun toTransactionType(value: String): TransactionType = 
        TransactionType.valueOf(value)
    
    @TypeConverter
    fun fromTransactionSource(value: TransactionSource): String = value.name
    
    @TypeConverter
    fun toTransactionSource(value: String): TransactionSource = 
        TransactionSource.valueOf(value)
    
    @TypeConverter
    fun fromAccountType(value: AccountType): String = value.name
    
    @TypeConverter
    fun toAccountType(value: String): AccountType = 
        AccountType.valueOf(value)
}
```

---

## 4. 核心功能模块设计

### 4.1 短信/通知自动识别模块

#### 4.1.1 Android 权限要求

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.READ_SMS" />
<uses-permission android:name="android.permission.RECEIVE_SMS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

<!-- 通知监听权限 (需要用户手动授权) -->
<!-- 通过 Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS 引导用户开启 -->
```

#### 4.1.2 短信监听服务

```kotlin
// ===== SmsReceiver.kt =====
class SmsReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages?.forEach { smsMessage ->
            val sender = smsMessage.displayOriginatingAddress ?: return@forEach
            val body = smsMessage.messageBody ?: return@forEach
            
            // 判断是否为银行/支付短信
            if (SmsParser.isBankSms(sender, body)) {
                val parsed = SmsParser.parse(body)
                if (parsed != null) {
                    // 通过WorkManager异步处理
                    val workRequest = OneTimeWorkRequestBuilder<SmsProcessWorker>()
                        .setInputData(workDataOf(
                            "sender" to sender,
                            "amount" to parsed.amount,
                            "merchant" to parsed.merchant,
                            "type" to parsed.type.name,
                            "originalMessage" to body
                        ))
                        .build()
                    
                    WorkManager.getInstance(context)
                        .enqueueUniqueWork(
                            "sms_process_${System.currentTimeMillis()}",
                            ExistingWorkPolicy.KEEP,
                            workRequest
                        )
                }
            }
        }
    }
}
```

#### 4.1.3 通知监听服务

```kotlin
// ===== NotificationListenerServiceImpl.kt =====
class NotificationListenerServiceImpl : NotificationListenerService() {
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras
        
        // 只处理支付宝、微信、银行APP
        if (packageName !in SUPPORTED_PACKAGES) return
        
        val title = extras.getString(Notification.EXTRA_TITLE) ?: return
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        
        val fullText = bigText ?: text
        
        // 判断是否为支付/账单通知
        if (NotificationParser.isPaymentNotification(packageName, title, fullText)) {
            val parsed = NotificationParser.parse(packageName, title, fullText)
            if (parsed != null) {
                scope.launch {
                    processNotification(parsed)
                }
            }
        }
    }
    
    private suspend fun processNotification(parsed: ParsedTransaction) {
        // 存入待确认队列，用户可确认或忽略
        // 避免重复记录
    }
    
    companion object {
        val SUPPORTED_PACKAGES = setOf(
            "com.eg.android.AlipayGphone",     // 支付宝
            "com.tencent.mm",                   // 微信
            "com.icbc",                         // 工商银行
            "com.chinamworld.bocmbci",          // 中国银行
            "com.ccb.start",                    // 建设银行
            "comcmb.pb",                        // 招商银行
            // ... 其他银行APP
        )
    }
}
```

#### 4.1.4 消息解析规则

```kotlin
// ===== SmsParser.kt =====
object SmsParser {
    
    // 银行短信关键词
    private val BANK_KEYWORDS = setOf(
        "支出", "收入", "消费", "转账", "还款",
        "扣款", "入账", "到账", "付款", "收款",
        "交易", "支付", "刷卡", "取现"
    )
    
    // 银行发送号码关键词
    private val BANK_SENDERS = setOf(
        "95588",  // 工商银行
        "95533",  // 建设银行
        "95566",  // 中国银行
        "95555",  // 招商银行
        "95568",  // 民生银行
        "95599",  // 农业银行
        "1069",   // 短信服务商前缀
    )
    
    // 金额匹配正则
    private val AMOUNT_PATTERNS = listOf(
        Regex("""(?:支出|消费|扣款|付款|转账)[：:\s]*(?:人民币)?[¥￥]?\s*(\d+\.?\d*)"""),
        Regex("""[¥￥]\s*(\d+\.?\d*)"""),
        Regex("""(\d+\.?\d*)\s*元"""),
        Regex("""(?:余额|可用额度)[：:\s]*(?:人民币)?[¥￥]?\s*(\d+\.?\d*)"""),
    )
    
    // 商户名称匹配
    private val MERCHANT_PATTERNS = listOf(
        Regex("""(?:在|于|商户)\s*(.+?)(?:消费|支出|付款|交易)"""),
        Regex("""(?:商户[：:]\s*)(.+?)(?:\s|$)"""),
        Regex("""(?:付款给|转账给|收款方)\s*(.+?)(?:\s|$)"""),
    )
    
    fun isBankSms(sender: String, body: String): Boolean {
        val senderMatch = BANK_SENDERS.any { sender.contains(it) }
        val contentMatch = BANK_KEYWORDS.any { body.contains(it) }
        return senderMatch || contentMatch
    }
    
    fun parse(body: String): ParsedTransaction? {
        val amount = extractAmount(body) ?: return null
        val merchant = extractMerchant(body)
        val type = determineType(body)
        
        return ParsedTransaction(
            amount = amount,
            merchant = merchant,
            type = type,
            originalMessage = body
        )
    }
    
    private fun extractAmount(body: String): Double? {
        for (pattern in AMOUNT_PATTERNS) {
            val match = pattern.find(body)
            if (match != null) {
                return match.groupValues[1].toDoubleOrNull()
            }
        }
        return null
    }
    
    private fun extractMerchant(body: String): String? {
        for (pattern in MERCHANT_PATTERNS) {
            val match = pattern.find(body)
            if (match != null) {
                return match.groupValues[1].trim()
            }
        }
        return null
    }
    
    private fun determineType(body: String): TransactionType {
        val expenseKeywords = listOf("支出", "消费", "扣款", "付款", "转出")
        val incomeKeywords = listOf("收入", "到账", "入账", "转入", "收款")
        
        return when {
            expenseKeywords.any { body.contains(it) } -> TransactionType.EXPENSE
            incomeKeywords.any { body.contains(it) } -> TransactionType.INCOME
            else -> TransactionType.EXPENSE  // 默认为支出
        }
    }
}

data class ParsedTransaction(
    val amount: Double,
    val merchant: String?,
    val type: TransactionType,
    val originalMessage: String
)
```

```kotlin
// ===== NotificationParser.kt =====
object NotificationParser {
    
    private val PAYMENT_KEYWORDS = setOf(
        "支付成功", "付款成功", "消费成功", "转账成功",
        "收款成功", "到账", "红包", "退款"
    )
    
    fun isPaymentNotification(packageName: String, title: String, text: String): Boolean {
        return PAYMENT_KEYWORDS.any { text.contains(it) }
    }
    
    fun parse(packageName: String, title: String, text: String): ParsedTransaction? {
        return when (packageName) {
            "com.eg.android.AlipayGphone" -> parseAlipay(text)
            "com.tencent.mm" -> parseWechat(text)
            else -> parseGenericBank(text)
        }
    }
    
    private fun parseAlipay(text: String): ParsedTransaction? {
        // 支付宝通知格式示例：
        // "成功付款12.50元给星巴克"
        // "你已成功转账500.00元给张三"
        val amount = Regex("""(\d+\.?\d*)\s*元""").find(text)
            ?.groupValues?.get(1)?.toDoubleOrNull() ?: return null
        
        val merchant = Regex("""(?:给|向)\s*(.+?)(?:\s|$)""").find(text)
            ?.groupValues?.get(1)?.trim()
        
        return ParsedTransaction(
            amount = amount,
            merchant = merchant,
            type = if (text.contains("退款")) TransactionType.INCOME else TransactionType.EXPENSE,
            originalMessage = text
        )
    }
    
    private fun parseWechat(text: String): ParsedTransaction? {
        // 微信支付通知格式示例：
        // "微信支付收款100.00元" (商家收款)
        // "微信支付凭证-支出12.50元" (用户付款)
        val amount = Regex("""(\d+\.?\d*)\s*元""").find(text)
            ?.groupValues?.get(1)?.toDoubleOrNull() ?: return null
        
        val isIncome = text.contains("收款") || text.contains("收入")
        
        return ParsedTransaction(
            amount = amount,
            merchant = null,
            type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
            originalMessage = text
        )
    }
    
    private fun parseGenericBank(text: String): ParsedTransaction? {
        // 通用银行通知解析，复用SmsParser逻辑
        return SmsParser.parse(text)
    }
}
```

### 4.2 手动记账模块

#### 4.2.1 UI 界面设计

```kotlin
// ===== AddTransactionScreen.kt =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加账单") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveTransaction() },
                        enabled = uiState.isFormValid
                    ) {
                        Text("保存")
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
                .padding(16.dp)
        ) {
            // 交易类型选择
            TransactionTypeSelector(
                selectedType = uiState.type,
                onTypeSelected = viewModel::updateType
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 金额输入
            AmountInput(
                amount = uiState.amount,
                onAmountChanged = viewModel::updateAmount,
                isError = uiState.amountError
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 分类选择
            CategorySelector(
                categories = uiState.categories,
                selectedCategoryId = uiState.categoryId,
                onCategorySelected = viewModel::updateCategory
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 账户选择
            AccountSelector(
                accounts = uiState.accounts,
                selectedAccountId = uiState.accountId,
                onAccountSelected = viewModel::updateAccount
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 日期选择
            DatePicker(
                selectedDate = uiState.date,
                onDateSelected = viewModel::updateDate
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 备注输入
            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::updateNote,
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        }
    }
}

@Composable
private fun TransactionTypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TransactionType.entries.forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        when (type) {
                            TransactionType.EXPENSE -> "支出"
                            TransactionType.INCOME -> "收入"
                            TransactionType.TRANSFER -> "转账"
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun AmountInput(
    amount: String,
    onAmountChanged: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = amount,
        onValueChange = { newValue ->
            // 只允许数字和小数点，最多两位小数
            if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                onAmountChanged(newValue)
            }
        },
        label = { Text("金额") },
        prefix = { Text("¥ ") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        isError = isError,
        supportingText = if (isError) {
            { Text("请输入有效金额") }
        } else null,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}
```

#### 4.2.2 ViewModel

```kotlin
// ===== AddTransactionViewModel.kt =====
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
                _uiState.update { it.copy(accounts = accounts) }
            }
        }
    }
    
    fun updateType(type: TransactionType) {
        _uiState.update { it.copy(type = type) }
    }
    
    fun updateAmount(amount: String) {
        _uiState.update { 
            it.copy(
                amount = amount,
                amountError = false
            ) 
        }
    }
    
    fun updateCategory(categoryId: Long) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }
    
    fun updateAccount(accountId: Long) {
        _uiState.update { it.copy(accountId = accountId) }
    }
    
    fun updateDate(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }
    
    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }
    
    // 智能分类推荐
    fun suggestCategory(note: String) {
        viewModelScope.launch {
            val suggested = classifyTransactionUseCase(note)
            suggested?.let { categoryId ->
                _uiState.update { it.copy(categoryId = categoryId) }
            }
        }
    }
    
    fun saveTransaction() {
        val state = _uiState.value
        
        // 表单验证
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = true) }
            return
        }
        
        if (state.accountId == nullL) {
            _uiState.update { it.copy(accountError = true) }
            return
        }
        
        viewModelScope.launch {
            try {
                addTransactionUseCase(
                    AddTransactionParams(
                        amount = amount,
                        type = state.type,
                        categoryId = state.categoryId,
                        accountId = state.accountId,
                        note = state.note,
                        date = state.date
                    )
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
```

### 4.3 智能分类模块

#### 4.3.1 分类规则引擎

```kotlin
// ===== ClassifyTransactionUseCase.kt =====
class ClassifyTransactionUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    // 分类关键词映射
    private val categoryKeywords = mapOf(
        "餐饮" to listOf(
            "美团", "饿了么", "肯德基", "麦当劳", "星巴克", "瑞幸",
            "奶茶", "咖啡", "火锅", "烧烤", "餐厅", "饭店", "食堂",
            "外卖", "餐饮", "食品", "超市买菜", "菜市场"
        ),
        "交通" to listOf(
            "滴滴", "高德打车", "地铁", "公交", "出租", "加油",
            "停车", "高速", "ETC", "铁路", "12306", "航空",
            "机票", "火车票", "汽车票"
        ),
        "购物" to listOf(
            "淘宝", "天猫", "京东", "拼多多", "唯品会",
            "商场", "超市", "便利店", "百货", "专卖店"
        ),
        "娱乐" to listOf(
            "电影", "KTV", "游戏", "Steam", "爱奇艺",
            "腾讯视频", "优酷", "B站", "抖音", "快手"
        ),
        "居住" to listOf(
            "房租", "物业", "水费", "电费", "燃气", "宽带",
            "维修", "装修", "家具", "家电"
        ),
        "医疗" to listOf(
            "医院", "药店", "诊所", "体检", "牙科", "眼科",
            "药房", "挂号", "门诊"
        ),
        "教育" to listOf(
            "学费", "培训", "课程", "书店", "文具",
            "考试", "教材", "网课"
        ),
        "通讯" to listOf(
            "移动", "联通", "电信", "话费", "流量",
            "充值", "宽带"
        ),
        "转账" to listOf(
            "转账", "还款", "借款", "红包"
        ),
        "工资" to listOf(
            "工资", "薪资", "奖金", "绩效", "报销"
        )
    )
    
    suspend operator fun invoke(note: String): Long? {
        // 1. 基于关键词匹配
        val matchedCategory = matchByKeywords(note)
        if (matchedCategory != null) return matchedCategory
        
        // 2. 基于历史记录匹配
        val historicalMatch = matchByHistory(note)
        if (historicalMatch != null) return historicalMatch
        
        return null  // 无法自动分类，让用户手动选择
    }
    
    private suspend fun matchByKeywords(note: String): Long? {
        val lowerNote = note.lowercase()
        
        for ((categoryName, keywords) in categoryKeywords) {
            if (keywords.any { lowerNote.contains(it.lowercase()) }) {
                val category = categoryRepository.getByName(categoryName)
                return category?.id
            }
        }
        return null
    }
    
    private suspend fun matchByHistory(note: String): Long? {
        // 查找历史记录中类似备注的分类
        // 使用简单的字符串相似度匹配
        val recentTransactions = categoryRepository.getRecentTransactionCategories(100)
        
        for (transaction in recentTransactions) {
            if (calculateSimilarity(note, transaction.note) > 0.6) {
                return transaction.categoryId
            }
        }
        return null
    }
    
    private fun calculateSimilarity(s1: String, s2: String): Double {
        if (s1.isEmpty() || s2.isEmpty()) return 0.0
        
        val chars1 = s1.toCharArray()
        val chars2 = s2.toCharArray()
        
        var matches = 0
        for (char in chars1) {
            if (chars2.contains(char)) matches++
        }
        
        return matches.toDouble() / maxOf(chars1.size, chars2.size)
    }
}
```

---

## 5. UI 设计

### 5.1 导航结构

```kotlin
// ===== AppNavigation.kt =====
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddTransaction : Screen("add_transaction")
    data object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
    }
    data object Category : Screen("category")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
    data object SmsInbox : Screen("sms_inbox")  // 待确认的自动识别账单
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onAddTransaction = {
                        navController.navigate(Screen.AddTransaction.route)
                    },
                    onTransactionClick = { id ->
                        navController.navigate(Screen.EditTransaction.createRoute(id))
                    }
                )
            }
            
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.EditTransaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) {
                EditTransactionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Category.route) {
                CategoryScreen()
            }
            
            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
```

### 5.2 首页设计

```kotlin
// ===== HomeScreen.kt =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddTransaction: () -> Unit,
    onTransactionClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("智能记账") },
                actions = {
                    // 通知权限检查
                    if (!uiState.hasNotificationPermission) {
                        IconButton(onClick = { viewModel.requestNotificationPermission() }) {
                            Icon(Icons.Outlined.Notifications, "通知权限")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransaction) {
                Icon(Icons.Default.Add, "添加账单")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 本月概览卡片
            item {
                MonthlySummaryCard(
                    income = uiState.monthlyIncome,
                    expense = uiState.monthlyExpense,
                    balance = uiState.monthlyBalance
                )
            }
            
            // 待确认的自动识别账单
            if (uiState.pendingTransactions.isNotEmpty()) {
                item {
                    PendingTransactionsBanner(
                        count = uiState.pendingTransactions.size,
                        onClick = { /* 导航到待确认页面 */ }
                    )
                }
            }
            
            // 今日账单
            item {
                Text(
                    "今日账单",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            items(uiState.todayTransactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
            
            // 历史账单按日期分组
            uiState.groupedByDate.forEach { (date, transactions) ->
                item {
                    Text(
                        date,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onTransactionClick(transaction.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(
    income: Double,
    expense: Double,
    balance: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "本月概览",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "收入",
                    amount = income,
                    color = Color(0xFF4CAF50)
                )
                SummaryItem(
                    label = "支出",
                    amount = expense,
                    color = Color(0xFFF44336)
                )
                SummaryItem(
                    label = "结余",
                    amount = balance,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    amount: Double,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "¥ %.2f".format(amount),
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
    }
}
```

---

## 6. 技术实现要点

### 6.1 依赖配置 (libs.versions.toml)

```toml
[versions]
kotlin = "1.9.22"
compose-bom = "2024.02.00"
hilt = "2.50"
room = "2.6.1"
navigation-compose = "2.7.7"
lifecycle = "2.7.0"
coroutines = "1.7.3"

[libraries]
# Compose
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-material-icons = { group = "androidx.compose.material", name = "material-icons-extended" }

# Lifecycle
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }

# Navigation
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.1.0" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

# Coroutines
coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }
coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

# WorkManager (用于后台短信处理)
work-runtime = { group = "androidx.work", name = "work-runtime-ktx", version = "2.9.0" }

# DataStore (用于偏好设置)
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version = "1.0.0" }

[plugins]
android-application = { id = "com.android.application", version = "8.2.2" }
android-library = { id = "com.android.library", version = "8.2.2" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "1.9.22-1.0.17" }
```

### 6.2 Hilt 依赖注入

```kotlin
// ===== DatabaseModule.kt =====
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .addMigrations()
        .build()
    }
    
    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()
    
    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    
    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
}

// ===== RepositoryModule.kt =====
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository
    
    @Binds
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository
    
    @Binds
    abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository
}
```

### 6.3 预填数据

```kotlin
// ===== PrepopulateData.kt =====
object PrepopulateData {
    
    val defaultCategories = listOf(
        // 支出分类
        CategoryEntity(name = "餐饮", icon = "restaurant", color = 0xFFFF5722, sortOrder = 1),
        CategoryEntity(name = "交通", icon = "directions_car", color = 0xFF2196F3, sortOrder = 2),
        CategoryEntity(name = "购物", icon = "shopping_bag", color = 0xFFE91E63, sortOrder = 3),
        CategoryEntity(name = "娱乐", icon = "sports_esports", color = 0xFF9C27B0, sortOrder = 4),
        CategoryEntity(name = "居住", icon = "home", color = 0xFF795548, sortOrder = 5),
        CategoryEntity(name = "医疗", icon = "local_hospital", color = 0xFFF44336, sortOrder = 6),
        CategoryEntity(name = "教育", icon = "school", color = 0xFF4CAF50, sortOrder = 7),
        CategoryEntity(name = "通讯", icon = "phone", color = 0xFF00BCD4, sortOrder = 8),
        CategoryEntity(name = "其他支出", icon = "more_horiz", color = 0xFF607D8B, sortOrder = 9),
        
        // 收入分类
        CategoryEntity(name = "工资", icon = "payments", color = 0xFF4CAF50, sortOrder = 10, isSystem = true),
        CategoryEntity(name = "奖金", icon = "emoji_events", color = 0xFFFFC107, sortOrder = 11, isSystem = true),
        CategoryEntity(name = "投资收益", icon = "trending_up", color = 0xFF8BC34A, sortOrder = 12, isSystem = true),
        CategoryEntity(name = "其他收入", icon = "more_horiz", color = 0xFF607D8B, sortOrder = 13, isSystem = true),
    )
    
    val defaultAccounts = listOf(
        AccountEntity(name = "现金", type = AccountType.CASH, balance = 0.0),
        AccountEntity(name = "支付宝", type = AccountType.ALIPAY, balance = 0.0),
        AccountEntity(name = "微信", type = AccountType.WECHAT, balance = 0.0),
        AccountEntity(name = "银行卡", type = AccountType.BANK_CARD, balance = 0.0),
    )
}
```

---

## 7. 分阶段实施计划

### Phase 1: 项目搭建 + 基础UI + 手动记账功能 (2-3周)

#### Week 1: 项目初始化
- [ ] 使用 Android Studio 创建项目
- [ ] 配置 Gradle 依赖 (Version Catalog)
- [ ] 搭建模块结构 (app, core, domain, data, feature)
- [ ] 配置 Hilt 依赖注入
- [ ] 配置 Room 数据库
- [ ] 创建基础实体类和 DAO

#### Week 2: 基础 UI
- [ ] 实现主题系统 (Theme, Color, Typography)
- [ ] 实现底部导航栏
- [ ] 实现首页账单列表
- [ ] 实现添加账单页面
- [ ] 实现分类选择器
- [ ] 实现账户选择器

#### Week 3: 手动记账功能
- [ ] 实现表单验证逻辑
- [ ] 实现账单保存功能
- [ ] 实现账单编辑/删除
- [ ] 预填默认分类和账户
- [ ] 基本功能测试

#### Phase 1 交付物:
- ✅ 可运行的 Android 应用
- ✅ 手动添加/编辑/删除账单
- ✅ 基础分类和账户管理
- ✅ 首页账单列表展示

---

### Phase 2: 短信/通知自动识别功能 (2-3周)

#### Week 4: 短信监听
- [ ] 实现 SmsReceiver 广播接收器
- [ ] 配置 AndroidManifest 权限
- [ ] 实现 SmsParser 解析逻辑
- [ ] 处理重复账单检测
- [ ] WorkManager 异步处理

#### Week 5: 通知监听
- [ ] 实现 NotificationListenerService
- [ ] 引导用户开启通知监听权限
- [ ] 实现各APP通知解析 (支付宝/微信/银行)
- [ ] 实现通知解析器适配器模式

#### Week 6: 待确认功能
- [ ] 实现待确认账单列表
- [ ] 实现确认/忽略/编辑操作
- [ ] 实现账单去重逻辑
- [ ] 整合短信和通知来源

#### Phase 2 交付物:
- ✅ 自动识别银行短信
- ✅ 自动识别支付宝/微信通知
- ✅ 待确认账单管理
- ✅ 重复账单检测

---

### Phase 3: 智能分类 + 数据统计 (2周)

#### Week 7: 智能分类
- [ ] 实现关键词匹配引擎
- [ ] 实现历史记录匹配
- [ ] 优化分类推荐准确度
- [ ] 实现分类置信度评分

#### Week 8: 数据统计
- [ ] 实现月度/年度统计
- [ ] 实现分类饼图
- [ ] 实现趋势折线图
- [ ] 实现收支对比图表
- [ ] 使用 MPAndroidChart 或 Compose 图表库

#### Phase 3 交付物:
- ✅ AI 自动分类推荐
- ✅ 收支统计图表
- ✅ 分类占比分析
- ✅ 趋势分析

---

### Phase 4: 优化和测试 (1-2周)

#### Week 9: 优化
- [ ] 性能优化 (数据库查询、UI渲染)
- [ ] 内存优化
- [ ] 电池优化 (后台服务)
- [ ] 数据备份/恢复功能

#### Week 10: 测试
- [ ] 单元测试 (ViewModel, UseCase, Repository)
- [ ] UI 测试 (Compose Testing)
- [ ] 集成测试
- [ ] 多设备兼容性测试

#### Phase 4 交付物:
- ✅ 性能优化完成
- ✅ 测试覆盖率达到 70%+
- ✅ 多设备兼容
- ✅ 可发布版本

---

## 8. 关键技术决策

### 8.1 为什么选择这些技术?

| 技术选择 | 理由 |
|---------|------|
| Jetpack Compose | 声明式UI，代码量少，Google官方推荐 |
| Hilt | 依赖注入标准方案，与Compose集成好 |
| Room | 官方ORM，类型安全，支持Flow |
| Navigation Compose | 官方导航方案，支持类型安全路由 |
| StateFlow | 响应式状态管理，与Compose完美配合 |
| WorkManager | 后台任务调度，保证任务执行 |

### 8.2 架构原则

1. **单向数据流**: UI → Event → ViewModel → State → UI
2. **关注点分离**: UI/Domain/Data 三层清晰分离
3. **依赖倒置**: Domain层不依赖具体实现
4. **响应式编程**: 使用 Flow 处理异步数据

### 8.3 错误处理策略

```kotlin
// 统一错误处理
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

// ViewModel 中使用
viewModelScope.launch {
    _uiState.update { it.copy(loading = true) }
    
    addTransactionUseCase(params)
        .onSuccess { 
            _uiState.update { it.copy(isSaved = true, loading = false) }
        }
        .onFailure { e ->
            _uiState.update { it.copy(error = e.message, loading = false) }
        }
}
```

---

## 9. 开发环境配置

### 9.1 安装步骤

1. **安装 Android Studio**
   - 下载: https://developer.android.com/studio
   - 版本: Hedgehog (2023.1.1) 或更新

2. **安装 JDK**
   - Android Studio 自带 JDK 17
   - 无需额外安装

3. **配置 SDK**
   - SDK Platforms: Android 14 (API 34)
   - Build Tools: 34.0.0
   - Android Emulator: 用于测试

4. **克隆项目**
   ```bash
   cd D:\
   git clone <repository-url> AutoAccounting
   ```

5. **打开项目**
   - Android Studio → Open → 选择 D:\AutoAccounting

6. **同步 Gradle**
   - 等待 Gradle Sync 完成
   - 首次同步可能需要下载依赖

### 9.2 运行配置

```bash
# 命令行构建
./gradlew assembleDebug

# 运行测试
./gradlew test

# 代码检查
./gradlew lint
```

---

## 10. 安全与隐私

### 10.1 数据安全
- 所有数据存储在本地数据库
- 不上传任何用户数据到服务器
- 短信内容仅用于解析，解析后可选择删除

### 10.2 权限管理
- 最小权限原则
- 敏感权限需要用户明确授权
- 提供权限说明和使用场景

### 10.3 用户控制
- 用户可随时关闭自动识别功能
- 用户可删除任何自动识别的记录
- 提供数据导出/删除功能

---

## 附录: 快速参考

### 常用命令

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug

# 运行单元测试
./gradlew testDebugUnitTest

# 清理项目
./gradlew clean
```

### 项目结构速查

```
app/          → 应用入口
core/         → 基础工具和UI组件
domain/       → 领域模型和业务逻辑
data/         → 数据库和数据源
feature/      → 各功能模块
```

### 联系方式

- 项目负责人: [待填写]
- 技术支持: [待填写]
- 文档维护: [待填写]

---

**文档版本**: v1.0
**最后更新**: 2026-06-11
