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

package com.alexvanyo.composelife.ui.cells

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import com.alexvanyo.composelife.preferences.CurrentShape
import com.alexvanyo.composelife.ui.mobile.ComposeLifeTheme

/**
 * An individual cell that is interactable.
 *
 * This cell has no inherent size, which must be specified via [modifier].
 *
 * The cell is alive if [isAlive] is true, and [onValueChange] will be called when the living state should be toggled.
 */
@Suppress("LongParameterList")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InteractableCell(
    // noinspection ComposeModifierWithoutDefault
    modifier: Modifier,
    drawState: DrawState,
    shape: CurrentShape,
    contentDescription: String,
    onValueChange: (isAlive: Boolean) -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    val aliveColor = ComposeLifeTheme.aliveCellColor
    val pendingAliveColor = ComposeLifeTheme.pendingAliveCellColor
    val pendingDeadColor = ComposeLifeTheme.pendingDeadCellColor

    val value = when (drawState) {
        DrawState.Alive, DrawState.PendingAlive -> true
        DrawState.Dead, DrawState.PendingDead -> false
    }

    val state = ToggleableState(value = value)

    val onClick = { onValueChange(!value) }

    Canvas(
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
            }
            .combinedClickable(
                role = Role.Switch,
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .semantics {
                toggleableState = state
            },
    ) {
        val drawColor = when (drawState) {
            DrawState.Alive -> aliveColor
            DrawState.PendingAlive -> pendingAliveColor
            DrawState.Dead -> Color.Unspecified
            DrawState.PendingDead -> pendingDeadColor
        }
        if (drawColor.isSpecified) {
            when (shape) {
                is CurrentShape.RoundRectangle -> {
                    drawRoundRect(
                        color = drawColor,
                        topLeft = Offset(
                            size.width * (1f - shape.sizeFraction) / 2f,
                            size.height * (1f - shape.sizeFraction) / 2f,
                        ),
                        size = size * shape.sizeFraction,
                        cornerRadius = CornerRadius(
                            size.width * shape.sizeFraction * shape.cornerFraction,
                        ),
                    )
                }
            }
        }
    }
}

sealed interface DrawState {
    data object Alive : DrawState
    data object Dead : DrawState
    data object PendingAlive : DrawState
    data object PendingDead : DrawState
}
