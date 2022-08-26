package com.example.quest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var accessToken: String
    lateinit var currentLocation: LatLng
    var map: GoogleMap? = null
    lateinit var locationManager: LocationManager
    lateinit var locListner: LocationListener
    lateinit var mapFragment: SupportMapFragment

    lateinit var currentTask: List<TaskListItem>
    lateinit var currentQuest: QuestsList

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
        accessToken = Prefs.getString("TOKEN","").toString()
        mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.container_view, mapFragment).commit()
        binding.contentMain.questCard?.detailsTv?.setOnClickListener {
            val intent = Intent(this,QuestListActivity::class.java)
            intent.putExtra("Quest",currentQuest)
            startActivity(intent)
        }
        setMaps()
        setRecyclerView()
        setUserProfile()
    }
    fun setSequence(){
       binding.contentMain.tasksSequence?.setAdapter(SequenceAdapt(currentTask))
    }
    fun getWeather(city: String){
        CoroutineScope(Dispatchers.IO).launch {
            val weatherInfo: WeatherInfo = ApiService.getWeather(accessToken,city.lowercase())
            withContext(Dispatchers.Main){
                binding.contentMain.weatherCard?.pressureTv?.text = weatherInfo.pressure.toString()
                binding.contentMain.weatherCard?.humidityTv?.text = weatherInfo.humidity
                binding.contentMain.weatherCard?.windTv?.text = weatherInfo.windSpeed.toString()
                val temp = (((weatherInfo.temperature-32)*5)
                        /9).roundToLong()
                binding.contentMain.weatherCard?.tempTv?.text = temp.toString()
                binding.contentMain.weatherCard?.dateTv?.text = LocalDate.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
        }


    }
    fun setUserProfile(){
        CoroutineScope(Dispatchers.Main).launch {
            val profile:Profile = ApiService.getUserProfile(accessToken).body() as Profile
            (profile.content.firstName +" "+ profile.content.lastName).also { binding.contentMain
                .nameTv?.text = it }
            binding.contentMain.profileIv?.let {
                Glide.with(this@MainActivity).load(profile.content.avatar).into(
                    it
                )
            }
            val quests = profile.content.completedQuests?.filter {
                it.tasks?.any { task->
                    task.status == "COMPLETED" || task.status == "IN-PROGRESS"

                } ?: false
            }
            if (quests != null) {
                calculateLevel(quests)
            }
        }
    }
    fun calculateLevel(quests: List<Quest>){
        var questPoints:Double = 0.0
        currentTask = quests[0].tasks!!
        setSequence()
        quests.forEach {
            Log.i("QUESTNAME",it.name)
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
        var level = (ln((questPoints/5)+1)+1).roundToInt()
        binding.contentMain.levelTv?.text = level.toString()
        binding.contentMain.levelNumberTv?.text = level.toString()
    }
    fun setRecyclerView(){
        binding.contentMain.questsRecycler?.layoutManager = GridLayoutManager(this , 2,
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
                    binding.contentMain.questsRecycler?.adapter =
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
                Log.i("FRE","exception ${exc.localizedMessage}")
                Toast.makeText(this@MainActivity, "exception ${exc.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setMaps() {
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
                binding.contentMain.weatherCard?.cityTv?.text =  citys
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
        } else {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}