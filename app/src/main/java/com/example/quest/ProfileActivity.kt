package com.example.quest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quest.adapters.AchievementAdapter
import com.example.quest.adapters.PopularQuestsAdapter
import com.example.quest.databinding.ActivityProfileBinding
import com.example.quest.model.Achievements
import com.example.quest.model.Profile
import com.example.quest.model.QuestsList
import com.example.quest.network.ApiService
import com.example.quest.network.ApiService.getWeather
import com.example.quest.utils.Prefs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private val pickImage: Int = 123
    lateinit var binding: ActivityProfileBinding

    lateinit var locationManager: LocationManager
    lateinit var locListner: LocationListener
    private var cityName = "Khazan"

    var photoFile: File? = null
    val CAPTURE_IMAGE_REQUEST = 1
    var mCurrentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUi()
        getLocation()
        setUserProfile()
        handleClicks()
        setAchievements()
        setRecyclerView()
        val snack = Snackbar.make(binding.root,"No new achievements",Snackbar.LENGTH_LONG)
        snack.setBackgroundTint(getColor(R.color.white))
        snack.show()
        binding.profileIvLt.profileFrame.setOnClickListener{
            binding.profileIvLt.profileIv?.setOnClickListener {
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
                    captureImage()
                }
                closeBtn.setOnClickListener {
                    dialog.dismiss()
                }

            }
        }

    }
    private fun captureImage() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile()
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            this,
                            "com.example.quest.fileprovider",
                            photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                    }
                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    displayMessage(baseContext, ex.message.toString())
                }

            } else {
                displayMessage(baseContext, "Null")

            }
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun setRecyclerView(){
        binding.questsRecycler?.layoutManager = GridLayoutManager(this , 2,
            RecyclerView.VERTICAL,false)
        binding.createdQuestsRecycler?.layoutManager = LinearLayoutManager(this ,
            RecyclerView.VERTICAL,false)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = Prefs.getString("TOKEN", "")
                val response = token?.let { ApiService.getPopularQuests(it) }
                if (response?.code() == 200 && response.body() != null) {

                    val result = response.body() as QuestsList
                    binding.questsRecycler?.adapter =
                        PopularQuestsAdapter(result.content,"main")
                    binding.createdQuestsRecycler?.adapter =
                        PopularQuestsAdapter(result.content,"main")

                } else {
                    Toast.makeText(
                        this@ProfileActivity, "${
                            response?.errorBody()?.charStream()
                                ?.readText()
                        }", Toast
                            .LENGTH_SHORT
                    ).show()
                }
            }catch (exc :Exception){
                Log.i("FRE","exception ${exc.localizedMessage}")
                Toast.makeText(this@ProfileActivity, "exception ${exc.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun setAchievements() {
        val achievements = listOf(Achievements(R.drawable.acheivement_img,"Fastest Quest","Done " +
                "very fastly brfore the completion time"),
            Achievements(R.drawable.acheivement_img,"First Quest","Done " +
                    "very fastly brfore the completion time"),
            Achievements(R.drawable.acheivement_img,"Given Rating","Done " +
                    "very fastly brfore the completion time"),
            Achievements(R.drawable.acheivement_img,"Team Player","You are a team player"),
            Achievements(R.drawable.acheivement_img,"Favourite Quest","Done " +
                    "added to one favourite quest"))
        binding.achievementRecycler.layoutManager = LinearLayoutManager(this,RecyclerView
            .HORIZONTAL,false)
       binding.achievementRecycler.adapter =  AchievementAdapter(achievements)
    }

    private fun handleClicks(){
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
                binding.cityBtn.text = cityName
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
    fun setUserProfile(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = Prefs.getString("TOKEN", "")
                val profile: Profile = token?.let { ApiService.getUserProfile(it).body() } as Profile
                (profile.content.firstName + " " + profile.content.lastName).also {
                    binding.nameTv.text = it
                }
                binding.nickNameTv.text = profile.content.nickName
                binding.cityBtn.text = profile.content.city
                binding.emailTv.text = profile.content.email
                val quests = profile.content.completedQuests?.filter {
                    it.tasks?.any { task ->
                        task.status == "COMPLETED" || task.status == "IN-PROGRESS"

                    } ?: false
                }
            }catch (excption:Exception){
                Log.i("RUNTIMEEXCEPTION",excption.localizedMessage)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            binding.profileIvLt.profileIv.setImageBitmap(myBitmap)
        } else if(requestCode == pickImage && resultCode == Activity.RESULT_OK ){
            val imageUri = data?.data
            binding.profileIvLt.profileIv?.setImageURI(imageUri)
        }else{
            displayMessage(baseContext, "Request cancelled or something went wrong.")
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