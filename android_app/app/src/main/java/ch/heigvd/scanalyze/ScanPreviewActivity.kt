package ch.heigvd.scanalyze

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class ScanPreviewActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_preview)

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

    private fun startCameraPreview() {
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
        // Load image into Mat object using OpenCV
        val mat = Imgcodecs.imread(file.absolutePath)

        if (mat.empty()) {
            Log.e("OpenCV", "Failed to load image")
            return
        }

        //Correct the image
        val correctedMat = correctRotation(mat)

        try {
            // Convert corrected Mat to Bitmap
            val correctedBitmap =
                Bitmap.createBitmap(
                    correctedMat.cols(),
                    correctedMat.rows(),
                    Bitmap.Config.ARGB_8888
                )
            Utils.matToBitmap(correctedMat, correctedBitmap)

            // Create InputImage from Bitmap
            val image = InputImage.fromBitmap(correctedBitmap, 0)

            //Release native resources
            mat.release()
            correctedMat.release()

            val correctedFile =
                File(externalMediaDirs.first(), "${System.currentTimeMillis()}_corrected.png")

            val outputStream = FileOutputStream(correctedFile)
            correctedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val intent = Intent(this, DisplayCorrectedImageActivity::class.java)
            intent.putExtra("corrected_image_path", correctedFile.absolutePath)
            startActivity(intent)

        } catch (e: Exception) {
            println(e.message)
        }

//        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//        recognizer.process(image)
//            .addOnSuccessListener { visionText ->
//
//                try {
//                    visionText.resolveLines(10)
//                } catch (e: Exception) {
//                    Toast.makeText(baseContext, "Failed to parse text: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("TextRecognition", "Failed to process image: ${e.message}")
//            }

    }

    private fun correctRotation(inputMat: Mat): Mat {
        try {

            val concatList = mutableListOf<Mat>()

            // 1. Convert to Grayscale
            val grayMat = Mat()
            Imgproc.cvtColor(inputMat, grayMat, Imgproc.COLOR_BGR2GRAY)
            concatList.add(grayMat.clone())

            // 2. Thresholding
            val threshMat = Mat()
            Imgproc.threshold(
                grayMat,
                threshMat,
                0.0,
                255.0,
                Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU
            )
            concatList.add(threshMat.clone())

            //ROI
            val contours: MutableList<MatOfPoint> = ArrayList()
            val hierarchy = Mat()
            Imgproc.findContours(
                threshMat.clone(),
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
            )

            // Filter and sort contours
            val iterator = contours.iterator()
            while (iterator.hasNext()) {
                val contour = iterator.next()
                val area = Imgproc.contourArea(contour)
                if (area < 100) {
                    iterator.remove()
                }
            }
            contours.sortWith(compareByDescending { Imgproc.contourArea(it) })

            // Assuming the largest contour is the receipt (this may not always be the case)
            var roiMat = threshMat.clone()
            if (contours.isNotEmpty()) {
                val largestContour = contours[0]
                val rect = Imgproc.boundingRect(largestContour)
                val roi = Rect(rect.x, rect.y, rect.width, rect.height)
                roiMat = Mat(threshMat, roi)
                concatList.add(roiMat)
            }

            val edges = Mat()
            Imgproc.Canny(roiMat, edges, 50.0, 150.0)

            val lines = Mat()
            Imgproc.HoughLinesP(edges, lines, 1.0, Math.PI / 180, 50, 50.0, 10.0)

            var angleSum = 0.0
            var count = 0

            // Create a new Mat to draw lines on, with the same dimensions and type as your image
            val linesMat = Mat.zeros(grayMat.size(), grayMat.type())

            for (i in 0 until lines.rows()) {
                val vec = lines[i, 0]
                val x1 = vec[0]
                val y1 = vec[1]
                val x2 = vec[2]
                val y2 = vec[3]

                val length = sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))

                if (length > 20) { // Filter based on length
                    val angle = atan2((y2 - y1), (x2 - x1))
                    angleSum += angle
                    count++
                }
                Imgproc.line(linesMat, Point(x1, y1), Point(x2, y2), Scalar(255.0, 255.0, 255.0), 1)
            }

            // Add the Mat with drawn lines to concatList
            concatList.add(linesMat.clone())

            if (count == 0) return inputMat

            val averageAngle = angleSum / count
            val angleInDegrees = Math.toDegrees(averageAngle)
            println("Calculated angle in degrees: $angleInDegrees")

            val rotatedMat = Mat()
            val center = Point((roiMat.width() / 2).toDouble(), (roiMat.height() / 2).toDouble())
            val rotationMatrix = Imgproc.getRotationMatrix2D(center, -angleInDegrees, 1.0)
            val size = Size(roiMat.width().toDouble(), roiMat.height().toDouble())
            Imgproc.warpAffine(inputMat, rotatedMat, rotationMatrix, size, Imgproc.INTER_LINEAR)

            concatList.add(rotatedMat)

            val resultMat = Mat()

            Core.vconcat(concatList, resultMat)

            lines.release()
            edges.release()
            concatList.forEach { it.release() }

            return resultMat
        } catch (e: Exception) {
            println(e.message)
        }
        return inputMat
    }


}
