package ch.heigvd.scanalyze.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.scanalyze.R
import ch.heigvd.scanalyze.Receipt
import ch.heigvd.scanalyze.adapters.ReceiptDetailAdapter
import ch.heigvd.scanalyze.databinding.ActivityReceiptDetailBinding

class ReceiptDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receipt: Receipt? = intent.getParcelableExtra("receipt")

        if (receipt != null) {
            binding.textViewDetailDate.text = receipt.getFormattedDate()
            binding.textViewDetailShop.text = receipt.shopBranch
            binding.textViewDetailScannedDate.text = receipt.getFormattedDate()
            binding.imageViewDetailShopIcon.setImageResource(receipt.shop.resourceImage)
            binding.recyclerViewDetailReceipts.adapter = ReceiptDetailAdapter(receipt.products)
            binding.textViewDetailTotal.text = receipt.totalPrice.toString()
        }
    }


}