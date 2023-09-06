package ch.heigvd.scanalyze.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.scanalyze.Shop.Shop
import ch.heigvd.scanalyze.adapters.ReceiptDetailAdapter
import ch.heigvd.scanalyze.databinding.ActivityReceiptDetailBinding
import ch.heigvd.scanalyze.receipt.JsonReceipt
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReceiptDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptDetailBinding
    private lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receipt: JsonReceipt? = intent.getParcelableExtra("receipt")

        if (receipt != null) {
            if (!receipt.imgFilePath.isNullOrEmpty()) {
                bitmap = BitmapFactory.decodeFile(receipt.imgFilePath)
                binding.imageViewScannedImage.setImageBitmap(bitmap)
            }

            try {
                binding.textViewDetailDate.text = LocalDateTime
                    .parse(receipt.date, DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            } catch (e: Exception) {
                binding.textViewDetailDate.text = "Date unknown"
            }

            binding.textViewDetailShop.text = receipt.shopBranch ?: ""
            binding.imageViewDetailShopIcon.setImageResource(Shop.func.fromString(receipt.shopName).resourceImage)
            binding.textViewDetailTotal.text = String.format("%.2f", receipt.total)
            binding.recyclerViewDetailReceipts.adapter = ReceiptDetailAdapter(receipt)

        }
    }
}