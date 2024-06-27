package com.odhen.aiassistant.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.odhen.aiassistant.Interface.AudioRecordingListener

class AudioRecordingService : Service() {

    private lateinit var mediaRecorder: MediaRecorder
    private val CHANNEL_ID = "AudioRecordingChannel"
    private var listener: AudioRecordingListener? = null
    private var isMicrophoneListening = false // Variável para rastrear o estado do microfone

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaRecorder = MediaRecorder().apply {
            // ... configure your MediaRecorder here ...
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        sendBroadcastMessage("AUDIO_RECORDING_STARTED")
        mediaRecorder.start()

        isMicrophoneListening = true
        sendBroadcastMessage("MICROPHONE_LISTENING", isMicrophoneListening.toString()) // Notifica a Activity

        listener?.onRecordingStarted()

        return START_STICKY
    }

    private fun stopRecording() {
        // ... (lógica para parar o MediaRecorder)
        isMicrophoneListening = false
        sendBroadcastMessage("MICROPHONE_LISTENING", isMicrophoneListening.toString()) // Notifica a Activity
    }

    inner class AudioRecordingBinder : Binder() {
        fun getService(): AudioRecordingService = this@AudioRecordingService
    }

    private val binder = AudioRecordingBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun setRecordingListener(listener: AudioRecordingListener?) {
        this.listener = listener
    }

    override fun onDestroy() {
        mediaRecorder.stop()
        mediaRecorder.release()
        super.onDestroy()
    }

    private fun sendBroadcastMessage(action: String, message: String? = null) {
        val intent = Intent()
        intent.action = action
        message?.let { intent.putExtra("message", it) }
        sendBroadcast(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Audio Recording",
            NotificationManager.IMPORTANCE_HIGH
        )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Recording Audio")
            .setContentText("Audio recording in progress")
            //.setSmallIcon(R.drawable.ic_record) // Replace with your icon
            .build()
    }
}