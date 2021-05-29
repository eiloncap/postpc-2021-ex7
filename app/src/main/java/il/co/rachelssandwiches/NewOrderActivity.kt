package il.co.rachelssandwiches

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class NewOrderActivity : AppCompatActivity() {

    lateinit var picklesAdd: Button
    lateinit var picklesQuantityView: TextView
    lateinit var picklesRemove: Button
    var picklesQuantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        picklesAdd = findViewById<Button>(R.id.picklesAdd)
        picklesQuantityView = findViewById<TextView>(R.id.picklesQuantity)
        picklesRemove = findViewById<Button>(R.id.picklesRemove)
        val hummusCheckBox = findViewById<CheckBox>(R.id.hummusCheckBox)
        val tahiniCheckBox = findViewById<CheckBox>(R.id.tahiniCheckBox)
        val commentsEditText = findViewById<EditText>(R.id.commentsEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        picklesAdd.setOnClickListener {
            picklesQuantity++
            picklesQuantityView.text = picklesQuantity.toString()
            updateAddRemoveButtons()
        }
        picklesRemove.setOnClickListener {
            picklesQuantity--
            picklesQuantityView.text = picklesQuantity.toString()
            updateAddRemoveButtons()
        }

        updateAddRemoveButtons()

        saveButton.setOnClickListener {
            it.isEnabled = false
            val newId = UUID.randomUUID().toString()
            val order = FirestoreOrder(
                id = newId,
                customer_name = nameEditText.text.toString(),
                pickles = picklesQuantity,
                hummus = hummusCheckBox.isChecked,
                tahini = tahiniCheckBox.isChecked,
                comment = commentsEditText.text.toString()
            )
            RachelsSandwichesApp.instance.uploadOrder(
                order
            ) {
                startActivity(Intent(this, EditOrderActivity::class.java))
                finish()
            }
        }
    }

    private fun updateAddRemoveButtons() {
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