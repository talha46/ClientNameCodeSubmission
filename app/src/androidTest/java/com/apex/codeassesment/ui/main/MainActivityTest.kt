package com.apex.codeassesment.ui.main

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import com.apex.codeassesment.ui.details.DetailsActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.apex.codeassesment.R


@LargeTest
@RunWith(JUnit4::class)
class MainActivityTest {
    private lateinit var scenarioActivity: ActivityScenario<MainActivity>

    @Before
    fun initialisation() {
        scenarioActivity = ActivityScenario.launch(MainActivity::class.java)
        scenarioActivity.moveToState(Lifecycle.State.RESUMED)
        Intents.init()
    }

    @Test
    fun testViewDetails() {
        onView(withId(R.id.main_see_details_button)).perform(click())
        intended(hasComponent(DetailsActivity::class.simpleName))
    }
}