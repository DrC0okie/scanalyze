package ch.heigvd.scanalyze.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ch.heigvd.scanalyze.Utils.Utils.showErrorDialog
import ch.heigvd.scanalyze.api.Api
import ch.heigvd.scanalyze.databinding.ActivityScanPreviewBinding
import ch.heigvd.scanalyze.image_processing.ReceiptPreprocessor
import ch.heigvd.scanalyze.ocr.*
import ch.heigvd.scanalyze.receipt.Receipt
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.opencv.android.OpenCVLoader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

            try {
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
                            runOnUiThread { showErrorDialog(error, this@ScanPreviewActivity) }
                        }
                    }
                )
            } catch (e: Exception) {
                runOnUiThread { showErrorDialog(e, this) }
            }
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

    private fun processImageWithOCR(image: Bitmap, imagePath: File){
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
            }
            .addOnFailureListener { e ->
                runOnUiThread { showErrorDialog(e, this) }
            }
    }

    private fun handleOcrSuccess(receipt: Receipt) {
        try {

//            postReceiptToAPI(receipt, Api.endpoints.postReceipt)
            displaySnackBar(receipt)


        } catch (e: Exception) {
            runOnUiThread { showErrorDialog(e, this) }
        }
    }

    private fun postReceiptToAPI(receipt: Receipt, endPoint: String) {

        // Get the client
        val client = OkHttpClient()

        //Create the request body with header
        val requestBody = receipt.toJson()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // Build the request
        val request = Request.Builder().url(endPoint).post(requestBody).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showErrorDialog(e, this@ScanPreviewActivity)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    showErrorDialog(
                        IOException("Error in response: ${response.code}"),
                        this@ScanPreviewActivity
                    )
                } else {
                    val intent = Intent(
                        this@ScanPreviewActivity,
                        ReceiptDetailActivity::class.java
                    )
                    intent.putExtra("receipt", receipt)
                    startActivity(intent)
                }
            }
        })
    }

    private fun displaySnackBar(receipt: Receipt)
    {
        // Inflate the custom Snackbar layout
        val inflater = layoutInflater
        val customSnackbarView = inflater.inflate(ch.heigvd.scanalyze.R.layout.custom_snackbar_layout, null)

        // Get the root view and add the custom Snackbar view to it
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rootView.addView(customSnackbarView, params)

        // Prepare the animation
        val slideDown = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, -1.0f,
            Animation.RELATIVE_TO_SELF, 0.0f
        )
        slideDown.duration = 500
        slideDown.fillAfter = true

        // Start the animation
        customSnackbarView.startAnimation(slideDown)

        // Hide the Snackbar after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            rootView.removeView(customSnackbarView)
            val intent = Intent(
                this@ScanPreviewActivity,
                ReceiptDetailActivity::class.java
            )
            intent.putExtra("receipt", receipt)
            startActivity(intent)
        }, 2000)  // 3 seconds delay
    }
}