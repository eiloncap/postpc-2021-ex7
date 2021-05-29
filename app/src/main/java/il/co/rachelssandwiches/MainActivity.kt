package il.co.rachelssandwiches

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = when (RachelsSandwichesApp.instance.orderState) {
            OrderState.DONE -> Intent(this, NewOrderActivity::class.java)
            OrderState.WAITING -> Intent(this, EditOrderActivity::class.java)
            OrderState.IN_PROGRESS -> Intent(this, OrderInProgressActivity::class.java)
            OrderState.READY -> Intent(this, OrderReadyActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}