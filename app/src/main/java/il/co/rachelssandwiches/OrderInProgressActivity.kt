package il.co.rachelssandwiches

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class OrderInProgressActivity : AppCompatActivity() {

    private var snapshotListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_in_progress)

        listenToChangesOnOrder(RachelsSandwichesApp.instance.order?.id!!)
    }

    private fun listenToChangesOnOrder(id: String) {
        val db = FirebaseFirestore.getInstance()
        snapshotListener = db.collection(RachelsSandwichesApp.ORDERS_COLLECTION).document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // TODO: handle error
                } else if (value == null) {
                    // TODO: handle no value
                } else if (!value.exists()) {
                    // TODO: handle deletion
                } else {
                    val updatedOrder = value.toObject(FirestoreOrder::class.java)
                    if (updatedOrder != null && updatedOrder.status == OrderStatus.READY) {
                        RachelsSandwichesApp.instance.order = updatedOrder
                        startActivity(Intent(this, OrderReadyActivity::class.java))
                        finish()
                    }
                }
            }
    }

    override fun onDestroy() {
        snapshotListener?.remove()
        super.onDestroy()
    }
}