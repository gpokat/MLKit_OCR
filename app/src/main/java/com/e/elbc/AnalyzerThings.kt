import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors
import androidx.core.content.ContextCompat
import com.e.elbc.DrawThings

class AnalyzerThings(applicationContext: Context, drawThings: DrawThings) {

    private val nonBlocking = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST

    val imageAnalysis = ImageAnalysis.Builder()
        //.setTargetResolution(Size(480,640))
        .setBackpressureStrategy(nonBlocking)
        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
        //.setTargetRotation(Surface.ROTATION_0)
        .build()

    val imageAnalyzer = imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(),ThingsAnalyser(applicationContext, drawThings))

    class ThingsAnalyser(applicationContext: Context, drawThings: DrawThings) : ImageAnalysis.Analyzer {

        val appContex:Context = applicationContext
        val overlay: DrawThings = drawThings

       private fun normalizeOutputRect(inImage: ImageProxy) {
            val rotatedDeg =  inImage.imageInfo.rotationDegrees
            var  isRotated = (rotatedDeg == 90 || rotatedDeg == 270)

            val height = if(isRotated) inImage.width.toFloat() else inImage.height.toFloat()
            val width =  if(isRotated) inImage.height.toFloat() else inImage.width.toFloat()
           overlay.setOverlayScale(height,width)

        }
        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(inImage: ImageProxy) {

            val mImage = inImage.image
            if (mImage != null) {
                val image = InputImage.fromMediaImage(mImage, inImage.imageInfo.rotationDegrees)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                val txtResult = recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        normalizeOutputRect(inImage)
                        val recognizedWords = visionText.textBlocks.flatMap { it.lines }.flatMap { it.elements }
                        overlay.drawWordsBounds(recognizedWords)
                    }
                    .addOnFailureListener { e ->
                        Log.e("NO TEXT FOUND", e.message.toString())
                    }
                    .addOnCompleteListener {
                        mImage.close()
                        inImage.close()
                    }
            }
        }
    }

}