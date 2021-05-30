package il.co.rachelssandwiches

import android.content.Context
import android.os.Looper.getMainLooper
import android.widget.Button
import android.widget.TextView
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@LooperMode(LooperMode.Mode.PAUSED)
class ExampleUnitTest : TestCase() {

    private var newOrderActivityController: ActivityController<NewOrderActivity>? = null
    private var editOrderActivityController: ActivityController<EditOrderActivity>? = null
    private var orderInProgressActivityController: ActivityController<OrderInProgressActivity>? =
        null
    private var orderReadyActivityController: ActivityController<OrderReadyActivity>? = null

    @Mock
    private val contextMock: Context? = null

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        newOrderActivityController = Robolectric.buildActivity(NewOrderActivity::class.java)
        editOrderActivityController = Robolectric.buildActivity(EditOrderActivity::class.java)
        orderInProgressActivityController =
            Robolectric.buildActivity(OrderInProgressActivity::class.java)
        orderReadyActivityController = Robolectric.buildActivity(OrderReadyActivity::class.java)
    }

    @Test
    fun when_picklesCountIsOver10OrUnder0_then_buttonsShouldBeDisabled() {
        // setup
        newOrderActivityController!!.create().visible()
        val activityUnderTest = newOrderActivityController!!.get()
        val picklesAdd = activityUnderTest.findViewById<Button>(R.id.picklesAdd)
        val picklesQuantity = activityUnderTest.findViewById<TextView>(R.id.picklesQuantity)
        val picklesRemove = activityUnderTest.findViewById<Button>(R.id.picklesRemove)
        // verify
        assertEquals(0, Integer.parseInt(picklesQuantity.text.toString()))
        for (i in 0..15) {
            picklesAdd.performClick()
        }
        shadowOf(getMainLooper()).idle()
        assertEquals(10, Integer.parseInt(picklesQuantity.text.toString()))
        assertFalse(picklesAdd.isEnabled)

        for (i in 0..11) {
            picklesRemove.performClick()
        }
        shadowOf(getMainLooper()).idle()
        assertEquals(0, Integer.parseInt(picklesQuantity.text.toString()))
        assertFalse(picklesRemove.isEnabled)
    }
}