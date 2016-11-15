package com.codemate.brewflop.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

object BrewFailureContract {
    val TABLE_NAME = "BrewFailures"
    val USER_ID = "user_id"
    val USER_NAME = "user_name"
    val FAILURE_TIMESTAMP = "failure_timestamp"
}

class BrewFailureDatabaseOpenHelper(private val context: Context) : ManagedSQLiteOpenHelper(context, "BrewFailureDB", null, 1) {
    companion object {
        private var instance: BrewFailureDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(context: Context): BrewFailureDatabaseOpenHelper {
            if (instance == null) {
                instance = BrewFailureDatabaseOpenHelper(context)
            }

            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(BrewFailureContract.TABLE_NAME, true,
                "_id" to INTEGER + PRIMARY_KEY + UNIQUE,
                BrewFailureContract.USER_ID to TEXT,
                BrewFailureContract.USER_NAME to TEXT,
                BrewFailureContract.FAILURE_TIMESTAMP to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}

val Context.brewFailureDB: SQLiteDatabase
    get() = BrewFailureDatabaseOpenHelper.getInstance(applicationContext).writableDatabase