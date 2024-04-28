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

package com.alexvanyo.composelife.ui.app.action

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.alexvanyo.composelife.parameterizedstring.parameterizedStringResource
import com.alexvanyo.composelife.ui.app.resources.Allow
import com.alexvanyo.composelife.ui.app.resources.Clipboard
import com.alexvanyo.composelife.ui.app.resources.ClipboardWatchingOnboarding
import com.alexvanyo.composelife.ui.app.resources.Disallow
import com.alexvanyo.composelife.ui.app.resources.Pinned
import com.alexvanyo.composelife.ui.app.resources.Strings
import com.alexvanyo.composelife.ui.util.DeferredTargetAnimation
import com.alexvanyo.composelife.ui.util.animatePlacement
import com.alexvanyo.composelife.ui.util.approachLayout
import com.alexvanyo.composelife.ui.util.lookaheadSize
import kotlin.math.floor

context(ClipboardCellStatePreviewInjectEntryPoint, ClipboardCellStatePreviewLocalEntryPoint)
@Composable
fun ClipboardWatchingSection(
    clipboardWatchingState: ClipboardWatchingState,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = clipboardWatchingState,
        contentKey = {
            when (clipboardWatchingState) {
                ClipboardWatchingState.ClipboardWatchingDisabled -> 0
                is ClipboardWatchingState.ClipboardWatchingEnabled -> 1
                is ClipboardWatchingState.Onboarding -> 2
            }
        },
        transitionSpec = {
            fadeIn(animationSpec = tween(220, delayMillis = 90))
                .togetherWith(fadeOut(animationSpec = tween(90)))
        },
        modifier = modifier,
    ) { targetState ->
        when (targetState) {
            ClipboardWatchingState.ClipboardWatchingDisabled -> Unit
            is ClipboardWatchingState.ClipboardWatchingEnabled -> { ClipboardWatchingEnabled(targetState) }
            is ClipboardWatchingState.Onboarding -> { ClipboardWatchingOnboarding(targetState) }
        }
    }
}

context(ClipboardCellStatePreviewInjectEntryPoint, ClipboardCellStatePreviewLocalEntryPoint)
@Suppress("LongMethod")
@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ClipboardWatchingEnabled(
    clipboardWatchingState: ClipboardWatchingState.ClipboardWatchingEnabled,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AnimatedVisibility(clipboardWatchingState.isLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        Text(
            text = parameterizedStringResource(Strings.Clipboard),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )

        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            clipboardWatchingState.clipboardPreviewStates.forEachIndexed { index, clipboardPreviewState ->
                key(clipboardPreviewState.id) {
                    val sizeAnimation = remember { DeferredTargetAnimation(IntSize.VectorConverter) }
                    val coroutineScope = rememberCoroutineScope()

                    ClipboardCellStatePreview(
                        deserializationResult = clipboardPreviewState.deserializationResult,
                        isPinned = clipboardPreviewState.isPinned,
                        onPaste = clipboardPreviewState::onPaste,
                        onPinChanged = clipboardPreviewState::onPinChanged,
                        modifier = Modifier
                            .animatePlacement()
                            .layout { measurable, constraints ->
                                val maxWidth = floor(constraints.maxWidth * if (index == 0) 1f else 0.5f).toInt()

                                val placeable = measurable.measure(
                                    constraints.copy(
                                        maxWidth = maxWidth,
                                    ),
                                )

                                layout(placeable.width, placeable.height) {
                                    placeable.placeRelative(0, 0)
                                }
                            }
                            .approachLayout(
                                isMeasurementApproachInProgress = { lookaheadSize ->
                                    sizeAnimation.updateTarget(lookaheadSize, coroutineScope)
                                    !sizeAnimation.isIdle
                                },
                            ) { measurable, _ ->
                                val (width, height) = sizeAnimation.updateTarget(lookaheadSize, coroutineScope)
                                // Creates a fixed set of constraints using the animated size
                                val animatedConstraints = Constraints.fixed(width, height)
                                // Measure child with animated constraints.
                                val placeable = measurable.measure(animatedConstraints)
                                layout(lookaheadSize.width, lookaheadSize.height) {
                                    placeable.placeRelative(0, 0)
                                }
                            }
                            .padding(8.dp),
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(16.dp))

        AnimatedContent(
            targetState = clipboardWatchingState.pinnedClipboardPreviewStates,
            transitionSpec = {
                (fadeIn() + expandVertically()).togetherWith(fadeOut() + shrinkVertically())
            },
            contentAlignment = Alignment.BottomCenter,
            contentKey = { it.isEmpty() },
        ) { targetState ->
            if (targetState.isEmpty()) {
                Spacer(Modifier.fillMaxWidth())
            } else {
                Column {
                    Text(
                        text = parameterizedStringResource(Strings.Pinned),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        maxItemsInEachRow = 2,
                    ) {
                        targetState.forEach { pinnedClipboardPreviewState ->
                            key(pinnedClipboardPreviewState.id) {
                                ClipboardCellStatePreview(
                                    deserializationResult = pinnedClipboardPreviewState.deserializationResult,
                                    isPinned = true,
                                    onPaste = pinnedClipboardPreviewState::onPaste,
                                    onPinChanged = pinnedClipboardPreviewState::onUnpin,
                                    modifier = Modifier
                                        .animatePlacement()
                                        .layout { measurable, constraints ->
                                            val maxWidth = floor(constraints.maxWidth * 0.5f).toInt()

                                            val placeable = measurable.measure(
                                                constraints.copy(
                                                    maxWidth = maxWidth,
                                                ),
                                            )

                                            layout(placeable.width, placeable.height) {
                                                placeable.placeRelative(0, 0)
                                            }
                                        }
                                        .padding(8.dp),
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClipboardWatchingOnboarding(
    clipboardWatchingState: ClipboardWatchingState.Onboarding,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = parameterizedStringResource(Strings.ClipboardWatchingOnboarding),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        ) {
            OutlinedButton(
                onClick = clipboardWatchingState::onDisallowClipboardWatching,
            ) {
                Text(parameterizedStringResource(Strings.Disallow))
            }
            Button(
                onClick = clipboardWatchingState::onAllowClipboardWatching,
            ) {
                Text(parameterizedStringResource(Strings.Allow))
            }
        }
    }
}
