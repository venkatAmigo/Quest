package com.example.quest

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.quest.model.TaskParticipant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class PartcipantsActivity : FragmentActivity() {

    lateinit var mapFragment: SupportMapFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partcipants)
        mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.container_view,mapFragment).commit()
        mapFragment.getMapAsync {
            getTasks(it)
        }

        val back = findViewById<Button>(R.id.back_btn)
        back.setOnClickListener {
            startActivity(Intent(this,TasksListActivity::class.java))
        }

    }
    private fun getTasks(map: GoogleMap){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response =ApiService.getTaskParticipants("ff4ae715-9936-488c-c7fd-1739e2551415")
                if (response.code() == 200 && response.body() != null) {
                    val result = response.body() as TaskParticipant
                    val players = findViewById<TextView>(R.id.all_count)
                    players.text = result.size.toString()+" Players online"
                    result.forEach { participant ->
                        val url = URL(participant.avatar)
                        var bmp: Bitmap? = null
                        withContext(Dispatchers.IO){
                           bmp = BitmapFactory.decodeStream(url.openConnection()
                               .getInputStream())
                        }
                        val scaled = bmp?.let { Bitmap.createScaledBitmap(it, 80, 80, false) }

                        var markerOptions =
                            MarkerOptions().position(LatLng(participant.lat, participant.lon))
                                .title(participant.name).anchor(0.5f, 0.5f).icon(scaled?.let {
                                    BitmapDescriptorFactory.fromBitmap(it) }).snippet(participant.name)
                        map.addMarker(markerOptions)!!
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    participant.lat,
                                    participant.lon
                                ), map.maxZoomLevel
                            )
                        )
                        map.moveCamera(
                            CameraUpdateFactory.newLatLng(
                                LatLng(
                                    participant.lat,
                                    participant.lon
                                )
                            )
                        )
                    }
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                result[result
                                    .size - 1].lat, result[result.size - 1].lon
                            ), 6.0f
                        )
                    )
                } else {
                    Toast.makeText(
                        this@PartcipantsActivity, "${
                            response.errorBody()?.charStream()
                                ?.readText()
                        }", Toast
                            .LENGTH_SHORT
                    ).show()
                }
            }catch (exc :Exception){
                Log.i("FREMSG","exception ${exc.localizedMessage}")
                Toast.makeText(this@PartcipantsActivity, "exception ${exc}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}