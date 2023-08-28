package ch.heigvd.scanalyze

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
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
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class ScanPreviewActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var imageCapture: ImageCapture

    // Constants
    private val cameraPermission = Manifest.permission.CAMERA
    private val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val requestCodeCamera = 0
    private val requestCodeStorage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_preview)

        setupPermissions()

        findViewById<Button>(R.id.button_capture).setOnClickListener {
            captureAndAnalyzeImage()
        }
    }

    // Utility function for showing Toast messages
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Check permissions and start camera
    private fun setupPermissions() {
        val permissions = mutableListOf(cameraPermission)

        if (Build.VERSION.SDK_INT < 29) {
            permissions.add(storagePermission)
        }

        if (allPermissionsGranted(permissions.toTypedArray())) {
            startCameraPreview()
        } else {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), requestCodeCamera)
        }
    }

    // Capture an image and analyze it
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

    // Check if all permissions are granted
    private fun allPermissionsGranted(permissions: Array<String>) = permissions.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Start the camera
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

            cameraProvider.unbindAll()
            try {
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
                )
            } catch (e: Exception) {
                showToast("Camera initialization failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // Analyze the captured image
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        val image =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val imageAnnotator = ImageAnnotator()
                val bitmap = imageProxyToBitmap(imageProxy) ?: return@addOnSuccessListener
                val annotatedBitmap = imageAnnotator.annotateImage(bitmap, visionText)
                // Save this annotatedBitmap to your desired location
                saveBitmapToStorage(annotatedBitmap, this)


                // Close the ImageProxy
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Failed to process image: ${e.message}")
                // Close the ImageProxy
                imageProxy.close()
            }
    }

    private fun saveBitmapToStorage(bitmap: Bitmap, context: Context) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Annotated_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri: Uri? = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )

        uri?.let {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            } else {
                showToast("Error: OutputStream is null")
            }
            outputStream?.flush()
            outputStream?.close()
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {

        val format = imageProxy.format
        var bitmap: Bitmap? = null

        if (format == ImageFormat.YUV_420_888) {
            val yPlane = imageProxy.planes.getOrNull(0) ?: run {
                Log.e("TextRecognition", "Y-plane is null")
                return null
            }

            val yBuffer = yPlane.buffer ?: run {
                Log.e("TextRecognition", "Y-buffer is null")
                return null
            }

            val ySize = yBuffer.remaining()
            val yData = ByteArray(ySize)
            yBuffer.get(yData) // read buffer into byte array

            val width = imageProxy.width
            val height = imageProxy.height

            val pixels = IntArray(width * height)

            for (i in 0 until height) {
                for (j in 0 until width) {
                    val y = yData[i * width + j].toInt()
                    // converting pixel to RGB format; a pixel is assumed to be in full opacity (0xFF as alpha)
                    pixels[i * width + j] = Color.argb(0xFF, y, y, y)
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        } else {
            // Fallback for other formats
            val buffer = imageProxy.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            BitmapFactory.Options().run {
                inMutable = true
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
            }
        }

        return bitmap

    }

}
