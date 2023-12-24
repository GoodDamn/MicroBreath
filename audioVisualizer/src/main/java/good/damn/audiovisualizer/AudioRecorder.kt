package good.damn.audiovisualizer

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread

class AudioRecorder
    : AudioRecord,
      Runnable {

    private val TAG = "AudioRecorder"

    private val mSampleData = ByteArray(1)
    private var mIsRecording = false

    private var mOnSampleListener: OnSampleListener? = null

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    constructor() : super(
        AudioSource.MIC,
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_8BIT,
        8192
    )

    override fun startRecording() {
        super.startRecording()
        mIsRecording = true
        Thread(this)
            .start()
    }

    override fun release() {
        super.release()
        mIsRecording = false
    }

    override fun run() {
        val fByteMax = Byte.MAX_VALUE.toFloat()
        while(mIsRecording) {
            read(mSampleData,0,1)

            val sample = mSampleData[0] / fByteMax
            mOnSampleListener?.onSample(sample)

            Log.d(TAG, "run: $sample")
            Thread.sleep(150)
        }

        Thread.currentThread()
            .interrupt()
    }

    fun setOnSampleListener(
        l: OnSampleListener
    ) {
        mOnSampleListener = l
    }

    fun isRecording(): Boolean {
        return mIsRecording
    }

    interface OnSampleListener {
        @WorkerThread
        fun onSample(sample: Float)
    }

}