package good.damn.animstatemachine

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import good.damn.statemachine.VectorView
import good.damn.statemachine.vertices.NormVertex
import android.Manifest
import android.annotation.SuppressLint
import android.text.method.ScrollingMovementMethod
import android.util.Size
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import good.damn.audiovisualizer.AudioRecorder
import good.damn.audiovisualizer.BubbleView

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dm = resources.displayMetrics

        val w = dm.widthPixels
        val h = dm.heightPixels

        val nativeSize = Size(
            w, h
        )

        val vectorViewSize = Size(
            75,
            75
        )

        val root = FrameLayout(this)
        val vectorView = VectorView(this)
        val textView = TextView(this)
        val bubbleView = BubbleView(this)

        textView.movementMethod = ScrollingMovementMethod()

        val vectorParams = FrameLayout.LayoutParams(
            vectorViewSize.width,
            vectorViewSize.height
        )

        vectorParams.topMargin = textView.textSize.toInt()
        vectorView.layoutParams = vectorParams


        root.addView(bubbleView)

        root.addView(vectorView)

        root.addView(textView,-1,textView.textSize.toInt())

        val permissionL = registerForActivityResult(ActivityResultContracts
            .RequestPermission()) { isGranted ->
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED) {
                return@registerForActivityResult
            }

            val audioRecord = AudioRecorder()

            audioRecord.setOnSampleListener(object : AudioRecorder.OnSampleListener {
                @SuppressLint("SetTextI18n")
                override fun onSample(
                    sample: Float,
                    digSample: ByteArray) {
                    runOnUiThread {
                        bubbleView.addBubble(sample)
                        textView.text = "$sample"
                    }
                }
            })

            vectorView.setOnClickListener {
                audioRecord.apply {
                    if (isRecording()) {
                        release()
                        vectorView.startAnimation(true)
                        bubbleView.interrupt()
                        return@apply
                    }
                    startRecording()
                    vectorView.startAnimation()
                    bubbleView.listen()
                }
            }
        }

        vectorView.setStateVertices(
            arrayOf(
                NormVertex(
                    0.8f,0.5f,
                    0.8f,0.2f, vectorViewSize
                ),
                NormVertex(
                    0.2f,0.2f,
                    0.8f,0.8f, vectorViewSize
                ),
                NormVertex(
                    0.2f,0.8f,
                    0.2f, 0.8f, vectorViewSize
                ),
                NormVertex(
                    0.2f,0.8f,
                    0.2f,0.2f, vectorViewSize
                )
            )
        )

        setContentView(root)

        permissionL.launch(Manifest.permission.RECORD_AUDIO)
    }
}