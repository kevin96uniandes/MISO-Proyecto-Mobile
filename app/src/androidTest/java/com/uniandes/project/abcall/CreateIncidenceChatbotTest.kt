import android.content.Intent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.ui.LoginActivity
import com.uniandes.project.abcall.ui.dashboard.DashboardActivity
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Rule
import kotlin.concurrent.thread

class CreateIncidenceChatbotTest {

    private val intent = Intent(ApplicationProvider.getApplicationContext(), DashboardActivity::class.java).apply {
        putExtra("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTczMDU5NzU0OCwianRpIjoiM2RmYjFkZTktN2I1MS00NjAxLWExZGUtMDk1NzdkMGJmNmE4IiwidHlwZSI6ImFjY2VzcyIsInN1YiI6IjQiLCJuYmYiOjE3MzA1OTc1NDgsImNzcmYiOiJhNDA4NzAyMi04ZjU1LTRlNmQtOGI0MC1kZWVkNmUwOTA1YTAiLCJleHAiOjE3MzE0NjE1NDgsImlkIjo0LCJpZF9wZXJzb24iOjMsImlkX2NvbXBhbnkiOm51bGwsInVzZXJfdHlwZSI6InVzdWFyaW8ifQ.ZB3k_xXxYg_1a4auUt26YHF7VwLQIFuGZZnWZ36VpcM")
    }

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule<DashboardActivity>(intent)

    @Before
    fun setup() {
        Intents.init() // Inicializa Intents antes de cada prueba
    }

    @After
    fun tearDown() {
        Intents.release() // Libera Intents después de cada prueba
    }

    @Test
    fun testDisplayIncidentListWhenUserIdIsThree() {
        onView(withId(R.id.incidentRecyclerView)).check(matches(isDisplayed()))
    }
}
