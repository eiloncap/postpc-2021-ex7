package il.co.rachelssandwiches

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
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
    private var picklesQuantity = 0
    private var snapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_order)
        picklesAdd = findViewById(R.id.picklesAdd)
        picklesQuantityView = findViewById(R.id.picklesQuantity)
        picklesRemove = findViewById(R.id.picklesRemove)
        hummusCheckBox = findViewById(R.id.hummusCheckBox)
        tahiniCheckBox = findViewById(R.id.tahiniCheckBox)
        val commentsEditText = findViewById<EditText>(R.id.commentsEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        val order: FirestoreOrder = RachelsSandwichesApp.instance.order!!

        picklesAdd.setOnClickListener {
            picklesQuantity++
            updateAddRemoveButtons()
        }
        picklesRemove.setOnClickListener {
            picklesQuantity--
            updateAddRemoveButtons()
        }
        picklesQuantity = order.pickles
        updateAddRemoveButtons()

        hummusCheckBox.isChecked = order.hummus
        tahiniCheckBox.isChecked = order.tahini

        commentsEditText.setText(order.comment)

        saveButton.setOnClickListener {
            it.isEnabled = false
            findViewById<View>(R.id.shadingLayer).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            order.hummus = hummusCheckBox.isChecked
            order.tahini = tahiniCheckBox.isChecked
            order.comment = commentsEditText.text.toString()
            order.pickles = picklesQuantity
            RachelsSandwichesApp.instance.uploadOrder()?.observe(this) { retVal: Boolean ->
                if (retVal) {
                    it.isEnabled = true
                    findViewById<View>(R.id.shadingLayer).visibility = View.GONE
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                }
            }
        }
        cancelButton.setOnClickListener {
            findViewById<View>(R.id.shadingLayer).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            RachelsSandwichesApp.instance.uploadDeleteOrder()?.observe(this) { retVal: Boolean ->
                if (retVal) {
                    startActivity(Intent(this, NewOrderActivity::class.java))
                    finish()
                }
            }
        }
        listenToChangesOnOrder(order.id!!)
    }


    private fun updateAddRemoveButtons() {
        picklesQuantityView.text = picklesQuantity.toString()
        picklesAdd.isEnabled = picklesQuantity < RachelsSandwichesApp.MAX_PICKLES
        picklesRemove.isEnabled = picklesQuantity > 0
    }

    private fun listenToChangesOnOrder(id: String) {
        val db = FirebaseFirestore.getInstance()
        snapshotListener = db.collection(RachelsSandwichesApp.ORDERS_COLLECTION).document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // TODO: handle error
                } else if (value == null) {
                    // TODO: handle no value
                } else if (!value.exists()) {
                    // TODO: handle deletion
                } else {
                    val updatedOrder = value.toObject(FirestoreOrder::class.java)
                    if (updatedOrder != null) {
                        val intent = when (updatedOrder.status) {
                            OrderStatus.IN_PROGRESS -> {
                                Intent(this, OrderInProgressActivity::class.java)
                            }
                            OrderStatus.READY -> {
                                Intent(this, OrderReadyActivity::class.java)
                            }
                            else -> return@addSnapshotListener
                        }
                        RachelsSandwichesApp.instance.order = updatedOrder
                        startActivity(intent)
                        finish()
                    }
                }
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