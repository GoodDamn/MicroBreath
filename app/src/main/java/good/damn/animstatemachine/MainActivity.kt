package good.damn.animstatemachine

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import good.damn.statemachine.VectorView
import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PointF
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import good.damn.audiovisualizer.AudioRecorder
import good.damn.audiovisualizer.BubbleView
import good.damn.audiovisualizer.CounterView
import good.damn.statemachine.VectorImage

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
        val counterView = CounterView(this)

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
        counterView.layoutParams = timerParams

        root.addView(bubbleView)
        root.addView(vectorView)
        root.addView(textView,-1,textView.textSize.toInt())
        root.addView(counterView)

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
                        counterView.pauseCounter()
                        vectorView.setTransitionIndex(1,0)
                        vectorView.startAnimation()
                        bubbleView.interrupt()
                        return@apply
                    }

                    startRecording()
                    counterView.startCounter()
                    vectorView.setTransitionIndex(0,1)
                    vectorView.startAnimation()
                    bubbleView.listen()
                }
            }
        }

        vectorView.setTransitionIndex(0,1)
        vectorView.setVectorImages(
            arrayOf(
                VectorImage(
                    0xffff0000.toInt(),
                    arrayOf(
                        PointF(0.8f * vectorViewSize.width,0.5f * vectorViewSize.height),
                        PointF(0.2f * vectorViewSize.width,0.2f * vectorViewSize.height),
                        PointF(0.2f * vectorViewSize.width,0.8f * vectorViewSize.height),
                        PointF(0.2f * vectorViewSize.width,0.8f * vectorViewSize.height)
                    )
                ),
                VectorImage(
                    0xffff0000.toInt(),
                    arrayOf(
                       PointF(0.8f * vectorViewSize.width,0.2f * vectorViewSize.height),
                       PointF(0.8f * vectorViewSize.width,0.8f * vectorViewSize.height),
                       PointF(0.2f * vectorViewSize.width,0.8f * vectorViewSize.height),
                       PointF(0.2f * vectorViewSize.width,0.2f * vectorViewSize.height)
                    )
                )
            )
        )

        val sRelax = "relax"
        val sInhale = "inhale"
        val sHold = "hold"
        val sExhale= "exhale"
        val sSuperb = "Superb!"

        counterView.setStartTime(35)
        counterView.setOnTickListener(object: CounterView.OnTickListener {
            override fun onTickAnimation(tickTime: Int): CounterView.Tick? {
                Log.d(TAG, "onTickAnimation: $tickTime")

                return when(tickTime) {
                    35 -> CounterView.Tick(
                        0.5f,
                        7000)
                    28 -> CounterView.Tick(
                        0.0f,
                        6000)
                    22 -> CounterView.Tick(
                        1.0f,
                        3000)
                    19 -> CounterView.Tick(
                        1.0f,
                        6000)
                    13 -> CounterView.Tick(
                        0.0f,
                        10000)
                    else -> null
                }
            }

            override fun onTickMessage(tickTime: Int): String? {
                Log.d(TAG, "onTickMessage: $tickTime")
                return when(tickTime) {
                    35 -> sRelax
                    22 -> sInhale
                    19 -> sHold
                    13 -> sExhale
                    3 -> sSuperb
                    else -> null
                }
            }
        })

        setContentView(root)
        permissionL.launch(Manifest.permission.RECORD_AUDIO)
    }
}