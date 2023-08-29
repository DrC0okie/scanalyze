package ch.heigvd.scanalyze

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.InflateException
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

class ScanPreviewActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var imageCapture: ImageCapture
    private lateinit var textOverlay: TextOverlay

    // Constants
    private val cameraPermission = Manifest.permission.CAMERA
    private val requestCodeCamera = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_preview)

        textOverlay = findViewById(R.id.text_overlay)

        setupPermissions()

        findViewById<Button>(R.id.button_capture).setOnClickListener {
            captureAndAnalyzeImage()
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPermissions() {
        val permissions = mutableListOf(cameraPermission)

        if (allPermissionsGranted(permissions.toTypedArray())) {
            startCameraPreview()
        } else {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), requestCodeCamera)
        }
    }

    private fun captureAndAnalyzeImage() {
        // Setup image capture listener which is triggered after photo has been taken
        imageCapture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    analyzeImage(image)
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast("Photo capture failed: ${exception.message}")
                }
            }
        )
    }

    private fun allPermissionsGranted(permissions: Array<String>) = permissions.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(findViewById<PreviewView>(R.id.previewView).surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                analyzeImage(imageProxy)
            }

            cameraProvider.unbindAll()
            try {
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture, imageAnalysis
                )
            } catch (e: Exception) {
                showToast("Camera initialization failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        val image =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val visionTextRects = visionText.textBlocks.flatMap { block ->
                    block.lines.flatMap { line ->
                        line.elements.map { element ->
                            // Convert element's bounding box to RectF or your custom object
                            RectF(element.boundingBox)
                        }
                    }
                }
                textOverlay.updateRects(visionTextRects)
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Failed to process image: ${e.message}")
                imageProxy.close()
            }
    }
}
