package il.co.rachelssandwiches

data class FirestoreOrder(
    val id: String? = null,
    val customer_name: String = "",
    val pickles: Int = 0,
    val hummus: Boolean= false,
    val tahini: Boolean = false,
    val comment: String = ""
)