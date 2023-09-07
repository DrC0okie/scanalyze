package ch.heigvd.scanalyze.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.scanalyze.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonStats.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }

        binding.buttonReceipts.setOnClickListener {
            startActivity(Intent(this, ReceiptActivity::class.java))
        }

        binding.buttonScan.setOnClickListener {
            startActivity(Intent(this, ScanPreviewActivity::class.java))
        }
    }
}