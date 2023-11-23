package andpact.project.wid.util

import andpact.project.wid.model.WiD
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

fun getTotalDuration(wiDList: List<WiD>): Duration {
    return wiDList.map { it.duration }.reduceOrNull(Duration::plus) ?: Duration.ZERO
}

fun getTotalDurationPercentage(wiDList: List<WiD>): Int {
    val totalMinutes = 24 * 60 // 1440분 (24시간)
    val totalDuration = (wiDList.map { it.duration }.reduceOrNull(Duration::plus) ?: Duration.ZERO).toMinutes().toInt()

    return (totalDuration * 100) / totalMinutes
}

fun getTotalDurationMap(wiDList: List<WiD>): Map<String, Duration> {
    val result = mutableMapOf<String, Duration>()

    for (wiD in wiDList) {
        val title = wiD.title
        val duration = wiD.duration

        // 제목 별로 소요 시간을 누적
        val totalDuration = result[title]
        if (totalDuration != null) {
            result[title] = totalDuration.plus(duration)
        } else {
            result[title] = duration
        }
    }

    // 소요 시간을 기준으로 내림차순으로 정렬
    val sortedResult = result.entries.sortedByDescending { it.value }

    // 정렬된 결과를 새로운 Map으로 반환
    return sortedResult.associate { it.toPair() }
}

//fun getWeeklyAllTitleDurationMap(date: LocalDate, wiDList: List<WiD>): Map<String, Duration> {
//    val result = mutableMapOf<String, Duration>()
//
//    val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
//    val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
//
//    var currentDate = startOfWeek
//
//    while (currentDate <= endOfWeek) {
//        val dailyTotalDurations = getTotalDurationMap(currentDate, wiDList)
//
//        // 각 날짜별 소요 시간을 누적
//        for ((title, duration) in dailyTotalDurations) {
//            val totalDuration = result[title]
//            if (totalDuration != null) {
//                result[title] = totalDuration.plus(duration)
//            } else {
//                result[title] = duration
//            }
//        }
//
//        currentDate = currentDate.plusDays(1)
//    }
//
//    // 소요 시간을 기준으로 내림차순으로 정렬
//    val sortedResult = result.entries.sortedByDescending { it.value }
//
//    // 정렬된 결과를 새로운 Map으로 반환
//    return sortedResult.associate { it.toPair() }
//}
//
//fun getMonthlyAllTitleDurationMap(date: LocalDate, wiDList: List<WiD>): Map<String, Duration> {
//    val result = mutableMapOf<String, Duration>()
//
//    // 해당 달과 일치하는 WiD만 필터링
//    val filteredWiDList = wiDList.filter {
//        val wiDYearMonth = YearMonth.from(it.date)
//        val targetYearMonth = YearMonth.from(date)
//        wiDYearMonth == targetYearMonth
//    }
//
//    for (wiD in filteredWiDList) {
//        val title = wiD.title
//        val duration = wiD.duration
//
//        // 제목 별로 소요 시간을 누적
//        val totalDuration = result[title]
//        if (totalDuration != null) {
//            result[title] = totalDuration.plus(duration)
//        } else {
//            result[title] = duration
//        }
//    }
//
//    // 결과를 소요 시간 내림차순으로 정렬
//    val sortedResult = result.entries.sortedByDescending { it.value }
//
//    // 정렬된 결과를 새로운 맵으로 변환
//    val sortedMap = sortedResult.associate { it.toPair() }
//
//    return sortedMap
//}
//
//fun getWeeklyMaxTitleDuration(date: LocalDate, wiDList: List<WiD>, title: String): Duration {
//    val result = mutableMapOf<LocalDate, Duration>()
//
//    val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
//    val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
//
//    var currentDate = startOfWeek
//
//    while (currentDate <= endOfWeek) {
//        val weeklyDuration = wiDList
//            .filter { it.date == currentDate && it.title == title }
//            .map { it.duration }
//            .reduceOrNull(Duration::plus) ?: Duration.ZERO
//
//        result[currentDate] = weeklyDuration
//
//        currentDate = currentDate.plusDays(1)
//    }
//
//    return result.values.maxOrNull() ?: Duration.ZERO
//}
//
//fun getMonthlyMaxTitleDuration(date: LocalDate, wiDList: List<WiD>, title: String): Duration {
//    val result = mutableMapOf<LocalDate, Duration>()
//
//    val startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth())
//    val endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth())
//
//    var currentDate = startOfMonth
//
//    while (currentDate <= endOfMonth) {
//        val monthlyDuration = wiDList
//            .filter { it.date == currentDate && it.title == title }
//            .map { it.duration }
//            .reduceOrNull(Duration::plus) ?: Duration.ZERO
//
//        result[currentDate] = monthlyDuration
//
//        currentDate = currentDate.plusDays(1)
//    }
//
//    return result.values.maxOrNull() ?: Duration.ZERO
//}
//
//fun getWeeklyAverageTitleDuration(date: LocalDate, wiDList: List<WiD>, title: String): Duration {
//    val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
//    val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
//
//    val dailyTotalDurations = mutableMapOf<LocalDate, Duration>()
//
//    var currentDate = startOfWeek
//
//    while (currentDate <= endOfWeek) {
//        val dailyWiDs = wiDList.filter { it.date == currentDate && it.title == title }
//
//        if (dailyWiDs.isNotEmpty()) {
//            val dailyTotalDuration = dailyWiDs.map { it.duration }.reduce(Duration::plus)
//            dailyTotalDurations[currentDate] = dailyTotalDuration
//        }
//
//        currentDate = currentDate.plusDays(1)
//    }
//
//    val totalDuration = dailyTotalDurations.values.reduceOrNull(Duration::plus) ?: Duration.ZERO
//    val dayCount = dailyTotalDurations.size
//
//    // 평균 소요 시간 계산
//    return if (dayCount > 0) totalDuration.dividedBy(dayCount.toLong()) else Duration.ZERO
//}
//
//fun getMonthlyAverageTitleDuration(date: LocalDate, wiDList: List<WiD>, title: String): Duration {
//    val startOfMonth = date.withDayOfMonth(1)
//    val endOfMonth = date.withDayOfMonth(date.month.length(date.isLeapYear))
//
//    val dailyTotalDurations = mutableMapOf<LocalDate, Duration>()
//
//    var currentDate = startOfMonth
//
//    while (currentDate <= endOfMonth) {
//        val dailyWiDs = wiDList.filter { it.date == currentDate && it.title == title }
//
//        if (dailyWiDs.isNotEmpty()) {
//            val dailyTotalDuration = dailyWiDs.map { it.duration }.reduce(Duration::plus)
//            dailyTotalDurations[currentDate] = dailyTotalDuration
//        }
//
//        currentDate = currentDate.plusDays(1)
//    }
//
//    val totalDuration = dailyTotalDurations.values.reduceOrNull(Duration::plus) ?: Duration.ZERO
//    val dayCount = dailyTotalDurations.size
//
//    // 평균 소요 시간 계산
//    return if (dayCount > 0) totalDuration.dividedBy(dayCount.toLong()) else Duration.ZERO
//}
