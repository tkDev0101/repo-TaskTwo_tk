package com.example.tasktwo_tk

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore


import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

class KameraTwo : AppCompatActivity() {

    //VARIABLES
    lateinit var imageViewCam: ImageView
    lateinit var btnChoose : Button
    lateinit var btnTakePic: Button
    lateinit var btnUpload : Button

    //GLOBALS
    var filePath : Uri? =null
    val PICK_IMAGE_REQUEST = 22
    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.reference
    val firestore = FirebaseFirestore.getInstance()
    val REQUEST_IMAGE_CAPTURE =1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kamera_two)

        //TYPECASTING
        imageViewCam = findViewById(R.id.imageViewCam2)
        btnChoose = findViewById(R.id.btnCam2Choose)
        btnTakePic = findViewById(R.id.btnCam2TakePic)
        btnUpload = findViewById(R.id.btnCam2UploadImage)


        // BTN Listener -> Choose Image
        btnChoose.setOnClickListener{
            selectImage()
        }

        // BTN Listener -> Take Pic
        btnTakePic.setOnClickListener{
            dispatchTakePictureIntent()
        }

        // BTN Listener -> Upload Image
        btnUpload.setOnClickListener{
            uploadImage()
        }


    }//end_onCreate


    //METHOD -> to Choose an Image
    fun selectImage()
    {
        val intent = Intent()
        intent.type ="image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent,"Select image"),
            PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data
            != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,
                    filePath)
                imageViewCam.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageViewCam.setImageBitmap(imageBitmap)
            // Save image to Firebase
            saveImageToFirebase(imageBitmap)
        }
    }//end method

    //METHOD -> Take Pic
    fun dispatchTakePictureIntent()
    {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also{
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    //METHOD -> to save the image url
    fun saveImageUrlToFirestore(imageURL :String)
    {
        val imageMap = hashMapOf( "imageUrl" to imageURL)
        firestore.collection("ChildNode-images ")
            .add(imageMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Image Saved online(Firestore)", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show() }
    }

    //METHOD -> Save Image To Firebase
    private fun saveImageToFirebase(imageBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100,baos)
        val data = baos.toByteArray()
        val imageName = UUID.randomUUID().toString() + ".jpg"
        val imageRef = storageReference.child("ChildNode-images/$imageName")
        imageRef.putBytes(data)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageURL = uri.toString()
                    saveImageUrlToFirestore(imageURL)
                }.addOnFailureListener{
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    //METHOD -> uploads Image to FireStore
    fun uploadImage() {
        filePath?.let { filePath ->
            if (contentResolver.openInputStream(filePath) != null) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading ...")
                progressDialog.show()

                val ref = storageReference.child("ChildNode-images/${UUID.randomUUID()}.jpg")
                ref.putFile(filePath).addOnSuccessListener { taskSnapshot ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()

                    //get the download url from firestore
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        val imageURL = uri.toString()
                        //save image to firebase
                        saveImageUrlToFirestore(imageURL)
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to get url", Toast.LENGTH_SHORT).show() }
                }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage("Uploaded: ${progress.toInt()} %")
                    }
            } else {
                Toast.makeText(this, "File doesn't exist", Toast.LENGTH_SHORT).show() }
        } ?: run {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }

    }//end method


}
//ngicela bang'bizela oMalume