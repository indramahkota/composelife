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
@file:Suppress("TooManyFunctions")

package com.alexvanyo.composelife.ui.app.resources

import com.alexvanyo.composelife.parameterizedstring.ParameterizedString
import com.alexvanyo.composelife.parameterizedstring.ParameterizedStringArgument
import com.alexvanyo.composelife.ui.app.R

internal actual fun Strings.TargetStepsPerSecondLabelAndValue(targetStepsPerSecond: Double): ParameterizedString =
    ParameterizedString(
        R.string.target_steps_per_second_label_and_value,
        ParameterizedStringArgument(targetStepsPerSecond),
    )

internal actual fun Strings.TargetStepsPerSecondValue(targetStepsPerSecond: Double): ParameterizedString =
    ParameterizedString(
        R.string.target_steps_per_second_value,
        ParameterizedStringArgument(targetStepsPerSecond),
    )

internal actual val Strings.TargetStepsPerSecondLabel: ParameterizedString get() =
    ParameterizedString(R.string.target_steps_per_second_label)

internal actual fun Strings.GenerationsPerStepLabelAndValue(generationsPerStep: Int): ParameterizedString =
    ParameterizedString(
        R.string.generations_per_step_label_and_value,
        ParameterizedStringArgument(generationsPerStep),
    )

internal actual fun Strings.GenerationsPerStepValue(generationsPerStep: Int): ParameterizedString =
    ParameterizedString(
        R.string.generations_per_step_value,
        ParameterizedStringArgument(generationsPerStep),
    )

internal actual val Strings.GenerationsPerStepLabel: ParameterizedString get() =
    ParameterizedString(R.string.generations_per_step_label)

internal actual fun Strings.OffsetInfoMessage(x: Float, y: Float): ParameterizedString =
    ParameterizedString(
        R.string.offset,
        ParameterizedStringArgument(x),
        ParameterizedStringArgument(y),
    )

internal actual fun Strings.ScaleInfoMessage(scale: Float): ParameterizedString =
    ParameterizedString(
        R.string.scale,
        ParameterizedStringArgument(scale),
    )

internal actual val Strings.PausedMessage: ParameterizedString get() =
    ParameterizedString(R.string.paused)

internal actual fun Strings.GenerationsPerSecondShortMessage(generationsPerSecond: Double): ParameterizedString =
    ParameterizedString(
        R.string.generations_per_second_short,
        ParameterizedStringArgument(generationsPerSecond),
    )

internal actual fun Strings.GenerationsPerSecondLongMessage(generationsPerSecond: Double): ParameterizedString =
    ParameterizedString(
        R.string.generations_per_second_long,
        ParameterizedStringArgument(generationsPerSecond),
    )

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

internal actual val Strings.ClearSelection: ParameterizedString get() =
    ParameterizedString(R.string.clear_selection)

internal actual val Strings.Copy: ParameterizedString get() =
    ParameterizedString(R.string.copy)

internal actual val Strings.Cut: ParameterizedString get() =
    ParameterizedString(R.string.cut)

internal actual val Strings.Paste: ParameterizedString get() =
    ParameterizedString(R.string.paste)

internal actual val Strings.CancelPaste: ParameterizedString get() =
    ParameterizedString(R.string.cancel_paste)

internal actual val Strings.ApplyPaste: ParameterizedString get() =
    ParameterizedString(R.string.apply_paste)

internal actual val Strings.DisableAutofit: ParameterizedString get() =
    ParameterizedString(R.string.disable_autofit)

internal actual val Strings.EnableAutofit: ParameterizedString get() =
    ParameterizedString(R.string.enable_autofit)

internal actual val Strings.DisableImmersiveMode: ParameterizedString get() =
    ParameterizedString(R.string.disable_immersive_mode)

internal actual val Strings.EnableImmersiveMode: ParameterizedString get() =
    ParameterizedString(R.string.enable_immersive_mode)

internal actual val Strings.EnterHomeSpaceMode: ParameterizedString get() =
    ParameterizedString(R.string.enter_home_space_mode)

internal actual val Strings.EnterFullSpaceMode: ParameterizedString get() =
    ParameterizedString(R.string.enter_full_space_mode)

internal actual val Strings.Speed: ParameterizedString get() =
    ParameterizedString(R.string.speed)

internal actual val Strings.Edit: ParameterizedString get() =
    ParameterizedString(R.string.edit)

internal actual val Strings.Settings: ParameterizedString get() =
    ParameterizedString(R.string.settings)

internal actual val Strings.Touch: ParameterizedString get() =
    ParameterizedString(R.string.touch)

internal actual val Strings.TouchTool: ParameterizedString get() =
    ParameterizedString(R.string.touch_tool)

internal actual val Strings.Stylus: ParameterizedString get() =
    ParameterizedString(R.string.stylus)

internal actual val Strings.StylusTool: ParameterizedString get() =
    ParameterizedString(R.string.stylus_tool)

internal actual val Strings.Mouse: ParameterizedString get() =
    ParameterizedString(R.string.mouse)

internal actual val Strings.MouseTool: ParameterizedString get() =
    ParameterizedString(R.string.mouse_tool)

internal actual val Strings.Pan: ParameterizedString get() =
    ParameterizedString(R.string.pan)

internal actual val Strings.Draw: ParameterizedString get() =
    ParameterizedString(R.string.draw)

internal actual val Strings.Erase: ParameterizedString get() =
    ParameterizedString(R.string.erase)

internal actual val Strings.Select: ParameterizedString get() =
    ParameterizedString(R.string.select)

internal actual val Strings.None: ParameterizedString get() =
    ParameterizedString(R.string.none)

internal actual val Strings.EmptyClipboard: ParameterizedString get() =
    ParameterizedString(R.string.empty_clipboard)

internal actual val Strings.Clipboard: ParameterizedString get() =
    ParameterizedString(R.string.clipboard)

internal actual val Strings.Pinned: ParameterizedString get() =
    ParameterizedString(R.string.pinned)

internal actual val Strings.Pin: ParameterizedString get() =
    ParameterizedString(R.string.pin)

internal actual val Strings.Unpin: ParameterizedString get() =
    ParameterizedString(R.string.unpin)

internal actual val Strings.Back: ParameterizedString get() =
    ParameterizedString(R.string.back)

internal actual val Strings.Close: ParameterizedString get() =
    ParameterizedString(R.string.close)

internal actual val Strings.ClipboardWatchingOnboarding: ParameterizedString get() =
    ParameterizedString(R.string.clipboard_watching_onboarding)

internal actual val Strings.Allow: ParameterizedString get() =
    ParameterizedString(R.string.allow)

internal actual val Strings.Disallow: ParameterizedString get() =
    ParameterizedString(R.string.disallow)

internal actual val Strings.DeserializationSucceeded: ParameterizedString get() =
    ParameterizedString(R.string.deserialization_succeeded)

internal actual val Strings.DeserializationFailed: ParameterizedString get() =
    ParameterizedString(R.string.deserialization_failed)

internal actual val Strings.Warnings: ParameterizedString get() =
    ParameterizedString(R.string.warnings)

internal actual val Strings.Errors: ParameterizedString get() =
    ParameterizedString(R.string.errors)
