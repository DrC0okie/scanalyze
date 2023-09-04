package ch.heigvd.scanalyze.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.scanalyze.receipt.Product
import ch.heigvd.scanalyze.receipt.Receipt
import ch.heigvd.scanalyze.Shop.Shop
import ch.heigvd.scanalyze.adapters.ReceiptAdapter
import ch.heigvd.scanalyze.databinding.ActivityReceiptBinding
import java.time.LocalDate

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView: RecyclerView = binding.recyclerViewReceipts

        // Test values
        val products = arrayListOf(
            Product("Mclass sésame décorti.", 3.0f, 2.20f),
            Product("Dlr sel des ales fin", 1.0f, 1.50f),
            Product("Sun Queen basilic cit.", 1.0f, 5.30f),
            Product("Valflora UHT 12x1l", 1.0f, 15.35f),
            Product("Concombre", 1.0f, 1.80f),
            Product("Oignons", 1.0f, 2.00f),
            Product("Pommes Jazz", 1.0f, 4.95f),
            Product("Bananes", 1.0f, 2.50f),
            Product("BJ F&W abricots", 1.0f, 1.95f),
            Product("St.Albray", 1.0f, 5.10f),
            Product("MClass Poitrine dinde", 1.0f, 2.95f),
            Product("Mbud Salami volaille", 1.0f, 2.85f),
        )

        val date = LocalDate.now().toString()

        val receipts = arrayOf(
            Receipt("1", date, date, Shop.MIGROS, "MM Payerne", products, 48.45),
            Receipt("1", date, date, Shop.COOP, "Yverdon-les-Bains", products, 48.45),
            Receipt("1", date, date, Shop.ALDI, "Vugelles-La Mothe", products, 48.45),
            Receipt("1", date, date, Shop.ALDI, "Crissier", products, 48.45),
            Receipt("1", date, date, Shop.LIDL,"Sion", products, 48.45),
            Receipt("1", date, date, Shop.COOP, "Lausanne", products, 48.45),
            Receipt("1", date, date, Shop.MIGROS, "MMM Yverdon", products, 48.45),
            Receipt("1", date, date, Shop.LIDL,"Vevey", products, 48.45)
        )

        recyclerView.adapter = ReceiptAdapter(receipts)
    }

}