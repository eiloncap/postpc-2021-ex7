package il.co.rachelssandwiches

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class RachelsSandwichesApp : Application() {

    companion object {
        lateinit var instance: RachelsSandwichesApp
            private set
        val MAX_PICKLES = 10
    }

    var orderState: OrderState = OrderState.DONE
    var order: FirestoreOrder? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        instance = this
    }

    private fun downloadOrder() {
        val db = FirebaseFirestore.getInstance()
        // todo: get ID from phone
        db.collection("orders").document("EgUpS4fSEvf0dVV6M8bM").get()
            .addOnSuccessListener { result ->
                order = result.toObject(FirestoreOrder::class.java)
            }
    }

    fun uploadOrder(order: FirestoreOrder, callback: (() -> Unit)? = null) {
        val db = FirebaseFirestore.getInstance()
        db.collection("orders").document(order.id!!).set(order)
            .addOnSuccessListener {
                if (callback != null) {
                    callback()
                }
                Log.d("eilon", "success")
            }
            .addOnFailureListener {
                // todo: deal with failure
                Log.d("eilon", "fail ${order.id}")
            }
    }

    fun uploadDeleteOrder(order: FirestoreOrder) {
        val db = FirebaseFirestore.getInstance()
        db.collection("orders").document(order.id!!).delete()
            .addOnSuccessListener { /**/ }
    }
}