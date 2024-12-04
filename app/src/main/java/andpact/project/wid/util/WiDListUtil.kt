package andpact.project.wid.util

import andpact.project.wid.model.WiD
import andpact.project.wid.model.YearlyWiDList
import android.util.Log
import com.google.firebase.Timestamp
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.random.Random

fun generateUniqueId(): String {
    Log.d("WiDListUtil", "generateUniqueId executed")

    val timestamp = Instant.now().epochSecond  // 10자리 타임스탬프
    val random = UUID.randomUUID().toString().substring(0, 4)  // 4자리 랜덤 값
    return "$timestamp$random"
}

fun WiD.toDocument(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "date" to date.toString(),
        "title" to title.name,
        "start" to Timestamp(Date.from(start.atDate(date).atZone(ZoneId.systemDefault()).toInstant())),
        "finish" to Timestamp(Date.from(finish.atDate(date).atZone(ZoneId.systemDefault()).toInstant())),
        "duration" to duration.seconds.toInt(),
        "createdBy" to createdBy.name
    )
}

fun Map<String, Any>.toWiD(): WiD {
    val startTimestamp = this["start"] as? Timestamp
    val finishTimestamp = this["finish"] as? Timestamp

    return WiD(
        id = this["id"] as String,
        date = LocalDate.parse(this["date"] as String),
        title = Title.valueOf(this["title"] as String),
        start = startTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime() ?: LocalTime.MIDNIGHT,
        finish = finishTimestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime() ?: LocalTime.MIDNIGHT,
        duration = Duration.ofSeconds(this["duration"] as Long),
        createdBy = CurrentTool.valueOf(this["createdBy"] as String)
    )
}

fun YearlyWiDList.toDocument(): Map<String, Any> {
    return mapOf(
        "wiDList" to wiDList.map { it.toDocument() } // wiDList의 각 WiD 객체를 toDocument()로 변환
    )
}

fun Map<String, Any>.toYearlyWiDList(): YearlyWiDList {
    val wiDList = (this["wiDList"] as List<Map<String, Any>>).map { it.toWiD() } // "wiDList"의 각 항목을 WiD로 변환

    return YearlyWiDList(wiDList = wiDList)
}

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
            title = Title.UNTITLED,
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
                title = Title.UNTITLED,
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
                title = Title.UNTITLED,
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

fun getWiDTitleTotalDurationMap(wiDList: List<WiD>): Map<Title, Duration> {
    val result = mutableMapOf<Title, Duration>()

    for (wiD in wiDList) {
        // WiD의 title을 Title enum으로 변환
        val title = Title.values().find { it == wiD.title } ?: continue // kr 값으로 Title을 찾음
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

fun getWiDTitleAverageDurationMap(wiDList: List<WiD>): Map<Title, Duration> {
//    Log.d("WiDListUtil", "getWiDTitleAverageDurationMap executed")

    val result = mutableMapOf<Title, Duration>()

    val totalDurationMapByTitleByDate = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

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

fun getWiDTitleMaxDurationMap(wiDList: List<WiD>): Map<Title, Duration> {
//    Log.d("WiDListUtil", "getWiDTitleMaxDurationMap executed")

    val result = mutableMapOf<Title, Duration>()

    val totalDurationMapByTitleByDate = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

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

fun getWiDTitleMinDurationMap(wiDList: List<WiD>): Map<Title, Duration> {
//    Log.d("WiDListUtil", "getWiDTitleMinDurationMap executed")

    val result = mutableMapOf<Title, Duration>()

    val totalDurationMapByTitleByDate = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

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

fun getWiDTitleMaxDateMap(wiDList: List<WiD>): Map<Title, LocalDate> {
//    Log.d("WiDListUtil", "getWiDTitleMaxDateMap executed")

    // Step 1: Title 별로 날짜 별 Duration을 누적
    val durationMap = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

    for (wiD in wiDList) {
        val title = wiD.title
        val date = wiD.date
        val duration = wiD.duration

        // Title과 날짜별로 누적 시간을 저장
        val dateDurationMap = durationMap.getOrPut(title) { mutableMapOf() }
        dateDurationMap[date] = dateDurationMap.getOrDefault(date, Duration.ZERO).plus(duration)
    }

    // Step 2: 각 Title에 대해 최대 Duration을 가진 날짜 찾기
    return durationMap.mapValues { (_, dateMap) ->
        dateMap.maxByOrNull { it.value }?.key ?: LocalDate.now() // 최대 Duration을 가진 날짜 반환
    }
}

fun getWiDTitleMinDateMap(wiDList: List<WiD>): Map<Title, LocalDate> {
//    Log.d("WiDListUtil", "getWiDTitleMinDateMap executed")

    // Step 1: Title 별로 날짜 별 Duration을 누적
    val durationMap = mutableMapOf<Title, MutableMap<LocalDate, Duration>>()

    for (wiD in wiDList) {
        val title = wiD.title
        val date = wiD.date
        val duration = wiD.duration

        // Title과 날짜별로 누적 시간을 저장
        val dateDurationMap = durationMap.getOrPut(title) { mutableMapOf() }
        dateDurationMap[date] = dateDurationMap.getOrDefault(date, Duration.ZERO).plus(duration)
    }

    // Step 2: 각 Title에 대해 최소 Duration을 가진 날짜 찾기
    return durationMap.mapValues { (_, dateMap) ->
        dateMap.minByOrNull { it.value }?.key ?: LocalDate.now() // 최소 Duration을 가진 날짜 반환
    }
}

fun getWiDTitleDateCountMap(wiDList: List<WiD>): Map<Title, Int> {
//    Log.d("WiDListUtil", "getWiDTitleDateCountMap executed")

    // 제목별로 날짜를 저장하기 위한 맵을 초기화합니다.
    val titleDateSetMap = mutableMapOf<Title, MutableSet<LocalDate>>()

    // wiDList를 순회하면서 각 WiD의 제목과 날짜를 맵에 추가합니다.
    for (wid in wiDList) {
        // 제목별로 존재하는 날짜를 집합(Set)으로 관리합니다.
        val dateSet = titleDateSetMap.getOrPut(wid.title) { mutableSetOf() }
        dateSet.add(wid.date)
    }

    // 제목별 날짜 집합의 크기를 이용해 제목별 존재 일수를 계산하여 맵으로 변환합니다.
    return titleDateSetMap.mapValues { it.value.size }
}

val sampleDailyWiDList = listOf(
    WiD(
        id = "",
        date = LocalDate.now(),
        title = Title.WORK,
        start = LocalTime.of(8, 0),
        finish = LocalTime.of(9, 30),
        duration = Duration.ofMinutes(90),
        createdBy = CurrentTool.TIMER
    ),
    WiD(
        id = "",
        date = LocalDate.now(),
        title = Title.STUDY,
        start = LocalTime.of(10, 0),
        finish = LocalTime.of(11, 0),
        duration = Duration.ofMinutes(60),
        createdBy = CurrentTool.LIST
    ),
    WiD(
        id = "",
        date = LocalDate.now(),
        title = Title.EXERCISE,
        start = LocalTime.of(12, 0),
        finish = LocalTime.of(12, 45),
        duration = Duration.ofMinutes(45),
        createdBy = CurrentTool.STOPWATCH
    ),
    WiD(
        id = "",
        date = LocalDate.now(),
        title = Title.WORK,
        start = LocalTime.of(14, 0),
        finish = LocalTime.of(15, 0),
        duration = Duration.ofMinutes(60),
        createdBy = CurrentTool.TIMER
    ),
    WiD(
        id = "",
        date = LocalDate.now(),
        title = Title.RELAXATION,
        start = LocalTime.of(16, 0),
        finish = LocalTime.of(16, 30),
        duration = Duration.ofMinutes(30),
        createdBy = CurrentTool.STOPWATCH
    ),
    WiD(
        id = "",
        date = LocalDate.now(),
        title = Title.TRAVEL,
        start = LocalTime.of(17, 0),
        finish = LocalTime.of(17, 20),
        duration = Duration.ofMinutes(20),
        createdBy = CurrentTool.TIMER
    ),
    WiD(
        id = "",
        date = LocalDate.now(),
        title = Title.HOBBY,
        start = LocalTime.of(18, 0),
        finish = LocalTime.of(19, 0),
        duration = Duration.ofMinutes(60),
        createdBy = CurrentTool.LIST
    )
)

fun generateSampleWeeklyWiDList(): List<WiD> {
    val sampleWeeklyWiDList = mutableListOf<WiD>()
    val startDate = LocalDate.of(2024, 11, 11) // 시작 날짜

    for (i in 0 until 7) { // 일주일 간의 WiD 생성
        val currentDate = startDate.plusDays(i.toLong()) // 날짜 증가

        val startHour = Random.nextInt(0, 23) // 0~22시 중 랜덤 선택
        val startMinute = Random.nextInt(0, 60) // 0~59분 중 랜덤 선택
        val startSecond = Random.nextInt(0, 60) // 0~59분 중 랜덤 선택
        val startTime = LocalTime.of(startHour, startMinute, startSecond)

        val finishHour = 1
        val finishMinute = Random.nextInt(0, 60) // 0~59분 중 랜덤 선택
        val finishSecond = Random.nextInt(0, 60) // 0~59분 중 랜덤 선택
        val finishTime = startTime.plusHours(finishHour.toLong()).plusMinutes(finishMinute.toLong()).plusSeconds(finishSecond.toLong())

        // duration은 시작 시간과 종료 시간의 차이로 계산
        val duration = Duration.between(startTime, finishTime)

        // WiD 생성 및 리스트에 추가
        val wiD = WiD(
            id = "",
            date = currentDate,
            title = Title.STUDY,
            start = startTime,
            finish = finishTime,
            duration = duration,
            createdBy = CurrentTool.LIST
        )

        sampleWeeklyWiDList.add(wiD)
    }
    return sampleWeeklyWiDList
}