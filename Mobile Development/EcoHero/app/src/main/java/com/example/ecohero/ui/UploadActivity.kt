package com.example.ecohero.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.ecohero.data.History
import com.example.ecohero.databinding.ActivityUploadBinding
import com.example.ecohero.utils.reduceFileImage
import com.example.ecohero.utils.uriToFile
import com.example.ecohero.viewmodels.UploadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private val uploadViewModel by viewModels<UploadViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        uploadViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.imageUpload.setOnClickListener {
            val options = arrayOf("Camera", "Gallery")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Image Source")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.resolveActivity(this.packageManager)

                        com.example.ecohero.utils.createTempFile(this.application).also {
                            val photoURI: Uri = FileProvider.getUriForFile(this, "com.example.ecohero", it)
                            currentPhotoPath = it.absolutePath
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            launcherIntentCamera.launch(intent)
                        }
                    }
                    1 -> {
                        val intent = Intent()
                        intent.action = Intent.ACTION_GET_CONTENT
                        intent.type = "image/*"
                        val chooser = Intent.createChooser(intent, "Choose a Picture")
                        launcherIntentGallery.launch(chooser)
                    }
                }
            }
            builder.show()
        }

        binding.upload.setOnClickListener {
            uploadImage()
        }

        binding.back.setOnClickListener {
            finish()
        }


    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.imageUpload.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.imageUpload.setImageURI(selectedImg)
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, "Did not get permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage() {
        if (getFile == null) {
            Toast.makeText(this, "Please insert picture", Toast.LENGTH_SHORT).show()
        } else {
            val file = reduceFileImage(getFile as File)
            val requestFile = file.asRequestBody("image/*".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("data", file.name, requestFile)

            val description = "Upload image for analysis"
            val descriptionPart = MultipartBody.Part.createFormData("description", description)
            Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show()

            uploadViewModel.upload(imageMultipart, descriptionPart)
            uploadViewModel.result.observe(this) { result ->
                if (result != null) {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference
                    val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

                    val drawable: Drawable = binding.imageUpload.drawable
                    val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()

                    val uploadTask = imageRef.putBytes(data)

                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val photoUrl = uri.toString()

                            val date = System.currentTimeMillis()
                            val history = History(photoUrl, result, date)

                            val database = FirebaseDatabase.getInstance()
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            val historyRef = database.getReference("account/$userId/history")

                            val newItemRef = historyRef.push()
                            newItemRef.setValue(history)
                                .addOnSuccessListener {
                                    showLoading(false)
                                    finish()
                                    val intent = Intent(this, DetailResponseActivity::class.java)
                                    intent.putExtra(DetailResponseActivity.EXTRA_RESULT, result)
                                    intent.putExtra(DetailResponseActivity.EXTRA_PICTURE, photoUrl)
                                    intent.putExtra(DetailResponseActivity.EXTRA_DESCRIPTION, uploadViewModel.description.value)
                                    intent.putExtra(DetailResponseActivity.EXTRA_HANDLING,uploadViewModel.handling.value)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}