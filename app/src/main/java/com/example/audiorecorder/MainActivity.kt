package com.example.audiorecorder

import android.Manifest
import android.Manifest.permission
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
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
    var playableSeconds = 0
    var dummySeconds = 0
    var path = ""
    private var timerJob: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        mediaRecorder = MediaRecorder()
        setContentView(binding.root)
        //Shared Preferences
        /*val sharedPref = getSharedPreferences("myPref",0)
        val editor = sharedPref.edit()
        editor.putString("name","Abhas")*/

        //Store files in internal storage
        /*val fos:FileOutputStream = openFileOutput("hello_file",Context.MODE_PRIVATE)
        fos.write("abhas is a good boy".toByteArray())
        fos.close()

        val readFile = openFileInput("hello_file")
        readFile.read()
        readFile.close()*/


        binding.record.setOnClickListener {
            if(checkRecordingPermission()) {
                if (!isRecording) {
                    isRecording = true
                    GlobalScope.launch {
                        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        mediaRecorder?.setOutputFile(getRecordingFilePath())
                        path = getRecordingFilePath()
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

                        mediaRecorder?.stop()
                        mediaRecorder?.release()
                        mediaRecorder =  null
                        playableSeconds = seconds
                        dummySeconds = seconds
                        seconds = 0
                        isRecording = false
                        binding.record.setImageDrawable(ContextCompat.getDrawable(this@MainActivity,R.drawable.ic_record))
                        binding.path.visibility = View.VISIBLE
                        binding.path.text = path

                    }

                }

            else{
                requestRecordingPermission()
            }
        }

        binding.playRecording.setOnClickListener {
            mediaPlayer = MediaPlayer()
            if (!isPlaying){
                if (path != null){
                    mediaPlayer?.setDataSource(path)
                }

                mediaPlayer?.prepare()
                mediaPlayer?.start()
                isPlaying = true
                binding.playRecording.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_pause))
                binding.simpleBg.visibility = View.GONE
                binding.playAnimation.visibility = View.VISIBLE
//                startTimer()

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
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                runTimer()
                delay(1000) // Delay for 1 second
            }
            timerJob?.cancel()
        }
    }

        fun runTimer() {
            val minutes = (seconds%3600)/60
            val secs = seconds%60
            val time = String.format(Locale.getDefault(),"%02d:%02d", minutes,secs)

//            binding.recordTime.text = time
            if (isRecording){
                seconds++
//                playableSeconds--
                /*if (playableSeconds == -1 && isPlaying){
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
                }*/
            }
            binding.recordTime.text = time


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
        val music = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(music,"audiorecording.mp3")
        return file.path

    }
}