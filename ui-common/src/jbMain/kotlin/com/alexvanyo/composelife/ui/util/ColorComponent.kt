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

package com.alexvanyo.composelife.ui.util

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.alexvanyo.composelife.serialization.sealedEnumSaver
import com.alexvanyo.composelife.ui.util.ColorComponent.RgbIntComponent
import com.livefront.sealedenum.GenSealedEnum
import com.livefront.sealedenum.SealedEnum

/**
 * A specific component of a [Color].
 */
sealed interface ColorComponent {

    /**
     * An RGB integer component of a [Color].
     */
    sealed interface RgbIntComponent {
        data object Red : RgbIntComponent
        data object Green : RgbIntComponent
        data object Blue : RgbIntComponent

        @GenSealedEnum
        companion object {
            val Saver = sealedEnumSaver(_sealedEnum)
        }
    }
}

/**
 * Modifies the given [color] with updating the given [component] with the given [value].
 */
fun Color.withComponent(
    component: RgbIntComponent,
    @IntRange(from = 0, to = 255) value: Int,
): Color =
    Color(
        red = if (component == RgbIntComponent.Red) value else get(RgbIntComponent.Red),
        green = if (component == RgbIntComponent.Green) value else get(RgbIntComponent.Green),
        blue = if (component == RgbIntComponent.Blue) value else get(RgbIntComponent.Blue),
        alpha = toArgb().alpha,
    )

/**
 * Returns the given [component] of the [Color] as an integer value (from 0 to 255).
 *
 * This implies a color space conversion is applied, if needed.
 *
 * @see Color.toArgb
 */
fun Color.get(component: RgbIntComponent): Int =
    toArgb().let {
        when (component) {
            RgbIntComponent.Blue -> it.blue
            RgbIntComponent.Green -> it.green
            RgbIntComponent.Red -> it.red
        }
    }

/**
 * Return the alpha component of a color int.
 * ```
 */
private inline val @receiver:ColorInt Int.alpha: Int
    get() = (this shr 24) and 0xff

/**
 * Return the red component of a color int.
 * ```
 */
private inline val @receiver:ColorInt Int.red: Int
    get() = (this shr 16) and 0xff

/**
 * Return the green component of a color int.
 * ```
 */
private inline val @receiver:ColorInt Int.green: Int
    get() = (this shr 8) and 0xff

/**
 * Return the blue component of a color int.
 * ```
 */
private inline val @receiver:ColorInt Int.blue: Int
    get() = this and 0xff

internal expect val ColorComponent.RgbIntComponent.Companion._sealedEnum: SealedEnum<ColorComponent.RgbIntComponent>
