package il.co.rachelssandwiches

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OrderReadyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_ready)

        val gotItButton = findViewById<Button>(R.id.gotItButton)
        gotItButton.setOnClickListener {
            RachelsSandwichesApp.viewModel.markOrderDoneAndRestartNewOne()?.observe(this) { retVal: Boolean ->
                if (retVal) {
                    startActivity(Intent(this, NewOrderActivity::class.java))
                    finish()
                }
            }
        }
    }
}