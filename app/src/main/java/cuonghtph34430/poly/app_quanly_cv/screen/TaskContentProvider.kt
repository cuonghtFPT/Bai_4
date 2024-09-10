package cuonghtph34430.poly.app_quanly_cv.screen

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class TaskProvider : ContentProvider() {
    companion object {
        private const val AUTHORITY = "cuonghtph34430.poly.app_quanly_cv.provider"
        private const val BASE_PATH = "tasks"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$BASE_PATH")

        private const val TASKS = 1
        private const val TASK_ID = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, BASE_PATH, TASKS)
            addURI(AUTHORITY, "$BASE_PATH/#", TASK_ID)
        }
    }

    private lateinit var databaseHelper: TaskDatabase

    override fun onCreate(): Boolean {
        TaskDatabase(context!!).also { databaseHelper = it }
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val db = databaseHelper.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            TASKS -> db.query(TaskDatabase.TABLE_TASKS, projection, selection, selectionArgs, null, null, sortOrder)
            TASK_ID -> db.query(
                TaskDatabase.TABLE_TASKS, projection, "${TaskDatabase.COLUMN_ID}=?",
                arrayOf(uri.lastPathSegment), null, null, sortOrder
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            TASKS -> "vnd.android.cursor.dir/$BASE_PATH"
            TASK_ID -> "vnd.android.cursor.item/$BASE_PATH"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = databaseHelper.writableDatabase
        val id = db.insert(TaskDatabase.TABLE_TASKS, null, values)
        context!!.contentResolver.notifyChange(uri, null)
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = databaseHelper.writableDatabase
        val rowsDeleted = when (uriMatcher.match(uri)) {
            TASKS -> db.delete(TaskDatabase.TABLE_TASKS, selection, selectionArgs)
            TASK_ID -> db.delete(
                TaskDatabase.TABLE_TASKS, "${TaskDatabase.COLUMN_ID}=?",
                arrayOf(uri.lastPathSegment)
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db = databaseHelper.writableDatabase
        val rowsUpdated = when (uriMatcher.match(uri)) {
            TASKS -> db.update(TaskDatabase.TABLE_TASKS, values, selection, selectionArgs)
            TASK_ID -> db.update(
                TaskDatabase.TABLE_TASKS, values, "${TaskDatabase.COLUMN_ID}=?",
                arrayOf(uri.lastPathSegment)
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }
}