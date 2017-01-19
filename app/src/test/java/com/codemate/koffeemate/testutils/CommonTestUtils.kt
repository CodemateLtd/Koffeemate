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

package com.codemate.koffeemate.testutils

import com.codemate.koffeemate.data.network.models.Profile
import com.codemate.koffeemate.data.network.models.User
import java.io.File

fun Any.getResourceFile(path: String): File {
    return File(javaClass.classLoader.getResource(path).file)
}

fun fakeUser() = User().apply {
    id = "abc123"
    profile = Profile()
    profile.first_name = "Jorma"
    profile.real_name = "Jorma"
}