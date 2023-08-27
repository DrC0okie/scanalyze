package ch.heigvd.scanalyze

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import java.io.File
import java.util.concurrent.Executors

class ScanPreviewActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_preview)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0
            )
        }

        val captureButton: Button = findViewById(R.id.button_capture)
        captureButton.setOnClickListener {
            // Define where the image will be saved
            val file = File(externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()


            // Take the photo and save it to the file
            imageCapture.takePicture(
                outputFileOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(file)
                        val message = "Photo capture succeeded: $savedUri"
                        runOnUiThread {
                            Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
                        }

                        // Run your analysis here on the saved image file
                        analyzeImage(file)
                    }

                    override fun onError(error: ImageCaptureException) {
                        runOnUiThread {
                            Toast.makeText(
                                baseContext,
                                "Photo capture failed: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(findViewById<PreviewView>(R.id.previewView).surfaceProvider) }
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
        // Create an InputImage from the saved file
        val image = InputImage.fromFilePath(this, Uri.fromFile(file))

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Handle the text found in the image
                // Get the public downloads directory
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                // Create a file in the downloads directory
                val resultFile = File(path, "visionText.txt")

                try {
                    // Write the text to the file
                    resultFile.writeText(visionText.text)
                    Toast.makeText(baseContext, "Text saved to ${file.path}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(baseContext, "Failed to save text: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Failed to process image: ${e.message}")
            }
    }
}
