package andpact.project.wid.service

import andpact.project.wid.model.WiD
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

class WiDService(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "WiDDatabase"
        private const val TABLE_NAME = "wid_table"

        private const val COLUMN_ID = "id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_START = "start"
        private const val COLUMN_FINISH = "finish"
        private const val COLUMN_DURATION = "duration"
        private const val COLUMN_DETAIL = "detail"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_DATE TEXT,
                $COLUMN_TITLE TEXT,
                $COLUMN_START TEXT,
                $COLUMN_FINISH TEXT,
                $COLUMN_DURATION TEXT,
                $COLUMN_DETAIL TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun createWiD(wid: WiD) {
        Log.d("WiDService", "createWiD executed")

        val db = writableDatabase
        val values = ContentValues().apply {
//            put(COLUMN_ID, wid.id) // 이거 필요없음
            put(COLUMN_DATE, wid.date.toString())
            put(COLUMN_TITLE, wid.title)
            put(COLUMN_START, wid.start.toString())
            put(COLUMN_FINISH, wid.finish.toString())
//            put(COLUMN_DURATION, wid.duration.toString())
            put(COLUMN_DURATION, wid.duration.toMillis())
            put(COLUMN_DETAIL, wid.detail)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getYearList(): List<String> {
        val years = mutableListOf<String>()
        val query = "SELECT DISTINCT substr($COLUMN_DATE, 1, 4) AS year FROM $TABLE_NAME ORDER BY year DESC"

        val db = readableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val year = cursor.getString(cursor.getColumnIndex("year"))
                years.add(year)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return years
    }

    fun readWiDById(id: Long): WiD? {
        val db = readableDatabase

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        var wiD: WiD? = null

        if (cursor.moveToFirst()) {
            val date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
            val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
            val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
            val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))

            wiD = WiD(id, date, title, startTime, finishTime, durationMillis, detail)
        }

        cursor.close()
        db.close()

        return wiD
    }

    fun getDailyTitleDurationMap(date: LocalDate): Map<String, Duration> {
        val db = readableDatabase
        val dailyTitleDurationMap = mutableMapOf<String, Duration>()

        val selectQuery = """
            SELECT $COLUMN_TITLE, SUM(CAST($COLUMN_DURATION AS INTEGER)) AS total_duration
            FROM $TABLE_NAME
            WHERE $COLUMN_DATE = ?
            GROUP BY $COLUMN_TITLE
        """.trimIndent()
        val selectionArgs = arrayOf(date.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            val totalDuration = cursor.getLong(cursor.getColumnIndex("total_duration"))
            dailyTitleDurationMap[title] = Duration.ofMillis(totalDuration)
        }

        cursor.close()
        db.close()

        return dailyTitleDurationMap
    }

    fun getWeeklyTitleDurationMap(date: LocalDate): Map<String, Duration> {
        val db = readableDatabase
        val weeklyTitleDurationMap = mutableMapOf<String, Duration>()

        val firstDayOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val lastDayOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val selectQuery = """
            SELECT $COLUMN_TITLE, SUM(CAST($COLUMN_DURATION AS INTEGER)) AS total_duration
            FROM $TABLE_NAME
            WHERE $COLUMN_DATE BETWEEN ? AND ?
            GROUP BY $COLUMN_TITLE
        """.trimIndent()
        val selectionArgs = arrayOf(firstDayOfWeek.toString(), lastDayOfWeek.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            val totalDuration = cursor.getLong(cursor.getColumnIndex("total_duration"))
            weeklyTitleDurationMap[title] = Duration.ofMillis(totalDuration)
        }

        cursor.close()
        db.close()

        return weeklyTitleDurationMap
    }

    fun getMonthlyTitleDurationMap(date: LocalDate): Map<String, Duration> {
        val db = readableDatabase
        val monthlyTitleDurationMap = mutableMapOf<String, Duration>()

        val firstDayOfMonth = date.withDayOfMonth(1)
        val lastDayOfMonth = date.withDayOfMonth(date.month.length(date.isLeapYear))

        val selectQuery = """
            SELECT $COLUMN_TITLE, SUM(CAST($COLUMN_DURATION AS INTEGER)) AS total_duration
            FROM $TABLE_NAME
            WHERE $COLUMN_DATE BETWEEN ? AND ?
            GROUP BY $COLUMN_TITLE
        """.trimIndent()
        val selectionArgs = arrayOf(firstDayOfMonth.toString(), lastDayOfMonth.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            val totalDuration = cursor.getLong(cursor.getColumnIndex("total_duration"))
            monthlyTitleDurationMap[title] = Duration.ofMillis(totalDuration)
        }

        cursor.close()
        db.close()

        return monthlyTitleDurationMap
    }

    fun readDailyWiDListByDate(date: LocalDate): List<WiD> {
        Log.d("WiDService", "readWiDListByDate executed")

        val db = readableDatabase
        val wiDList = mutableListOf<WiD>()

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ?"
        val selectionArgs = arrayOf(date.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
                val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
                val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
                val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))

                val wiD = WiD(id, date, title, startTime, finishTime, durationMillis, detail)
                wiDList.add(wiD)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return wiDList.sortedBy { it.start }
    }

    fun readWiDListByDateRange(startDate: LocalDate, finishDate: LocalDate): List<WiD> {
        Log.d("WiDService", "readWiDListByDateRange executed")

        val db = readableDatabase
        val wiDList = mutableListOf<WiD>()

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE BETWEEN ? AND ?"
        val selectionArgs = arrayOf(startDate.toString(), finishDate.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))
                val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
                val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
                val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
                val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))

                val wiD = WiD(id, date, title, startTime, finishTime, durationMillis, detail)
                wiDList.add(wiD)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return wiDList.sortedWith(compareBy<WiD> { it.date }.thenBy { it.start })
    }

//    fun readDailyWiDListByDate(date: LocalDate, title: String): List<WiD> {
//        Log.d("WiDService", "readWiDListByDate executed")
//
//        val db = readableDatabase
//        val wiDList = mutableListOf<WiD>()
//
//        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ? AND $COLUMN_TITLE = ?"
//        val selectionArgs = arrayOf(date.toString(), title)
//
//        val cursor = db.rawQuery(selectQuery, selectionArgs)
//
//        if (cursor.moveToFirst()) {
//            do {
//                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
//                val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
//                val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
//                val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
//                val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))
//
//                val wiD = WiD(id, date, title, startTime, finishTime, durationMillis, detail)
//                wiDList.add(wiD)
//            } while (cursor.moveToNext())
//        }
//
//        cursor.close()
//        db.close()
//
//        return wiDList.sortedBy { it.start }
//    }

    fun readWeeklyWiDListByDate(date: LocalDate): List<WiD> {
        val firstDayOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val lastDayOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val db = readableDatabase
        val wiDList = mutableListOf<WiD>()

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE BETWEEN ? AND ?"
        val selectionArgs = arrayOf(firstDayOfWeek.toString(), lastDayOfWeek.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))
                val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
                val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
                val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
                val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))

                val wiD = WiD(id, date, title, startTime, finishTime, durationMillis, detail)
                wiDList.add(wiD)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return wiDList.sortedBy { it.date }
    }

//    fun readWeeklyWiDListByDate(date: LocalDate, title: String): List<WiD> {
//        val firstDayOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
//        val lastDayOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
//
//        val db = readableDatabase
//        val wiDList = mutableListOf<WiD>()
//
//        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE BETWEEN ? AND ? AND $COLUMN_TITLE = ?"
//        val selectionArgs = arrayOf(firstDayOfWeek.toString(), lastDayOfWeek.toString(), title)
//
//        val cursor = db.rawQuery(selectQuery, selectionArgs)
//
//        if (cursor.moveToFirst()) {
//            do {
//                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
//                val date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))
//                val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
//                val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
//                val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
//                val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))
//
//                val wiD = WiD(id, date, title, startTime, finishTime, durationMillis, detail)
//                wiDList.add(wiD)
//            } while (cursor.moveToNext())
//        }
//
//        cursor.close()
//        db.close()
//
//        return wiDList.sortedBy { it.date }
//    }

    fun readMonthlyWiDListByDate(date: LocalDate): List<WiD> {
        val yearMonth = YearMonth.from(date)
        val firstDayOfMonth = date.withDayOfMonth(1)
        val lastDayOfMonth = date.withDayOfMonth(yearMonth.lengthOfMonth())

        val db = readableDatabase
        val wiDList = mutableListOf<WiD>()

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE BETWEEN ? AND ?"
        val selectionArgs = arrayOf(firstDayOfMonth.toString(), lastDayOfMonth.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))
                val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
                val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
                val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
                val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))

                val wiD = WiD(id, date, title, startTime, finishTime, durationMillis, detail)
                wiDList.add(wiD)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return wiDList.sortedBy { it.date }
    }

//    fun readMonthlyWiDListByDate(date: LocalDate, title: String): List<WiD> {
//        val yearMonth = YearMonth.from(date)
//        val firstDayOfMonth = date.withDayOfMonth(1)
//        val lastDayOfMonth = date.withDayOfMonth(yearMonth.lengthOfMonth())
//
//        val db = readableDatabase
//        val wiDList = mutableListOf<WiD>()
//
//        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE BETWEEN ? AND ? AND $COLUMN_TITLE = ?"
//        val selectionArgs = arrayOf(firstDayOfMonth.toString(), lastDayOfMonth.toString(), title)
//
//        val cursor = db.rawQuery(selectQuery, selectionArgs)
//
//        if (cursor.moveToFirst()) {
//            do {
//                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
//                val date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))
//                val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
//                val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
//                val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
//                val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))
//
//                val wiD = WiD(id, date, title, startTime, finishTime, durationMillis, detail)
//                wiDList.add(wiD)
//            } while (cursor.moveToNext())
//        }
//
//        cursor.close()
//        db.close()
//
//        return wiDList.sortedBy { it.date }
//    }


    fun getLongestStreak(title: String, startDate: LocalDate, finishDate: LocalDate): Pair<LocalDate, LocalDate>? {
        val db = readableDatabase
        var currentRangeStart: LocalDate? = null
        var currentRangeEnd: LocalDate? = null
        var longestRangeStart: LocalDate? = null
        var longestRangeEnd: LocalDate? = null

        val selectQuery: String
        val selectionArgs: Array<String>

        if (title == "ALL") {
            selectQuery = """
                SELECT DISTINCT $COLUMN_DATE
                FROM $TABLE_NAME
                WHERE $COLUMN_DATE BETWEEN ? AND ?
                ORDER BY $COLUMN_DATE ASC
            """.trimIndent()
            selectionArgs = arrayOf(startDate.toString(), finishDate.toString())
        } else {
            selectQuery = """
                SELECT DISTINCT $COLUMN_DATE
                FROM $TABLE_NAME
                WHERE $COLUMN_TITLE = ? AND $COLUMN_DATE BETWEEN ? AND ?
                ORDER BY $COLUMN_DATE ASC
            """.trimIndent()
            selectionArgs = arrayOf(title, startDate.toString(), finishDate.toString())
        }


        val cursor = db.rawQuery(selectQuery, selectionArgs)
        var previousDate: LocalDate? = null

        while (cursor.moveToNext()) {
            val dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
            val currentDate = LocalDate.parse(dateString)

            if (previousDate == null) {
                currentRangeStart = currentDate
                currentRangeEnd = currentDate
            } else if (previousDate.plusDays(1) == currentDate) {
                // The current date continues the range.
                currentRangeEnd = currentDate
            }
//            else if (previousDate == currentDate) {
//                // The current date is the same as the previous one, continue to the next date.
//                continue
//            }
        else {
                // The current date breaks the range.
                if (currentRangeStart != null && currentRangeEnd != null) {
                    // Update the longest range if necessary.
                    if (longestRangeStart == null || ChronoUnit.DAYS.between(longestRangeStart, longestRangeEnd) < ChronoUnit.DAYS.between(currentRangeStart, currentRangeEnd)) {
                        longestRangeStart = currentRangeStart
                        longestRangeEnd = currentRangeEnd
                    }
                }
                currentRangeStart = currentDate
                currentRangeEnd = currentDate
            }

            previousDate = currentDate
        }

        // Check if the last range is the longest.
        if (currentRangeStart != null && currentRangeEnd != null) {
            if (longestRangeStart == null || ChronoUnit.DAYS.between(longestRangeStart, longestRangeEnd) < ChronoUnit.DAYS.between(currentRangeStart, currentRangeEnd)) {
                longestRangeStart = currentRangeStart
                longestRangeEnd = currentRangeEnd
            }
        }

        cursor.close()
        db.close()

        return if (longestRangeStart != null && longestRangeEnd != null) {
            Pair(longestRangeStart, longestRangeEnd)
        } else {
            null
        }
    }

    fun getCurrentStreak(title: String, startDate: LocalDate, finishDate: LocalDate): LocalDate? {
        val db = readableDatabase

        val selectQuery: String
        val selectionArgs: Array<String>

        if (title == "ALL") {
            selectQuery = """
                SELECT DISTINCT $COLUMN_DATE
                FROM $TABLE_NAME
                WHERE $COLUMN_DATE BETWEEN ? AND ?
                ORDER BY $COLUMN_DATE DESC
            """.trimIndent()
            selectionArgs = arrayOf(startDate.toString(), finishDate.toString())
        } else {
            selectQuery = """
                SELECT DISTINCT $COLUMN_DATE
                FROM $TABLE_NAME
                WHERE $COLUMN_TITLE = ? AND $COLUMN_DATE BETWEEN ? AND ?
                ORDER BY $COLUMN_DATE DESC
            """.trimIndent()
            selectionArgs = arrayOf(title, startDate.toString(), finishDate.toString())
        }

        val cursor = db.rawQuery(selectQuery, selectionArgs)
        var previousDate: LocalDate? = null

        while (cursor.moveToNext()) {
            val dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
            val date = LocalDate.parse(dateString)

            if (cursor.isFirst && date != finishDate) {
                cursor.close()
                db.close()
                return null
            }

//            if (previousDate != null && previousDate == date) {
//                // Skip duplicate dates.
//                continue
//            }

            if (previousDate == null || previousDate == date.plusDays(1)) {
                // Continuation of consecutive days or the first day.
                previousDate = date
            } else {
                // Streak is broken.
                cursor.close()
                db.close()
                return previousDate
            }
        }

        cursor.close()
        db.close()

        // If we have a consecutive streak, return the finish date.
        return previousDate
    }

    fun getTotalDaysAndDuration(title: String, startDate: LocalDate, finishDate: LocalDate): Pair<Long, Duration>? {
        val db = readableDatabase

        val selectQuery: String
        val selectionArgs: Array<String>

        if (title == "ALL") {
            selectQuery = """
                SELECT COUNT(DISTINCT $COLUMN_DATE) as days_count, SUM($COLUMN_DURATION) as total_duration
                FROM $TABLE_NAME
                WHERE $COLUMN_DATE BETWEEN ? AND ?
            """.trimIndent()
            selectionArgs = arrayOf(startDate.toString(), finishDate.toString())
        } else {
            selectQuery = """
                SELECT COUNT(DISTINCT $COLUMN_DATE) as days_count, SUM($COLUMN_DURATION) as total_duration
                FROM $TABLE_NAME
                WHERE $COLUMN_TITLE = ? AND $COLUMN_DATE BETWEEN ? AND ?
            """.trimIndent()
            selectionArgs = arrayOf(title, startDate.toString(), finishDate.toString())
        }

        val cursor = db.rawQuery(selectQuery, selectionArgs)
        var daysCount: Long? = null
        var totalDuration: Duration? = null

        if (cursor.moveToFirst()) {
            daysCount = cursor.getLong(cursor.getColumnIndex("days_count"))
            totalDuration = Duration.ofMillis(cursor.getLong(cursor.getColumnIndex("total_duration")))
        }

        cursor.close()
        db.close()

        return if (daysCount != null && totalDuration != null) {
            Pair(daysCount, totalDuration)
        } else {
            null
        }
    }

    fun getBestDateAndDuration(title: String, startDate: LocalDate, finishDate: LocalDate): Pair<Duration, LocalDate>? {
        val db = readableDatabase

        val selectQuery: String
        val selectionArgs: Array<String>

        if (title == "ALL") {
            selectQuery = """
                SELECT $COLUMN_DATE, SUM($COLUMN_DURATION) as total_duration
                FROM $TABLE_NAME
                WHERE $COLUMN_DATE BETWEEN ? AND ?
                GROUP BY $COLUMN_DATE
                ORDER BY total_duration DESC
                LIMIT 1
            """.trimIndent()
            selectionArgs = arrayOf(startDate.toString(), finishDate.toString())
        } else {
            selectQuery = """
                SELECT $COLUMN_DATE, SUM($COLUMN_DURATION) as total_duration
                FROM $TABLE_NAME
                WHERE $COLUMN_TITLE = ? AND $COLUMN_DATE BETWEEN ? AND ?
                GROUP BY $COLUMN_DATE
                ORDER BY total_duration DESC
                LIMIT 1
            """.trimIndent()
            selectionArgs = arrayOf(title, startDate.toString(), finishDate.toString())
        }

        val cursor = db.rawQuery(selectQuery, selectionArgs)
        var bestDate: LocalDate? = null
        var totalDuration: Duration? = null

        if (cursor.moveToFirst()) {
            val dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
            bestDate = LocalDate.parse(dateString)
            totalDuration = Duration.ofMillis(cursor.getLong(cursor.getColumnIndex("total_duration")))
        }

        cursor.close()
        db.close()

        return if (bestDate != null && totalDuration != null) {
            Pair(totalDuration, bestDate)
        } else {
            null
        }
    }

    fun readWiDListByDetail(detail: String): List<WiD> {
        val db = readableDatabase

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DETAIL LIKE ?" // LIKE 연산자 사용
        val selectionArgs = arrayOf("%$detail%") // detail을 와일드카드로 묶어 부분 일치 검색

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        val wiDList = mutableListOf<WiD>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
            val date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
            val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
            val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
            val retrievedDetail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))

            val wiD = WiD(id, date, title, startTime, finishTime, durationMillis, retrievedDetail)
            wiDList.add(wiD)
        }

        cursor.close()
        db.close()

        return wiDList
    }

//    fun readAllWiD(): List<WiD> {
//        val db = readableDatabase
//        val wiDList = mutableListOf<WiD>()
//
//        val selectQuery = "SELECT * FROM $TABLE_NAME"
//
//        val cursor = db.rawQuery(selectQuery, null)
//
//        if (cursor.moveToFirst()) {
//            do {
//                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
//                val date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))
//                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
//                val startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START)))
//                val finishTime = LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH)))
//                val durationMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION))
//                val detail = cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL))
//
//                val wiD = WiD(id, date, title, startTime, finishTime, durationMillis, detail)
//                wiDList.add(wiD)
//            } while (cursor.moveToNext())
//        }
//
//        cursor.close()
//        db.close()
//
//        return wiDList
//    }

    fun updateWiD(id: Long, date: LocalDate, title: String, start: LocalTime, finish: LocalTime, duration: Duration, detail: String) {
        val db = writableDatabase

        val updateQuery = "UPDATE $TABLE_NAME SET $COLUMN_DATE = ?, $COLUMN_TITLE = ?, $COLUMN_START = ?, $COLUMN_FINISH = ?, $COLUMN_DURATION = ?, $COLUMN_DETAIL = ? WHERE $COLUMN_ID = ?"
        val selectionArgs = arrayOf(date.toString(), title, start.toString(), finish.toString(), duration.toMillis(), detail, id.toString())

        db.execSQL(updateQuery, selectionArgs)

        db.close()
    }

    fun deleteWiDById(id: Long) {
        val db = writableDatabase

        val deleteQuery = "DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        db.execSQL(deleteQuery, selectionArgs)

        db.close()
    }

//    fun deleteAllWiD() {
//        val db = writableDatabase
//        db.execSQL("DELETE FROM $TABLE_NAME")
//        db.close()
//    }
}