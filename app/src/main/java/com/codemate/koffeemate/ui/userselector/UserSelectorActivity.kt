package com.codemate.koffeemate.ui.userselector

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.codemate.koffeemate.R
import com.codemate.koffeemate.data.network.models.User

class UserSelectorActivity : AppCompatActivity(), UserSelectorFragment.UserSelectListener {
    companion object {
        val RESULT_USER_ID = "user_id"
        val RESULT_USER_FULL_NAME = "user_full_name"
        val RESULT_USER_FIRST_NAME = "user_first_name"
        val RESULT_USER_PROFILE_LARGEST_PIC_URL = "user_profile_largest_pic_url"
        val RESULT_USER_PROFILE_SMALLEST_PIC_URL = "user_profile_smallest_pic_url"
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
        intent.putExtra(RESULT_USER_ID, user.id)
        intent.putExtra(RESULT_USER_FULL_NAME, user.profile.real_name)
        intent.putExtra(RESULT_USER_FIRST_NAME, user.profile.first_name)
        intent.putExtra(RESULT_USER_PROFILE_LARGEST_PIC_URL, user.profile.largestAvailableImage)
        intent.putExtra(RESULT_USER_PROFILE_SMALLEST_PIC_URL, user.profile.smallestAvailableImage)

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