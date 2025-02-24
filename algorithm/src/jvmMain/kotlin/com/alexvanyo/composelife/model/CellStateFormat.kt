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

package com.alexvanyo.composelife.model

import com.livefront.sealedenum.GenSealedEnum
import kotlinx.serialization.Serializable

sealed interface CellStateFormat {

    data object Unknown : CellStateFormat
    data object Life : CellStateFormat

    /**
     * A "fixed" format for a cell state. Any serialization or deserialization of a cell state should eventually
     * be done with one of these formats, and other [CellStateFormat]s might delegate to here.
     */
    @Serializable
    sealed interface FixedFormat : CellStateFormat {
        @Serializable
        data object Plaintext : FixedFormat

        @Serializable
        data object Life105 : FixedFormat

        @Serializable
        data object Life106 : FixedFormat

        @Serializable
        data object RunLengthEncoding : FixedFormat

        @Serializable
        data object Macrocell : FixedFormat

        @GenSealedEnum(generateEnum = true)
        companion object
    }

    @GenSealedEnum(generateEnum = true)
    companion object
}

expect val CellStateFormat.FixedFormat._name: String

fun CellStateFormat.Companion.fromFileExtension(fileExtension: String?): CellStateFormat =
    when (fileExtension) {
        "cells" -> CellStateFormat.FixedFormat.Plaintext
        "lif", "life" -> CellStateFormat.Life
        "rle" -> CellStateFormat.FixedFormat.RunLengthEncoding
        "mc" -> CellStateFormat.FixedFormat.Macrocell
        else -> CellStateFormat.Unknown
    }
