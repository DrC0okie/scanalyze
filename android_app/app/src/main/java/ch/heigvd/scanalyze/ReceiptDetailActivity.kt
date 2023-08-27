package ch.heigvd.scanalyze

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReceiptDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_detail)

        val dateReceipt: TextView = findViewById(R.id.text_view_detail_date)
        val shopName: TextView = findViewById(R.id.text_view_detail_shop)
        val dateScan: TextView = findViewById(R.id.text_view_detail_scanned_date)
        val shopImage: ImageView = findViewById(R.id.image_view_detail_shop_icon)
        val total: TextView = findViewById(R.id.text_view_detail_total)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_detail_receipts)

        val receipt: Receipt? = intent.getParcelableExtra("receipt")

        if (receipt != null) {
            dateReceipt.text = receipt.getFormattedDate()
            shopName.text = receipt.shopBranch
            dateScan.text = receipt.getFormattedDate()
            shopImage.setImageResource(receipt.shop.resourceImage)
            recyclerView.adapter = ReceiptDetailAdapter(receipt.products)
            total.text = receipt.totalPrice.toString()
        }
    }


}