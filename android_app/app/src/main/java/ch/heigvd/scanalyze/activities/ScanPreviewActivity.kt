package ch.heigvd.scanalyze.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ch.heigvd.scanalyze.databinding.ActivityScanPreviewBinding
import ch.heigvd.scanalyze.image_processing.ReceiptPreprocessor
import ch.heigvd.scanalyze.ocr.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.opencv.android.OpenCVLoader
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

class ScanPreviewActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var imageCapture: ImageCapture
    private lateinit var binding: ActivityScanPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV initialization failed.")
        } else {
            Log.d("OpenCV", "OpenCV initialization succeeded.")
        }

        if (allPermissionsGranted()) {
            startCameraPreview()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0
            )
        }

        binding.buttonCapture.setOnClickListener {
            // Define where the image will be saved
            val file = File(externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")

            // Take the photo, save it, and perform analysis
            imageCapture.takePicture(
                ImageCapture.OutputFileOptions.Builder(file).build(),
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        analyzeImage(file)
                    }

                    override fun onError(error: ImageCaptureException) {
                        runOnUiThread { showToast("Photo capture failed: ${error.message}") }
                    }
                }
            )
        }
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()

            try {
                cameraProvider.bindToLifecycle(
                    this@ScanPreviewActivity,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                // Handle exception (camera is used by another app, the device doesn't have a back camera, etc.)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzeImage(file: File) {

        //Correct the image rotation and perspective of the receipt
        val correctedImage = ReceiptPreprocessor.correctRotation(file)

        if (correctedImage != null) {
            //Save the corrected image
            val imagePath = File(filesDir, "${System.currentTimeMillis()}_corrected}.jpg")

            FileOutputStream(imagePath).use {
                correctedImage.compress(Bitmap.CompressFormat.JPEG, 50, it)
            }

            //Detect the text on the image
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(InputImage.fromBitmap(correctedImage, 0))
                .addOnSuccessListener { visionText ->
                    try {
                        val receipt = visionText.toReceipt()
                        receipt?.imgFilePath = imagePath.absolutePath
                        println(receipt)
                        val intent = Intent(this, ReceiptDetailActivity::class.java)
                        intent.putExtra("receipt", receipt)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            baseContext,
                            "Failed to parse text: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("TextRecognition", "Failed to process image: ${e.message}")
                }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
    }
}