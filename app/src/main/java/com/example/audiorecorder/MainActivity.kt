package com.example.audiorecorder

import android.Manifest
import android.Manifest.permission
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.audiorecorder.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    var mediaRecorder: MediaRecorder? = null
    var mediaPlayer: MediaPlayer? = null
    var isRecording = false
    var isPlaying = true
    var seconds = 0
    var dummySeconds = 0
    var playableSeconds = 0
    var path = ""
    private var timerJob: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        mediaPlayer = MediaPlayer()
        mediaRecorder = MediaRecorder()
        setContentView(binding.root)
        binding.record.setOnClickListener {
            if(checkRecordingPermission()) {
                if (!isRecording) {
                    isRecording = true
                    GlobalScope.launch(Dispatchers.IO) {
                        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        mediaRecorder?.setOutputFile(getRecordingFilePath())
                        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        mediaRecorder?.prepare()
                        mediaRecorder?.start()
                        withContext(Dispatchers.Main){
                            playableSeconds = 0
                            seconds = 0
                            dummySeconds = 0
                            binding.record.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.ic_recording))
                            startTimer()
                        }
                    }
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        mediaRecorder?.stop()
                        mediaRecorder?.release()
                        mediaRecorder =  null
                        playableSeconds = seconds
                        dummySeconds = seconds
                        seconds = 0
                        isRecording = false

                        withContext(Dispatchers.Main){
                            binding.record.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.ic_record))
                        }
                    }

                }
            }
            else{
                requestRecordingPermission()
            }
        }

        binding.playRecording.setOnClickListener {
            if (!isPlaying){
                if (path != null){
                    mediaPlayer?.setDataSource(getRecordingFilePath())
                }
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                isPlaying = true
                binding.playRecording.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_pause))
                binding.simpleBg.visibility = View.GONE
                binding.playAnimation.visibility = View.VISIBLE
                startTimer()

            } else {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                isPlaying = false
                seconds = 0
                binding.simpleBg.visibility = View.VISIBLE
                binding.playAnimation.visibility = View.GONE
                binding.playRecording.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_play))


            }
        }
    }

    fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                runTimer()
                delay(1000) // Delay for 1 second
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }
        fun runTimer() {
            val minutes = (seconds%3600)/60
            val secs = seconds%60
            val time = String.format(Locale.getDefault(),"%02d:%02d", minutes,secs)
            binding.recordTime.text = time.toString()

            if (isRecording || (isPlaying && playableSeconds != -1)){
                seconds++
                playableSeconds--
                if (playableSeconds == -1 && isPlaying){
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    mediaPlayer = MediaPlayer()
                    playableSeconds = dummySeconds
                    seconds = 0
                    binding.simpleBg.visibility = View.VISIBLE
                    binding.playAnimation.visibility = View.GONE
                    binding.playRecording.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_play))
                    return
                }
            }

        }

    fun startRecording() {

    }
    fun checkRecordingPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_DENIED){
            requestRecordingPermission()
            return false
        }
        return true
    }
    fun requestRecordingPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO),111)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111){
            if (grantResults.size > 0){
                Toast.makeText(this,"Permission given",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getRecordingFilePath():String {
        val contextWrapper = ContextWrapper(applicationContext)
        val music = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(music,"audiorecording")
        return file.path

    }
}