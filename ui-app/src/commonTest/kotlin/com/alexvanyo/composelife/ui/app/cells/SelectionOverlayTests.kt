/*
 * Copyright 2023 The Android Open Source Project
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

package com.alexvanyo.composelife.ui.app.cells

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.dragAndDrop
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import com.alexvanyo.composelife.geometry.toPx
import com.alexvanyo.composelife.kmpandroidrunner.KmpAndroidJUnit4
import com.alexvanyo.composelife.parameterizedstring.ParameterizedString
import com.alexvanyo.composelife.parameterizedstring.parameterizedStringResolver
import com.alexvanyo.composelife.ui.app.resources.SelectingBoxHandle
import com.alexvanyo.composelife.ui.app.resources.Strings
import com.alexvanyo.composelife.ui.app.util.isAndroid
import org.junit.Assume.assumeTrue
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
@RunWith(KmpAndroidJUnit4::class)
class SelectionOverlayTests {

    @Test
    fun no_selection_is_displayed_correctly() = runComposeUiTest {
        lateinit var resolver: (ParameterizedString) -> String

        setContent {
            resolver = parameterizedStringResolver()

            SelectionOverlay(
                selectionState = SelectionState.NoSelection,
                setSelectionState = {},
                scaledCellDpSize = 50.dp,
                cellWindow = IntRect(
                    IntOffset(0, 0),
                    IntOffset(8, 8),
                ),
            )
        }

        onNodeWithContentDescription(
            resolver(Strings.SelectingBoxHandle(0, 0)).substringBefore(":"),
        )
            .assertDoesNotExist()
    }

    @Test
    fun selecting_box_is_displayed_correctly() = runComposeUiTest {
        lateinit var resolver: (ParameterizedString) -> String

        setContent {
            resolver = parameterizedStringResolver()

            SelectionOverlay(
                selectionState = SelectionState.SelectingBox(
                    topLeft = IntOffset(1, 1),
                    width = 2,
                    height = 3,
                ),
                setSelectionState = {},
                scaledCellDpSize = 50.dp,
                cellWindow = IntRect(
                    IntOffset(0, 0),
                    IntOffset(8, 8),
                ),
            )
        }

        onNodeWithContentDescription(
            resolver(Strings.SelectingBoxHandle(1, 1)),
        )
            .assertIsDisplayed()

        onNodeWithContentDescription(
            resolver(Strings.SelectingBoxHandle(3, 1)),
        )
            .assertIsDisplayed()

        onNodeWithContentDescription(
            resolver(Strings.SelectingBoxHandle(1, 4)),
        )
            .assertIsDisplayed()

        onNodeWithContentDescription(
            resolver(Strings.SelectingBoxHandle(3, 4)),
        )
            .assertIsDisplayed()
    }

    @Test
    fun dragging_selecting_box_is_displayed_correctly() = runComposeUiTest {
        // TODO: This test tends to deadlock on desktop
        assumeTrue(isAndroid())

        lateinit var resolver: (ParameterizedString) -> String

        val mutableSelectionStateHolder = MutableSelectionStateHolder(
            SelectionState.SelectingBox(
                topLeft = IntOffset(2, 2),
                width = 2,
                height = 3,
            ),
        )

        setContent {
            resolver = parameterizedStringResolver()

            SelectionOverlay(
                selectionState = mutableSelectionStateHolder.selectionState,
                setSelectionState = { mutableSelectionStateHolder.selectionState = it },
                scaledCellDpSize = 50.dp,
                cellWindow = IntRect(
                    IntOffset(0, 0),
                    IntOffset(8, 8),
                ),
            )
        }

        onNodeWithContentDescription(
            resolver(Strings.SelectingBoxHandle(2, 2)),
        )
            .performMouseInput {
                val start = center
                val end = center + with(density) { DpOffset(200.dp, 200.dp).toPx() }
                dragAndDrop(start, end, 1_000)
            }

        assertEquals(
            SelectionState.SelectingBox(
                topLeft = IntOffset(4, 5),
                width = 2,
                height = 1,
            ),
            mutableSelectionStateHolder.selectionState,
        )
    }
}