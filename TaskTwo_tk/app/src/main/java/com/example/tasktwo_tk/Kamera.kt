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
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class Kamera : AppCompatActivity() {

    //VARIABLES
    lateinit var takePicBtn: Button
    lateinit var imgViewPic: ImageView
    lateinit var mainActBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kamera)

        //TYPECASTING
        takePicBtn = findViewById(R.id.btnCaptureImg)
        imgViewPic = findViewById(R.id.imageView)
        mainActBtn = findViewById(R.id.btnMainActivity)


        takePicBtn.setOnClickListener{

            //call 1.Method
            openCamera()
        }

        // BTN -> Go 2 main Activity
        mainActBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



    }//end_onCreate


        //1.METHOD -> to open camera feature on device
        fun openCamera()
        {
            //creates an intent to open the device camera
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE)
        }


        //inline variable that defines the number of pictures the request can take
        companion object{

            const val CAMERA_REQUEST_CODE = 100;
        }



    //2.METHOD ? -> called when the camera activity finishes
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?)
    {
        // calls the parent activity's
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the request code matches the camera request && if the camera captured the image successfully && checks if the camera activity finished successfully
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            // If so, extract the captured image as a Bitmap from the Intent's extras
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Call 3.Method -> save to firebase
            saveImageToFirebase(imageBitmap)
        }

    }//method ends


      //3.METHOD -> Save Image to firebase
        fun saveImageToFirebase(imageBitmap: Bitmap)
        {
            //Compressing and Encoding the Image

            // 1. Prepare Image for Storage:
            //   - Compress the image to reduce file size
            //   - Encode the compressed image to Base64 for efficient storage in Firebase

            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
            val base64Image = Base64.encodeToString(outputStream.toByteArray(),
                Base64.DEFAULT)

            //refer to the database
            // Creates a new child node && retrieves its unique key

            // 2. Access Firebase Database:
            //   - Get a reference to the "ChildNode-images" node in your Firebase database

            val databaseRefernce = FirebaseDatabase.getInstance().getReference("ChildNode-images")

            // 3. Generate Unique Identifier and Save Image:
            //   - Generate a unique key for the image data using push()
            //   - Save the Base64 encoded image string to the newly created child node under "ChildNode-images"

            val imgId = databaseRefernce.push().key

            //subFolder
            databaseRefernce.child(imgId!!).setValue(base64Image)

        }//method ends



    }//end Kamera Class