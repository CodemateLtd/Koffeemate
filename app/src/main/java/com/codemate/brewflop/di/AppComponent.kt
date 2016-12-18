package com.codemate.brewflop.di

import com.codemate.brewflop.ui.main.MainActivity
import com.codemate.brewflop.ui.userselector.UserSelectorActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        PersistenceModule::class,
        NetModule::class)
)
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(userSelectorActivity: UserSelectorActivity)
}