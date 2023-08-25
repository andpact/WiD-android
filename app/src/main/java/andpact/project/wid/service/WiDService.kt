package andpact.project.wid.service

import andpact.project.wid.model.WiD
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

    fun insertWiD(wid: WiD) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, wid.id)
            put(COLUMN_DATE, wid.date.toString())
            put(COLUMN_TITLE, wid.title)
            put(COLUMN_START, wid.start.toString())
            put(COLUMN_FINISH, wid.finish.toString())
            put(COLUMN_DURATION, wid.duration.toString())
            put(COLUMN_DETAIL, wid.detail)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}