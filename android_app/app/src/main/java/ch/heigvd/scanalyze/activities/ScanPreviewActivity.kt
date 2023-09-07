package ch.heigvd.scanalyze.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ch.heigvd.scanalyze.Utils.Utils.showErrorDialog
import ch.heigvd.scanalyze.api.HttpMethod
import ch.heigvd.scanalyze.databinding.ActivityScanPreviewBinding
import ch.heigvd.scanalyze.image_processing.ReceiptPreprocessor
import ch.heigvd.scanalyze.ocr.*
import ch.heigvd.scanalyze.receipt.Receipt
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.opencv.android.OpenCVLoader
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

class ScanPreviewActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var capture: ImageCapture
    private lateinit var binding: ActivityScanPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeOpenCV()
        initializeCamera()

        binding.buttonCapture.setOnClickListener { tryCaptureImage() }
    }

    private fun initializeCamera() {
        if (allPermissionsGranted()) {
            startCameraPreview()
        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
    }

    private fun initializeOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV initialization failed.")
        }
    }

    private fun tryCaptureImage() {

        // Define where the image will be saved
        val file = File(externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")

        // Take the photo, save it, and perform analysis
        capture.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    analyzeImage(file)
                }

                override fun onError(error: ImageCaptureException) {
                    runOnUiThread { showErrorDialog(error, this@ScanPreviewActivity) }
                }
            }
        )
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

            capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()

            cameraProvider.unbindAll()

            try {
                cameraProvider.bindToLifecycle(
                    this@ScanPreviewActivity,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    capture
                )
            } catch (e: Exception) {
                runOnUiThread { showErrorDialog(e, this) }
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzeImage(file: File) {
        try {
            //Correct the image rotation and perspective of the receipt
            val correctedImage = ReceiptPreprocessor.correctRotation(file)

            if (correctedImage != null) {
                //Save the corrected image
                val imagePath = saveCorrectedImage(correctedImage)

                processImageWithOCR(correctedImage, imagePath)
            }

        } catch (e: Exception) {
            runOnUiThread { showErrorDialog(e, this) }
        }
    }

    private fun saveCorrectedImage(correctedImage: Bitmap): File {
        val imagePath = File(filesDir, "${System.currentTimeMillis()}_corrected.jpg")

        FileOutputStream(imagePath).use {
            correctedImage.compress(Bitmap.CompressFormat.JPEG, 50, it)
        }

        return imagePath
    }

    private fun processImageWithOCR(image: Bitmap, imagePath: File) {
        //Detect the text on the image
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(InputImage.fromBitmap(image, 0))
            .addOnSuccessListener { visionText ->

                try {
                    // Reconstruct the receipt base on the OCR results
                    val receipt = visionText.toReceipt()

                    receipt.imgFilePath = imagePath.absolutePath

                    handleOcrSuccess(receipt)
                } catch (e: Exception) {
                    runOnUiThread { showErrorDialog(e, this) }
                }

            }.addOnFailureListener { e -> runOnUiThread { showErrorDialog(e, this) } }
    }

    private fun handleOcrSuccess(receipt: Receipt) {
        try {
            receipt.httpMethod = HttpMethod.POST

            val intent = Intent(this, ReceiptDetailActivity::class.java)

            intent.putExtra("receipt", receipt)

            startActivity(intent)
        } catch (e: Exception) {
            runOnUiThread { showErrorDialog(e, this) }
        }
    }
}