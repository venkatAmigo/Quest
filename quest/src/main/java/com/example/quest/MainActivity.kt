package com.example.quest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.quest.databinding.ActivityMainBinding

/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mainFragment: MainFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainFragment = MainFragment()
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_browse_fragment, mainFragment)
                .commitNow()
        }
        val categories = listOf("Themed","For children","Historical","In car")
        binding.catSpinner.adapter = ArrayAdapter(this,android.R.layout
            .simple_spinner_dropdown_item, categories)

        binding.searchBtn.setOnClickListener {
           // "For children"
            mainFragment.setFilter(binding.catSpinner.selectedItem.toString())
        }
        binding.logoutTv.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        checkPermission()
    }

    fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission
                .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),1)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 1){
        }else{
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}