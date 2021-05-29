package il.co.rachelssandwiches

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, OrderInProgressActivity::class.java))
        // todo: maybe should start with a main activity
//        when (RachelsSandwichesApp.instance.orderState) {
//            OrderState.DONE -> {
//                startActivity(Intent(this, NewOrderActivity::class.java))
//            }
//            OrderState.WAITING -> {
//                startActivity(Intent(this, EditOrderActivity::class.java))
//            }
//            OrderState.IN_PROGRESS -> {
//                startActivity(Intent(this, OrderInProgressActivity::class.java))
//            }
//            OrderState.READY -> {
//                startActivity(Intent(this, OrderReadyActivity::class.java))
//            }
//        }
    }
}