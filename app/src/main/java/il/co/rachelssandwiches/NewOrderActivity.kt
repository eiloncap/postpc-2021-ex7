package il.co.rachelssandwiches

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class NewOrderActivity : AppCompatActivity() {

    companion object {
        private const val PICKLES_BUNDLE = "pickles_count"
        private const val HUMMUS_BUNDLE = "hummus_bool"
        private const val TAHINI_BUNDLE = "tahini_bool"
        private const val SP_NAMES_HINTS = "sp_names"
    }

    private lateinit var picklesAdd: Button
    private lateinit var picklesQuantityView: TextView
    private lateinit var picklesRemove: Button
    private lateinit var hummusCheckBox: CheckBox
    private lateinit var tahiniCheckBox: CheckBox
    private var picklesQuantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)

        // find views
        picklesAdd = findViewById(R.id.picklesAdd)
        picklesQuantityView = findViewById(R.id.picklesQuantity)
        picklesRemove = findViewById(R.id.picklesRemove)
        hummusCheckBox = findViewById(R.id.hummusCheckBox)
        tahiniCheckBox = findViewById(R.id.tahiniCheckBox)
        val nameEditText = initNameEditTextView()
        val commentsEditText = findViewById<EditText>(R.id.commentsEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // init listeners
        initPicklesViews()

        saveButton.setOnClickListener {
            it.isEnabled = false
            findViewById<View>(R.id.shadingLayer).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            // create new order instance
            RachelsSandwichesApp.instance.order = FirestoreOrder(
                id = UUID.randomUUID().toString(),
                customer_name = nameEditText.text.toString(),
                pickles = picklesQuantity,
                hummus = hummusCheckBox.isChecked,
                tahini = tahiniCheckBox.isChecked,
                comment = commentsEditText.text.toString(),
                status = OrderStatus.WAITING
            )

            // save new name to hints
            this.getSharedPreferences(SP_NAMES_HINTS, Context.MODE_PRIVATE).edit()
                .putString(nameEditText.text.toString(), "").apply()

            // upload new order
            RachelsSandwichesApp.instance.uploadOrder()?.observe(this) { retVal: Boolean ->
                if (retVal) {
                    startActivity(Intent(this, EditOrderActivity::class.java))
                    finish()
                }
            }
        }
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
        updateAddRemoveButtons()
    }

    private fun initNameEditTextView(): AutoCompleteTextView {
        val nameEditText = findViewById<AutoCompleteTextView>(R.id.nameEditText)
        // get all used names
        val names: Array<String> =
            this.getSharedPreferences(SP_NAMES_HINTS, Context.MODE_PRIVATE).all.keys.toTypedArray()
        ArrayAdapter(this, android.R.layout.simple_list_item_1, names).also { adapter ->
            nameEditText.setAdapter(adapter)
        }

        return nameEditText
    }

    private fun updateAddRemoveButtons() {
        picklesQuantityView.text = picklesQuantity.toString()
        picklesAdd.isEnabled = picklesQuantity < RachelsSandwichesApp.MAX_PICKLES
        picklesRemove.isEnabled = picklesQuantity > 0
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
}