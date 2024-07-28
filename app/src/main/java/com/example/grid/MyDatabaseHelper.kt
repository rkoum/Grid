package com.example.grid

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "GridDatabase.db"
        private const val DATABASE_VERSION = 1
    }

    private val dbPath: String = context.applicationInfo.dataDir + "/databases/"

    init {
        val dbFile = File(dbPath + DATABASE_NAME)
        if (!dbFile.exists()) {
            try {
                copyDatabase(context)
                Log.d("Debug", "Database copied to ${dbFile.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("Error", "Failed to copy database: ${e.message}")
            }
        } else {
            Log.d("Debug", "Database already exists at ${dbFile.absolutePath}")
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // No need to create tables if using an existing database
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if necessary
    }

    @Throws(IOException::class)
    private fun copyDatabase(context: Context) {
        val inputStream: InputStream = context.assets.open(DATABASE_NAME)
        val outputDir = File(dbPath)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val outputFile = File(outputDir, DATABASE_NAME)
        val outputStream: OutputStream = FileOutputStream(outputFile)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

    fun isRecordExists(query: String, selectionArgs: Array<String>? = null): Boolean {
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, selectionArgs)
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }
}