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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.toOffset
import com.alexvanyo.composelife.model.CellState
import com.alexvanyo.composelife.model.CellWindow
import com.alexvanyo.composelife.model.GameOfLifeState
import com.alexvanyo.composelife.preferences.CurrentShape
import com.alexvanyo.composelife.ui.mobile.ComposeLifeTheme

@Suppress("LongParameterList")
@Composable
fun CanvasNonInteractableCells(
    gameOfLifeState: GameOfLifeState,
    scaledCellDpSize: Dp,
    cellWindow: CellWindow,
    shape: CurrentShape,
    pixelOffsetFromCenter: Offset,
    modifier: Modifier = Modifier,
) {
    val scaledCellPixelSize = with(LocalDensity.current) { scaledCellDpSize.toPx() }

    val aliveColor = ComposeLifeTheme.aliveCellColor
    val deadColor = ComposeLifeTheme.deadCellColor

    Canvas(
        modifier = modifier
            .graphicsLayer {
                this.translationX = -pixelOffsetFromCenter.x
                this.translationY = -pixelOffsetFromCenter.y
            }
            .requiredSize(
                scaledCellDpSize * cellWindow.width,
                scaledCellDpSize * cellWindow.height,
            ),
    ) {
        drawCells(
            cellState = gameOfLifeState.cellState,
            aliveColor = aliveColor,
            deadColor = deadColor,
            cellWindow = cellWindow,
            scaledCellPixelSize = scaledCellPixelSize,
            shape = shape,
        )
    }
}

@Suppress("LongParameterList")
fun DrawScope.drawCells(
    cellState: CellState,
    aliveColor: Color,
    deadColor: Color,
    cellWindow: CellWindow,
    scaledCellPixelSize: Float,
    shape: CurrentShape,
) {
    drawRect(
        color = deadColor,
    )

    cellState.getAliveCellsInWindow(cellWindow).forEach { cell ->
        when (shape) {
            is CurrentShape.RoundRectangle -> {
                drawRoundRect(
                    color = aliveColor,
                    topLeft = (cell - cellWindow.topLeft).toOffset() * scaledCellPixelSize +
                        Offset(
                            scaledCellPixelSize * (1f - shape.sizeFraction) / 2f,
                            scaledCellPixelSize * (1f - shape.sizeFraction) / 2f,
                        ),
                    size = Size(scaledCellPixelSize, scaledCellPixelSize) * shape.sizeFraction,
                    cornerRadius = CornerRadius(
                        scaledCellPixelSize * shape.sizeFraction * shape.cornerFraction,
                    ),
                )
            }
        }
    }
}
