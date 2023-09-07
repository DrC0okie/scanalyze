package ch.heigvd.scanalyze.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ch.heigvd.scanalyze.Shop.Shop
import ch.heigvd.scanalyze.Utils.Utils
import ch.heigvd.scanalyze.adapters.ReceiptDetailAdapter
import ch.heigvd.scanalyze.api.Api
import ch.heigvd.scanalyze.api.ApiCallback
import ch.heigvd.scanalyze.api.ApiRequest
import ch.heigvd.scanalyze.api.HttpMethod
import ch.heigvd.scanalyze.databinding.ActivityReceiptDetailBinding
import ch.heigvd.scanalyze.receipt.Receipt
import ch.heigvd.scanalyze.api.ApiResponse
import com.google.gson.Gson
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReceiptDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptDetailBinding
    private lateinit var apiResponse: ApiResponse
    private lateinit var gson: Gson
    private lateinit var bitmap: Bitmap
    private lateinit var receiptId: String
    private var bitmapFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get the receipt from previous activity
        val receiptOverview: Receipt? = intent.getParcelableExtra("receipt")

        //Init Gson
        gson = Gson()

        if (receiptOverview != null) {

            initView(receiptOverview)

            if (receiptOverview.httpMethod != null && receiptOverview.httpMethod == HttpMethod.POST) {
                postData(receiptOverview)
            }else{
               getData()
            }
        }
    }

    private fun initView(receipt: Receipt){

        receiptId = receipt.id

        with(binding) {
            // Display the receipt image if it exists
            if (!receipt.imgFilePath.isNullOrEmpty()) {
                bitmap = BitmapFactory.decodeFile(receipt.imgFilePath)
                bitmapFile = File(receipt.imgFilePath!!)
                imageViewScannedImage.setImageBitmap(bitmap)
            }

            try {
                textViewDetailDate.text = LocalDateTime
                    .parse(receipt.date, DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            } catch (e: Exception) {
                textViewDetailDate.text = "Date unknown"
            }

            textViewDetailShop.text = receipt.shopBranch ?: ""
            textViewDetailTotal.text = String.format("%.2f", receipt.total)
            imageViewDetailShopIcon.setImageResource(
                Shop.func.fromString(receipt.shopName).resourceImage)

            recyclerViewDetailReceipts.adapter = ReceiptDetailAdapter(receipt)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Delete the bitmap show after the scan
        Log.e("ReceiptDetail", "Destroying bitmap file")
        bitmapFile?.delete()
    }

    private fun postData(receipt: Receipt){
        val apiRequest = ApiRequest(receipt)
        val json = gson.toJson(apiRequest)

        Api.postReceipt(json, object : ApiCallback {
            override fun onSuccess(response: String) {

                // parse the received json
                apiResponse = gson.fromJson(response, ApiResponse::class.java)

                // Once we have the data, populate the recycleView
                runOnUiThread {
                    binding.recyclerViewDetailReceipts.adapter = ReceiptDetailAdapter(apiResponse.receiptDetail)
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
                    binding.recyclerViewDetailReceipts.adapter = ReceiptDetailAdapter(apiResponse.receiptDetail)
                }
            }

            override fun onFailure(errorMessage: String) {
                runOnUiThread { Utils.showErrorDialog(errorMessage, this@ReceiptDetailActivity) }
            }
        }, this@ReceiptDetailActivity)
    }
}

