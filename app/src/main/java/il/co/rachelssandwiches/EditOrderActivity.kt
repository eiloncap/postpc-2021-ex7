package il.co.rachelssandwiches

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ListenerRegistration

class EditOrderActivity : AppCompatActivity() {

    companion object {
        private const val PICKLES_BUNDLE = "pickles_count"
        private const val HUMMUS_BUNDLE = "hummus_bool"
        private const val TAHINI_BUNDLE = "tahini_bool"
    }

    private lateinit var picklesAdd: Button
    private lateinit var picklesQuantityView: TextView
    private lateinit var picklesRemove: Button
    private lateinit var hummusCheckBox: CheckBox
    private lateinit var tahiniCheckBox: CheckBox
    private lateinit var commentsEditText: EditText
    private var picklesQuantity = 0
    private var snapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_order)

        // find views
        picklesAdd = findViewById(R.id.picklesAdd)
        picklesQuantityView = findViewById(R.id.picklesQuantity)
        picklesRemove = findViewById(R.id.picklesRemove)
        hummusCheckBox = findViewById(R.id.hummusCheckBox)
        tahiniCheckBox = findViewById(R.id.tahiniCheckBox)
        commentsEditText = findViewById(R.id.commentsEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        val order: FirestoreOrder? = RachelsSandwichesApp.viewModel.order

        // init listeners
        initPicklesViews()

        updateViews(order)

        saveButton.setOnClickListener {
            it.isEnabled = false
            findViewById<View>(R.id.shadingLayer).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            // update App's order
            if (order != null) {
                order.hummus = hummusCheckBox.isChecked
                order.tahini = tahiniCheckBox.isChecked
                order.comment = commentsEditText.text.toString()
                order.pickles = picklesQuantity
            }

            // upload new order
            RachelsSandwichesApp.viewModel.uploadOrder()?.observe(this) { retVal: Boolean ->
                if (retVal) {
                    // move to Edit activity
                    it.isEnabled = true
                    findViewById<View>(R.id.shadingLayer).visibility = View.GONE
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                }
            }
        }
        cancelButton.setOnClickListener {
            findViewById<View>(R.id.shadingLayer).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            RachelsSandwichesApp.viewModel.uploadDeleteOrder()?.observe(this) { retVal: Boolean ->
                if (retVal) {
                    startActivity(Intent(this, NewOrderActivity::class.java))
                    finish()
                }
            }
        }
        if (order != null) {
            snapshotListener = RachelsSandwichesApp.viewModel.getSnapshotListener(
                order.id!!, ::responseToChangesOnOrder
            )
        }
    }

    private fun updateViews(order: FirestoreOrder?) {
        picklesQuantity = RachelsSandwichesApp.viewModel.order?.pickles ?: 0
        picklesQuantityView.text = picklesQuantity.toString()
        hummusCheckBox.isChecked = order?.hummus ?: false
        tahiniCheckBox.isChecked = order?.tahini ?: false
        commentsEditText.setText(order?.comment ?: "")
    }

    private fun initPicklesViews() {
        picklesAdd.setOnClickListener {
            if (picklesQuantity < RachelsSandwichesApp.MAX_PICKLES) {
                picklesQuantity++
                updateAddRemoveButtons()
            }
        }
        picklesRemove.setOnClickListener {
            if (picklesQuantity > 0) {
                picklesQuantity--
                updateAddRemoveButtons()
            }
        }
        picklesQuantity = RachelsSandwichesApp.viewModel.order?.pickles ?: 0
        updateAddRemoveButtons()
    }

    private fun updateAddRemoveButtons() {
        picklesQuantityView.text = picklesQuantity.toString()
        picklesAdd.isEnabled = picklesQuantity < RachelsSandwichesApp.MAX_PICKLES
        picklesRemove.isEnabled = picklesQuantity > 0
    }

    fun responseToChangesOnOrder(updatedOrder: FirestoreOrder?) {
        if (updatedOrder != null) {
            RachelsSandwichesApp.viewModel.order = updatedOrder
            updateViews(updatedOrder)
            val intent = when (updatedOrder.status) {
                OrderStatus.IN_PROGRESS -> {
                    Intent(this, OrderInProgressActivity::class.java)
                }
                OrderStatus.READY -> {
                    Intent(this, OrderReadyActivity::class.java)
                }
                else -> return
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(PICKLES_BUNDLE, picklesQuantity)
        outState.putBoolean(HUMMUS_BUNDLE, hummusCheckBox.isChecked)
        outState.putBoolean(TAHINI_BUNDLE, tahiniCheckBox.isChecked)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        picklesQuantity = savedInstanceState.getInt(PICKLES_BUNDLE)
        findViewById<CheckBox>(R.id.hummusCheckBox).isChecked =
            savedInstanceState.getBoolean(HUMMUS_BUNDLE)
        findViewById<CheckBox>(R.id.tahiniCheckBox).isChecked =
            savedInstanceState.getBoolean(TAHINI_BUNDLE)
    }

    override fun onDestroy() {
        snapshotListener?.remove()
        super.onDestroy()
    }
}