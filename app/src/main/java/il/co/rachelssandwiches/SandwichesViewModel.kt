package il.co.rachelssandwiches

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class SandwichesViewModel : ViewModel() {

    companion object {
        const val ORDERS_COLLECTION = "orders"
    }

    var order: FirestoreOrder? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun modifyOrder(pickles: Int, hummus: Boolean, tahini: Boolean, comment: String) {
        order!!.pickles = pickles
        order!!.hummus = hummus
        order!!.tahini = tahini
        order!!.comment = comment
    }

    /**
     * Returns a LiveData of current on going order status if exists, else null
     */
    fun downloadOrder(): LiveData<OrderStatus> {
        val id = RachelsSandwichesApp.id
        val liveData: MutableLiveData<OrderStatus> = MutableLiveData()
        liveData.value = null
        db.collection(ORDERS_COLLECTION).document(id!!).get()
            .addOnSuccessListener { result ->
                order = result.toObject(FirestoreOrder::class.java)
                liveData.value = order?.status
            }
            .addOnFailureListener {
                Log.e("DB error", "downloading error")
            }
        return liveData
    }

    /**
     * uploading current order to db
     * Returns a boolean LiveData of upload status, if order is null returns null
     */
    fun uploadOrder(): LiveData<Boolean>? =
        updateOrderAndReturnLiveDataDecorator { doc: DocumentReference,
                                                liveData: MutableLiveData<Boolean> ->
            doc.set(order!!)
                .addOnSuccessListener {
                    RachelsSandwichesApp.id = order!!.id
                    liveData.value = true
                }
                .addOnFailureListener {
                    Log.e("DB error", "uploading error")
                }
        }

    /**
     * deleting current order in db
     * Returns a boolean LiveData of delete status, if order is null returns null
     */
    fun uploadDeleteOrder(): LiveData<Boolean>? =
        updateOrderAndReturnLiveDataDecorator { doc: DocumentReference,
                                                liveData: MutableLiveData<Boolean> ->
            doc.set(order!!)
                .addOnSuccessListener {
                    RachelsSandwichesApp.id = null
                    order = null
                    liveData.value = true
                }
                .addOnFailureListener {
                    Log.e("DB error", "deleting error")
                }
        }

    /**
     * marks current order as done
     * Returns a boolean LiveData of upload status, if order is null returns null
     */
    fun markOrderDoneAndRestartNewOne(): LiveData<Boolean>? {
        order?.status = OrderStatus.DONE
        return updateOrderAndReturnLiveDataDecorator { doc: DocumentReference,
                                                       liveData: MutableLiveData<Boolean> ->
            doc.set(order!!)
                .addOnSuccessListener {
                    order = null
                    RachelsSandwichesApp.id = null
                    liveData.value = true
                }
                .addOnFailureListener {
                    Log.e("DB error", "uploading error")
                }
        }
    }

    /**
     * Generic fun for modification of db.
     * Returns a boolean LiveData of modification status, if order is null returns null
     */
    private fun updateOrderAndReturnLiveDataDecorator(
        f: (DocumentReference, MutableLiveData<Boolean>) -> Unit
    ): LiveData<Boolean>? {
        if (order == null) {
            return null
        }
        val liveData: MutableLiveData<Boolean> = MutableLiveData()
        liveData.value = false
        f(db.collection(ORDERS_COLLECTION).document(order!!.id!!), liveData)
        return liveData
    }

    fun getSnapshotListener(
        id: String,
        callback: (updatedOrder: FirestoreOrder?) -> Unit
    ): ListenerRegistration {
        return db.collection(ORDERS_COLLECTION).document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("DB error", "listenToChangesOnOrder error")
                } else if (value == null) {
                    Log.e("DB error", "listenToChangesOnOrder error, val is null")
                } else if (!value.exists()) {
                    Log.e("DB error", "listenToChangesOnOrder error, val doesn't exist")
                } else {
                    callback(value.toObject(FirestoreOrder::class.java))
                }
            }
    }
}