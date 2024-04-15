package com.example.tasktwo_tk

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class Kamera : AppCompatActivity() {

    //VRIABLES
    lateinit var takePicBtn: Button
    lateinit var imgViewPic: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kamera)

        //TYPECASTING
        takePicBtn = findViewById(R.id.btnCaptureImg)
        imgViewPic = findViewById(R.id.imageView)


        takePicBtn.setOnClickListener{

            //call 1.Method
            openCamera()
        }


    }//end_onCreate


        //1.METHOD -> to open camera feature on device
        fun openCamera()
        {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE)
        }


        //inline variable that defines the number of pictures the request can take
        companion object{

            const val CAMERA_REQUEST_CODE = 100;
        }


/*

    //2.METHOD ? ->
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Call 3.Method -> save to firebase
            saveImageToFirebase(imageBitmap)
        }

    }//method ends
 */


/*
      //3.METHOD -> Save Image to firebase
        fun saveImageToFirebase(imageBitmap: Bitmap)
        {
            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
            val base64Image = Base64.encodeToString(outputStream.toByteArray(),
                Base64.DEFAULT)

            //refer to the database
            val databaseRefernce = FirebaseDatabase.getInstance().getReference("images")
            val imgId = databaseRefernce.push().key

            //subFolder
            databaseRefernce.child(imgId!!).setValue(base64Image)

        }//method ends
     */








}