package com.codemate.brewflop.data.local

import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import com.codemate.brewflop.data.network.model.User
import org.jetbrains.anko.db.SqlOrderDirection
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

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
                BrewFailureContract.USER_NAME to user.profile.realName,
                BrewFailureContract.FAILURE_TIMESTAMP to System.currentTimeMillis()
        )
    }

    fun getUserWithMostFailures(onCompleteListener: (String) -> Unit){
        val userIdColumn = BrewFailureContract.USER_ID
        val accidentCountColumn = "accident_count"

        db.select(BrewFailureContract.TABLE_NAME,
                userIdColumn,
                "count($userIdColumn) AS $accidentCountColumn"
        ).groupBy(userIdColumn)
                .orderBy("count($userIdColumn)", SqlOrderDirection.DESC)
                .orderBy(BrewFailureContract.FAILURE_TIMESTAMP, SqlOrderDirection.ASC)
                .exec {
                    if (moveToFirst()) {
                        val userId = getString(getColumnIndexOrThrow(userIdColumn))
                        onCompleteListener(userId)
                    } else {
                        onCompleteListener("")
                    }
                }
    }
}