/*
 * Copyright 2017 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.koffeemate.ui.userselector

import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.data.network.models.User
import com.codemate.koffeemate.data.network.models.UserListResponse
import rx.Observable
import rx.Scheduler

open class LoadUsersUseCase(
        var slackApi: SlackApi,
        var subscriber: Scheduler,
        var observer: Scheduler
) {
    fun execute(): Observable<List<User>> {
        return slackApi.getUsers(BuildConfig.SLACK_AUTH_TOKEN)
                .subscribeOn(subscriber)
                .observeOn(observer)
                .map {
                    filterNonCompanyUsers(it).sortedBy {
                        it.profile.real_name
                    }
                }
    }

    private fun filterNonCompanyUsers(response: UserListResponse): List<User> {
        return response.members.filter {
                    !it.is_bot
                            // At Codemate, profiles starting with "Ext-" aren't employees,
                            // but customers instead: they don't hang out in the office.
                            && !it.profile.first_name.startsWith("Ext-")
                            && it.real_name != "slackbot"
                }
    }
}