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

package com.alexvanyo.composelife.ui.app.resources

import com.alexvanyo.composelife.parameterizedstring.ParameterizedString
import com.alexvanyo.composelife.ui.app.R

internal actual fun Strings.TargetStepsPerSecondLabelAndValue(targetStepsPerSecond: Double): ParameterizedString =
    ParameterizedString(R.string.target_steps_per_second_label_and_value, targetStepsPerSecond)

internal actual fun Strings.TargetStepsPerSecondValue(targetStepsPerSecond: Double): ParameterizedString =
    ParameterizedString(R.string.target_steps_per_second_value, targetStepsPerSecond)

internal actual val Strings.TargetStepsPerSecondLabel: ParameterizedString get() =
    ParameterizedString(R.string.target_steps_per_second_label)

internal actual fun Strings.GenerationsPerStepLabelAndValue(generationsPerStep: Int): ParameterizedString =
    ParameterizedString(R.string.generations_per_step_label_and_value, generationsPerStep)

internal actual fun Strings.GenerationsPerStepValue(generationsPerStep: Int): ParameterizedString =
    ParameterizedString(R.string.generations_per_step_value, generationsPerStep)

internal actual val Strings.GenerationsPerStepLabel: ParameterizedString get() =
    ParameterizedString(R.string.generations_per_step_label)

internal actual fun Strings.InteractableCellContentDescription(x: Int, y: Int): ParameterizedString =
    ParameterizedString(R.string.cell_content_description, x, y)

internal actual fun Strings.OffsetInfoMessage(x: Float, y: Float): ParameterizedString =
    ParameterizedString(R.string.offset, x, y)

internal actual fun Strings.ScaleInfoMessage(scale: Float): ParameterizedString =
    ParameterizedString(R.string.scale, scale)

internal actual val Strings.PausedMessage: ParameterizedString get() =
    ParameterizedString(R.string.paused)

internal actual fun Strings.GenerationsPerSecondShortMessage(generationsPerSecond: Double): ParameterizedString =
    ParameterizedString(R.string.generations_per_second_short, generationsPerSecond)

internal actual fun Strings.GenerationsPerSecondLongMessage(generationsPerSecond: Double): ParameterizedString =
    ParameterizedString(R.string.generations_per_second_long, generationsPerSecond)

internal actual val Strings.Collapse: ParameterizedString get() =
    ParameterizedString(R.string.collapse)

internal actual val Strings.Expand: ParameterizedString get() =
    ParameterizedString(R.string.expand)

internal actual val Strings.Pause: ParameterizedString get() =
    ParameterizedString(R.string.pause)

internal actual val Strings.Play: ParameterizedString get() =
    ParameterizedString(R.string.play)

internal actual val Strings.Step: ParameterizedString get() =
    ParameterizedString(R.string.step)

internal actual val Strings.DisableAutofit: ParameterizedString get() =
    ParameterizedString(R.string.disable_autofit)

internal actual val Strings.EnableAutofit: ParameterizedString get() =
    ParameterizedString(R.string.enable_autofit)