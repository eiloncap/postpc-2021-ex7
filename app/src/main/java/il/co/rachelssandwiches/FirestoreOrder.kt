package il.co.rachelssandwiches

data class FirestoreOrder(
    val id: String? = null,
    val customer_name: String = "",
    var pickles: Int = 0,
    var hummus: Boolean= false,
    var tahini: Boolean = false,
    var comment: String = "",
    var status: OrderStatus = OrderStatus.DONE
)