package ch.heigvd.scanalyze.image_processing

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import kotlin.math.sqrt


object ReceiptPreprocessor {

    fun correctRotation(file: File): Bitmap? {

        // Load image into Mat object
        val inputMat = Imgcodecs.imread(file.absolutePath, Imgcodecs.IMREAD_GRAYSCALE)

        //Delete original picture that has been taken
        file.delete()

        if (inputMat.empty()) throw RuntimeException("OpenCV: Failed to read the image")

        // Preprocessing to get a well-defined receipt on the image to work on
        val processedMat = preprocessing(inputMat)

        // Get the corners of the rectangle that has been approximated from the processed mat
        val corners = approximateRectangle(processedMat)

        if (corners.size != 4) {
            inputMat.release()
            processedMat.release()
            throw RuntimeException("Preprocessing failed: Receipt not found correctly!")
        }

        // Get mass center of the receipt shape
        val center = getMassCenter(corners)

        // Sort the 4 corners of the receipt
        sortCorners(corners, center)

        // Draw the input image
        drawLines(inputMat, corners)
        drawPoints(inputMat, corners)
        drawMassCenter(inputMat, center)

        // Create a mat that match the output height and width
        val outputMat = getSizedMat(corners)

        // Create a list of points that represent the corners of the rectangle
        // But with the dimension of the output mat
        val quadPts: MutableList<Point> = ArrayList()
        quadPts.add(Point(0.0, 0.0))
        quadPts.add(Point(outputMat.cols().toDouble(), 0.0))
        quadPts.add(Point(outputMat.cols().toDouble(), outputMat.rows().toDouble()))
        quadPts.add(Point(0.0, outputMat.rows().toDouble()))

        val perspectiveCorners = MatOfPoint2f(*corners.toTypedArray())
        val quadMat = MatOfPoint2f()
        quadMat.fromList(quadPts)

        //Transform the source image to the dimensions of the detected rectangle
        val transformMatrix = Imgproc.getPerspectiveTransform(perspectiveCorners, quadMat)
        Imgproc.warpPerspective(inputMat, outputMat, transformMatrix, outputMat.size())

        // Improve contrast for OCR readability
        val enhancedOutputMat = enhanceContrast(outputMat)

        //Generate a bitmap from the corrected mat
        val outputImage = enhancedOutputMat.toBitmap(Bitmap.Config.ARGB_8888)

        // Release all the native resources as they are not garbage collected
        inputMat.release()
        processedMat.release()
        perspectiveCorners.release()
        quadMat.release()
        transformMatrix.release()
        outputMat.release()

        return outputImage
    }

    private fun preprocessing(inputMat: Mat): Mat {

        // Apply a blur for the thresholding to be effective
        val blurMat = Mat()
        Imgproc.blur(inputMat, blurMat, Size(25.0, 25.0))

        // Thresholding to to separate the foreground from the background
        val threshMat = Mat()
        Imgproc.threshold(blurMat, threshMat, 0.0, 255.0, Imgproc.THRESH_OTSU)

        // Apply dilation to reduce noise
        val dElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(100.0, 100.0))
        Imgproc.dilate(threshMat, threshMat, dElem)

        // Apply erosion to get the (almost) initial shape size
        val eElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(80.0, 80.0))
        Imgproc.erode(threshMat, threshMat, eElem)

        // Release all the native resources as they are not garbage collected
        blurMat.release()
        dElem.release()
        eElem.release()

        return threshMat
    }

    private fun sortCorners(corners: MutableList<Point>, center: Point) {

        // Sort the corners into top and bottom based on their relationship with the center
        val top = corners.filter { it.y < center.y }.sortedBy { it.x }
        val bottom = corners.filter { it.y >= center.y }.sortedBy { it.x }

        corners[0] = top[0]  // Top-left
        corners[1] = top[1]  // Top-right
        corners[2] = bottom[1] // Bottom-right
        corners[3] = bottom[0] // Bottom-left
    }

    private fun getMassCenter(points: MutableList<Point>): Point {

        val center = Point()
        points.forEach { center.x += it.x; center.y += it.y }
        center.x /= points.size
        center.y /= points.size

        return center
    }

    private fun approximateRectangle(src: Mat): MutableList<Point> {

        //Find the contours of the receipt
        val contours: MutableList<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        val mode = Imgproc.RETR_EXTERNAL
        val method = Imgproc.CHAIN_APPROX_SIMPLE
        Imgproc.findContours(src, contours, hierarchy, mode, method)

        // Sort contours to have the largest rectangle (our receipt)
        val sortedContours = contours.sortedByDescending { Imgproc.contourArea(it) }

        // Get all the points that constitute the contour of the receipt
        val contourPoints = MatOfPoint2f(*sortedContours[0].toArray())

        // maximum distance from contour to approximated contour
        // This parameter is used to find a square and not a polygon with more than 4 corners
        val epsilon = 0.02 * Imgproc.arcLength(contourPoints, true)

        // Get the contour as a polygonal shape (hopefully we get a rectangle)
        val rectangleCorners = MatOfPoint2f()
        val contourCurve = MatOfPoint2f(*sortedContours[0].toArray())
        Imgproc.approxPolyDP(contourCurve, rectangleCorners, epsilon, true)

        // Transform a the mat of points to a list
        val corners = MatOfPoint(*rectangleCorners.toArray())
        val cornersList: MutableList<Point> = ArrayList()
        corners.toArray().forEach { cornersList.add(Point(it.x, it.y)) }

        // Release all the native resources as they are not garbage collected
        hierarchy.release()
        contourPoints.release()
        contourCurve.release()
        corners.release()
        rectangleCorners.release()

        return cornersList
    }

    private fun drawPoints(
        mat: Mat,
        points: List<Point>,
        radius: Int = 20,
        color: Scalar = Scalar(255.0, 0.0, 0.0)
    ) {
        points.forEach { Imgproc.circle(mat, it, radius, color, -1) }
    }

    private fun drawLines(mat: Mat, intersect: MutableList<Point>) {
        // Draw lines
        for (i in 0 until intersect.size) {
            val point1 = intersect[i]
            val point2 = intersect[(i + 1) % intersect.size] // ensures loop back to the first point
            Imgproc.line(mat, point1, point2, Scalar(0.0, 255.0, 0.0), 10)
        }
    }

    private fun drawMassCenter(
        mat: Mat,
        center: Point,
        radius: Int = 10,
        color: Scalar = Scalar(191.0, 0.0, 255.0)
    ) {
        Imgproc.circle(mat, center, radius, color, -1)
    }

    private fun distance(p1: Point, p2: Point): Double {
        val powX = (p1.x - p2.x) * (p1.x - p2.x)
        val powY = (p1.y - p2.y) * (p1.y - p2.y)
        return sqrt(powX + powY)
    }

    private fun getSizedMat(corners: MutableList<Point>): Mat {

        // Calculate widths and heights of the segments composing the rectangle
        val topWidth = distance(corners[0], corners[1])
        val bottomWidth = distance(corners[2], corners[3])
        val leftHeight = distance(corners[0], corners[3])
        val rightHeight = distance(corners[1], corners[2])

        // Get the dimensions as pixel so we have to convert to Int
        val maxWidth = topWidth.coerceAtLeast(bottomWidth).toInt()
        val maxHeight = leftHeight.coerceAtLeast(rightHeight).toInt()

        //Here we have to convert it back to double as "zeros" takes Double
        return Mat.zeros(Size(maxWidth.toDouble(), maxHeight.toDouble()), CvType.CV_8UC3)
    }

    private fun enhanceContrast(inputMat: Mat): Mat {
        val alpha = 2.0// Contrast control (2.0 means enhance contrast by 100%)
        val beta = -220.0 // Brightness control

        // Create a new Mat with the same size and type as the original, filled with zeros
        val zeros: Mat = Mat.zeros(inputMat.size(), CvType.CV_8UC1)

        // Compute the contrast-enhanced image
        val outputMat = Mat()
        Core.addWeighted(inputMat, alpha, zeros, 1 - alpha, beta, outputMat)

        inputMat.release()

        return outputMat
    }

    fun Mat.toBitmap(config: Bitmap.Config): Bitmap {
        val bitmap = Bitmap.createBitmap(
            this.cols(),
            this.rows(),
            config
        )

        Utils.matToBitmap(this, bitmap)

        return bitmap
    }
}