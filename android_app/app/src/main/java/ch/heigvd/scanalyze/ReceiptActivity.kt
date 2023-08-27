package ch.heigvd.scanalyze

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

// Test values
val products = arrayListOf(Product(1, "Mclass sésame décorti.", 3.0, 2.20),
    Product(1, "Dlr sel des ales fin", 1.0, 1.50),
    Product(1, "Sun Queen basilic cit.", 1.0, 5.30),
    Product(1, "Valflora UHT 12x1l", 1.0, 15.35),
    Product(1, "Concombre", 1.0, 1.80),
    Product(1, "Oignons", 1.0, 2.00),
    Product(1, "Pommes Jazz", 1.0, 4.95),
    Product(1, "Bananes", 1.0, 2.50),
    Product(1, "BJ F&W abricots", 1.0, 1.95),
    Product(1, "St.Albray", 1.0, 5.10),
    Product(1, "MClass Poitrine dinde", 1.0, 2.95),
    Product(1, "Mbud Salami volaille", 1.0, 2.85),
)

val date = LocalDate.now()

val receipts = arrayOf(
    Receipt(1, date, date, Shop.MIGROS, "MM Payerne", products, 48.45),
    Receipt(1, date, date, Shop.COOP, "Yverdon-les-Bains", products, 48.45),
    Receipt(1, date, date, Shop.ALDI, "Vugelles-La Mothe", products, 48.45),
    Receipt(1, date, date, Shop.ALDI, "Crissier", products, 48.45),
    Receipt(1, date, date, Shop.LIDL,"Sion", products, 48.45),
    Receipt(1, date, date, Shop.COOP, "Lausanne", products, 48.45),
    Receipt(1, date, date, Shop.MIGROS, "MMM Yverdon", products, 48.45),
    Receipt(1, date, date, Shop.LIDL,"Vevey", products, 48.45)
)


class ReceiptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_receipts)
        recyclerView.adapter = ReceiptAdapter(receipts)
    }

}