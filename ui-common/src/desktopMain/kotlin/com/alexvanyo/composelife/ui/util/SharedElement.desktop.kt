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

package com.alexvanyo.composelife.ui.util

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.ui.Modifier

context(SharedTransitionScope)
@Suppress("LongParameterList")
@OptIn(ExperimentalSharedTransitionApi::class)
actual fun Modifier.sharedElement(
    s: SharedContentState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    boundsTransform: BoundsTransform,
    placeHolderSize: PlaceHolderSize,
    renderInOverlayDuringTransition: Boolean,
    zIndexInOverlay: Float,
    clipInOverlayDuringTransition: OverlayClip,
): Modifier = sharedElement(
    state = s,
    animatedVisibilityScope = animatedVisibilityScope,
    boundsTransform = boundsTransform,
    placeHolderSize = placeHolderSize,
    renderInOverlayDuringTransition = renderInOverlayDuringTransition,
    zIndexInOverlay = zIndexInOverlay,
    clipInOverlayDuringTransition = clipInOverlayDuringTransition,
)