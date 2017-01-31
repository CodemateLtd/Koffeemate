/*
 * Copyright 2014 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm;

import static junit.framework.Assert.fail;

public class TestHelper {
    /**
     * Wait and check if all tasks in BaseRealm.asyncTaskExecutor can be finished in 5 seconds, otherwise fail the test.
     */
    public static void waitRealmThreadExecutorFinish() {
        int counter = 50;
        while (counter > 0) {
            if (BaseRealm.asyncTaskExecutor.getActiveCount() == 0) {
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
            counter--;
        }
        fail("'BaseRealm.asyncTaskExecutor' is not finished in " + counter/10 + " seconds");
    }
}