package andpact.project.wid.service

import andpact.project.wid.model.WiD
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

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
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_DATE TEXT,
                $COLUMN_TITLE TEXT,
                $COLUMN_START TEXT,
                $COLUMN_FINISH TEXT,
                $COLUMN_DURATION TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun createWiD(wid: WiD): Long {
        Log.d("WiDService", "createWiD executed")

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, wid.date.toString())
            put(COLUMN_TITLE, wid.title)
            put(COLUMN_START, wid.start.toString())
            put(COLUMN_FINISH, wid.finish.toString())
            put(COLUMN_DURATION, wid.duration.toMillis())
        }

        // Insert the data and get the ID of the newly inserted row
        val newRowId = db.insert(TABLE_NAME, null, values)

        // Close the database
        db.close()

        // Return the ID of the newly inserted row
        return newRowId
    }

//    fun getYearList(): List<String> {
//        val years = mutableListOf<String>()
//        val query = "SELECT DISTINCT substr($COLUMN_DATE, 1, 4) AS year FROM $TABLE_NAME ORDER BY year DESC"
//
//        val db = readableDatabase
//        val cursor = db.rawQuery(query, null)
//
//        if (cursor.moveToFirst()) {
//            do {
//                val year = cursor.getString(cursor.getColumnIndex("year"))
//                years.add(year)
//            } while (cursor.moveToNext())
//        }
//
//        cursor.close()
//        db.close()
//
//        return years
//    }

    fun readWiDById(id: Long): WiD? {
        val db = readableDatabase

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        var wiD: WiD? = null

        with(cursor) {
            if (moveToFirst()) {
                val date = LocalDate.parse(getString(getColumnIndexOrThrow(COLUMN_DATE)))
                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val startTime = LocalTime.parse(getString(getColumnIndexOrThrow(COLUMN_START)))
                val finishTime = LocalTime.parse(getString(getColumnIndexOrThrow(COLUMN_FINISH)))
                val durationMillis = getLong(getColumnIndexOrThrow(COLUMN_DURATION))

                wiD = WiD(id, date, title, startTime, finishTime, durationMillis)
            }
            close()
        }

        db.close()

        return wiD
    }

    fun readDailyWiDListByDate(date: LocalDate): List<WiD> {
        Log.d("WiDService", "readWiDListByDate executed")

        val db = readableDatabase
        val wiDList = mutableListOf<WiD>()

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ? ORDER BY $COLUMN_START ASC"
        val selectionArgs = arrayOf(date.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        with(cursor) {
            if (moveToFirst()) {
                do {
                    val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                    val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                    val startTime = LocalTime.parse(getString(getColumnIndexOrThrow(COLUMN_START)))
                    val finishTime = LocalTime.parse(getString(getColumnIndexOrThrow(COLUMN_FINISH)))
                    val durationMillis = getLong(getColumnIndexOrThrow(COLUMN_DURATION))

                    val wiD = WiD(id, date, title, startTime, finishTime, durationMillis)
                    wiDList.add(wiD)
                } while (moveToNext())
            }
            close()
        }

        db.close()

        return wiDList
    }

    fun readWiDListByDateRange(startDate: LocalDate, finishDate: LocalDate): List<WiD> {
        Log.d("WiDService", "readWiDListByDateRange executed")

        val db = readableDatabase
        val wiDList = mutableListOf<WiD>()

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE BETWEEN ? AND ? ORDER BY $COLUMN_DATE, $COLUMN_START"
        val selectionArgs = arrayOf(startDate.toString(), finishDate.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        with(cursor) {
            if (moveToFirst()) {
                do {
                    val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                    val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                    val date = LocalDate.parse(getString(getColumnIndexOrThrow(COLUMN_DATE)))
                    val startTime = LocalTime.parse(getString(getColumnIndexOrThrow(COLUMN_START)))
                    val finishTime = LocalTime.parse(getString(getColumnIndexOrThrow(COLUMN_FINISH)))
                    val durationMillis = getLong(getColumnIndexOrThrow(COLUMN_DURATION))

                    val wiD = WiD(id, date, title, startTime, finishTime, durationMillis)
                    wiDList.add(wiD)
                } while (moveToNext())
            }
            close()
        }

        db.close()

        return wiDList
    }

    fun getRandomWiDList(): List<WiD> {
        Log.d("WiDService", "getRandomWiDList executed")

        val db = readableDatabase
        val wiDList = mutableListOf<WiD>()

        // Query to get all distinct dates from the wid_table
        val distinctDatesQuery = "SELECT DISTINCT $COLUMN_DATE FROM $TABLE_NAME"
        val distinctDatesCursor = db.rawQuery(distinctDatesQuery, null)

        with(distinctDatesCursor) {
            if (moveToFirst()) {
                val dateList = mutableListOf<String>()
                do {
                    dateList.add(getString(getColumnIndexOrThrow(COLUMN_DATE)))
                } while (moveToNext())

                // Check if there are any dates in the table
                if (dateList.isNotEmpty()) {
                    // Randomly select a date from the list
                    val randomDate = dateList.random()

                    // Query to get WiD entries for the randomly selected date
                    val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ? ORDER BY $COLUMN_START ASC"
                    val selectionArgs = arrayOf(randomDate)

                    val cursor = db.rawQuery(selectQuery, selectionArgs)

                    with(cursor) {
                        if (moveToFirst()) {
                            do {
                                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                                val startTime = LocalTime.parse(getString(getColumnIndexOrThrow(COLUMN_START)))
                                val finishTime = LocalTime.parse(getString(getColumnIndexOrThrow(COLUMN_FINISH)))
                                val durationMillis = getLong(getColumnIndexOrThrow(COLUMN_DURATION))

                                val wiD = WiD(id, LocalDate.parse(randomDate), title, startTime, finishTime, durationMillis)
                                wiDList.add(wiD)
                            } while (moveToNext())
                        }
                        close()
                    }
                }
            }
            close()
        }

        db.close()

        return wiDList
    }

//    fun getLongestStreak(title: String, startDate: LocalDate, finishDate: LocalDate): Pair<LocalDate, LocalDate>? {
//        val db = readableDatabase
//        var currentRangeStart: LocalDate? = null
//        var currentRangeEnd: LocalDate? = null
//        var longestRangeStart: LocalDate? = null
//        var longestRangeEnd: LocalDate? = null
//
//        val selectQuery: String
//        val selectionArgs: Array<String>
//
//        if (title == "ALL") {
//            selectQuery = """
//                SELECT DISTINCT $COLUMN_DATE
//                FROM $TABLE_NAME
//                WHERE $COLUMN_DATE BETWEEN ? AND ?
//                ORDER BY $COLUMN_DATE ASC
//            """.trimIndent()
//            selectionArgs = arrayOf(startDate.toString(), finishDate.toString())
//        } else {
//            selectQuery = """
//                SELECT DISTINCT $COLUMN_DATE
//                FROM $TABLE_NAME
//                WHERE $COLUMN_TITLE = ? AND $COLUMN_DATE BETWEEN ? AND ?
//                ORDER BY $COLUMN_DATE ASC
//            """.trimIndent()
//            selectionArgs = arrayOf(title, startDate.toString(), finishDate.toString())
//        }
//
//
//        val cursor = db.rawQuery(selectQuery, selectionArgs)
//        var previousDate: LocalDate? = null
//
//        while (cursor.moveToNext()) {
//            val dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
//            val currentDate = LocalDate.parse(dateString)
//
//            if (previousDate == null) {
//                currentRangeStart = currentDate
//                currentRangeEnd = currentDate
//            } else if (previousDate.plusDays(1) == currentDate) {
//                // The current date continues the range.
//                currentRangeEnd = currentDate
//            }
////            else if (previousDate == currentDate) {
////                // The current date is the same as the previous one, continue to the next date.
////                continue
////            }
//            else {
//                // The current date breaks the range.
//                if (currentRangeStart != null && currentRangeEnd != null) {
//                    // Update the longest range if necessary.
//                    if (longestRangeStart == null || ChronoUnit.DAYS.between(longestRangeStart, longestRangeEnd) < ChronoUnit.DAYS.between(currentRangeStart, currentRangeEnd)) {
//                        longestRangeStart = currentRangeStart
//                        longestRangeEnd = currentRangeEnd
//                    }
//                }
//                currentRangeStart = currentDate
//                currentRangeEnd = currentDate
//            }
//
//            previousDate = currentDate
//        }
//
//        // Check if the last range is the longest.
//        if (currentRangeStart != null && currentRangeEnd != null) {
//            if (longestRangeStart == null || ChronoUnit.DAYS.between(longestRangeStart, longestRangeEnd) < ChronoUnit.DAYS.between(currentRangeStart, currentRangeEnd)) {
//                longestRangeStart = currentRangeStart
//                longestRangeEnd = currentRangeEnd
//            }
//        }
//
//        cursor.close()
//        db.close()
//
//        return if (longestRangeStart != null && longestRangeEnd != null) {
//            Pair(longestRangeStart, longestRangeEnd)
//        } else {
//            null
//        }
//    }
//
//    fun getCurrentStreak(title: String, startDate: LocalDate, finishDate: LocalDate): LocalDate? {
//        val db = readableDatabase
//
//        val selectQuery: String
//        val selectionArgs: Array<String>
//
//        if (title == "ALL") {
//            selectQuery = """
//                SELECT DISTINCT $COLUMN_DATE
//                FROM $TABLE_NAME
//                WHERE $COLUMN_DATE BETWEEN ? AND ?
//                ORDER BY $COLUMN_DATE DESC
//            """.trimIndent()
//            selectionArgs = arrayOf(startDate.toString(), finishDate.toString())
//        } else {
//            selectQuery = """
//                SELECT DISTINCT $COLUMN_DATE
//                FROM $TABLE_NAME
//                WHERE $COLUMN_TITLE = ? AND $COLUMN_DATE BETWEEN ? AND ?
//                ORDER BY $COLUMN_DATE DESC
//            """.trimIndent()
//            selectionArgs = arrayOf(title, startDate.toString(), finishDate.toString())
//        }
//
//        val cursor = db.rawQuery(selectQuery, selectionArgs)
//        var previousDate: LocalDate? = null
//
//        while (cursor.moveToNext()) {
//            val dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
//            val date = LocalDate.parse(dateString)
//
//            if (cursor.isFirst && date != finishDate) {
//                cursor.close()
//                db.close()
//                return null
//            }
//
////            if (previousDate != null && previousDate == date) {
////                // Skip duplicate dates.
////                continue
////            }
//
//            if (previousDate == null || previousDate == date.plusDays(1)) {
//                // Continuation of consecutive days or the first day.
//                previousDate = date
//            } else {
//                // Streak is broken.
//                cursor.close()
//                db.close()
//                return previousDate
//            }
//        }
//
//        cursor.close()
//        db.close()
//
//        // If we have a consecutive streak, return the finish date.
//        return previousDate
//    }

    fun updateWiD(id: Long, date: LocalDate, title: String, start: LocalTime, finish: LocalTime, duration: Duration) {
        val db = writableDatabase

        val updateQuery = "UPDATE $TABLE_NAME SET $COLUMN_DATE = ?, $COLUMN_TITLE = ?, $COLUMN_START = ?, $COLUMN_FINISH = ?, $COLUMN_DURATION = ? WHERE $COLUMN_ID = ?"
        val selectionArgs = arrayOf(date.toString(), title, start.toString(), finish.toString(), duration.toMillis(), id.toString())

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