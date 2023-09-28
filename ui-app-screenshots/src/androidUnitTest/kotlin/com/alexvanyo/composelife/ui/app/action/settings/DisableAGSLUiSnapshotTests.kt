/*
 * Copyright 2022 The Android Open Source Project
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

package com.alexvanyo.composelife.ui.app.action.settings

import com.alexvanyo.composelife.ui.app.util.BaseRoborazziTest
import com.alexvanyo.composelife.ui.app.util.RoborazziParameterization
import kotlin.test.Test

class DisableAGSLUiSnapshotTests(
    roborazziParameterization: RoborazziParameterization,
) : BaseRoborazziTest(roborazziParameterization) {

    @Test
    fun disable_agsl_ui_disabled_preview() {
        snapshot {
            DisableAGSLUiDisabledPreview()
        }
    }

    @Test
    fun disable_agsl_ui_enabled_preview() {
        snapshot {
            DisableAGSLUiEnabledPreview()
        }
    }
}
