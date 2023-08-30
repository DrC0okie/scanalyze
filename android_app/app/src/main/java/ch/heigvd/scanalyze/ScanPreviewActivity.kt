package ch.heigvd.scanalyze

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
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
            val bitmap = Bitmap.createBitmap(
                correctedMat.cols(),
                correctedMat.rows(),
                Bitmap.Config.ARGB_8888
            )


            Utils.matToBitmap(correctedMat, bitmap)

            //Release native resources
            mat.release()
            correctedMat.release()

            val imagePath =
                File(
                    externalMediaDirs.first(),
                    "${System.currentTimeMillis()}_corrected}.png"
                )

            val outputStream = FileOutputStream(imagePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val intent = Intent(this, DisplayCorrectedImageActivity::class.java)
            intent.putExtra("corrected_image_path", imagePath.absolutePath)
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

        // Step 1: Convert the image to grayscale
        val grayMat = Mat()
        Imgproc.cvtColor(inputMat, grayMat, Imgproc.COLOR_BGR2GRAY)


        val blurMat = Mat()
        Imgproc.blur(grayMat, blurMat, Size(40.0, 40.0))

        grayMat.release()

        // 2. Thresholding
        val threshMat = Mat()
        Imgproc.threshold(
            blurMat,
            threshMat,
            0.0,
            255.0,
            Imgproc.THRESH_OTSU
        )

        blurMat.release()

        // Apply dilation to connect broken lines
        Imgproc.dilate(
            threshMat,
            threshMat,
            Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(500.0, 500.0))
        )
        Imgproc.erode(
            threshMat,
            threshMat,
            Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(400.0, 400.0))
        )

        val contours: MutableList<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        Imgproc.findContours(
            threshMat,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        threshMat.release()
        hierarchy.release()

        val curve = MatOfPoint2f(*contours[0].toArray())
        val epsilon = 0.02 * Imgproc.arcLength(curve, true)
        val approxRect = MatOfPoint2f()
        Imgproc.approxPolyDP(MatOfPoint2f(*contours[0].toArray()), approxRect, epsilon, true)
        val approxList = MatOfPoint(*approxRect.toArray())

        curve.release()
        approxRect.release()

        if (approxList.rows() != 4) {
            println("Receipt border not found correctly!")
            return inputMat  // or return an empty Mat or throw an exception
        }

        val intersect: MutableList<Point> = ArrayList()
        approxList.toArray().forEach { intersect.add(Point(it.x, it.y)) }

        // Get mass center
        val center = Point()
        intersect.forEach { center.x += it.x; center.y += it.y }

        center.x /= intersect.size
        center.y /= intersect.size

        sortCorners(intersect, center)

        // Draw lines
        drawLines(inputMat, intersect)

        // Draw corner points
        drawPoints(inputMat, intersect)

        // Draw mass center
        Imgproc.circle(inputMat, center, 10, Scalar(191.0, 0.0, 255.0), -1)

        val topWidth = sqrt(
            (intersect[0].x - intersect[1].x).pow(2.0) + (intersect[0].y - intersect[1].y).pow(
                2.0
            )
        )
        val bottomWidth = sqrt(
            (intersect[2].x - intersect[3].x).pow(2.0) + (intersect[2].y - intersect[3].y).pow(
                2.0
            )
        )
        val leftHeight = sqrt(
            (intersect[0].x - intersect[3].x).pow(2.0) + (intersect[0].y - intersect[3].y).pow(
                2.0
            )
        )
        val rightHeight = sqrt(
            (intersect[1].x - intersect[2].x).pow(2.0) + (intersect[1].y - intersect[2].y).pow(
                2.0
            )
        )

        val maxWidth = topWidth.coerceAtLeast(bottomWidth).toInt()
        val maxHeight = leftHeight.coerceAtLeast(rightHeight).toInt()
        val quad = Mat.zeros(Size(maxWidth.toDouble(), maxHeight.toDouble()), CvType.CV_8UC3)
        val quadPts: MutableList<Point> = ArrayList()
        quadPts.add(Point(0.0, 0.0))
        quadPts.add(Point(quad.cols().toDouble(), 0.0))
        quadPts.add(Point(quad.cols().toDouble(), quad.rows().toDouble()))
        quadPts.add(Point(0.0, quad.rows().toDouble()))
        val quadMat = MatOfPoint2f()
        quadMat.fromList(quadPts)

        val transmtx =
            Imgproc.getPerspectiveTransform(MatOfPoint2f(*intersect.toTypedArray()), quadMat)
        Imgproc.warpPerspective(inputMat, quad, transmtx, quad.size())

        quadMat.release()
        transmtx.release()

        return quad
    }

    private fun drawPoints(
        mat: Mat,
        points: List<Point>,
        radius: Int = 20,
        color: Scalar = Scalar(255.0, 0.0, 0.0)
    ) {
        points.forEach { Imgproc.circle(mat, it, radius, color, -1) }
    }

    private fun sortCorners(corners: MutableList<Point>, center: Point) {
        val top: MutableList<Point> = ArrayList()
        val bot: MutableList<Point> = ArrayList()

        for (corner in corners) {
            if (corner.y < center.y) {
                top.add(corner)
            } else {
                bot.add(corner)
            }
        }

        corners.clear()

        if (top.size == 2 && bot.size == 2) {
            val tl = if (top[0].x > top[1].x) top[1] else top[0]
            val tr = if (top[0].x > top[1].x) top[0] else top[1]
            val bl = if (bot[0].x > bot[1].x) bot[1] else bot[0]
            val br = if (bot[0].x > bot[1].x) bot[0] else bot[1]

            corners.add(tl)
            corners.add(tr)
            corners.add(br)
            corners.add(bl)
        }
    }


    private fun drawLines(mat: Mat, intersect: MutableList<Point>) {
        // Draw lines
        for (i in 0 until intersect.size) {
            val point1 = intersect[i]
            val point2 = intersect[(i + 1) % intersect.size] // ensures loop back to the first point
            Imgproc.line(mat, point1, point2, Scalar(0.0, 255.0, 0.0), 10)
        }
    }
}