package il.co.rachelssandwiches

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class EditOrderActivity : AppCompatActivity() {

    private lateinit var picklesAdd: Button
    private lateinit var picklesQuantityView: TextView
    private lateinit var picklesRemove: Button
    private var picklesQuantity = 0
    private var snapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_order)
        picklesAdd = findViewById(R.id.picklesAdd)
        picklesQuantityView = findViewById(R.id.picklesQuantity)
        picklesRemove = findViewById(R.id.picklesRemove)
        val hummusCheckBox = findViewById<CheckBox>(R.id.hummusCheckBox)
        val tahiniCheckBox = findViewById<CheckBox>(R.id.tahiniCheckBox)
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
            order.hummus = hummusCheckBox.isChecked
            order.tahini = tahiniCheckBox.isChecked
            order.comment = commentsEditText.text.toString()
            order.pickles = picklesQuantity
            RachelsSandwichesApp.instance.uploadOrder { it.isEnabled = true }
        }
        cancelButton.setOnClickListener {
            RachelsSandwichesApp.instance.uploadDeleteOrder {
                startActivity(Intent(this, NewOrderActivity::class.java))
                finish()
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

    override fun onDestroy() {
        snapshotListener?.remove()
        super.onDestroy()
    }
}