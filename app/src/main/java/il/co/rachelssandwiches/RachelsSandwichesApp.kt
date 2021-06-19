package il.co.rachelssandwiches

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp

class RachelsSandwichesApp : Application() {

    companion object {
        const val MAX_PICKLES = 10
        private const val SP_ORDERS_ID = "order_id"
        lateinit var viewModel: SandwichesViewModel

        private lateinit var sp: SharedPreferences
        var id: String? = null
            get() {
                val t = sp.getString(SP_ORDERS_ID, null)
                return t
            }
            set(value) {
                field = value
                if (value == null) {
                    sp.edit().clear().apply()
                } else {
                    sp.edit().putString(SP_ORDERS_ID, value).apply()
                }
            }
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        sp = PreferenceManager.getDefaultSharedPreferences(this)
        viewModel = SandwichesViewModel()
    }

}