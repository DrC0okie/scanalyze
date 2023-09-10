package ch.heigvd.scanalyze.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.scanalyze.Utils.Utils
import ch.heigvd.scanalyze.adapters.ReceiptAdapter
import ch.heigvd.scanalyze.api.Api.getReceiptsOverview
import ch.heigvd.scanalyze.api.ApiCallback
import ch.heigvd.scanalyze.databinding.ActivityReceiptBinding
import ch.heigvd.scanalyze.api.ApiResponse
import com.google.gson.Gson

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding
    private lateinit var apiResponse: ApiResponse
    private lateinit var recyclerView: RecyclerView
    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerViewReceipts
        gson = Gson()

        //Get the data from the api on creating the page
        getReceiptsOverview(object : ApiCallback {
            override fun onSuccess(response: String) {
                // parse the received json
                apiResponse = gson.fromJson(response, ApiResponse::class.java)

                // Once we have the data, populate the recycleView
                runOnUiThread {
                    //Sort the receipts
                    val receipts =
                        apiResponse.receipts.sortedByDescending { it.date }.toTypedArray()

                    //feed the adapter
                    recyclerView.adapter = ReceiptAdapter(receipts)
                }
            }

            override fun onFailure(errorMessage: String) {
                runOnUiThread { Utils.showErrorDialog(errorMessage, this@ReceiptActivity) }
            }
        }, this)


        // Get the data from the api on "swipe to refresh"
        binding.swipeRefreshLayout.setOnRefreshListener {

            getReceiptsOverview(object : ApiCallback {
                override fun onSuccess(response: String) {

                    apiResponse = gson.fromJson(response, ApiResponse::class.java)

                    runOnUiThread {

                        val receipts =
                            apiResponse.receipts.sortedByDescending { it.date }.toTypedArray()

                        recyclerView.adapter = ReceiptAdapter(receipts)

                        //Stop the spinning wheel
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }

                override fun onFailure(errorMessage: String) {
                    runOnUiThread {
                        Utils.showErrorDialog(errorMessage, this@ReceiptActivity)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }, this)
        }
    }

}