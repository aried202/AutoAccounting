# AutoAccounting - 智能记账App

一款基于 Kotlin + Jetpack Compose 的智能记账 Android 应用。

## 功能特性

- 📊 **首页概览** — 本月结余、收支双卡片、最近交易
- ➕ **快速记账** — 金额输入、收支切换、8大分类选择
- 📈 **统计分析** — 收支趋势、分类排行、进度条展示
- 📄 **账单管理** — 按日期分组、搜索筛选、月度概览
- 👤 **个人中心** — 用户名自定义、设置管理
- 💰 **预算管理** — 旅行预算创建与跟踪

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose + Material3
- **架构**: MVVM + Clean Architecture
- **依赖注入**: Hilt
- **数据库**: Room
- **异步**: Kotlin Coroutines + Flow
- **导航**: Navigation Compose

## 项目结构

```
app/                    # 应用模块
core/
  common/              # 公共工具类
  ui/                  # 主题和通用组件
domain/                 # 领域层（Model, Repository接口, UseCase）
data/                   # 数据层（Room数据库, Repository实现）
feature/
  home/                # 首页
  addtransaction/      # 记账
  bills/               # 账单
  statistics/          # 统计
  profile/             # 我的
  category/            # 分类管理
  settings/            # 设置
  sms/                 # 短信识别
```

## 开发环境

- Android Studio Hedgehog+
- Kotlin 1.9.22
- AGP 8.2.2
- compileSdk 34
- minSdk 26

## 许可证

MIT License
