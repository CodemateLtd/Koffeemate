package com.codemate.brewflop.data.local

import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import com.codemate.brewflop.data.network.model.User
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.doAsync

class BrewFailureLogger(private val db: SQLiteDatabase) {
    fun getFailureCountForUser(user: User): Long {
        return DatabaseUtils.queryNumEntries(
                db,
                BrewFailureContract.TABLE_NAME,
                BrewFailureContract.USER_ID + " = ?",
                arrayOf(user.id)
        )
    }

    fun incrementFailureCountForUser(user: User) {
        db.insert(BrewFailureContract.TABLE_NAME,
                BrewFailureContract.USER_ID to user.id,
                BrewFailureContract.FAILURE_TIMESTAMP to System.currentTimeMillis()
        )
    }
}