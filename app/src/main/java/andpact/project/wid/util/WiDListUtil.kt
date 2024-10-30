package andpact.project.wid.util

import andpact.project.wid.model.WiD
import android.util.Log
import java.time.*
import java.time.temporal.ChronoUnit

//fun getEmptyWiDListFromWiDList(date: LocalDate, currentTime: LocalTime, wiDList: List<WiD>): List<WiD> {
//    Log.d("WiDListUtil", "getEmptyWiDListFromWiDList executed")
//
//    if (wiDList.isEmpty()) {
//        return emptyList()
//    }
//
//    val emptyWiDList = mutableListOf<WiD>()
//
//    var emptyWiDStart = LocalTime.MIN
//
//    // 데이터 베이스에서 가져온 WiD는 0나노 세컨드를 가짐.
//    for (currentWiD in wiDList) {
//        val emptyWiDFinish = currentWiD.start
//
//        if (emptyWiDStart.equals(emptyWiDFinish)) {
//            emptyWiDStart = currentWiD.finish
//            continue
//        }
//
//        val emptyWiD = WiD(
//            id = 0,
//            date = date,
//            title = "",
//            start = emptyWiDStart,
//            finish = emptyWiDFinish,
//            duration = Duration.between(emptyWiDStart, emptyWiDFinish)
//        )
//        emptyWiDList.add(emptyWiD)
//
//        emptyWiDStart = currentWiD.finish
//    }
//
//    // 빈 WiD가 오늘 날짜의 현재 시간을 넘어가지 않도록함.
//    val today = LocalDate.now()
//    val endOfDay = if (date == today) {
//        currentTime
//    } else {
//        LocalTime.MAX
//    }
//
//    // equals()에 의해서 나노 세컨드 값까지 비교가되므로, 나노세컨드 단위를 버리고 비교함.
//    return if (emptyWiDStart.truncatedTo(ChronoUnit.SECONDS).equals(endOfDay.truncatedTo(ChronoUnit.SECONDS))) {
//        emptyWiDList
//    } else { // 마지막 빈 WiD 추가
//        val lastEmptyWiD = WiD(
//            id = 0,
//            date = date,
//            title = "",
//            start = emptyWiDStart,
//            finish = endOfDay,
//            duration = Duration.between(emptyWiDStart, endOfDay)
//        )
//        emptyWiDList.add(lastEmptyWiD)
//
//        emptyWiDList
//    }
//}

fun getFullWiDListFromWiDList(
    date: LocalDate, // 조회 날짜
    wiDList: List<WiD>,
    today: LocalDate, // date가 today면 다르게 동작하도록 함.
    currentTime: LocalTime? // currentTime이 Null이면 다르게 동작함.
): List<WiD> {
    Log.d("WiDListUtil", "getFullWiDListFromWiDList executed")

    if (wiDList.isEmpty()) { return emptyList() }

    val fullWiDList = mutableListOf<WiD>()

    var emptyWiDStart = LocalTime.MIN

    // 데이터 베이스에서 가져온 WiD는 0나노 세컨드를 가짐.
    for (currentWiD in wiDList) {
        val emptyWiDFinish = currentWiD.start

        if (emptyWiDStart.equals(emptyWiDFinish)) {
            emptyWiDStart = currentWiD.finish

            fullWiDList.add(currentWiD)
            continue
        }

        val emptyWiD = WiD(
            id = "newWiD",
            date = date,
            title = "기록 없음",
            start = emptyWiDStart,
            finish = emptyWiDFinish,
            duration = Duration.between(emptyWiDStart, emptyWiDFinish),
            createdBy = CurrentTool.LIST
        )

        fullWiDList.add(emptyWiD)
        fullWiDList.add(currentWiD) // 당연히 currentWiD를 emptyWiD 뒤에 넣어줘야 제대로 동작함

        emptyWiDStart = currentWiD.finish
    }

    if (date == today) { // 오늘 날짜 조회
        if (currentTime == null) { // 도구 시작 상태
            return fullWiDList
        } else { // 도구 정지 및 중지 상태
            val lastWiD = WiD(
                id = "lastNewWiD",
                date = today,
                title = "기록 없음",
                start = emptyWiDStart,
                finish = currentTime,
                duration = Duration.between(emptyWiDStart, currentTime),
                createdBy = CurrentTool.LIST
            )
            fullWiDList.add(lastWiD)

            return fullWiDList
        }
    } else { // 오늘 아닌 날짜 조회
        val maxTime = LocalTime.MAX.withNano(0)
        val emptyWiDDuration = Duration.between(emptyWiDStart, maxTime)
        if (Duration.ZERO < emptyWiDDuration) { // 마지막 WiD의 소요 시간이 있으면 추가
            val emptyWiD = WiD(
                id = "newWiD",
                date = date,
                title = "기록 없음",
                start = emptyWiDStart,
                finish = maxTime,
                duration = emptyWiDDuration,
                createdBy = CurrentTool.LIST
            )
            fullWiDList.add(emptyWiD)
        }

        return fullWiDList
    }
}

fun getTitlePercentageOfDay(duration: Duration): Float {
//    Log.d("WiDListUtil", "getTitlePercentageOfDay executed")

    // 하루의 총 초
    val totalSecondsInDay = 24 * 60 * 60

    // 주어진 Duration의 초
    val durationInSeconds = duration.seconds

    // 퍼센트 계산 (백분율)
    val percentage = (durationInSeconds.toFloat() / totalSecondsInDay) * 100

    return percentage
}

//fun getTotalDurationFromWiDList(wiDList: List<WiD>): Duration {
//    Log.d("WiDListUtil", "getTotalDurationFromWiDList executed")
//
//    return wiDList.map { it.duration }.reduceOrNull(Duration::plus) ?: Duration.ZERO
//}
//
//fun getTotalDurationPercentageFromWiDList(wiDList: List<WiD>): Int {
//    Log.d("WiDListUtil", "getTotalDurationPercentageFromWiDList executed")
//
//    val totalMinutes = 24 * 60 // 1440분 (24시간)
//    val totalDuration = (wiDList.map { it.duration }.reduceOrNull(Duration::plus) ?: Duration.ZERO).toMinutes().toInt()
//
//    return (totalDuration * 100) / totalMinutes
//}

fun getTotalDurationMapByTitle(wiDList: List<WiD>): Map<String, Duration> {
//    Log.d("WiDListUtil", "getTotalDurationMapByTitle executed")

    val result = mutableMapOf<String, Duration>()

    for (wiD in wiDList) {
        val title = wiD.title.takeUnless { it.isEmpty() } ?: "기록 없음"
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

//fun getTotalDurationMapByDate(wiDList: List<WiD>): Map<LocalDate, Duration> {
//    Log.d("WiDListUtil", "getTotalDurationMapByDate executed")
//
//    val result = mutableMapOf<LocalDate, Duration>()
//
//    for (wiD in wiDList) {
//        val date = wiD.date
//        val duration = wiD.duration
//
//        // 날짜 별로 소요 시간을 누적
//        val totalDuration = result[date]
//        if (totalDuration != null) {
//            result[date] = totalDuration.plus(duration)
//        } else {
//            result[date] = duration
//        }
//    }
//
//    return result
//}

fun getAverageDurationMapByTitle(wiDList: List<WiD>): Map<String, Duration> {
//    Log.d("WiDListUtil", "getAverageDurationMapByTitle executed")

    val result = mutableMapOf<String, Duration>()

    val totalDurationMapByTitleByDate = mutableMapOf<String, MutableMap<LocalDate, Duration>>()

    // 각 제목에 대한 날짜별 총 소요 시간을 계산
    for (wiD in wiDList) {
        val title = wiD.title
        val date = wiD.date
        val duration = wiD.duration

        val titleMap = totalDurationMapByTitleByDate.computeIfAbsent(title) { mutableMapOf() }
        titleMap[date] = titleMap.getOrDefault(date, Duration.ZERO) + duration
    }

    // 각 제목에 대한 평균 소요 시간을 계산하여 result 맵에 할당
    for ((title, totalDurationMapByDate) in totalDurationMapByTitleByDate) {
        val totalDuration = totalDurationMapByDate.values.reduce(Duration::plus)
        val averageDuration = totalDuration.dividedBy(totalDurationMapByDate.size.toLong())
        result[title] = averageDuration
    }

    // 소요 시간을 기준으로 내림차순으로 정렬
    val sortedResult = result.entries.sortedByDescending { it.value }

    // 정렬된 결과를 새로운 Map으로 반환
    return sortedResult.associate { it.toPair() }
}

fun getMinDurationMapByTitle(wiDList: List<WiD>): Map<String, Duration> {
//    Log.d("WiDListUtil", "getMinDurationMapByTitle executed")

    val result = mutableMapOf<String, Duration>()

    val totalDurationMapByTitleByDate = mutableMapOf<String, MutableMap<LocalDate, Duration>>()

    // 각 제목에 대한 날짜별 총 소요 시간을 계산
    for (wiD in wiDList) {
        val title = wiD.title
        val date = wiD.date
        val duration = wiD.duration

        val titleMap = totalDurationMapByTitleByDate.computeIfAbsent(title) { mutableMapOf() }
        titleMap[date] = titleMap.getOrDefault(date, Duration.ZERO) + duration
    }

    // 각 제목에 대한 최소 소요 시간을 계산하여 result 맵에 할당
    for ((title, totalDurationMapByDate) in totalDurationMapByTitleByDate) {
        val minDuration = totalDurationMapByDate.values.minOrNull() ?: Duration.ZERO
        result[title] = minDuration
    }

    // 소요 시간을 기준으로 내림차순으로 정렬
    val sortedResult = result.entries.sortedByDescending { it.value }

    // 정렬된 결과를 새로운 Map으로 반환
    return sortedResult.associate { it.toPair() }
}

fun getMaxDurationMapByTitle(wiDList: List<WiD>): Map<String, Duration> {
//    Log.d("WiDListUtil", "getMaxDurationMapByTitle executed")

    val result = mutableMapOf<String, Duration>()

    val totalDurationMapByTitleByDate = mutableMapOf<String, MutableMap<LocalDate, Duration>>()

    // 각 제목에 대한 날짜별 총 소요 시간을 계산
    for (wiD in wiDList) {
        val title = wiD.title
        val date = wiD.date
        val duration = wiD.duration

        val titleMap = totalDurationMapByTitleByDate.computeIfAbsent(title) { mutableMapOf() }
        titleMap[date] = titleMap.getOrDefault(date, Duration.ZERO) + duration
    }

    // 각 제목에 대한 최대 소요 시간을 계산하여 result 맵에 할당
    for ((title, totalDurationMapByDate) in totalDurationMapByTitleByDate) {
        val maxDuration = totalDurationMapByDate.values.maxOrNull() ?: Duration.ZERO
        result[title] = maxDuration
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
