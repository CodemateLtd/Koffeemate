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
import com.codemate.koffeemate.data.local.UserRepository
import com.codemate.koffeemate.data.network.SlackApi
import com.codemate.koffeemate.data.network.models.User
import com.codemate.koffeemate.data.network.models.isFreshEnough
import rx.Observable
import rx.Scheduler
import java.util.concurrent.TimeUnit

open class LoadUsersUseCase(
        var userRepository: UserRepository,
        var slackApi: SlackApi,
        var subscriber: Scheduler,
        var observer: Scheduler
) {
    val MAX_CACHE_STALENESS = TimeUnit.HOURS.toMillis(12)

    fun execute(): Observable<List<User>> {
        val currentTime = System.currentTimeMillis()
        val cachedUsers = Observable.just(userRepository.getAll())
        val networkUsers = slackApi.getUsers(BuildConfig.SLACK_AUTH_TOKEN)
                .flatMap { userResponse ->
                    val usersWithTimestamp = userResponse.members.toMutableList()
                    usersWithTimestamp.forEach {
                        it.last_updated = currentTime
                    }

                    Observable.just(usersWithTimestamp)
                }
                .subscribeOn(subscriber)
                .observeOn(observer)
                .map {
                    filterNonCompanyUsers(it).sortedBy {
                        it.profile.real_name
                    }
                }
                .doOnNext { userRepository.addAll(it) }

        return Observable
                .concat(cachedUsers, networkUsers)
                .first {
                    it.isNotEmpty() && it.isFreshEnough(MAX_CACHE_STALENESS)
                }
    }

    private fun filterNonCompanyUsers(response: List<User>): List<User> {
        return response.filter {
                    !it.is_bot
                            // At Codemate, profiles starting with "Ext-" aren't employees,
                            // but customers instead: they don't hang out in the office.
                            && !it.profile.first_name.toLowerCase().startsWith("ext-")
                            && it.real_name != "slackbot"
                            && it.deleted == false
                }
    }
}