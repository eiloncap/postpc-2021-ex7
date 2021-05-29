package il.co.rachelssandwiches

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = when (RachelsSandwichesApp.instance.order?.status) {
            OrderStatus.DONE -> Intent(this, NewOrderActivity::class.java)
            OrderStatus.WAITING -> Intent(this, EditOrderActivity::class.java)
            OrderStatus.IN_PROGRESS -> Intent(this, OrderInProgressActivity::class.java)
            OrderStatus.READY -> Intent(this, OrderReadyActivity::class.java)
            else -> Intent(this, NewOrderActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}