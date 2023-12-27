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
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import good.damn.audiovisualizer.AudioRecorder

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dm = resources.displayMetrics

        val root = FrameLayout(this)
        val vectorView = VectorView(this)
        val textView = TextView(this)
        textView.setBackgroundColor(0)
        textView.movementMethod = ScrollingMovementMethod()

        root.addView(vectorView)
        root.addView(textView,-1,350)

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
                        textView.text = "$sample"
                    }
                }
            })

            vectorView.setOnClickListener {
                audioRecord.apply {
                    if (isRecording()) {
                        release()
                        vectorView.startAnimation(true)
                        return@apply
                    }
                    startRecording()
                    vectorView.startAnimation()
                }

            }
        }

        vectorView.setStateVertices(
            arrayOf(
                NormVertex(
                    0.8f,0.5f,
                    0.8f,0.2f, dm
                ),
                NormVertex(
                    0.2f,0.2f,
                    0.8f,0.8f, dm
                ),
                NormVertex(
                    0.2f,0.8f,
                    0.2f, 0.8f, dm
                ),
                NormVertex(
                    0.2f,0.8f,
                    0.2f,0.2f, dm
                )
            )
        )

        setContentView(root)

        permissionL.launch(Manifest.permission.RECORD_AUDIO)
    }
}