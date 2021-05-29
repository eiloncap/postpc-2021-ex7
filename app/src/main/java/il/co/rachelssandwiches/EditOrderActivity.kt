package il.co.rachelssandwiches

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EditOrderActivity : AppCompatActivity() {

    private lateinit var picklesAdd: Button
    private lateinit var picklesQuantityView: TextView
    private lateinit var picklesRemove: Button
    private var picklesQuantity = 0

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
    }


    private fun updateAddRemoveButtons() {
        picklesQuantityView.text = picklesQuantity.toString()
        if (picklesQuantity >= RachelsSandwichesApp.MAX_PICKLES) {
            picklesAdd.isEnabled = false
            picklesRemove.isEnabled = true
        } else if (picklesQuantity <= 0) {
            picklesRemove.isEnabled = false
            picklesAdd.isEnabled = true
        } else {
            picklesRemove.isEnabled = true
            picklesAdd.isEnabled = true
        }
    }
}