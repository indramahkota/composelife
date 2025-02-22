/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexvanyo.composelife.serialization

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.alexvanyo.composelife.kmpandroidrunner.KmpAndroidJUnit4
import com.alexvanyo.composelife.kmpstaterestorationtester.KmpStateRestorationTester
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalTestApi::class)
@RunWith(KmpAndroidJUnit4::class)
class UuidSaverTests {

    @Test
    fun uuid_saver_is_correct() = runComposeUiTest {
        val stateRestorationTester = KmpStateRestorationTester(this)

        var value: Uuid? = null

        stateRestorationTester.setContent {
            value = rememberSaveable(saver = uuidSaver) { Uuid.random() }
        }

        val initialValue = value
        value = null

        stateRestorationTester.emulateStateRestore()

        val restoredValue = value

        assertEquals(initialValue, restoredValue)
    }
}
