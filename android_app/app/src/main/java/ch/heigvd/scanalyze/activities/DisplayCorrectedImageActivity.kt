package ch.heigvd.scanalyze.activities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import ch.heigvd.scanalyze.R
import ch.heigvd.scanalyze.databinding.ActivityDisplayCorrectedImageBinding

class DisplayCorrectedImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayCorrectedImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayCorrectedImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val correctedFilePath = intent.getStringExtra("corrected_image_path")
        val bitmap = BitmapFactory.decodeFile(correctedFilePath)

        val imageView: ImageView = findViewById(R.id.image_view_corrected)
        imageView.setImageBitmap(bitmap)
    }
}