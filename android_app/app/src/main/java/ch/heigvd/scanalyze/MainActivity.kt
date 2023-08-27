package ch.heigvd.scanalyze

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.time.LocalDate
import java.util.Date

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_receipts).setOnClickListener{
            startActivity(Intent(this, ReceiptActivity::class.java))
        }

        findViewById<Button>(R.id.button_scan).setOnClickListener{
            startActivity(Intent(this, ScanPreviewActivity::class.java))
        }

    }
}