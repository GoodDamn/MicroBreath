package good.damn.audiovisualizer

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread

class AudioRecorder
    : AudioRecord,
      Runnable {

    companion object {
        private const val TAG = "AudioRecorder"
        private const val mSampleRate = 44100
        private const val mBufferSize = 8192
    }

    private val mSampleData = ByteArray(mBufferSize)
    private var mOnSampleListener: OnSampleListener? = null

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    constructor() : super(
        AudioSource.MIC,
        mSampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        mBufferSize
    )

    override fun startRecording() {
        super.startRecording()
        Thread(this)
            .start()
    }

    override fun release() {
        super.release()
    }

    override fun run() {
        val fMax = Short.MAX_VALUE.toFloat()
        while(recordingState == RECORDSTATE_RECORDING) {
            when (read(mSampleData,0,mSampleData.size)) {
                ERROR_INVALID_OPERATION -> {
                    Log.d(TAG, "run: ERROR_INVALID_OPERATION")
                }
                ERROR_BAD_VALUE -> {
                    Log.d(TAG, "run: ERROR_BAD_VALUE")
                }
                ERROR_DEAD_OBJECT -> {
                    Log.d(TAG, "run: ERROR_DEAD_OBJECT")
                }
                ERROR -> {
                    Log.d(TAG, "run: ERROR")
                }
            }

            val i = mSampleData.size - 1
            val digSample = (mSampleData[i-1].toInt() shl 8) or (mSampleData[i].toInt());
            val sample = digSample / fMax
            mOnSampleListener?.onSample(sample, mSampleData)

            Log.d(TAG, "run: $sample")
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
        return recordingState == RECORDSTATE_RECORDING
    }

    interface OnSampleListener {
        @WorkerThread
        fun onSample(
            sample: Float,
            digSample: ByteArray)
    }
}