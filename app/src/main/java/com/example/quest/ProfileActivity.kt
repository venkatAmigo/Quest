package com.example.quest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.quest.databinding.ActivityProfileBinding
import com.example.quest.network.ApiService.getWeather
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding

    lateinit var locationManager: LocationManager
    lateinit var locListner: LocationListener
    private var cityName = "Khazan"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUi()
        getLocation()
        binding.changePwdBtn.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.change_pwd_dialog)
            val changeBtn = dialog.findViewById<Button>(R.id.change_dlg_btn)
            val closeBtn = dialog.findViewById<Button>(R.id.close_dialog_btn)
            dialog.show()
            changeBtn.setOnClickListener {
                dialog.dismiss()
            }
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
        binding.cityBtn.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.city_dialog)
            val yesBtn = dialog.findViewById<Button>(R.id.yes_dialog_btn)
            val selectBtn = dialog.findViewById<Button>(R.id.select_dialog_btn)
            val closeBtn = dialog.findViewById<Button>(R.id.close_dialog_btn)
            val cityTv = dialog.findViewById<TextView>(R.id.city_dlg_tv)
            cityTv.text = cityName
            dialog.show()
            selectBtn.setOnClickListener {

            }
            yesBtn.setOnClickListener{
                dialog.dismiss()
            }
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun getLocation(){
        locListner = LocationListener { location ->
            val coder = Geocoder(this)
            val current = coder.getFromLocation(location.latitude, location.longitude, 1)
            cityName = current.first().locality

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
    private fun setUi() {
        binding.nameEditIcn.setOnClickListener {
            binding.nameEt.setText(binding.nameTv.text)
            binding.nameEt.visibility = View.VISIBLE
            binding.nameSaveIcn.visibility = View.VISIBLE
            binding.nameTv.visibility = View.GONE
            binding.nameEditIcn.visibility = View.GONE
        }
        binding.nameSaveIcn.setOnClickListener {
            binding.nameTv.text = binding.nameEt.text
            binding.nameTv.visibility = View.VISIBLE
            binding.nameEditIcn.visibility = View.VISIBLE
            binding.nameEt.visibility = View.GONE
            binding.nameSaveIcn.visibility = View.GONE
        }
        binding.nickNameEditIcn.setOnClickListener {
            binding.nickNameEt.setText(binding.nickNameTv.text)
            binding.nickNameEt.visibility = View.VISIBLE
            binding.nickNameSaveIcn.visibility = View.VISIBLE
            binding.nickNameTv.visibility = View.GONE
            binding.nickNameEditIcn.visibility = View.GONE
        }
        binding.nickNameSaveIcn.setOnClickListener {
            binding.nickNameTv.text = binding.nickNameEt.text
            binding.nickNameTv.visibility = View.VISIBLE
            binding.nickNameEditIcn.visibility = View.VISIBLE
            binding.nickNameEt.visibility = View.GONE
            binding.nickNameSaveIcn.visibility = View.GONE
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
        } else{
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}