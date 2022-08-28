package com.example.quest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quest.databinding.ActivityTaskBinding
import com.example.quest.model.TaskResponse
import com.google.android.gms.location.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.roundToInt

class TaskActivity : Activity() {

    private lateinit var binding: ActivityTaskBinding

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (applicationContext.resources.configuration.isScreenRound) {
            val insets = (0.146467f * resources.displayMetrics.widthPixels).toInt()
            binding.contentView.setPadding(insets, insets, insets, insets)
        }
        var a=""
        run {
            a = "{\n" +
                    "  \"meta\": [],\n" +
                    "  \"content\": {\n" +
                    "    \"id\": \"2\",\n" +
                    "    \"name\": \"Almaz or Altyn?\",\n" +
                    "    \"goalType\": \"LOCATION\",\n" +
                    "    \"goalValue\": \"Skilled\",\n" +
                    "    \"description\": \"Olympic bear, Zabivak’s wolf … And our competitions have their own symbols. Find them on the WorldSkills site and ask for the secret key.\",\n" +
                    "    \"completionTime\": 20,\n" +
                    "    \"photos\": [\n" +
                    "      \"http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(2).jpg\",\n" +
                    "      \"http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(1).jpg\",\n" +
                    "      \"http://wsk2019.mad.hakta.pro/uploads/files/Kazan-Expo-Kazan-Ekspo%20(1).jpg\"\n" +
                    "    ],\n" +
                    "    \"audios\": [\n" +
                    "      \"http://wsk2019.mad.hakta.pro/uploads/files/song1.mp3\",\n" +
                    "      \"http://wsk2019.mad.hakta.pro/uploads/files/song1.mp3\"\n" +
                    "    ],\n" +
                    "    \"videos\": [\n" +
                    "      \"http://wsk2019.mad.hakta.pro/uploads/files/mad.mp4\"\n" +
                    "    ],\n" +
                    "    \"startDate\": \"2019-08-23 11:00:00\",\n" +
                    "    \"finishDate\": \"2019-08-24 10:00:00\",\n" +
                    "    \"startDateConstraint\": \"2019-08-20\",\n" +
                    "    \"endDateConstraint\": \"2019-08-30\",\n" +
                    "    \"quest\": {\n" +
                    "      \"id\": 1,\n" +
                    "      \"name\": \"WorldSkills 2020\",\n" +
                    "      \"description\": \"The 45th WorldSkills Competition will bring together Competitors from 63 countries and regions who will compete in 56 skills.\\r\\nEvery two years WorldSkills hosts the world skills competition which attracts more than 1,300 Competitors from more than 60 countries. At this event, young people from all corners of the globe gather together for the chance to win a prestigious medal in their chosen skill. There are competitions in 56 skills across a wide range of industries — from joinery to floristry; hairdressing to electronics; and autobody repair to bakery. The Competitors represent the best of their peers and are selected from skills competitions that are held in WorldSkills Member countries and regions.\",\n" +
                    "      \"photos\": [\n" +
                    "        \"http://wsk2019.mad.hakta.pro/uploads/files/0125.jpg\",\n" +
                    "        \"http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b.jpg\",\n" +
                    "        \"http://wsk2019.mad.hakta.pro/uploads/files/Kazan-Expo-Kazan-Ekspo.jpg\",\n" +
                    "        \"http://wsk2019.mad.hakta.pro/uploads/files/enot-poloskun_enot_tone_7.jpg\"\n" +
                    "      ],\n" +
                    "      \"creationDate\": \"2019-08-01 14:53:58\",\n" +
                    "      \"startDate\": \"2019-12-24 07:53:25\",\n" +
                    "      \"endDate\": \"2019-12-30 07:53:25\",\n" +
                    "      \"mainPhoto\": \"http://wsk2019.mad.hakta.pro/uploads/files/Kazan-Expo-Kazan-Ekspo%20(2).jpg\",\n" +
                    "      \"difficulty\": 4,\n" +
                    "      \"category\": {\n" +
                    "        \"id\": 4,\n" +
                    "        \"name\": \"For children\",\n" +
                    "        \"description\": \"Do you want to have fun, but have no one to leave your child with? Do you dream to arrange your child an unforgettable holiday? Complete the quest together!\",\n" +
                    "        \"photo\": \"http://wsk2019.mad.hakta.pro/uploads/files/children.png\"\n" +
                    "      },\n" +
                    "      \"tags\": [\n" +
                    "        \"russia\"\n" +
                    "      ],\n" +
                    "      \"rating\": 4,\n" +
                    "      \"authorName\": \"Wsk2 Johnson\",\n" +
                    "      \"tasks\": [\n" +
                    "        {\n" +
                    "          \"name\": \"What is WorldSkills?\",\n" +
                    "          \"id\": \"1\",\n" +
                    "          \"status\": \"COMPLETED\",\n" +
                    "          \"taskCompletionTime\": \"40\",\n" +
                    "          \"startDate\": \"2019-08-20\",\n" +
                    "          \"endDate\": \"2019-08-22\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"name\": \"Almaz or Altyn?\",\n" +
                    "          \"id\": \"2\",\n" +
                    "          \"status\": \"IN_PROGRESS\",\n" +
                    "          \"taskCompletionTime\": \"20\",\n" +
                    "          \"startDate\": \"2019-08-20\",\n" +
                    "          \"endDate\": \"2019-08-22\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"name\": \"Mobile or not mobile?\",\n" +
                    "          \"id\": \"3\",\n" +
                    "          \"status\": \"NOT_STARTED\",\n" +
                    "          \"taskCompletionTime\": \"40\",\n" +
                    "          \"startDate\": \"2019-08-20\",\n" +
                    "          \"endDate\": \"2019-08-22\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"name\": \"Architectural Stonemasonry\",\n" +
                    "          \"id\": \"7\",\n" +
                    "          \"status\": \"NOT_STARTED\",\n" +
                    "          \"taskCompletionTime\": \"40\",\n" +
                    "          \"startDate\": \"2019-08-20\",\n" +
                    "          \"endDate\": \"2019-08-22\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"name\": \"3D Digital Game Art\",\n" +
                    "          \"id\": \"8\",\n" +
                    "          \"status\": \"NOT_STARTED\",\n" +
                    "          \"taskCompletionTime\": \"180\",\n" +
                    "          \"startDate\": \"2019-08-20\",\n" +
                    "          \"endDate\": \"2019-08-22\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"name\": \"IT Software Solutions for Business\",\n" +
                    "          \"id\": \"9\",\n" +
                    "          \"status\": \"NOT_STARTED\",\n" +
                    "          \"taskCompletionTime\": \"140\",\n" +
                    "          \"startDate\": \"2019-08-20\",\n" +
                    "          \"endDate\": \"2019-08-22\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"name\": \"Chemical Laboratory Technology\",\n" +
                    "          \"id\": \"10\",\n" +
                    "          \"status\": \"NOT_STARTED\",\n" +
                    "          \"taskCompletionTime\": \"30\",\n" +
                    "          \"startDate\": \"2019-08-20\",\n" +
                    "          \"endDate\": \"2019-08-22\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    \"location\": {\n" +
                    "      \"lat\": \"53.61194000\",\n" +
                    "      \"lng\": \"44.29716700\"\n" +
                    "    }\n" +
                    "  }\n }"
        }
        val jsonSTring = JSONObject(a)
        var task = Gson().fromJson(jsonSTring.toString(), TaskResponse::class.java)

        binding.taskDescription.text = task.content.description
        binding.taskNameTv.text = task.content.name
        binding.stepsCountTv.text = task.content.goalValue

        binding.sendResultBtn.setOnClickListener {
            when (task.content.goalType) {
                "STEPS" -> Toast.makeText(this, "Use phone to finish this task", Toast.LENGTH_SHORT)
                    .show()
                "QR_CODE" -> Toast.makeText(
                    this,
                    "Use phone to finish this task",
                    Toast.LENGTH_SHORT
                )
                    .show()
                "LOCATION" -> {
                    if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission
                                .ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            this, Manifest.permission
                                .ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationShow()
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(
                                Manifest.permission
                                    .ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ), 100
                        )
                    }
                }
            }
        }


    }

    @SuppressLint("MissingPermission")
    fun locationShow(){
        val locationRequest = CurrentLocationRequest.Builder().setPriority(
            Priority
                .PRIORITY_BALANCED_POWER_ACCURACY
        ).build()

        val location = fusedLocationProviderClient.getCurrentLocation(
            locationRequest,
            null
        )
        location.addOnCompleteListener {
            Toast.makeText(this, "Location: " + location.result.longitude.roundToInt() + "," + location
                .result
                .latitude.roundToInt(), Toast.LENGTH_SHORT).show()

        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED && requestCode == 100){
            locationShow()
        }else{
            ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission_group.LOCATION)
        }
    }
}