package ch.heigvd.scanalyze.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.scanalyze.Utils.Utils
import ch.heigvd.scanalyze.adapters.ReceiptAdapter
import ch.heigvd.scanalyze.api.Api.getReceiptsData
import ch.heigvd.scanalyze.api.ApiCallback
import ch.heigvd.scanalyze.databinding.ActivityReceiptBinding
import ch.heigvd.scanalyze.statistics.ApiResponse
import com.google.gson.Gson
import java.time.LocalDate

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding
    private lateinit var receipts: ApiResponse
    private lateinit var recyclerView: RecyclerView
    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerViewReceipts
        gson = Gson()

        //Get the data from the api
        getReceiptsData(object : ApiCallback {
            override fun onSuccess(response: String) {
                // parse the received json
                receipts = gson.fromJson(response, ApiResponse::class.java)

                // Once we have the data, populate the recycleView
                runOnUiThread {
                    recyclerView.adapter = ReceiptAdapter(receipts.receipts.toTypedArray())
                }
            }

            override fun onFailure(errorMessage: String) {
                runOnUiThread { Utils.showErrorDialog(errorMessage, this@ReceiptActivity) }
            }
        }, this)
    }

}