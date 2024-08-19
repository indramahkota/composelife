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
@file:Suppress("MatchingDeclarationName")

package com.alexvanyo.composelife.ui.app.action.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.alexvanyo.composelife.model.GameOfLifeState
import com.alexvanyo.composelife.model.toCellState
import com.alexvanyo.composelife.sessionvalue.SessionValue
import com.alexvanyo.composelife.ui.cells.CellWindowInjectEntryPoint
import com.alexvanyo.composelife.ui.cells.CellWindowInteractionState
import com.alexvanyo.composelife.ui.cells.CellWindowLocalEntryPoint
import com.alexvanyo.composelife.ui.cells.CellWindowState
import com.alexvanyo.composelife.ui.cells.ImmutableCellWindow
import com.alexvanyo.composelife.ui.cells.SelectionState
import com.alexvanyo.composelife.ui.cells.ViewportInteractionConfig
import com.benasher44.uuid.uuid4

interface CellStatePreviewUiInjectEntryPoint :
    CellWindowInjectEntryPoint

interface CellStatePreviewUiLocalEntryPoint :
    CellWindowLocalEntryPoint

context(CellStatePreviewUiInjectEntryPoint, CellStatePreviewUiLocalEntryPoint)
@Suppress("LongMethod")
@Composable
fun CellStatePreviewUi(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        var selectionSessionState by remember {
            mutableStateOf<SessionValue<SelectionState>>(
                SessionValue(
                    sessionId = uuid4(),
                    valueId = uuid4(),
                    value = SelectionState.NoSelection,
                ),
            )
        }
        ImmutableCellWindow(
            gameOfLifeState = GameOfLifeState(
                """
                |.....
                |..O..
                |...O.
                |.OOO.
                |.....
                """.toCellState(),
            ),
            modifier = Modifier.size(96.dp).clipToBounds(),
            cellWindowInteractionState = object : CellWindowInteractionState {
                override val viewportInteractionConfig = ViewportInteractionConfig.Fixed(
                    CellWindowState(
                        offset = Offset(2f, 2f),
                        scale = 1f,
                    ),
                )

                override var selectionSessionState: SessionValue<SelectionState>
                    get() = selectionSessionState
                    set(value) {
                        selectionSessionState = value
                    }
            },
            cellDpSize = 96.dp / 5,
            inOverlay = true,
        )
    }
}
