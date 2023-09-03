package ch.heigvd.scanalyze.activities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.scanalyze.receipt.Receipt
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
            val bitmap = BitmapFactory.decodeFile(receipt.imgFilePath)
            binding.textViewDetailDate.text = receipt.getReadableDate(receipt.date)
            binding.textViewDetailShop.text = receipt.shopBranch
            binding.textViewDetailScannedDate.text = receipt.getReadableDate(receipt.scanDate)
            binding.imageViewDetailShopIcon.setImageResource(receipt.shop.resourceImage)
            binding.recyclerViewDetailReceipts.adapter = ReceiptDetailAdapter(receipt)
            binding.textViewDetailTotal.text = String.format("%.2f", receipt.totalPrice)
            binding.imageViewScannedImage.setImageBitmap(bitmap)
        }
    }
}