package com.codemate.koffeemate.data.local

import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.rules.ExternalResource
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * A JUnit Rule that sets up a test Realm database before each test,
 * clears all data from it, and deletes the whole database file alltogether
 * after the test completes.
 *
 * Usage:
 *
 * @Rule @JvmField
 * val realmRule: RealmTestRule = RealmTestRule()
 *
 * The above assumes that your application logic that uses Realm always uses
 * the default instance. Meaning that your code calls Realm.getDefaultInstance()
 * and uses that for all database logic.
 */
class RealmTestRule : ExternalResource() {
    val testConfig: RealmConfiguration = RealmConfiguration.Builder()
            .name("test.realm")
            .build()

    override fun before() {
        Realm.setDefaultConfiguration(testConfig)

        with (Realm.getDefaultInstance()) {
            executeTransaction(Realm::deleteAll)
            close()
        }
    }

    override fun apply(base: Statement?, description: Description?): Statement {
        return super.apply(base, description)
    }

    override fun after() {
        Realm.deleteRealm(testConfig)
    }
}