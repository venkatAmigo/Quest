package com.example.quest

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.quest.adapters.ViewPagerAdapter
import com.example.quest.databinding.ActivityQuestDetailsBinding
import com.example.quest.model.Quest
import com.example.quest.model.QuestsList
import com.example.quest.utils.AlertHelper
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanOptions
import org.w3c.dom.Text
import java.util.jar.Attributes
import kotlin.concurrent.fixedRateTimer

class QuestDetailsActivity : AppCompatActivity(){
    lateinit var binding: ActivityQuestDetailsBinding
    lateinit var seekBar:SeekBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityQuestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent.extras
        val quest = intent?.getSerializable("Quest") as Quest
        setUi(quest)

        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT,"Achievement")
            intent.putExtra(Intent.EXTRA_TEXT,"I've completed the task \"${quest.tasks?.get(0)?.name}\" " +
                    "from " +
                    "quest \"${quest.name}\" in QuestApp!!!")
            startActivity(Intent.createChooser(intent,"Share Achievement"))
        }
        binding.taskGoal.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            integrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(resultCode,data)
        if(result !=null && result.contents!=null){
            AlertHelper.showAlert(this,"Scan result","Correct Answer")
        }
    }
    fun setUi(quest: Quest){
        binding.dateTv.text = quest.startDate
        binding.difficultyTv.text = "Difficulty: ${quest.difficulty}"
        binding.userTv.text = quest.authorName
        binding.categoryTv.text = quest.category?.name
        binding.lbl2Tv.text = quest.tasks?.get(0)?.name ?: "Welcome to Khazan"
        binding.lbl1Tv.text =quest.name
        binding.taskDescTv.text =  quest.description
        binding.questDescTv.text =  quest.description
        binding.taskGoal.text = quest.tasks?.get(0)?.goalType ?: "Scan Qr"
        binding.taskDate.text = quest.tasks?.get(0)?.startDate?: "Scan Qr"
        binding.taskTime.text = quest.tasks?.get(0)?.taskCompletionTime.toString()+" min"

        val photos = listOf("http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(2).jpg",
        "http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(1).jpg",
        "http://wsk2019.mad.hakta.pro/uploads/files/Kazan-Expo-Kazan-Ekspo%20(1).jpg")
        val vide = "http://wsk2019.mad.hakta.pro/uploads/files/mad.mp4"
        val audio = "http://wsk2019.mad.hakta.pro/uploads/files/song1.mp3"

        binding.viewpager1.adapter = ViewPagerAdapter(this,photos )
        binding.viewpager2.adapter = ViewPagerAdapter(this,photos )

        binding.dotsIndicator.attachTo(binding.viewpager1)
        binding.dotsIndicator2.attachTo(binding.viewpager2)

        binding.videoItem.mediaName.text ="Mad Worldskills"
        binding.videoItem.mediaName.setOnClickListener {
            val dialog =  Dialog(this)
            dialog.setContentView(R.layout.video_dialog)
            dialog.show()
            val videoView = dialog.findViewById<VideoView>(R.id.video_view)
            val closeBtn = dialog.findViewById<TextView>(R.id.close_btn)
            val playPauseButton = findViewById<ImageView>(R.id.playPauseButton)
            videoView?.setOnCompletionListener {

            }
            val runningTime = dialog.findViewById<TextView>(R.id.runningTime)
            runningTime?.setText("00:00")
            videoView?.setOnPreparedListener {
                seekBar.max = videoView.duration
                val fixedRateTimer = fixedRateTimer(name = "hello-timer",
                    initialDelay = 0, period = 1000) {
                    seekBar?.setProgress(videoView?.getCurrentPosition()!!);

                    if (videoView?.isPlaying()!! == false) {
                    }

                    var time = videoView?.getCurrentPosition()!! / 1000;
                    var minute = time / 60;
                    var second = time % 60;

                    runOnUiThread {
                        runningTime?.setText(minute.toString() + ":" + second.toString());
                    }
                }
            }
            var currentPosition =0
            playPauseButton?.setOnClickListener {
                if(videoView.isPlaying) {
                    currentPosition = videoView.currentPosition
                    videoView.stopPlayback()
                    playPauseButton.setImageResource(R.drawable.play_icon)
                }else{
                    videoView.resume()
                    videoView.seekTo(currentPosition)
                    playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24)
                }

            }

            val stopButton = dialog.findViewById<ImageView>(R.id.stopButton)
            stopButton?.setOnClickListener {

            }

            seekBar = dialog.findViewById<SeekBar>(R.id.seekBar)
            seekBar?.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }

            })
            closeBtn.setOnClickListener {
                videoView.stopPlayback()
                dialog.dismiss()
            }
            val uri = Uri.parse(vide)
            videoView.setVideoURI(uri)
            videoView.start()
        }

        binding.audioItem.mediaName.text ="Song"
        binding.audioItem.mediaName.setOnClickListener {

        }
    }
}