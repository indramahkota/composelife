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

package com.alexvanyo.composelife.ui.app.cells

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.HistoricalChange
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.util.packInts
import androidx.compose.ui.util.unpackInt1
import androidx.compose.ui.util.unpackInt2
import com.alexvanyo.composelife.geometry.LineSegmentPath
import com.alexvanyo.composelife.geometry.cellIntersections
import com.alexvanyo.composelife.geometry.containedPoints
import com.alexvanyo.composelife.model.GameOfLifeState
import com.alexvanyo.composelife.model.MutableGameOfLifeState
import com.alexvanyo.composelife.model.setCellState
import com.alexvanyo.composelife.parameterizedstring.parameterizedStringResolver
import com.alexvanyo.composelife.parameterizedstring.parameterizedStringResource
import com.alexvanyo.composelife.preferences.ToolConfig
import com.alexvanyo.composelife.preferences.di.LoadedComposeLifePreferencesProvider
import com.alexvanyo.composelife.ui.app.resources.InteractableCellContentDescription
import com.alexvanyo.composelife.ui.app.resources.SelectingBoxHandle
import com.alexvanyo.composelife.ui.app.resources.Strings
import com.alexvanyo.composelife.ui.util.AnchoredDraggable2DState
import com.alexvanyo.composelife.ui.util.DraggableAnchors2D
import com.alexvanyo.composelife.ui.util.anchoredDraggable2D
import com.alexvanyo.composelife.ui.util.snapTo
import com.alexvanyo.composelife.ui.util.uuidSaver
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

interface InteractableCellsLocalEntryPoint : LoadedComposeLifePreferencesProvider

/**
 * A fixed size composable that displays a specific [cellWindow] into the given [GameOfLifeState].
 *
 * The [GameOfLifeState] is interactable, so each cell is displayed by a unique [InteractableCell].
 */
context(InteractableCellsLocalEntryPoint)
@Suppress("LongParameterList", "LongMethod")
@Composable
fun InteractableCells(
    gameOfLifeState: MutableGameOfLifeState,
    selectionStateHolder: MutableSelectionStateHolder,
    scaledCellDpSize: Dp,
    cellWindow: IntRect,
    pixelOffsetFromCenter: Offset,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .graphicsLayer {
                this.translationX = -pixelOffsetFromCenter.x
                this.translationY = -pixelOffsetFromCenter.y
            }
            .requiredSize(
                scaledCellDpSize * (cellWindow.width + 1),
                scaledCellDpSize * (cellWindow.height + 1),
            ),
    ) {
        val scaledCellPixelSize = with(LocalDensity.current) { scaledCellDpSize.toPx() }

        val numColumns = cellWindow.width + 1
        val numRows = cellWindow.height + 1

        val pendingCellChanges = remember { mutableStateMapOf<IntOffset, Boolean>() }

        val drawingPointerTypes =
            setOfNotNull(
                PointerType.Touch.takeIf { preferences.touchToolConfig == ToolConfig.Draw },
                PointerType.Stylus.takeIf { preferences.stylusToolConfig == ToolConfig.Draw },
                PointerType.Mouse.takeIf { preferences.mouseToolConfig == ToolConfig.Draw },
            )
        val erasingPointerTypes =
            setOfNotNull(
                PointerType.Touch.takeIf { preferences.touchToolConfig == ToolConfig.Erase },
                PointerType.Stylus.takeIf { preferences.stylusToolConfig == ToolConfig.Erase },
                PointerType.Mouse.takeIf { preferences.mouseToolConfig == ToolConfig.Erase },
            )

        var selectionState by selectionStateHolder::selectionState

        Box(
            Modifier
                .requiredSize(
                    scaledCellDpSize * numColumns,
                    scaledCellDpSize * numRows,
                )
                .testTag("CellCanvas")
                .drawingCellInput(
                    drawingPointerTypes = drawingPointerTypes,
                    erasingPointerTypes = erasingPointerTypes,
                    gameOfLifeState = gameOfLifeState,
                    pendingCellChanges = pendingCellChanges,
                    scaledCellPixelSize = scaledCellPixelSize,
                    cellWindow = cellWindow,
                ),
        ) {
            Layout(
                modifier = Modifier.fillMaxSize(),
                content = {
                    cellWindow.containedPoints().forEach { cell ->
                        key(cell) {
                            val isAliveInState = cell in gameOfLifeState.cellState.aliveCells
                            InteractableCell(
                                modifier = Modifier
                                    .size(scaledCellDpSize),
                                drawState = when (pendingCellChanges[cell]) {
                                    false -> if (isAliveInState) DrawState.PendingDead else DrawState.Dead
                                    true -> if (isAliveInState) DrawState.Alive else DrawState.PendingAlive
                                    null -> if (isAliveInState) DrawState.Alive else DrawState.Dead
                                },
                                shape = preferences.currentShape,
                                contentDescription = parameterizedStringResource(
                                    Strings.InteractableCellContentDescription(
                                        x = cell.x,
                                        y = cell.y,
                                    ),
                                ),
                                onValueChange = { isAlive ->
                                    gameOfLifeState.setCellState(
                                        cellCoordinate = cell,
                                        isAlive = isAlive,
                                    )
                                },
                                onLongClick = {
                                    selectionState = SelectionState.SelectingBox(
                                        topLeft = cell,
                                        width = 1,
                                        height = 1,
                                    )
                                },
                            )
                        }
                    }
                },
                measurePolicy = { measurables, _ ->
                    val placeables = measurables.map { it.measure(Constraints()) }

                    layout(
                        (numColumns * scaledCellPixelSize).roundToInt(),
                        (numRows * scaledCellPixelSize).roundToInt(),
                    ) {
                        placeables.forEachIndexed { index, placeable ->
                            val rowIndex = index / numColumns
                            val columnIndex = index % numColumns
                            placeable.place(
                                (columnIndex * scaledCellPixelSize).roundToInt(),
                                (rowIndex * scaledCellPixelSize).roundToInt(),
                            )
                        }
                    }
                },
            )

            SelectionOverlay(
                selectionState = selectionState,
                setSelectionState = { selectionState = it },
                scaledCellPixelSize = scaledCellPixelSize,
                cellWindow = cellWindow,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * The overlay based on the [selectionState].
 */
@Composable
private fun SelectionOverlay(
    selectionState: SelectionState,
    setSelectionState: (SelectionState) -> Unit,
    scaledCellPixelSize: Float,
    cellWindow: IntRect,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = selectionState,
        transitionSpec = {
            fadeIn(animationSpec = tween(220, delayMillis = 90))
                .togetherWith(fadeOut(animationSpec = tween(90)))
        },
        contentAlignment = Alignment.Center,
        contentKey = {
            when (it) {
                SelectionState.NoSelection -> 0
                is SelectionState.SelectingBox -> 1
                is SelectionState.Selection -> 2
            }
        },
        modifier = modifier,
    ) { targetSelectionState ->
        when (targetSelectionState) {
            SelectionState.NoSelection -> {
                Spacer(Modifier.fillMaxSize())
            }
            is SelectionState.SelectingBox -> {
                SelectingBoxOverlay(
                    selectionState = targetSelectionState,
                    scaledCellPixelSize = scaledCellPixelSize,
                    cellWindow = cellWindow,
                    setSelectionState = setSelectionState,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is SelectionState.Selection -> {
                Spacer(Modifier.fillMaxSize())
            }
        }
    }
}

/**
 * The overlay for a [SelectionState.SelectingBox] [selectionState].
 *
 * This includes the selection box, along with 4 handles draggable in two dimensions to allow changing the selecting
 * box bounds.
 */
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
private fun SelectingBoxOverlay(
    selectionState: SelectionState.SelectingBox,
    setSelectionState: (SelectionState) -> Unit,
    scaledCellPixelSize: Float,
    cellWindow: IntRect,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        /**
         * The key for the editing session, to allow wiping state on an external change.
         */
        var editingSessionKey by rememberSaveable(stateSaver = uuidSaver) {
            mutableStateOf(UUID.randomUUID())
        }

        /**
         * The known [SelectionState.SelectingBox] for valid values that we are setting.
         */
        var knownSelectionState: SelectionState.SelectingBox by remember {
            mutableStateOf(selectionState)
        }

        /**
         * If `true`, the [selectionState] was updated externally, and not via our own updates
         * (which would have updated [knownSelectionState])
         */
        val didValueUpdateOutOfBand = knownSelectionState != selectionState

        // If a value update occurred out of band, then update our editing session
        if (didValueUpdateOutOfBand) {
            // Update the editing session key
            editingSessionKey = UUID.randomUUID()
            knownSelectionState = selectionState
        }

        val initialHandleAOffset = selectionState.topLeft
        val initialHandleBOffset = selectionState.topLeft +
            IntOffset(selectionState.width, 0)
        val initialHandleCOffset = selectionState.topLeft +
            IntOffset(selectionState.width, selectionState.height)
        val initialHandleDOffset = selectionState.topLeft +
            IntOffset(0, selectionState.height)

        val initialHandles = listOf(
            initialHandleAOffset,
            initialHandleBOffset,
            initialHandleCOffset,
            initialHandleDOffset,
        )

        val handleAnchors = remember(scaledCellPixelSize, cellWindow) {
            object : DraggableAnchors2D<IntOffset> {
                override fun positionOf(value: IntOffset): Offset =
                    (value.toOffset() - cellWindow.topLeft.toOffset()) * scaledCellPixelSize

                override fun hasAnchorFor(value: IntOffset): Boolean = true
                override fun closestAnchor(position: Offset): IntOffset =
                    (position / scaledCellPixelSize).round() + cellWindow.topLeft

                override val size: Int = Int.MAX_VALUE
            }
        }

        val confirmValueChangeStates = List(initialHandles.size) { index ->
            key(index) {
                remember { mutableStateOf({ _: IntOffset -> true }) }
            }
        }

        val coroutineScope = rememberCoroutineScope()

        @Stable
        class HandleState(
            val state: AnchoredDraggable2DState<IntOffset>,
        ) {
            var reentrancyCount by mutableStateOf(0)
        }

        @Stable
        class SelectionDraggableHandleState(
            val state: HandleState,
            val horizontalPairState: HandleState,
            val verticalPairState: HandleState,
            val oppositeCornerState: HandleState,
        ) {
            val confirmValueChange: (IntOffset) -> Boolean = { intOffset ->
                if (state.reentrancyCount == 0) {
                    val minX = min(intOffset.x, oppositeCornerState.state.targetValue.x)
                    val maxX = max(intOffset.x, oppositeCornerState.state.targetValue.x)
                    val minY = min(intOffset.y, oppositeCornerState.state.targetValue.y)
                    val maxY = max(intOffset.y, oppositeCornerState.state.targetValue.y)

                    val newSelectionState = SelectionState.SelectingBox(
                        IntOffset(minX, minY),
                        width = maxX - minX,
                        height = maxY - minY,
                    )

                    setSelectionState(newSelectionState)
                    knownSelectionState = newSelectionState

                    coroutineScope.launch {
                        try {
                            horizontalPairState.reentrancyCount++
                            horizontalPairState.state.snapTo(
                                IntOffset(
                                    x = horizontalPairState.state.targetValue.x,
                                    y = intOffset.y,
                                ),
                            )
                        } finally {
                            horizontalPairState.reentrancyCount--
                        }
                    }
                    coroutineScope.launch {
                        try {
                            verticalPairState.reentrancyCount++
                            verticalPairState.state.snapTo(
                                IntOffset(
                                    x = intOffset.x,
                                    y = verticalPairState.state.targetValue.y,
                                ),
                            )
                        } finally {
                            verticalPairState.reentrancyCount--
                        }
                    }
                }
                true
            }

            val offsetCalculator: () -> Offset = {
                val xReferenceState = when {
                    state.state.isDraggingOrAnimating() -> state
                    verticalPairState.state.isDraggingOrAnimating() -> verticalPairState
                    else -> state
                }
                val yReferenceState = when {
                    state.state.isDraggingOrAnimating() -> state
                    horizontalPairState.state.isDraggingOrAnimating() -> horizontalPairState
                    else -> state
                }

                Offset(
                    xReferenceState.state.requireOffset().x,
                    yReferenceState.state.requireOffset().y,
                )
            }
        }

        val handleAnchoredDraggable2DStates =
            initialHandles.mapIndexed { index, initialHandleOffset ->
                key(index, editingSessionKey, scaledCellPixelSize, cellWindow) {
                    rememberSaveable(
                        saver = Saver(
                            save = { packInts(it.currentValue.x, it.currentValue.y) },
                            restore = {
                                AnchoredDraggable2DState(
                                    initialValue = IntOffset(unpackInt1(it), unpackInt2(it)),
                                    animationSpec = spring(),
                                    confirmValueChange = { intOffset ->
                                        confirmValueChangeStates[index].value.invoke(intOffset)
                                    },
                                )
                            },
                        ),
                    ) {
                        AnchoredDraggable2DState(
                            initialValue = initialHandleOffset,
                            animationSpec = spring(),
                            confirmValueChange = { intOffset ->
                                confirmValueChangeStates[index].value.invoke(intOffset)
                            },
                        )
                    }.apply {
                        updateAnchors(handleAnchors)
                    }
                }
            }

        val handleStates = handleAnchoredDraggable2DStates.mapIndexed { index, handleAnchoredDraggable2DState ->
            key(index) {
                remember(handleAnchoredDraggable2DState) {
                    HandleState(handleAnchoredDraggable2DState)
                }
            }
        }

        val handleAState = handleStates[0]
        val handleBState = handleStates[1]
        val handleCState = handleStates[2]
        val handleDState = handleStates[3]

        val selectionHandleStates = remember(handleStates) {
            listOf(
                SelectionDraggableHandleState(
                    state = handleAState,
                    horizontalPairState = handleBState,
                    verticalPairState = handleDState,
                    oppositeCornerState = handleCState,
                ),
                SelectionDraggableHandleState(
                    state = handleBState,
                    horizontalPairState = handleAState,
                    verticalPairState = handleCState,
                    oppositeCornerState = handleDState,
                ),
                SelectionDraggableHandleState(
                    state = handleCState,
                    horizontalPairState = handleDState,
                    verticalPairState = handleBState,
                    oppositeCornerState = handleAState,
                ),
                SelectionDraggableHandleState(
                    state = handleDState,
                    horizontalPairState = handleCState,
                    verticalPairState = handleAState,
                    oppositeCornerState = handleBState,
                ),
            )
        }

        selectionHandleStates.forEachIndexed { index, selectionDraggableHandleState ->
            confirmValueChangeStates[index].value = selectionDraggableHandleState.confirmValueChange
        }

        handleAnchoredDraggable2DStates.forEach {
            it.updateAnchors(handleAnchors)
        }

        val selectionColor = MaterialTheme.colorScheme.secondary
        SelectingBox(
            handleAOffsetCalculator = selectionHandleStates[0].offsetCalculator,
            handleBOffsetCalculator = selectionHandleStates[1].offsetCalculator,
            handleCOffsetCalculator = selectionHandleStates[2].offsetCalculator,
            handleDOffsetCalculator = selectionHandleStates[3].offsetCalculator,
            selectionColor = selectionColor,
            modifier = Modifier.fillMaxSize(),
        )

        val parameterizedStringResolver = parameterizedStringResolver()

        selectionHandleStates
            .forEachIndexed { index, selectionDraggableHandleState ->
                key(index) {
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .offset {
                                selectionDraggableHandleState.offsetCalculator().round()
                            }
                            .offset((-24).dp, (-24).dp)
                            .anchoredDraggable2D(
                                state = selectionDraggableHandleState.state.state,
                                interactionSource = interactionSource,
                            )
                            .size(48.dp)
                            .semantics {
                                val targetValue = selectionDraggableHandleState.state.state.targetValue
                                contentDescription = parameterizedStringResolver(
                                    Strings.SelectingBoxHandle(
                                        targetValue.x,
                                        targetValue.y,
                                    ),
                                )
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        val isDragged by interactionSource.collectIsDraggedAsState()
                        val isHovered by interactionSource.collectIsHoveredAsState()
                        val isPressed by interactionSource.collectIsPressedAsState()

                        val isActive = isDragged || isHovered || isPressed
                        val size by animateDpAsState(if (isActive) 24.dp else 16.dp)
                        val elevation by animateDpAsState(if (isActive) 4.dp else 0.dp)
                        Surface(
                            modifier = Modifier.size(size),
                            shape = CircleShape,
                            shadowElevation = elevation,
                            color = selectionColor,
                        ) {}
                    }
                }
            }
    }
}

/**
 * The selecting box itself.
 */
@Suppress("LongParameterList")
@Composable
private fun SelectingBox(
    handleAOffsetCalculator: () -> Offset,
    handleBOffsetCalculator: () -> Offset,
    handleCOffsetCalculator: () -> Offset,
    handleDOffsetCalculator: () -> Offset,
    selectionColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .layout { measurable, constraints ->
                // Calculate the offsets of the 4 handles
                val aOffset = handleAOffsetCalculator()
                val bOffset = handleBOffsetCalculator()
                val cOffset = handleCOffsetCalculator()
                val dOffset = handleDOffsetCalculator()

                // Determine the top left and bottom right points of the offset, coercing to the edges of this box
                // to avoid drawing the box outside of the bounds.
                val minX = min(min(aOffset.x, bOffset.x), min(cOffset.x, dOffset.x))
                    .coerceIn(0f, constraints.maxWidth.toFloat())
                val maxX = max(max(aOffset.x, bOffset.x), max(cOffset.x, dOffset.x))
                    .coerceIn(0f, constraints.maxWidth.toFloat())
                val minY = min(min(aOffset.y, bOffset.y), min(cOffset.y, dOffset.y))
                    .coerceIn(0f, constraints.maxHeight.toFloat())
                val maxY = max(max(aOffset.y, bOffset.y), max(cOffset.y, dOffset.y))
                    .coerceIn(0f, constraints.maxHeight.toFloat())

                val topLeft = Offset(minX, minY)
                val size = Size(maxX - minX, maxY - minY)

                val selectionBoxConstraints = Constraints(
                    minWidth = size.width.roundToInt(),
                    maxWidth = size.width.roundToInt(),
                    minHeight = size.height.roundToInt(),
                    maxHeight = size.height.roundToInt(),
                )

                // Measure the contents exactly to the constraints.
                val placeable = measurable.measure(selectionBoxConstraints)

                layout(constraints.maxWidth, constraints.maxHeight) {
                    placeable.place(topLeft.round())
                }
            },
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {
            drawRect(
                color = selectionColor,
                alpha = 0.2f,
            )
            drawSelectionRect(
                selectionColor = selectionColor,
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(
                        24.dp.toPx(),
                        24.dp.toPx(),
                    ),
                    phase = 12.dp.toPx(),
                ),
            )
        }
    }
}

/**
 * Draws a selection rectangle, with the given [PathEffect].
 *
 * This results in the [PathEffect] being applied symmetrically via 8 line segments: from the middle of each side,
 * to each corner.
 */
private fun DrawScope.drawSelectionRect(
    selectionColor: Color,
    strokeWidth: Float,
    pathEffect: PathEffect,
    rect: Rect = size.toRect(),
) {
    // Draw the selection outlines in a way that is symmetric, and hides the extra of the lines near the
    // corners with the selection handles
    drawLine(
        color = selectionColor,
        start = rect.topCenter,
        end = rect.topLeft,
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )
    drawLine(
        color = selectionColor,
        start = rect.topCenter,
        end = rect.topRight,
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )
    drawLine(
        color = selectionColor,
        start = rect.centerLeft,
        end = rect.topLeft,
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )
    drawLine(
        color = selectionColor,
        start = rect.centerLeft,
        end = rect.bottomLeft,
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )
    drawLine(
        color = selectionColor,
        start = rect.bottomCenter,
        end = rect.bottomLeft,
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )
    drawLine(
        color = selectionColor,
        start = rect.bottomCenter,
        end = rect.bottomRight,
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )
    drawLine(
        color = selectionColor,
        start = rect.centerRight,
        end = rect.topRight,
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )
    drawLine(
        color = selectionColor,
        start = rect.centerRight,
        end = rect.bottomRight,
        strokeWidth = strokeWidth,
        pathEffect = pathEffect,
    )
}

@Suppress("LongParameterList")
@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.drawingCellInput(
    drawingPointerTypes: Set<PointerType>,
    erasingPointerTypes: Set<PointerType>,
    gameOfLifeState: MutableGameOfLifeState,
    pendingCellChanges: MutableMap<IntOffset, Boolean>,
    scaledCellPixelSize: Float,
    cellWindow: IntRect,
): Modifier = composed {
    val currentScaledCellPixelSize by rememberUpdatedState(scaledCellPixelSize)
    val currentCellWindow by rememberUpdatedState(cellWindow)

    pointerInput(drawingPointerTypes, erasingPointerTypes, pendingCellChanges, gameOfLifeState) {
        detectDragGestures(
            onDragStart = {
                pendingCellChanges.clear()
            },
            onDragEnd = {
                if (pendingCellChanges.size == 1) {
                    // If the drawing change only hit one cell, treat it as a toggle
                    val cellCoordinate = pendingCellChanges.keys.first()
                    gameOfLifeState.setCellState(
                        cellCoordinate,
                        cellCoordinate !in gameOfLifeState.cellState.aliveCells,
                    )
                } else {
                    // Otherwise, add all of them
                    pendingCellChanges.forEach { (cellCoordinate, isAlive) ->
                        gameOfLifeState.setCellState(
                            cellCoordinate = cellCoordinate,
                            isAlive = isAlive,
                        )
                    }
                }
                pendingCellChanges.clear()
            },
            onDrag = { change, _ ->
                val path = LineSegmentPath(
                    (
                        listOf(change.previousPosition) +
                            change.historical.map(HistoricalChange::position) +
                            listOf(change.position)
                        )
                        .filter(Offset::isSpecified)
                        .map { it / currentScaledCellPixelSize },
                )
                val isAlive = when (change.type) {
                    in drawingPointerTypes -> true
                    in erasingPointerTypes -> false
                    else -> throw CancellationException("Non-stylus type!")
                }
                path.cellIntersections().forEach { localCoordinate ->
                    pendingCellChanges[localCoordinate + currentCellWindow.topLeft] = isAlive
                }
            },
        )
    }
}

fun <T> AnchoredDraggable2DState<T>.isDraggingOrAnimating(): Boolean =
    anchors.positionOf(currentValue) != requireOffset()
