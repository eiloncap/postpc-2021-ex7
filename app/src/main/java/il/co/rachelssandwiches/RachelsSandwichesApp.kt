package il.co.rachelssandwiches

import android.app.Application
import android.content.Intent


class RachelsSandwichesApp : Application() {

    companion object {
        lateinit var instance: RachelsSandwichesApp
            private set
    }

    var orderState: OrderState = OrderState.DONE

    override fun onCreate() {
        super.onCreate()
//        FirebaseApp.initializeApp(this)

//        // todo: maybe should start with a main activity
//        when (orderState) {
//            OrderState.DONE -> {
//                startActivity(Intent(this, NewOrderActivity::class.java))
//            }
//            OrderState.WAITING -> {
//                startActivity(Intent(this, EditOrderActivity::class.java))
//            }
//            OrderState.IN_PROGRESS -> {
//                startActivity(Intent(this, InProgressActivity::class.java))
//            }
//            OrderState.READY -> {
//                startActivity(Intent(this, OrderReadyActivity::class.java))
//            }
//        }
    }
}