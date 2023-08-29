package ch.heigvd.scanalyze

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Display
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
    private lateinit var graphicOverlay: GraphicOverlay
    private lateinit var textOverlay: TextOverlay
    private lateinit var previewView: PreviewView

    // Constants
    private val cameraPermission = Manifest.permission.CAMERA
    private val requestCodeCamera = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_preview)

        // Initialize graphicOverlay
        graphicOverlay = findViewById(R.id.graphic_overlay)
        previewView = findViewById(R.id.previewView)

        // Initialize textOverlay and add it to the graphicOverlay
        textOverlay = TextOverlay(graphicOverlay)
        graphicOverlay.add(textOverlay)

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
            previewView.post{
                startCameraPreview()
            }
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

            val rotation = windowManager.defaultDisplay.rotation


            val preview = Preview.Builder()
                .setTargetRotation(rotation)
                .setTargetResolution(Size(previewView.width, previewView.height))
                .build()
                .apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            var hasSetOverlayInfo = false

            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                // Only set overlay info once, assuming the camera resolution doesn't change during the session.
                if (!hasSetOverlayInfo) {
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    val imageWidth = imageProxy.width
                    val imageHeight = imageProxy.height
                    graphicOverlay.setCameraInfo(imageWidth, imageHeight, rotationDegrees)
                    hasSetOverlayInfo = true
                }
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
                val visionTextRects = visionText.textBlocks.map { block ->
                    RectF(block.boundingBox)
                }
                textOverlay.updateBoundingBoxes(visionTextRects)
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Failed to process image: ${e.message}")
                imageProxy.close()
            }
    }
}
