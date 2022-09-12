package com.example.quest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quest.adapters.PopularQuestsAdapter
import com.example.quest.adapters.SequenceAdapt
import com.example.quest.databinding.ActivityMainBinding
import com.example.quest.model.*
import com.example.quest.network.ApiService
import com.example.quest.utils.Prefs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class MainActivity : AppCompatActivity(), MotionLayout.TransitionListener {
    private val CAM_PERMISSION: Int = 125
    private val IMAGE_CAPTURE_CODE: Int = 124
    private val pickImage: Int = 123
    lateinit var binding: ActivityMainBinding
    lateinit var accessToken: String
    lateinit var currentLocation: LatLng
    var map: GoogleMap? = null
    lateinit var locationManager: LocationManager
    lateinit var locListner: LocationListener
    lateinit var mapFragment: SupportMapFragment

    lateinit var currentTask: List<TaskListItem>
    lateinit var currentQuest: QuestsList

    var vFilename: String = ""
    private lateinit var imageUri:Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupUI()
        binding.main.setTransitionListener(this)
        binding.button.button.setTransitionListener(this)

        binding.button.minimize.setOnClickListener {
            if(binding.main.progress == 0f){
                binding.button.minimize.setBackgroundResource(R.drawable.close)
                binding.main.transitionToEnd()
            }else{
                binding.button.minimize.setBackgroundResource(R.drawable.open)
                binding.main.transitionToStart()
            }
        }




        accessToken = Prefs.getString("TOKEN","").toString()
        mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.container_view, mapFragment).commit()
        binding.include.questCard?.detailsTv?.setOnClickListener {
            val intent = Intent(this,QuestListActivity::class.java)
            intent.putExtra("Quest",currentQuest)
            startActivity(intent)
        }
        binding.include.profileIv?.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.image_picker_dialog)
            val camera = dialog.findViewById<ImageView>(R.id.camera)
            val gallery = dialog.findViewById<ImageView>(R.id.gallery)
            val closeBtn = dialog.findViewById<Button>(R.id.close_dialog_btn)
            dialog.show()
            gallery.setOnClickListener {
                dialog.dismiss()
                val gall = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gall, pickImage)
            }
            camera.setOnClickListener{
                dialog.dismiss()
                openCamera()
            }
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

        }
        setMaps()
        setRecyclerView()
        setUserProfile()
    }

    private fun setupUI() {
        binding.button.iconCompass.setOnClickListener {
            startActivity(Intent(this,CompassActivity::class.java))
        }
        binding.button.textCompass.setOnClickListener {
            startActivity(Intent(this,CompassActivity::class.java))
        }
        binding.button.iconRatings.setOnClickListener {
            startActivity(Intent(this,RatingActivity::class.java))
        }
        binding.button.textRatings.setOnClickListener {
            startActivity(Intent(this,RatingActivity::class.java))
        }
    }

    private fun setSequence(){
       binding.include.tasksSequence?.setAdapter(SequenceAdapt(currentTask) { taskNo ->
           setTask(
               taskNo
           )
       })
    }

    private fun setTask(number: Int) {

    }

    private fun getWeather(city: String){

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val weatherInfo: WeatherInfo =
                        ApiService.getWeather(accessToken, city.lowercase())
                    withContext(Dispatchers.Main) {
                        binding.include.weatherCard?.pressureTv?.text =
                            weatherInfo.pressure.toString()
                        binding.include.weatherCard?.humidityTv?.text = weatherInfo.humidity
                        binding.include.weatherCard?.windTv?.text =
                            weatherInfo.windSpeed.toString()
                        val temp = (((weatherInfo.temperature - 32) * 5)
                                / 9).roundToLong()
                        binding.include.weatherCard?.tempTv?.text = temp.toString()
                        binding.include.weatherCard?.dateTv?.text = LocalDate.now().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        )
                    }
                } catch (exception: Exception) {
                    Log.i("Exception", exception.localizedMessage)
                }
            }

    }
    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission
                        .CAMERA
                ), CAM_PERMISSION
            )
        }else {

            Toast.makeText(this, "inside 2", Toast.LENGTH_SHORT).show()
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")

            //camera intent
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // set filename
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            vFilename = "FOTO_" + timeStamp + ".jpg"

            // set direcory folder
            val file = File(Environment.getExternalStorageDirectory().path, vFilename);
             imageUri = FileProvider.getUriForFile(
                this,
                this.getApplicationContext().getPackageName() + ".provider",
                file
            );

            // cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
        }
    }
    private fun setUserProfile(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val profile: Profile = ApiService.getUserProfile(accessToken).body() as Profile
                (profile.content.firstName + " " + profile.content.lastName).also {
                    binding.include
                        .nameTv?.text = it
                }
//                binding.include.profileIv?.let {
//                    Glide.with(this@MainActivity).load(profile.content.avatar).into(
//                        it
//                    )
//                }
                val quests = profile.content.completedQuests?.filter {
                    it.tasks?.any { task ->
                        task.status == "COMPLETED" || task.status == "IN-PROGRESS"

                    } ?: false
                }
                if (quests != null) {
                    currentTask = quests[0].tasks!!
                    setSequence()
                    val level = calculateLevel(quests)
                    binding.include.levelTv?.text = level.toString()
                    binding.include.levelNumberTv?.text = level.toString()
                }
            }catch (excption:Exception){
                Log.i("RUNTIMEEXCEPTION",excption.localizedMessage)
            }
        }
    }

    //This function is part of Session - 2 calculating level of the player
    private fun calculateLevel(quests: List<Quest>):Int{
        var questPoints:Double = 0.0
        quests.forEach {
            var tasksPoints:Double =0.0
            it.tasks?.forEach { task ->
                if(task.status == "COMPLETED"){
                    val completionTime = task.taskCompletionTime?.toDouble()
                    val minutes = Period.between(
                        LocalDate.parse(task.startDate),
                        LocalDate.parse(task.endDate)).days*24*60
                    tasksPoints += completionTime?.div(minutes.toDouble())!!
                }
            }
            questPoints += it.difficulty?.times(tasksPoints) ?: 0.0
        }
        Log.i("QUESTNAME", "questPoints$questPoints")
       return (ln((questPoints/5)+1)+1).roundToInt()
    }
    private fun setRecyclerView(){
        binding.include.questsRecycler?.layoutManager = GridLayoutManager(this , 2,
            RecyclerView.VERTICAL,false)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = Prefs.getString("TOKEN", "")
                if (token != null) {
                    accessToken = token
                }
                val response = token?.let { ApiService.getPopularQuests(it) }
                if (response?.code() == 200 && response.body() != null) {

                    val result = response.body() as QuestsList
                    currentQuest = result
                    binding.include.questsRecycler?.adapter =
                        PopularQuestsAdapter(result.content,"main")

                } else {
                    Toast.makeText(
                        this@MainActivity, "${
                            response?.errorBody()?.charStream()
                                ?.readText()
                        }", Toast
                            .LENGTH_SHORT
                    ).show()
                }
            }catch (exc :Exception){
                Log.i("FREEXC","exception ${exc.localizedMessage}")
                Toast.makeText(this@MainActivity, "exception ${exc.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setMaps() {
        mapFragment.getMapAsync { imap ->
            map = imap
            imap.mapType = MAP_TYPE_SATELLITE
            locListner = LocationListener { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
                imap.addMarker(
                    MarkerOptions()
                        .position(currentLocation)
                        .title("you")
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN))
                )
                imap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                imap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f))
                val coder = Geocoder(this)
                val current = coder.getFromLocation(location.latitude, location.longitude, 1)
                val citys = current.first().locality
                getWeather(citys)
                binding.include.weatherCard?.cityTv?.text =  citys
                Toast.makeText(this, citys, Toast.LENGTH_SHORT).show()
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
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000,
                    10f,
                    locListner
                )

            }

        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 100) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10f,
                locListner
            )
        } else if(grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == CAM_PERMISSION){
            openCamera()
        }else{
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            val imageUri = data?.data
            Toast.makeText(this, "imamge: $imageUri", Toast.LENGTH_SHORT).show()
            Log.i("IMAGE","imamge: $imageUri")
            binding.include.profileIv?.setImageURI(imageUri)
        }
        Log.i("IMAGEss","imamge: $requestCode ${data?.extras}")
        if (requestCode == IMAGE_CAPTURE_CODE) {
            val imageUri = data?.data
            Log.i("IMAGEss","imamge: $imageUri")
            Log.i("IMAGEss","imamge: ${data?.extras?.get("data")}")
            binding.include.profileIv?.setImageURI(imageUri)
        }

    }

    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
        updateNestedMotionLayout(motionLayout)
    }

    override fun onTransitionChange(
        motionLayout: MotionLayout?,
        startId: Int,
        endId: Int,
        progress: Float
    ) {
        updateNestedMotionLayout(motionLayout)
    }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
        updateNestedMotionLayout(motionLayout)
    }

    override fun onTransitionTrigger(
        motionLayout: MotionLayout?,
        triggerId: Int,
        positive: Boolean,
        progress: Float
    ) {
        updateNestedMotionLayout(motionLayout)
    }

    private fun updateNestedMotionLayout(motionLayout: MotionLayout?) {
        motionLayout.let {
            if(it?.id == binding.main.id){
                binding.button.button.progress = it.progress
            }
        }
    }


}