package andpact.project.wid.service

import andpact.project.wid.model.Diary
import andpact.project.wid.model.WiD
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDate
import java.time.LocalTime

class DiaryService(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "DiaryDatabase"
        private const val TABLE_NAME = "diary_table"

        private const val COLUMN_ID = "id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_DATE TEXT UNIQUE,
                $COLUMN_TITLE TEXT,
                $COLUMN_CONTENT TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun createDiary(diary: Diary): Long {
        Log.d("DiaryService", "createDiary executed")

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, diary.date.toString())
            put(COLUMN_TITLE, diary.title)
            put(COLUMN_CONTENT, diary.content)
        }
        val newRowId = db.insert(TABLE_NAME, null, values)

        // Close the database
        db.close()

        return newRowId
    }

    fun getDiaryCount(): Int {
        Log.d("DiaryService", "getDiaryCount executed")

        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()

        return count
    }

//    fun getDiaryById(diaryId: Long): Diary? {
//        val db = readableDatabase
//
//        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
//        val selectionArgs = arrayOf(diaryId.toString())
//
//        val cursor = db.rawQuery(selectQuery, selectionArgs)
//
//        var diary: Diary? = null
//
//        return with(cursor) {
//            if (moveToFirst()) {
//                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
//                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
//                val content = getString(getColumnIndexOrThrow(COLUMN_CONTENT))
//                // Convert the stored date String back to LocalDate
//                val storedDate = LocalDate.parse(getString(getColumnIndexOrThrow(COLUMN_DATE)))
//
//                diary = Diary(id, storedDate, title, content)
//            }
//
//            close()
//            diary
//        }
//    }

    fun readDiaryByDate(date: LocalDate): Diary? {
        Log.d("DiaryService", "getDiaryByDate executed")

        val db = readableDatabase

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ?"
        val selectionArgs = arrayOf(date.toString())
        val cursor = db.rawQuery(selectQuery, selectionArgs)

        var diary: Diary? = null

        with(cursor) {
            if (moveToFirst()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val storedDate = LocalDate.parse(getString(getColumnIndexOrThrow(COLUMN_DATE)))
                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val content = getString(getColumnIndexOrThrow(COLUMN_CONTENT))

                diary = Diary(id, storedDate, title, content)
            }
            close()
        }

        db.close()

        return diary
    }

//    fun getDiaryListByTitle(title: String): List<Diary> {
//        if (title.isBlank()) {
//            return emptyList()
//        }
//
//        val db = readableDatabase
//
//        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TITLE LIKE ? ORDER BY $COLUMN_DATE ASC"
//        val selectionArgs = arrayOf("%$title%")
//        val cursor = db.rawQuery(selectQuery, selectionArgs)
//
//        val diaryList = mutableListOf<Diary>()
//
//        with(cursor) {
//            while (moveToNext()) {
//                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
//                val storedDate = LocalDate.parse(getString(getColumnIndexOrThrow(COLUMN_DATE)))
//                val storedTitle = getString(getColumnIndexOrThrow(COLUMN_TITLE))
//                val storedContent = getString(getColumnIndexOrThrow(COLUMN_CONTENT))
//
//                val diary = Diary(id, storedDate, storedTitle, storedContent)
//                diaryList.add(diary)
//            }
//            close()
//        }
//
//        db.close()
//
//        return diaryList
//    }

//    fun getDiaryListByContent(content: String): List<Diary> {
//        if (content.isBlank()) {
//            return emptyList()
//        }
//
//        val db = readableDatabase
//
//        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_CONTENT LIKE ? ORDER BY $COLUMN_DATE ASC"
//        val selectionArgs = arrayOf("%$content%")
//        val cursor = db.rawQuery(selectQuery, selectionArgs)
//
//        val diaryList = mutableListOf<Diary>()
//
//        with(cursor) {
//            while (moveToNext()) {
//                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
//                val storedDate = LocalDate.parse(getString(getColumnIndexOrThrow(COLUMN_DATE)))
//                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
//                val storedContent = getString(getColumnIndexOrThrow(COLUMN_CONTENT))
//
//                val diary = Diary(id, storedDate, title, storedContent)
//                diaryList.add(diary)
//            }
//            close()
//        }
//
//        db.close()
//
//        return diaryList
//    }

    fun readDiaryListByTitleOrContent(searchText: String): List<Diary> {
        Log.d("DiaryService", "getDiaryListByTitleOrContent executed")

        if (searchText.isBlank()) {
            return emptyList()
        }

        val db = readableDatabase

        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TITLE LIKE ? OR $COLUMN_CONTENT LIKE ? ORDER BY $COLUMN_DATE ASC"
        val selectionArgs = arrayOf("%$searchText%", "%$searchText%")
        val cursor = db.rawQuery(selectQuery, selectionArgs)

        val diaryList = mutableListOf<Diary>()

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val storedDate = LocalDate.parse(getString(getColumnIndexOrThrow(COLUMN_DATE)))
                val storedTitle = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val storedContent = getString(getColumnIndexOrThrow(COLUMN_CONTENT))

                val diary = Diary(id, storedDate, storedTitle, storedContent)
                diaryList.add(diary)
            }
            close()
        }

        db.close()

        return diaryList
    }

//    fun getAllDiaries(): List<Diary> {
//        val db = readableDatabase
//
//        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DATE ASC"
//        val cursor = db.rawQuery(selectQuery, null)
//
//        val diaries = mutableListOf<Diary>()
//
//        with(cursor) {
//            while (moveToNext()) {
//                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
//                val date = LocalDate.parse(getString(getColumnIndexOrThrow(COLUMN_DATE)))
//                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
//                val content = getString(getColumnIndexOrThrow(COLUMN_CONTENT))
//
//                val diary = Diary(id, date, title, content)
//                diaries.add(diary)
//            }
//            close()
//        }
//
//        db.close()
//
//        return diaries
//    }

    fun updateDiary(id: Long, date: LocalDate, title: String, content: String): Int {
        Log.d("DiaryService", "updateDiary executed")

        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_DATE, date.toString())
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
        }

        val rowID = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))

        db.close()

        return rowID
    }

//    fun deleteDiary(id: Long): Int {
//        val db = writableDatabase
//
//        val rowID = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
//
//        db.close()
//
//        return rowID
//    }
}