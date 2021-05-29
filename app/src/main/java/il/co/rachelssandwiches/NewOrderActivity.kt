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

    private lateinit var picklesAdd: Button
    private lateinit var picklesQuantityView: TextView
    private lateinit var picklesRemove: Button
    private var picklesQuantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        picklesAdd = findViewById(R.id.picklesAdd)
        picklesQuantityView = findViewById(R.id.picklesQuantity)
        picklesRemove = findViewById(R.id.picklesRemove)
        val hummusCheckBox = findViewById<CheckBox>(R.id.hummusCheckBox)
        val tahiniCheckBox = findViewById<CheckBox>(R.id.tahiniCheckBox)
        val commentsEditText = findViewById<EditText>(R.id.commentsEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        picklesAdd.setOnClickListener {
            picklesQuantity++
            updateAddRemoveButtons()
        }
        picklesRemove.setOnClickListener {
            picklesQuantity--
            updateAddRemoveButtons()
        }

        updateAddRemoveButtons()

        saveButton.setOnClickListener {
            it.isEnabled = false
            RachelsSandwichesApp.instance.order = FirestoreOrder(
                id = UUID.randomUUID().toString(),
                customer_name = nameEditText.text.toString(),
                pickles = picklesQuantity,
                hummus = hummusCheckBox.isChecked,
                tahini = tahiniCheckBox.isChecked,
                comment = commentsEditText.text.toString(),
                status = OrderStatus.WAITING
            )
            RachelsSandwichesApp.instance.uploadOrder {
                startActivity(Intent(this, EditOrderActivity::class.java))
                finish()
            }
        }
    }

    private fun updateAddRemoveButtons() {
        picklesQuantityView.text = picklesQuantity.toString()
        picklesAdd.isEnabled = picklesQuantity < RachelsSandwichesApp.MAX_PICKLES
        picklesRemove.isEnabled = picklesQuantity > 0
    }
}