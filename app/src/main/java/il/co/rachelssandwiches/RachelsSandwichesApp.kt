package il.co.rachelssandwiches

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RachelsSandwichesApp : Application() {

    companion object {
        lateinit var instance: RachelsSandwichesApp
            private set
        const val MAX_PICKLES = 10
        const val ORDERS_COLLECTION = "orders"
        private const val SP_ORDERS_ID = "order_id"
    }

    var order: FirestoreOrder? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        instance = this
    }

    /**
     * Returns a LiveData of current on going order status if exists, else null
     */
    fun downloadOrder(): LiveData<OrderStatus>? {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val id = sp.getString(SP_ORDERS_ID, null) ?: return null
        val liveData: MutableLiveData<OrderStatus> = MutableLiveData()
        liveData.value = null
        val db = FirebaseFirestore.getInstance()
        db.collection(ORDERS_COLLECTION).document(id).get()
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
                    val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                    sp.edit().putString(SP_ORDERS_ID, order!!.id).apply()
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
                    val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                    sp.edit().clear().apply()
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
                    val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                    sp.edit().clear().apply()
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
        val db = FirebaseFirestore.getInstance()
        f(db.collection(ORDERS_COLLECTION).document(order!!.id!!), liveData)
        return liveData
    }
}