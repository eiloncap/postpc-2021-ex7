package il.co.rachelssandwiches

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (RachelsSandwichesApp.id == null) {  // no order in process
            startActivity(Intent(this, NewOrderActivity::class.java))
            finish()
        } else {
            val liveData: LiveData<OrderStatus> = RachelsSandwichesApp.viewModel.downloadOrder()
            liveData.observe(this) {
                if (it != null) {  // order in process
                    val intent = when (it) {
                        OrderStatus.DONE -> Intent(this, NewOrderActivity::class.java)
                        OrderStatus.WAITING -> Intent(this, EditOrderActivity::class.java)
                        OrderStatus.IN_PROGRESS -> Intent(this, OrderInProgressActivity::class.java)
                        OrderStatus.READY -> Intent(this, OrderReadyActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}