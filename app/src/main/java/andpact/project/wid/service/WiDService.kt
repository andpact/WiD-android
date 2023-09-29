package andpact.project.wid.service

import andpact.project.wid.model.WiD
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDate
import java.time.LocalTime

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

    fun readWiDListByDate(date: LocalDate): List<WiD> {
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

//    fun updateWiDStartAndFinish(id: Long, newStartTime: LocalTime, newFinishTime: LocalTime) {
//        val db = writableDatabase
//
//        val updateQuery = "UPDATE $TABLE_NAME SET $COLUMN_START = ?, $COLUMN_FINISH = ? WHERE $COLUMN_ID = ?"
//        val selectionArgs = arrayOf(newStartTime.toString(), newFinishTime.toString(), id.toString())
//
//        db.execSQL(updateQuery, selectionArgs)
//
//        db.close()
//    }
//
//    fun updateWiDDetail(id: Long, newDetail: String) {
//        val db = writableDatabase
//
//        val updateQuery = "UPDATE $TABLE_NAME SET $COLUMN_DETAIL = ? WHERE $COLUMN_ID = ?"
//        val selectionArgs = arrayOf(newDetail, id.toString())
//
//        db.execSQL(updateQuery, selectionArgs)
//
//        db.close()
//    }

    fun updateWiD(id: Long, newStartTime: LocalTime, newFinishTime: LocalTime, newDetail: String) {
        val db = writableDatabase

        val updateQuery = "UPDATE $TABLE_NAME SET $COLUMN_START = ?, $COLUMN_FINISH = ?, $COLUMN_DETAIL = ? WHERE $COLUMN_ID = ?"
        val selectionArgs = arrayOf(newStartTime.toString(), newFinishTime.toString(), newDetail, id.toString())

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