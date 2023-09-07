package ch.heigvd.scanalyze.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.scanalyze.Shop.Shop
import ch.heigvd.scanalyze.Utils.Utils
import ch.heigvd.scanalyze.adapters.ReceiptDetailAdapter
import ch.heigvd.scanalyze.api.Api
import ch.heigvd.scanalyze.api.ApiCallback
import ch.heigvd.scanalyze.api.ApiRequest
import ch.heigvd.scanalyze.api.HttpMethod
import ch.heigvd.scanalyze.databinding.ActivityReceiptDetailBinding
import ch.heigvd.scanalyze.receipt.JsonReceipt
import ch.heigvd.scanalyze.api.ApiResponse
import com.google.gson.Gson
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReceiptDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptDetailBinding
    private lateinit var apiResponse: ApiResponse
    private lateinit var recyclerView: RecyclerView
    private lateinit var gson: Gson
    private lateinit var bitmap: Bitmap
    private lateinit var receiptId: String
    private var bitmapFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiptOverview: JsonReceipt? = intent.getParcelableExtra("receipt")

        recyclerView = binding.recyclerViewDetailReceipts
        gson = Gson()

        // Get the receipt id
        receiptId = receiptOverview?.id ?: ""

        if (receiptOverview != null) {

            // Display the receipt image if it exists
            if (!receiptOverview.imgFilePath.isNullOrEmpty()) {
                bitmap = BitmapFactory.decodeFile(receiptOverview.imgFilePath)
                bitmapFile = File(receiptOverview.imgFilePath!!)
                binding.imageViewScannedImage.setImageBitmap(bitmap)
            }

            try {
                binding.textViewDetailDate.text = LocalDateTime
                    .parse(receiptOverview.date, DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            } catch (e: Exception) {
                binding.textViewDetailDate.text = "Date unknown"
            }

            binding.textViewDetailShop.text = receiptOverview.shopBranch ?: ""
            binding.imageViewDetailShopIcon.setImageResource(Shop.func.fromString(receiptOverview.shopName).resourceImage)
            binding.textViewDetailTotal.text = String.format("%.2f", receiptOverview.total)
            binding.recyclerViewDetailReceipts.adapter = ReceiptDetailAdapter(receiptOverview)

            if (receiptOverview.httpMethod != null && receiptOverview.httpMethod == HttpMethod.POST) {
                postData(receiptOverview)
            }else{
               getData()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Delete the bitmap show after the scan
        Log.e("ReceiptDetail", "Destroying bitmap file")
        bitmapFile?.delete()
    }

    private fun postData(receipt: JsonReceipt){
        val apiRequest = ApiRequest(receipt)
        val json = gson.toJson(apiRequest)

        Api.postReceipt(json, object : ApiCallback {
            override fun onSuccess(response: String) {

                // parse the received json
                apiResponse = gson.fromJson(response, ApiResponse::class.java)

                // Once we have the data, populate the recycleView
                runOnUiThread {
                    recyclerView.adapter = ReceiptDetailAdapter(apiResponse.receiptDetail)
                }
            }

            override fun onFailure(errorMessage: String) {
                runOnUiThread { Utils.showErrorDialog(errorMessage, this@ReceiptDetailActivity) }
            }
        }, this@ReceiptDetailActivity)
    }

    private fun getData() {
        //Get the data from the api
        Api.getReceiptDetail(receiptId, object : ApiCallback {
            override fun onSuccess(response: String) {
                // parse the received json
                apiResponse = gson.fromJson(response, ApiResponse::class.java)

                // Once we have the data, populate the recycleView
                runOnUiThread {
                    recyclerView.adapter = ReceiptDetailAdapter(apiResponse.receiptDetail)
                }
            }

            override fun onFailure(errorMessage: String) {
                runOnUiThread { Utils.showErrorDialog(errorMessage, this@ReceiptDetailActivity) }
            }
        }, this@ReceiptDetailActivity)
    }
}

