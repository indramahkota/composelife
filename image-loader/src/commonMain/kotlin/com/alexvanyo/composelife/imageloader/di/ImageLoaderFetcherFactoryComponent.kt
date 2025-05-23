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

package com.alexvanyo.composelife.imageloader.di

import coil3.fetch.Fetcher
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import kotlin.reflect.KClass

@ContributesTo(AppScope::class)
interface ImageLoaderFetcherFactoryComponent {
    val fetcherFactoriesWithType: Set<FetcherFactoryWithType<out Any>>
}

class FetcherFactoryWithType<T : Any>(
    val type: KClass<T>,
    val fetcherFactory: Fetcher.Factory<T>,
)

inline fun <reified T : Any> Fetcher.Factory<T>.withType(): FetcherFactoryWithType<T> =
    FetcherFactoryWithType(T::class, this)
