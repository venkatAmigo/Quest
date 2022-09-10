package com.example.quest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.utils.MDUtil.isLandscape
import com.example.quest.adapters.CommentsAdapter
import com.example.quest.adapters.SequenceAdapt
import com.example.quest.adapters.ViewPagerAdapter
import com.example.quest.database.QuestDatabase
import com.example.quest.databinding.ActivityQuestDetailsBinding
import com.example.quest.model.*
import com.example.quest.model.comments.Author
import com.example.quest.model.comments.Content
import com.example.quest.utils.AlertHelper
import com.example.quest.utils.Prefs
import com.example.quest.utils.putAny
import com.google.android.gms.maps.model.LatLng
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.concurrent.fixedRateTimer
import kotlin.math.ceil

class QuestDetailsActivity : AppCompatActivity(), SensorEventListener, Runnable,
    View.OnClickListener {
    lateinit var binding: ActivityQuestDetailsBinding
    lateinit var seekBar: SeekBar

    lateinit var sensorManager: SensorManager
    var totalSteps = 0f
    var previousSteps = 0f
    var running = false
    var fav = false
    var taskNo = 1
    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener
    lateinit var quest: Quest
    lateinit var currentLocation: LatLng

    var mediaPlayer: MediaPlayer? = null
    var wasPlaying = false
    var wasVideoPlaying = false
    var commentRating =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent.extras
        quest = intent?.getSerializable("Quest") as Quest
        setUi(quest)
        loadStepsData()
        setLocation()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission
                            .ACTIVITY_RECOGNITION
                    ), 200
                )
            }
        } else {
            running = true
            val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            if (stepSensor == null) {
                Toast.makeText(this, "Device has not step count sensor", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Device has step count sensor", Toast.LENGTH_SHORT).show()
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
            }
        }

        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Achievement")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "I've completed the task \"${quest.tasks?.get(taskNo)?.name}\" " +
                        "from " +
                        "quest \"${quest.name}\" in QuestApp!!!"
            )
            startActivity(Intent.createChooser(intent, "Share Achievement"))
        }
        if (quest.tasks?.isNotEmpty() == true) {
            quest.tasks?.get(taskNo)?.goalType = "STEPS"
            quest.tasks?.get(taskNo)?.goalValue = 100
            if (quest.tasks?.get(taskNo)?.goalType == "STEPS") {
                binding.sendResultBtn.text = "Step Counter"
            }
        }
        binding.sendResultBtn.setOnClickListener {
            if (taskNo == 1) {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.steps_counter_dialog)
                val progressbar = dialog.findViewById<ProgressBar>(R.id.progressBar)
                val percent = ((totalSteps / quest.tasks?.get(taskNo)?.goalValue!!) * 100).toInt()
                progressbar.progress = percent
                when (percent) {
                    in (0..21) -> progressbar.progressTintList = ColorStateList.valueOf(
                        getColor(
                            R
                                .color.level1
                        )
                    );
                    in (21..41) -> progressbar.progressTintList = ColorStateList.valueOf(
                        getColor(
                            R
                                .color.level2
                        )
                    );
                    in (41..61) -> progressbar.progressTintList = ColorStateList.valueOf(
                        getColor(
                            R
                                .color.level3
                        )
                    );
                    in (61..81) -> progressbar.progressTintList = ColorStateList.valueOf(
                        getColor(
                            R
                                .color.level4
                        )
                    );
                    in (81..101) -> progressbar.progressTintList = ColorStateList.valueOf(
                        getColor(
                            R
                                .color.level5
                        )
                    );
                }
                val current = dialog.findViewById<TextView>(R.id.current_tv)
                val goal = dialog.findViewById<TextView>(R.id.goal_tv)
                val closeBtn = dialog.findViewById<Button>(R.id.close_dialog_btn)
                current.text = totalSteps.toString()
                goal.text = quest.tasks?.get(taskNo)?.goalValue.toString()
                dialog.show()
                closeBtn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            if (taskNo == 2) {
                val integrator = IntentIntegrator(this)
                integrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                integrator.initiateScan()
            }
            if (taskNo == 0) {
                AlertHelper.showAlert(
                    this, "Location", "Your Location ${currentLocation.latitude}," +
                            " ${currentLocation.longitude}"
                )
            }
        }

        isItFav(quest.id, quest.name, this)
        binding.favIcn.setOnClickListener {
            if (fav) {
                binding.favIcn.setImageResource(R.drawable.fav_stroke)
                deleteFromFav(quest.id, this)
                Toast.makeText(this, "Deleted from Favourites", Toast.LENGTH_SHORT).show()
                fav = !fav
            } else {
                binding.favIcn.setImageResource(R.drawable.not_fav)
                addToFav(quest, this)
                Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show()
                fav = !fav
            }
        }
        binding.taskGoal.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            integrator.initiateScan()
        }

    }

    private fun setLocation() {
        locationListener = LocationListener { location ->
            val loc = LatLng(location.latitude, location.longitude)
            val city =
                Geocoder(this).getFromLocation(loc.latitude, loc.longitude, 1).first().locality
        }
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission
                        .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
            return
        } else {
            startUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startUpdates() {
        val loc = locationManager.getLastKnownLocation(GPS_PROVIDER)
        if (loc != null) {
            currentLocation = LatLng(loc.latitude, loc.longitude)
        }
        //val city = loc?.let { Geocoder(this).getFromLocation(it.latitude,loc.longitude,1).first
        //   ().locality }
        //locationManager.requestLocationUpdates(GPS_PROVIDER, 2000, 10f, locationListener)
    }

    private fun loadStepsData() {
        val steps = Prefs.getFloat("STEPS", 0f)
        previousSteps = steps
        totalSteps = steps
        //binding.taskTime.text = previousSteps.toString()
    }

    private fun checkTasks() {
        if (quest.tasks?.isEmpty() == true) {
            binding.statusTv.text = "New"
        } else {
            val len = quest.tasks?.filter {
                it.status == "COMPLETED"
            }?.size
            if (len == quest.tasks?.size) {
                binding.statusTv.text = "Completed"
            } else {
                binding.statusTv.text = "In Progress"
            }
        }
    }

    private fun checkDateRange() {
        val startDate = LocalDate.parse(
            quest.startDate, DateTimeFormatter.ofPattern(
                "yyyy-MM-dd " +
                        "hh:mm:ss"
            )
        )
        val endDate = LocalDate.parse(
            quest.endDate, DateTimeFormatter.ofPattern(
                "yyyy-MM-dd " +
                        "hh:mm:ss"
            )
        )
        val currentDate = LocalDate.now()
        val st = quest.startDate?.split(" ")
        val en = quest.endDate?.split(" ")
        if (currentDate.isAfter(endDate)) {
            binding.datesRangeTv.text = "Quest is valid from ${st?.get(0)} to ${en?.get(0)}"
        }
        if (currentDate.isAfter(startDate) && currentDate.isBefore(endDate)) {
            binding.datesRangeTv.setTextColor(getColor(R.color.green))

            binding.datesRangeTv.text = "Quest is valid from ${st?.get(0)} to ${en?.get(0)}"
        }
    }

    private fun setTask(taskNumber: Int) {
        taskNo = taskNumber
        binding.lbl2Tv.text = quest.tasks?.get(taskNo)?.name
        if (taskNo == 0) {
            quest.tasks?.get(taskNo)?.goalType = "LOCATION"
            quest.tasks?.get(taskNo)?.goalValue = 100
            binding.sendResultBtn.text = "Send Location"
            binding.taskGoal.text = "Find Location"
        }
        if (taskNo == 1) {
            quest.tasks?.get(taskNo)?.goalType = "STEPS"
            quest.tasks?.get(taskNo)?.goalValue = 100
            binding.sendResultBtn.text = "Step Counter"
            binding.taskGoal.text = "Steps"
        }
        if (taskNo == 2) {
            quest.tasks?.get(taskNo)?.goalType = "SCAN_QR"
            quest.tasks?.get(taskNo)?.goalValue = 100
            binding.taskGoal.text = "Scan QR"
            binding.sendResultBtn.text = "Scan QR"
        }
        if (taskNo == 3) {
            quest.tasks?.get(taskNo)?.goalType = "SECRET_KEY"
            quest.tasks?.get(taskNo)?.goalValue = 123
            binding.taskGoal.text = "Secret Key"
            binding.sendResultBtn.text = "Send Result"
        }

    }

    fun setSequence() {
        if (quest.tasks?.isNotEmpty() == true) {
            binding.tasksSequence.setAdapter(SequenceAdapt(quest.tasks!!) { taskNo -> setTask(taskNo) })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null && result.contents != null) {
            AlertHelper.showAlert(this, "Scan result", "Correct Answer")
        }
    }

    private fun isItFav(id: Int, name: String, context: Context) {
        var favQuestNo = 0
        CoroutineScope(Dispatchers.IO).launch {
            val favQuest = QuestDatabase.getDatabaseInstance(context).getDao().getOne(id)
            if (favQuest.size == 1) {
                if (favQuest[0].name == name) {
                    favQuestNo = 1
                    Log.i("ISFAV", "TRUEE")
                    withContext(Dispatchers.Main) {
                        binding.favIcn.setImageResource(R.drawable.not_fav)
                        fav = true
                    }

                }
            }

        }
    }

    private fun deleteFromFav(id: Int, context: Context) {
        var number = 0
        CoroutineScope(Dispatchers.IO).launch {
            number = QuestDatabase.getDatabaseInstance(context).getDao().delete(id)
        }
        if (number > 0) {
            AlertHelper.showAlert(context, "Deletion", "Quest delete from favourites")
        } else {
            AlertHelper.showAlert(context, "Error", "Deletion error")
        }
    }

    private fun addToFav(quest: Quest, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val newQuest = Quests(
                quest.id,
                quest.name,
                quest.description,
                quest.photos?.let { Photo(it) },
                quest.creationDate,
                quest.startDate,
                quest.endDate,
                quest.mainPhoto,
                quest.difficulty,
                quest.category,
                quest.tags?.let { Photo(it) },
                quest.tasks?.let { DummyTask(it) })
            QuestDatabase.getDatabaseInstance(context).getDao().insert(newQuest)
        }
    }

    private fun setFeedbackUi() {
        binding.feedbackStars.startOne.setOnClickListener(this)
        binding.feedbackStars.starTwo.setOnClickListener(this)
        binding.feedbackStars.starThree.setOnClickListener(this)
        binding.feedbackStars.starFour.setOnClickListener(this)
        binding.feedbackStars.starFive.setOnClickListener(this)
        binding.commentsRecycler.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL,
            false
        )
        val contentOne = Content(
            Author(
                "http://wsk2019.mad.hakta" +
                        ".pro/uploads/files/efc7ed6d-93d6-6be2-96a0-7e317afbfd11.jpg", 1, "John"
            ), "2022-10-09", 1, 3, "It was amazing! I recommend it for everyone"
        )
        val contentTwo = Content(
            Author(
                "http://wsk2019.mad.hakta.pro/uploads/files/efc7ed6d-93d6-6be2-96a0-7e317afbfd11.jpg",
                2,
                "Cherry Pie"
            ), "2022-10-10", 2, 2, "It was amazing! I recommend it for everyone"
        )
        val mutContentList = mutableListOf<Content>()
        mutContentList.add(contentOne)
        mutContentList.add(contentTwo)
        val adapter = CommentsAdapter(mutContentList)
        binding.commentsRecycler.adapter = adapter
        binding.sendFeedback.setOnClickListener {
            binding.feedbackLt.visibility = View.GONE
            val comment = binding.commentEt.text
            val newComment = Content(
                Author(
                    "http://wsk2019.mad.hakta.pro/uploads/files/efc7ed6d-93d6-6be2-96a0-7e317afbfd11.jpg",
                    2,
                    "Venkat"
                ), LocalDate.now().toString(), 2, commentRating, comment.toString()
            )
            mutContentList.add(newComment)
            adapter.notifyItemInserted(mutContentList.size - 1)
            binding.commentsRecycler.smoothScrollToPosition(mutContentList.size - 1)
            Toast.makeText(this, "Feedback successfully sent", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUi(quest: Quest) {
        checkDateRange()
        checkTasks()
        setSequence()
        setAudioUi()
        setFeedbackUi()
        binding.dateTv.text = quest.startDate
        binding.difficultyTv.text = "Difficulty: ${quest.difficulty}"
        binding.userTv.text = quest.authorName
        binding.categoryTv.text = quest.category?.name

        binding.lbl1Tv.text = quest.name
        binding.taskDescTv.text = quest.description
        binding.questDescTv.text = quest.description
        if (quest.tasks?.isNotEmpty() == true) {
            binding.lbl2Tv.text = quest.tasks[taskNo].name ?: "Welcome to Khazan"
            binding.taskGoal.text = quest.tasks[taskNo].goalType ?: "Scan Qr"
            binding.taskDate.text = quest.tasks[taskNo].startDate ?: "Scan Qr"
            binding.taskTime.text = quest.tasks[taskNo].taskCompletionTime.toString() + " min"
        }
        val photos = listOf(
            "http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(2).jpg",
            "http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(1).jpg",
            "http://wsk2019.mad.hakta.pro/uploads/files/Kazan-Expo-Kazan-Ekspo%20(1).jpg"
        )
        val vide = "http://wsk2019.mad.hakta.pro/uploads/files/mad.mp4"
        val audio = "http://wsk2019.mad.hakta.pro/uploads/files/song1.mp3"

        binding.viewpager1.adapter = ViewPagerAdapter(this, photos)
        binding.viewpager2.adapter = ViewPagerAdapter(this, photos)

        binding.dotsIndicator.attachTo(binding.viewpager1)
        binding.dotsIndicator2.attachTo(binding.viewpager2)

        binding.videoItem.mediaName.text = "Mad World skills"
        binding.videoItem.mediaName.setOnClickListener {
            playVideo(vide)
        }

        binding.audioItem.mediaName.text = "World skills"
        binding.audioItem.mediaName.setOnClickListener {
            binding.audioLayout.visibility = View.VISIBLE
        }

        binding.playBtn.setOnClickListener {
            playSong()
        }
    }

    private fun playVideo(video: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.video_dialog)
        val videoView = dialog.findViewById<VideoView>(R.id.video_view)
        val closeBtn = dialog.findViewById<TextView>(R.id.close_btn)
        val playPauseButton = dialog.findViewById<Button>(R.id.playPauseButton)

        videoView?.setOnCompletionListener {

        }
        val runningTime = dialog.findViewById<TextView>(R.id.runningTime)
        runningTime?.setText("00:00")

        var currentPosition = 0
        playPauseButton.setOnClickListener {
            if (videoView.isPlaying) {
                Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show()
                currentPosition = videoView.currentPosition
                // seekBar.progress = videoView.currentPosition
                videoView.pause()
                playPauseButton.setBackgroundResource(R.drawable.play_icon)
                wasVideoPlaying = true
            } else {
                if (!wasVideoPlaying) {
                    Toast.makeText(this, "starting first play", Toast.LENGTH_SHORT).show()
                    val uri = Uri.parse(video)
                    videoView.setVideoURI(uri)
                    videoView?.setOnPreparedListener {
                        seekBar.max = videoView.duration
                        val fixedRateTimer = fixedRateTimer(
                            name = "hello-timer",
                            initialDelay = 0, period = 1000
                        ) {
                            seekBar.progress = videoView.currentPosition;

                            if (!videoView.isPlaying) {
                            }

                            val time = videoView.currentPosition / 1000;
                            val minute = time / 60;
                            val second = time % 60;

                            runOnUiThread {
                                val runTime = minute.toString().format("%2D") + ":" + second
                                    .toString().format("%2D")
                                runningTime?.text = runTime
                            }
                        }
                        videoView.start()
                    }
                    playPauseButton.setBackgroundResource(R.drawable.mini_pause)
                } else {
                    videoView.resume()
                    videoView.seekTo(seekBar.progress)
                    Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show()
                    playPauseButton.setBackgroundResource(R.drawable.mini_pause)
                }
                wasVideoPlaying = false
            }

        }

        val stopButton = dialog.findViewById<ImageView>(R.id.stopButton)
        stopButton?.setOnClickListener {

        }

        seekBar = dialog.findViewById<SeekBar>(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                val x = ceil((progress / 1000f).toDouble()).toInt()
                if (x == 0 && !videoView?.isPlaying!!) {
                    seekBar.progress = 0
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (videoView.isPlaying) {
                    videoView.seekTo(seekBar.progress)
                }
            }

        })
        closeBtn.setOnClickListener {
            videoView.stopPlayback()
            dialog.dismiss()
        }
        dialog.show()
    }

    //audio
    private fun playSong() {
        try {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                binding.playBtn.setBackgroundResource(R.drawable.play_icon)
                //clearMediaPlayer()
                binding.seekbar.progress = mediaPlayer?.currentPosition!!
                mediaPlayer?.pause()
                wasPlaying = true
            }
            if (!wasPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer.let {
                        it?.setDataSource(
                            this, Uri.parse(
                                "http://wsk2019.mad.hakta.pro/uploads/files/song1.mp3"
                            )
                        )
                    }
                    mediaPlayer.let {
                        it?.prepareAsync()
                        it?.isLooping = false

                        it?.setVolume(0.5f, 0.5f);
                        it?.setOnPreparedListener { mediaPlay ->
                            mediaPlay.start()
                            Handler(mainLooper).postDelayed({
                                binding.seekbar.max = mediaPlay.duration
                                val minutes = mediaPlay.duration / 1000 / 60
                                val seconds = mediaPlay.duration / 1000 % 60
                                binding.songDurationTv.text = "/ $minutes : $seconds"
                                Thread(this).start()
                            }, 100)


                        }

                    }
                    binding.playBtn.setBackgroundResource(R.drawable.mini_pause)
                } else {
                    binding.playBtn.setBackgroundResource(R.drawable.mini_pause)
                    mediaPlayer?.start()
                    mediaPlayer?.seekTo(binding.seekbar.progress)
                }

            }
            wasPlaying = false
        } catch (exception: Exception) {

        }
    }

    //audio
    override fun run() {
        var currentPos = mediaPlayer?.currentPosition ?: 0
        var totalDuration = 0
        Handler(mainLooper).post {
            Toast.makeText(this, "Inside Thread", Toast.LENGTH_SHORT)
                .show()
        }
        totalDuration = mediaPlayer?.duration ?: 0
        while (mediaPlayer != null && mediaPlayer!!.isPlaying && currentPos < totalDuration) {
            try {
                Thread.sleep(1000)
                currentPos = mediaPlayer.let {
                    it?.currentPosition
                } ?: 0
                Handler(mainLooper).post {
                    mediaPlayer.let {
                        val minutes = (it?.currentPosition?.div(1000) ?: 0) / 60
                        val seconds = (it?.currentPosition?.div(1000) ?: 0) % 60
                        binding.songProgressTv.text = "$minutes : $seconds"
                    }
                }

            } catch (exception: Exception) {
                Handler(mainLooper).post {
                    Toast.makeText(
                        this,
                        "Exception ${exception.localizedMessage}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
            binding.seekbar.progress = currentPos
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }

    private fun setAudioUi() {
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                val x = ceil((progress / 1000f).toDouble()).toInt()
                if (x == 0 && mediaPlayer != null && !mediaPlayer?.isPlaying!!) {
                    clearMediaPlayer()
                    binding.seekbar.progress = 0
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.seekTo(binding.seekbar.progress)
                }
            }

        })
    }

    private fun clearMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 200) {

        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 100) {
            startUpdates()
        } else {
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Toast.makeText(this, "changed", Toast.LENGTH_SHORT).show()
        totalSteps = event!!.values[0]
        Prefs.putAny("STEPS", totalSteps)
        // binding.taskTime.text = totalSteps.toString()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private fun star(end: Int) {
        val a = listOf(
            binding.feedbackStars.startOne,
            binding.feedbackStars.starTwo,
            binding.feedbackStars.starThree,
            binding.feedbackStars.starFour,
            binding.feedbackStars.starFive
        )
        for (i in 0..(end)) {
            a[i].setImageResource(R.drawable.star)
        }
        for (i in (end + 1)..4) {
                a[i].setImageResource(R.drawable.start_stroke)
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.start_one -> {
                star(0)
                commentRating =1
            }
            R.id.star_two -> {
                star(1)
                commentRating =2
            }
            R.id.star_three -> {
                star(2)
                commentRating =3
            }
            R.id.star_four -> {
                star(3)
                commentRating =4
            }
            R.id.star_five -> {
                star(4)
                commentRating =5
            }
        }
    }

}