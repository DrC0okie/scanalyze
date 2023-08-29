package ch.heigvd.scanalyze

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class DisplayCorrectedImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_corrected_image)

        val correctedFilePath = intent.getStringExtra("corrected_image_path")
        val bitmap = BitmapFactory.decodeFile(correctedFilePath)

        val imageView: ImageView = findViewById(R.id.image_view_corrected)
        imageView.setImageBitmap(bitmap)
    }
}