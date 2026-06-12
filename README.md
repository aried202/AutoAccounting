# 💰 AutoAccounting - 智能记账

一款基于 **Kotlin + Jetpack Compose** 打造的现代简约风格记账 Android 应用，帮助你轻松掌控每一笔收支。

![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.02-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## ✨ 功能特性

### 🏠 首页
- 问候语 + 动态用户名显示
- 本月结余大数字卡片
- 收入/支出双卡片对比
- 最近交易列表，支持点击查看全部

### ➕ 快速记账
- 大字金额输入，支持小数
- 收入/支出一键切换
- 8大支出分类网格选择（餐饮、交通、购物、娱乐、居家、医疗、教育、其他）
- 日期选择 + 备注输入
- 智能分类推荐（输入备注自动匹配分类）

### 📊 统计分析
- 收入/支出/结余三卡片汇总（含趋势百分比）
- 分类支出排行进度条

### 📄 账单管理
- 月度支出概览
- 按日期分组的交易列表
- 搜索功能（预留）

### 👤 个人中心
- 用户名自定义（点击编辑）
- 主题设置、通知设置、数据导出
- 数据备份、隐私设置

### 💰 预算管理
- 旅行预算创建（名称、金额、日期范围）
- 预算进度条追踪
- 首页预算卡片展示

---

## 📸 界面预览

| 首页 | 记账 | 统计 | 账单 | 我的 |
|------|------|------|------|------|
| 问候语 + 结余 | 金额输入 | 收支汇总 | 支出概览 | 用户名编辑 |
| 收入支出双卡 | 分类网格 | 分类排行 | 分组列表 | 设置管理 |
| 最近交易 | 日期备注 | — | — | — |

---

## 🏗️ 技术架构

```
┌─────────────────────────────────────────┐
│              Presentation               │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │  Home   │ │  Bills  │ │ Profile │   │
│  │ Screen  │ │ Screen  │ │ Screen  │   │
│  └────┬────┘ └────┬────┘ └────┬────┘   │
│       │           │           │         │
│  ┌────┴────┐ ┌────┴────┐ ┌────┴────┐   │
│  │ViewModel│ │ViewModel│ │ViewModel│   │
│  └────┬────┘ └────┬────┘ └────┬────┘   │
├───────┼───────────┼───────────┼─────────┤
│       │    Domain │           │         │
│  ┌────┴───────────┴───────────┴────┐    │
│  │          UseCases               │    │
│  │  GetTransactions / AddTransaction│   │
│  │  GetMonthlySummary / etc.       │    │
│  └─────────────┬───────────────────┘    │
├────────────────┼────────────────────────┤
│                │   Data                 │
│  ┌─────────────┴───────────────────┐    │
│  │     Repository + Room DB        │    │
│  │  Transaction / Category / Budget│    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

### 技术栈

| 技术 | 用途 |
|------|------|
| **Kotlin** | 开发语言 |
| **Jetpack Compose** | 声明式 UI |
| **Material3** | 设计系统 |
| **MVVM** | 架构模式 |
| **Clean Architecture** | 代码分层 |
| **Hilt** | 依赖注入 |
| **Room** | 本地数据库 |
| **Coroutines + Flow** | 异步处理 |
| **Navigation Compose** | 页面导航 |
| **SharedPreferences** | 用户设置存储 |

---

## 📁 项目结构

```
AutoAccounting/
├── app/                          # 应用入口模块
│   ├── AppNavigation.kt          # 导航路由（5 Tab）
│   ├── MainActivity.kt           # Activity
│   └── di/AppModule.kt           # Hilt 依赖注入
│
├── core/
│   ├── common/                   # 公共模块
│   │   └── prefs/UserPrefs.kt    # 用户设置存储
│   └── ui/                       # UI 主题模块
│       └── theme/                # 颜色、字体、主题
│
├── domain/                       # 领域层
│   ├── model/                    # 数据模型
│   ├── repository/               # Repository 接口
│   └── usecase/                  # 业务用例
│
├── data/                         # 数据层
│   ├── local/
│   │   ├── dao/                  # Room DAO
│   │   ├── db/AppDatabase.kt     # 数据库配置
│   │   └── entity/               # 数据库实体
│   └── repository/               # Repository 实现
│
└── feature/                      # 功能模块
    ├── home/                     # 首页
    ├── addtransaction/           # 记账
    ├── bills/                    # 账单
    ├── statistics/               # 统计
    ├── profile/                  # 我的
    ├── category/                 # 分类管理
    ├── settings/                 # 设置
    └── sms/                      # 短信识别（预留）
```

---

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34

### 克隆项目

```bash
git clone https://github.com/aried202/AutoAccounting.git
cd AutoAccounting
```

### 编译运行

1. 用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 连接设备或启动模拟器
4. 点击 Run 运行

---

## 📦 主要依赖

```kotlin
// Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

// Hilt
implementation("com.google.dagger:hilt-android:2.50")
ksp("com.google.dagger:hilt-compiler:2.50")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

---

## 📋 数据库表结构

### transactions（交易记录）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| amount | Double | 金额 |
| type | String | 类型（EXPENSE/INCOME/TRANSFER） |
| categoryId | Long? | 分类ID |
| accountId | Long | 账户ID |
| note | String? | 备注 |
| transactionDate | Long | 交易时间 |

### categories（分类）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 分类名 |
| icon | String | 图标 |
| color | Long | 颜色 |
| isSystem | Boolean | 是否系统分类 |

### budgets（预算）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 预算名称 |
| amount | Double | 预算金额 |
| startDate | Long | 开始日期 |
| endDate | Long | 结束日期 |

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

---

## 👨‍💻 作者

**aried202** - [GitHub](https://github.com/aried202)

---

> 如果这个项目对你有帮助，请给个 ⭐ Star 支持一下！
