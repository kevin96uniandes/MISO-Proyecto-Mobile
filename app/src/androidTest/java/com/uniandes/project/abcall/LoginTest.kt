import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.config.ApiService
import com.uniandes.project.abcall.config.RetrofitClient
import com.uniandes.project.abcall.repositories.rest.AuthClient
import com.uniandes.project.abcall.ui.LoginActivity
import com.uniandes.project.abcall.viewmodels.AuthViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Callback
import retrofit2.Response

@RunWith(AndroidJUnit4ClassRunner::class)
class LoginTest {

    @MockK
    private lateinit var mockApiService: ApiService

    @get: Rule
    val loginActivityTestRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        // Inicializa las annotation de MockK
        MockKAnnotations.init(this)

        RetrofitClient.setApiService(mockApiService)
    }

    @Test
    fun goToLogin() {
        onView(withId(R.id.btn_log_in)).check(matches(isDisplayed()))
    }

    @Test
    fun validateForm_emptyUsername_showsError() {
        // Deja el campo de nombre de usuario vacío y escribe algo en el campo de contraseña
        onView(withId(R.id.et_password)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.btn_log_in)).perform(click())

        // Verifica que el error en el campo de nombre de usuario aparece
        onView(withId(R.id.ilUsername))
            .check(matches(hasDescendant(withText("Por favor ingresa tu nombre de usuario"))))
    }

    @Test
    fun validateForm_emptyPassword_showsError() {
        // Escribe algo en el campo de nombre de usuario y deja la contraseña vacía
        onView(withId(R.id.et_username)).perform(typeText("username"), closeSoftKeyboard())
        onView(withId(R.id.btn_log_in)).perform(click())

        // Verifica que el error en el campo de contraseña aparece
        onView(withId(R.id.ilPassword))
            .check(matches(hasDescendant(withText("Por favor ingresa tu contraseña"))))
    }

    @Test
    fun validateForm_validInputs_triggersAuthentication() {
        // Escribe nombre de usuario y contraseña válidos
        onView(withId(R.id.et_username)).perform(typeText("username"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.btn_log_in)).perform(click())
    }

    @Test
    fun testLoginButtonClick_wrong_credentials() {
        // Simular que el usuario ingresa un nombre de usuario y contraseña
        onView(withId(R.id.et_username)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("password"), closeSoftKeyboard())


        // Crear el slot para capturar el callback
        val callbackSlot = slot<Callback<AuthClient.LoginResponse>>()

        // Configura el mockApiService para que devuelva el mockCall
        val mockCall: retrofit2.Call<AuthClient.LoginResponse> = mockk(relaxed = true) // Usamos 'relaxed' para evitar configuraciones adicionales


        // Configura el mockApiService para que devuelva el mockCall
        every { mockApiService.login(any()) } returns mockCall

        // Simulando el comportamiento de enqueue
        every { mockCall.enqueue(capture(callbackSlot)) } answers {

            // Captura el callback y llama al método onResponse con un éxito simulado
            callbackSlot.captured.onResponse(mockCall, Response.success(null)) // Asegúrate de que 'mockCall' sea del tipo correcto
        }


        // Simular el click en el botón de login
        onView(withId(R.id.btn_log_in)).perform(click())


        onView(withText("Usuario y/o contraseña incorrecta"))
            .inRoot(isDialog()) // Asegúrate de que estás verificando el diálogo
            .check(matches(isDisplayed()))
        // Verificar que se llama al método authenticate con los valores esperados
        //verify(mockAuthViewModel).authenticate("usuario", "password")
    }

    @Test
    fun testLoginButtonClick_success_credentials() {
        // Simular que el usuario ingresa un nombre de usuario y contraseña
        onView(withId(R.id.et_username)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("password"), closeSoftKeyboard())

        // Crear el slot para capturar el callback
        val callbackSlot = slot<Callback<AuthClient.LoginResponse>>()

        // Configura el mockApiService para que devuelva el mockCall
        val mockCall: retrofit2.Call<AuthClient.LoginResponse> = mockk(relaxed = true) // Usamos 'relaxed' para evitar configuraciones adicionales


        // Configura el mockApiService para que devuelva el mockCall
        every { mockApiService.login(any()) } returns mockCall

        // Simulando el comportamiento de enqueue
        every { mockCall.enqueue(capture(callbackSlot)) } answers {
            // Crear la respuesta simulada
            //val mockResponse = mockk<AuthClient.LoginResponse>()
            val mockResponse = AuthClient.LoginResponse(token = "qwerty")

            // Captura el callback y llama al método onResponse con un éxito simulado
            callbackSlot.captured.onResponse(mockCall, Response.success(mockResponse)) // Asegúrate de que 'mockCall' sea del tipo correcto
        }

        // Simular el click en el botón de login
        onView(withId(R.id.btn_log_in)).perform(click())

        // Verifica que el usuario haya sido redirigido a la actividad de dashboard (o donde sea que quieras verificar)
        onView(withId(R.id.et_identification_number)).check(matches(isDisplayed()))
    }
}
