package com.transportsim.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager as AndroidAudioManager
import android.media.MediaPlayer
import android.util.Log
import com.transportsim.app.R
import com.transportsim.data.security.EncryptedPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptedPrefs: EncryptedPrefs
) {
    companion object {
        private const val TAG = "AudioManager"
        private const val KEY_MUSIC_VOLUME = "audio_music_volume"
        private const val KEY_SOUND_VOLUME = "audio_sound_volume"
        private const val KEY_MUTED = "audio_muted"
        private const val DEFAULT_VOLUME = 0.5f
    }

    private val _musicVolume = MutableStateFlow(DEFAULT_VOLUME)
    val musicVolume: StateFlow<Float> = _musicVolume

    private val _soundVolume = MutableStateFlow(DEFAULT_VOLUME)
    val soundVolume: StateFlow<Float> = _soundVolume

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted

    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying

    private var musicPlayer: MediaPlayer? = null
    private var soundPlayer: MediaPlayer? = null

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val settingsLoaded = Job()
    private val androidAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AndroidAudioManager

    private val audioFocusListener = AndroidAudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AndroidAudioManager.AUDIOFOCUS_LOSS,
            AndroidAudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pauseMusic()
            AndroidAudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> musicPlayer?.setVolume(0.2f, 0.2f)
            AndroidAudioManager.AUDIOFOCUS_GAIN -> {
                resumeMusic()
                musicPlayer?.setVolume(_musicVolume.value, _musicVolume.value)
            }
        }
    }

    init {
        loadSettings()
    }

    private fun loadSettings() {
        scope.launch {
            try {
                _musicVolume.value = encryptedPrefs.getFloat(KEY_MUSIC_VOLUME, DEFAULT_VOLUME)
                _soundVolume.value = encryptedPrefs.getFloat(KEY_SOUND_VOLUME, DEFAULT_VOLUME)
                _isMuted.value = encryptedPrefs.getBoolean(KEY_MUTED, false)
            } finally {
                settingsLoaded.complete()
            }
        }
    }

    /**
     * Plays the launch sound and starts background music upon completion.
     */
    fun playLaunchSound() {
        scope.launch {
            settingsLoaded.join()
            if (_isMuted.value) {
                Log.d(TAG, "App is muted, skipping launch sound, starting music check")
                startBackgroundMusic()
                return@launch
            }
            try {
                soundPlayer?.release()
                
                val attributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build()
                
                // MediaPlayer.create with attributes ensures they are set before preparation
                soundPlayer = MediaPlayer.create(context, R.raw.launch_sound, attributes, 0)
                soundPlayer?.apply {
                    setVolume(_soundVolume.value, _soundVolume.value)
                    setOnCompletionListener { 
                        it.release()
                        soundPlayer = null
                        Log.d(TAG, "Launch sound completed, starting background music")
                        startBackgroundMusic()
                    }
                    start()
                    Log.d(TAG, "Launch sound started")
                } ?: run {
                    Log.e(TAG, "Failed to create sound player for launch sound")
                    startBackgroundMusic()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error playing launch sound", e)
                startBackgroundMusic()
            }
        }
    }

    fun startBackgroundMusic() {
        scope.launch {
            settingsLoaded.join()
            if (_isMuted.value) {
                Log.d(TAG, "Background music skipped: Muted")
                return@launch
            }
            if (_isMusicPlaying.value) {
                Log.d(TAG, "Background music already playing")
                return@launch
            }

            try {
                if (requestAudioFocus()) {
                    musicPlayer?.release()
                    
                    val attributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                    
                    // Use attributes in create() to avoid "state 8" errors
                    val player = MediaPlayer.create(context, R.raw.theme_music, attributes, 0)
                    if (player != null) {
                        musicPlayer = player
                        player.apply {
                            isLooping = true
                            setVolume(_musicVolume.value, _musicVolume.value)
                            // MediaPlayer.create already prepares the player, do NOT call prepareAsync()
                            start()
                            _isMusicPlaying.value = true
                            Log.d(TAG, "Background music started")
                        }
                    } else {
                        Log.e(TAG, "Failed to create music player for theme music")
                    }
                } else {
                    Log.w(TAG, "Audio focus denied for background music")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start background music", e)
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        val result = androidAudioManager.requestAudioFocus(
            audioFocusListener,
            AndroidAudioManager.STREAM_MUSIC,
            AndroidAudioManager.AUDIOFOCUS_GAIN
        )
        return result == AndroidAudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun abandonAudioFocus() {
        androidAudioManager.abandonAudioFocus(audioFocusListener)
    }

    fun pauseMusic() {
        musicPlayer?.pause()
        _isMusicPlaying.value = false
    }

    fun resumeMusic() {
        if (_isMuted.value || _isMusicPlaying.value) return
        musicPlayer?.start()
        _isMusicPlaying.value = true
    }

    fun stopMusic() {
        musicPlayer?.stop()
        musicPlayer?.release()
        musicPlayer = null
        _isMusicPlaying.value = false
        abandonAudioFocus()
    }

    fun toggleMute() {
        setMuted(!_isMuted.value)
    }

    fun setMuted(muted: Boolean) {
        scope.launch {
            encryptedPrefs.putBoolean(KEY_MUTED, muted)
            _isMuted.value = muted
            if (muted) {
                pauseMusic()
            } else {
                startBackgroundMusic()
            }
        }
    }

    fun setMusicVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        scope.launch {
            encryptedPrefs.putFloat(KEY_MUSIC_VOLUME, clamped)
            _musicVolume.value = clamped
            musicPlayer?.setVolume(clamped, clamped)
            if (clamped == 0f) pauseMusic()
            else if (!_isMuted.value && !_isMusicPlaying.value) startBackgroundMusic()
        }
    }

    fun setSoundVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        scope.launch {
            encryptedPrefs.putFloat(KEY_SOUND_VOLUME, clamped)
            _soundVolume.value = clamped
        }
    }

    fun playSoundEffect(soundResId: Int) {
        if (_isMuted.value) return
        scope.launch {
            try {
                val player = MediaPlayer.create(context, soundResId)
                player?.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build()
                    )
                    setVolume(_soundVolume.value, _soundVolume.value)
                    setOnCompletionListener { it.release() }
                    start()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play sound effect", e)
            }
        }
    }

    fun release() {
        stopMusic()
        soundPlayer?.release()
        soundPlayer = null
    }
}
