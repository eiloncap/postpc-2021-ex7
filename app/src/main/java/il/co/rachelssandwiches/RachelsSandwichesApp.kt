package il.co.rachelssandwiches

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class RachelsSandwichesApp : Application() {

    companion object {
        lateinit var instance: RachelsSandwichesApp
            private set
        const val MAX_PICKLES = 10
        const val ORDERS_COLLECTION = "orders"
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
        db.collection(ORDERS_COLLECTION).document("EgUpS4fSEvf0dVV6M8bM").get()
            .addOnSuccessListener { result ->
                order = result.toObject(FirestoreOrder::class.java)
            }
    }

    fun uploadOrder(callback: (() -> Unit)? = null) {
        if (order != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection(ORDERS_COLLECTION).document(order!!.id!!).set(order!!)
                .addOnSuccessListener {
                    if (callback != null) {
                        callback()
                    }
                }
                .addOnFailureListener {
                    // todo: deal with failure
                }
        }
    }

    fun uploadDeleteOrder(callback: (() -> Unit)? = null) {
        if (order != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection(ORDERS_COLLECTION).document(order!!.id!!).delete()
                .addOnSuccessListener {
                    orderState = OrderState.DONE
                    order = null
                    if (callback != null) {
                        callback()
                    }
                }
                .addOnFailureListener {
                    // todo: deal with failure
                }
        }

    }
}