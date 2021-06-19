package il.co.rachelssandwiches

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ListenerRegistration

class OrderInProgressActivity : AppCompatActivity() {

    private var snapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_in_progress)
        snapshotListener = RachelsSandwichesApp.viewModel.getSnapshotListener(
            RachelsSandwichesApp.viewModel.order?.id!!,
            ::responseToChangesOnOrder
        )
    }

    fun responseToChangesOnOrder(updatedOrder: FirestoreOrder?) {
        if (updatedOrder != null && updatedOrder.status == OrderStatus.READY) {
            RachelsSandwichesApp.viewModel.order = updatedOrder
            startActivity(Intent(this, OrderReadyActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        snapshotListener?.remove()
        super.onDestroy()
    }
}