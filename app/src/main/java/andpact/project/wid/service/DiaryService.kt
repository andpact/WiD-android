package andpact.project.wid.service

import andpact.project.wid.model.Diary
import andpact.project.wid.model.WiD
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.time.LocalTime

class DiaryService(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "DiaryDatabase"
        private const val TABLE_NAME = "diary_table"

        private const val COLUMN_ID = "id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_DATE TEXT,
                $COLUMN_CONTENT TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 다이어리 생성
    fun createDiary(diary: Diary): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, diary.date.toString()) // Convert LocalDate to String
            put(COLUMN_CONTENT, diary.content)
        }
        val newRowId = db.insert(TABLE_NAME, null, values)

        // Close the database
        db.close()

        return newRowId
    }

    // 날짜로 다이어리 조회
    fun getDiaryByDate(date: LocalDate): Diary? {
        val db = readableDatabase

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ?"
        val selectionArgs = arrayOf(date.toString())

        val cursor = db.rawQuery(selectQuery, selectionArgs)

        var diary: Diary? = null

        return with(cursor) {
            if (moveToFirst()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val content = getString(getColumnIndexOrThrow(COLUMN_CONTENT))
                // Convert the stored date String back to LocalDate
                val storedDate = LocalDate.parse(getString(getColumnIndexOrThrow(COLUMN_DATE)))

                diary = Diary(id, storedDate, content)
            }

            close()
            diary
        }
    }

    // 날짜와 컨텐츠를 사용하여 다이어리 갱신
    fun updateDiary(id: Long, date: String, content: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_CONTENT, content)
        }

        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // ID를 사용하여 다이어리 삭제
    fun deleteDiary(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}