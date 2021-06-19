package il.co.rachelssandwiches

import android.content.Context
import android.os.Looper.getMainLooper
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
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
class AppTest : TestCase() {

    private var newOrderActivityController: ActivityController<NewOrderActivity>? = null
    private var editOrderActivityController: ActivityController<EditOrderActivity>? = null
    private var orderInProgressActivityController: ActivityController<OrderInProgressActivity>? =
        null

    @Mock
    private val contextMock: Context? = null

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        newOrderActivityController = Robolectric.buildActivity(NewOrderActivity::class.java)
        editOrderActivityController = Robolectric.buildActivity(EditOrderActivity::class.java)
        orderInProgressActivityController =
            Robolectric.buildActivity(OrderInProgressActivity::class.java)
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

    @Test
    fun when_orderContentChanges_then_editActivity_should_updateUI() {
        // setup
        val order = FirestoreOrder(
            "1234", "simon", 2,
            hummus = false, tahini = true, "no pickles please", OrderStatus.WAITING
        )
        editOrderActivityController!!.create().visible()
        val activityUnderTest = editOrderActivityController!!.get()
        val picklesQuantity = activityUnderTest.findViewById<TextView>(R.id.picklesQuantity)
        val hummusCheckBox = activityUnderTest.findViewById<CheckBox>(R.id.hummusCheckBox)
        val tahiniCheckBox = activityUnderTest.findViewById<CheckBox>(R.id.tahiniCheckBox)

        activityUnderTest.responseToChangesOnOrder(order)
        assertFalse(activityUnderTest.isFinishing)
        assertEquals(2, picklesQuantity.text.toString().toInt())
        assertFalse(hummusCheckBox.isChecked)
        assertTrue(tahiniCheckBox.isChecked)

        order.pickles = 5
        activityUnderTest.responseToChangesOnOrder(order)
        assertEquals(5, picklesQuantity.text.toString().toInt())
    }

    @Test
    fun when_orderStatusChanges_then_editActivity_should_Finish() {
        // setup
        val order = FirestoreOrder(
            "1234", "simon", 2,
            hummus = false, tahini = true, "no pickles please", OrderStatus.WAITING
        )
        editOrderActivityController!!.create().visible()
        val activityUnderTest = editOrderActivityController!!.get()

        activityUnderTest.responseToChangesOnOrder(order)
        assertFalse(activityUnderTest.isFinishing)
        order.status = OrderStatus.IN_PROGRESS
        activityUnderTest.responseToChangesOnOrder(order)
        assertTrue(activityUnderTest.isFinishing)
    }
}