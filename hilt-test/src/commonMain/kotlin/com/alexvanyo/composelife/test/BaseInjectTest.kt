package com.alexvanyo.composelife.test

import com.alexvanyo.composelife.kmpandroidrunner.KmpAndroidJUnit4
import com.alexvanyo.composelife.scopes.ApplicationComponent
import com.alexvanyo.composelife.updatable.Updatable
import com.alexvanyo.composelife.updatable.di.UpdatableModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * A base class for testing components that depend on injected classes.
 *
 * Subclasses must call [runAppTest] instead of [runTest] to properly initialize dependencies.
 */
@Suppress("UnnecessaryAbstractClass")
@RunWith(KmpAndroidJUnit4::class)
abstract class BaseInjectTest<T>(
    applicationComponentCreator: () -> T
) where T : ApplicationComponent, T : UpdatableModule {

    val applicationComponent: T = applicationComponentCreator()

    private val updatables: Set<Updatable>
        get() = applicationComponent.updatables

    fun runAppTest(
        context: CoroutineContext = EmptyCoroutineContext,
        timeout: Duration = 60.seconds,
        testBody: suspend TestScope.() -> Unit,
    ): TestResult = runTest(
        context = context,
        timeout = timeout,
    ) {
        updatables.forEach { updatable ->
            backgroundScope.launch {
                updatable.update()
            }
        }
        testBody()
    }
}