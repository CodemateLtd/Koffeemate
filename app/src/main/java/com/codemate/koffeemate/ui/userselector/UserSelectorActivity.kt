package com.codemate.koffeemate.ui.userselector

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.models.User

class UserSelectorActivity : AppCompatActivity(), UserSelectorFragment.UserSelectListener {
    companion object {
        val RESULT_USER = "user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, UserSelectorFragment.newInstance())
                .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.prompt_select_guilty_person)
    }

    override fun onUserSelected(user: User) {
        val intent = Intent()
        intent.putExtra(RESULT_USER, user)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}