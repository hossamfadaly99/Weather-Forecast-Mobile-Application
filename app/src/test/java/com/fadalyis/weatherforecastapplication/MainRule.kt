package com.example.android.architecture.blueprints.todoapp

import android.text.Editable
import kotlinx.coroutines.Dispatchers
import org.junit.rules.TestWatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.runner.Description

class MainRule(val dispatcher: TestCoroutineDispatcher= TestCoroutineDispatcher()) :TestWatcher(),TestCoroutineScope by TestCoroutineScope(dispatcher) {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
         Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

}