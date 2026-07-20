package com.transportsim.app

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TransportsimApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var audioManager: com.transportsim.app.audio.AudioManager

    override fun onCreate() {
        super.onCreate()

        // Initialize any app-wide configurations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Set up notification channels if needed
        }

        // Play launch sound (which will trigger background music on completion)
        audioManager.playLaunchSound()
    }

    override fun onTerminate() {
        super.onTerminate()
        audioManager.release()
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}