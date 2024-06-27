package com.odhen.aiassistant

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Color
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.odhen.aiassistant.Interface.AudioRecordingListener
import com.odhen.aiassistant.Service.AudioRecordingService

class MainActivity : ComponentActivity() {

    private val bakingViewModel: BakingViewModel by viewModels()
    private var audioRecordingService: AudioRecordingService? = null
    private var isBound = false
    private lateinit var mainLayout: ConstraintLayout // Layout principal da sua Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter().apply {
            addAction("AUDIO_RECORDING_STARTED")
            // ... adicione ações para outros eventos ...
        }
        registerReceiver(recordingStatusReceiver, filter)

        val serviceIntent = Intent(this, AudioRecordingService::class.java)
        startService(serviceIntent)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, AudioRecordingService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(recordingStatusReceiver)
    }

    private val recordingStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "AUDIO_RECORDING_STARTED" -> {
                    // Atualizar a UI para indicar que a gravação começou
                }
                // ... outros eventos ...
            }
        }
    }

    private val microphoneStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "MICROPHONE_LISTENING") {
                val isListening = intent.getStringExtra("message")?.toBoolean() ?: false
                updateBackgroundColor(isListening)
            }
        }
    }

    private fun updateBackgroundColor(isMicrophoneListening: Boolean) {
        val backgroundColor = if (isMicrophoneListening) {
            Color.GREEN // Mude para a cor desejada quando o microfone estiver ativo
        } else {
            Color.WHITE // Cor quando o microfone não estiver ativo
        }
        mainLayout.setBackgroundColor(backgroundColor)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AudioRecordingService.AudioRecordingBinder
            audioRecordingService = binder.getService()
            audioRecordingService?.setRecordingListener(object : AudioRecordingListener {
                override fun onRecordingStarted() {
                    // Atualizar a UI
                }

                override fun onRecordingStopped() {
                    TODO("Not yet implemented")
                }
                // ... outros callbacks ...
            })
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            audioRecordingService = null
            isBound = false
        }
    }
}