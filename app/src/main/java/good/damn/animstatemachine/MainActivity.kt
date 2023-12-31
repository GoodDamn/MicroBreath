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
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import good.damn.audiovisualizer.AudioRecorder
import good.damn.audiovisualizer.BubbleView
import good.damn.audiovisualizer.TimerView

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
            (w * 0.1f).toInt(),
            (w * 0.1f).toInt()
        )

        val timerViewSize = Size(
            (w * 0.6f).toInt(),
            (w * 0.6f).toInt()
        )

        val root = FrameLayout(this)
        val vectorView = VectorView(this)
        val textView = TextView(this)
        val bubbleView = BubbleView(this)
        val timerView = TimerView(this)

        textView.movementMethod = ScrollingMovementMethod()

        val vectorParams = FrameLayout.LayoutParams(
            vectorViewSize.width,
            vectorViewSize.height
        )

        val timerParams = FrameLayout.LayoutParams(
            timerViewSize.width,
            timerViewSize.height
        )

        vectorParams.topMargin = textView.textSize.toInt()
        vectorView.layoutParams = vectorParams

        timerParams.gravity = Gravity.CENTER_HORIZONTAL
        timerParams.topMargin = (0.5f * h - timerParams.height/2).toInt()
        timerView.layoutParams = timerParams

        root.addView(bubbleView)
        root.addView(vectorView)
        root.addView(textView,-1,textView.textSize.toInt())
        root.addView(timerView)

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
                        stop()
                        timerView.pauseTimer()
                        vectorView.startAnimation(true)
                        bubbleView.interrupt()
                        return@apply
                    }

                    startRecording()
                    timerView.startTimer()
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

        val sInhale = "inhale"
        val sHold = "hold"
        val sExhale= "exhale"

        timerView.setOnTickListener(object: TimerView.OnTickListener {
            override fun onTickAnimation(tickTime: Int): TimerView.Tick? {
                Log.d(TAG, "onTickAnimation: $tickTime")

                return when(tickTime) {
                    12 -> TimerView.Tick(
                        1.0f,
                        3000)
                    9 -> TimerView.Tick(
                        1.0f,
                        6000)
                    3 -> TimerView.Tick(
                        0.0f,
                        2000)
                    else -> null
                }
            }

            override fun onTickMessage(tickTime: Int): String? {
                Log.d(TAG, "onTickMessage: $tickTime")
                return when(tickTime) {
                    12 -> sInhale
                    9 -> sHold
                    3 -> sExhale
                    1 -> "Superb!"
                    else -> null
                }
            }
        })

        setContentView(root)
        permissionL.launch(Manifest.permission.RECORD_AUDIO)
    }
}