package com.odhen.aiassistant.Interface

interface AudioRecordingListener {
    fun onRecordingStarted()
    fun onRecordingStopped()
    // ... outros métodos de callback ...
}