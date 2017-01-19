package com.codemate.koffeemate.ui.userselector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.codemate.koffeemate.KoffeemateApp
import com.codemate.koffeemate.R
import com.codemate.koffeemate.common.BasicListItemAnimator
import com.codemate.koffeemate.data.network.models.User
import kotlinx.android.synthetic.main.activity_user_selector.*
import kotlinx.android.synthetic.main.activity_user_selector.view.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

class UserSelectorActivity : AppCompatActivity(), UserSelectorView {
    companion object {
        val RESULT_USER_ID = "user_id"
        val RESULT_USER_FULL_NAME = "user_full_name"
        val RESULT_USER_FIRST_NAME = "user_first_name"
        val RESULT_USER_PROFILE_LARGEST_PIC_URL = "user_profile_largest_pic_url"
        val RESULT_USER_PROFILE_SMALLEST_PIC_URL = "user_profile_smallest_pic_url"
    }

    private lateinit var userSelectorAdapter: UserSelectorAdapter

    @Inject
    lateinit var presenter: UserSelectorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selector)
        KoffeemateApp.appComponent.inject(this)

        setUpUserRecycler()

        presenter.attachView(this)
        presenter.loadUsers()

        errorLayout.tryAgain.onClick {
            presenter.loadUsers()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.prompt_select_person_below)
    }

    private fun setUpUserRecycler() {
        userSelectorAdapter = UserSelectorAdapter { user ->
            val intent = Intent()
            intent.putExtra(RESULT_USER_ID, user.id)
            intent.putExtra(RESULT_USER_FULL_NAME, user.profile.real_name)
            intent.putExtra(RESULT_USER_FIRST_NAME, user.profile.first_name)
            intent.putExtra(RESULT_USER_PROFILE_LARGEST_PIC_URL, user.profile.largestAvailableImage)
            intent.putExtra(RESULT_USER_PROFILE_SMALLEST_PIC_URL, user.profile.smallestAvailableImage)

            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        userRecycler.adapter = userSelectorAdapter
        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.itemAnimator = BasicListItemAnimator(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }

    override fun showError() {
        progress.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    override fun showUsers(users: List<User>) {
        userSelectorAdapter.setItems(users)
    }
}