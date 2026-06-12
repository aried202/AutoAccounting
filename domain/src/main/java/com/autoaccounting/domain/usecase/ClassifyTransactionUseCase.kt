package com.autoaccounting.domain.usecase

import com.autoaccounting.domain.model.Category
import com.autoaccounting.domain.repository.CategoryRepository
import javax.inject.Inject

class ClassifyTransactionUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
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
        "居家" to listOf(
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
        "其他" to listOf(
            "转账", "还款", "借款", "红包", "工资", "薪资",
            "奖金", "报销", "消费", "购买", "其他"
        )
    )

    suspend operator fun invoke(note: String): Long? {
        val lowerNote = note.lowercase()
        for ((categoryName, keywords) in categoryKeywords) {
            if (keywords.any { lowerNote.contains(it.lowercase()) }) {
                val category = categoryRepository.getByName(categoryName)
                return category?.id
            }
        }
        return null
    }
}
