package com.example.tasktwo_tk

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class NewTaskEntry : AppCompatActivity() {

    //1. declare Variables

//------------------New Task Entry VARIABLES --------------------------

    //VARIABLES
    lateinit var edTvTaskName: EditText
    lateinit var edTvTaskDesc: EditText

    //spinner variables
    lateinit var spinner: Spinner
    lateinit var edTvUserSpinnerInput: EditText
    lateinit var btnSaveUserSpinnerInput : Button
    lateinit var categoriesArrayList : ArrayList<String>
    lateinit var adapter : ArrayAdapter<String>

    //daily goals variables
    lateinit var edTvDailyMin: EditText
    lateinit var edTvDailyMax: EditText


    lateinit var startDateBtn: Button
    lateinit var startTimeBtn: Button
    lateinit var endDateBtn: Button
    lateinit var endTimeBtn: Button

    //CAMERA 2 Variables - 4/5 Variables

    lateinit var btnViewRec: Button //btnViewRecords
    lateinit var captureImgButton: Button //btnSaveTaskEntry

    //Database Reference
    lateinit var database: DatabaseReference


    //GLOBALS
    var startDate: Date?=null
    var startTime: Date?=null
    var endDate: Date?=null
    var endTime: Date?=null





//------------------ADVANCED CAMERA 2 --------------------------

    //VARIABLES
    lateinit var imageViewCam: ImageView
    lateinit var btnChoose : Button
    lateinit var btnTakePic: Button
    lateinit var btnUploadImage : Button

    lateinit var btnNrmlCamera : Button //ngozi

    //GLOBALS
    var filePath : Uri? =null
    val PICK_IMAGE_REQUEST = 22
    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.reference
    val firestore = FirebaseFirestore.getInstance()
    val REQUEST_IMAGE_CAPTURE =1

//------------------ADVANCED CAMERA 2 --------------------------



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task_entry)


        //2. TYPECASTING

        edTvTaskName = findViewById(R.id.edTaskName)
        edTvTaskDesc = findViewById(R.id.edTaskDescription)

        //SPINNER
        spinner = findViewById(R.id.spinner)
        edTvUserSpinnerInput = findViewById(R.id.edTextViewCategory)
        btnSaveUserSpinnerInput = findViewById(R.id.btnSaveUserCategory)

        edTvDailyMin =findViewById(R.id.edTvDailyMin)
        edTvDailyMax =findViewById(R.id.edTvDailyMax)

        startDateBtn = findViewById(R.id.btnStartDate)
        startTimeBtn = findViewById(R.id.btnStartTime)
        endDateBtn = findViewById(R.id.btnEndDate)
        endTimeBtn = findViewById(R.id.btnEndTime)

        captureImgButton = findViewById(R.id.btnSaveEntry) //btnSaveTaskEntry
        btnViewRec =findViewById(R.id.btnViewEntries) //btnViewRecords


        database = FirebaseDatabase.getInstance().reference


        //Populate Spinner - without using android resource array file
        categoriesArrayList = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesArrayList)
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


//----------------------BTN LISTENER EVENTS----------------------------

        // BTN Listener -> save user inout from edit text to spinner
        btnSaveUserSpinnerInput.setOnClickListener{

            val newCategory = edTvUserSpinnerInput.text.toString()
            if (newCategory.isNotEmpty() && !categoriesArrayList.contains(newCategory))
            {
                categoriesArrayList.add(newCategory)
                adapter.notifyDataSetChanged()
                edTvUserSpinnerInput.text.clear()
            }
        }


        //OnClickListener EVENTS ->  Call BTNS
        startDateBtn.setOnClickListener{showDatePicker((startDatelistener))}
        endDateBtn.setOnClickListener{showDatePicker((endDateListener))}
        startTimeBtn.setOnClickListener{showTimePicker((startTimeListener))}
        endTimeBtn.setOnClickListener{showTimePicker((endTimeListener))}


        // BTN Listener ->  Save Task Entry 2 Firebase
        captureImgButton.setOnClickListener{

            val selectedItem = spinner.selectedItem as String
            val taskName = edTvTaskName.text.toString()
            val taskDesc = edTvTaskDesc.text.toString()

            val taskdailyMin = edTvDailyMin.text.toString()
            val taskdailyMax = edTvDailyMax.text.toString()



            if(taskName.isEmpty())
            {
                edTvTaskName.error = "Asseblief, Please enter a Task Name Bru "
                return@setOnClickListener
            }

            if(taskDesc.isEmpty())
            {
                edTvTaskDesc.error = "Asseblief, Please enter a description my Guy!!!"
                return@setOnClickListener
            }

            //ELSE -> Call 3.Method -> Save 2 Firebase
            saveToFirebase(selectedItem,taskName, taskDesc, taskdailyMin, taskdailyMax)

        }//end_Firebase_BTN


        // BTN Listener -> Call Method -> View Task Entry Records
        btnViewRec.setOnClickListener() {
            fetchAndDisplay()
        }



//------------------ADVANCED CAMERA 2 --------------------------

        //TYPECASTING
        imageViewCam = findViewById(R.id.imageViewCam2)
        btnChoose = findViewById(R.id.btnCam2Choose)
        btnTakePic = findViewById(R.id.btnCam2TakePic)
        btnUploadImage = findViewById(R.id.btnCam2UploadImage)

        btnNrmlCamera = findViewById(R.id.btnNrmlCamera)



        // BTN Listener -> Choose Image
        btnChoose.setOnClickListener{
            selectImage()
        }

        // BTN Listener -> Take Pic
        btnTakePic.setOnClickListener{
            dispatchTakePictureIntent() //replace this with normal camera stuff
        }

        // BTN Listener -> Upload Image
        btnUploadImage.setOnClickListener{
            uploadImage()
        }

                //btn -> Go 2 Normal Camera

        // BTN Listener -> Go 2 Normal Camera
        btnNrmlCamera.setOnClickListener{
            val intent = Intent(this, Kamera::class.java)
            startActivity(intent)
        }

//------------------ADVANCED CAMERA 2 --------------------------



    }//END On Create




//------------------New Task Entry VARIABLES --------------------------

    //1.METHOD -> Pick Date
    fun showDatePicker(dateSetListener : DatePickerDialog.OnDateSetListener)
    {
        //calendar object --> year / month date
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,dateSetListener, year, month, day)
        datePickerDialog.show()
    }

    //2.METHOD -> Pick Time
    fun showTimePicker (timeSetListener: TimePickerDialog.OnTimeSetListener)
    {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this,timeSetListener, hour, minute,true)
        timePickerDialog.show()
    }


    //FORMAT --> fb --> date --> date util
    //string format start time


    //1.LISTENER -> startDate Btn
    val startDatelistener = DatePickerDialog.OnDateSetListener { _: DatePicker, year:Int, month:Int, day:Int
        ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(year, month, day)
        startDate = selectedCalendar.time
        val dateFormat  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateString  = dateFormat.format(startDate!!)
        startDateBtn.text = selectedDateString

    }

    //2.LISTENER -> endDate Btn
    val endDateListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year:Int, month:Int, day:Int
        ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(year,month,day)
        endDate = selectedCalendar.time
        //date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateString = dateFormat.format(endDate!!)
        endDateBtn.text = selectedDateString
    }

    //3.LISTENER -> startTime Btn
    val startTimeListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay:Int, minute:Int
        ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = startDate
        selectedCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
        selectedCalendar.set(Calendar.MINUTE,minute)
        startTime = selectedCalendar.time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val selectedTimeString = timeFormat.format(startTime!!)
        startTimeBtn.text = selectedTimeString

    }

    //4.LISTENER -> endTime BTN
    val endTimeListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay:Int, minute:Int
        ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = endDate
        selectedCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
        selectedCalendar.set(Calendar.MINUTE,minute)
        endTime = selectedCalendar.time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val selectedTimeString = timeFormat.format(endTime!!)
        endTimeBtn.text = selectedTimeString


    }


//1. declare variable in model class
//2. Assign value to variable
//3. Pass value into database


    //3.METHOD -> Save 2 Firebase
    fun saveToFirebase(item:String, taskName:String, taskDesc:String, taskdailyMin:String, taskdailyMax:String) {

        //FORMATS
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())


        //fetch the values from the local btns text
        val startDateString = startDateBtn.text.toString()
        val startTimeString = startTimeBtn.text.toString()
        val endDateString = endDateBtn.text.toString()
        val endTimeString = endTimeBtn.text.toString()

        //2. Assign value to variable
        val taskCat = spinner.selectedItem.toString()

        //val imgUrl =

        //parse values for firebase
        val startDate = dateFormat.parse(startDateString)
        val startTime = timeFormat.parse(startTimeString)
        val endDate = dateFormat.parse(endDateString)
        val endTime = timeFormat.parse(endTimeString)

        //calcs --> Optional
        val totalTimeinMillis = endDate.time - startDate.time + endTime.time - startTime.time
        val totalMinutes = totalTimeinMillis / (1000 * 60)
        val totalHours = totalMinutes / 60
        val minutesRemaining = totalMinutes % 60
        val totalTimeString = String.format( Locale.getDefault(), "%02d:%02d", totalHours, minutesRemaining )


        // Creates a new child node && retrieves its unique key
        val key = database.child("ChildNode-TaskItems").push().key
        if (key != null)
        {
            //i. Creating a TaskModel Object:
            val task = TaskModel2( taskName,taskDesc,startDateString, startTimeString, endDateString,
                endTimeString, totalTimeString, taskCat, taskdailyMin, taskdailyMax  )

            //ii. Saving the TaskModel Object to Firebase:
            database.child("ChildNode-TaskItems").child(key).setValue(task)
                .addOnSuccessListener {
                    Toast.makeText(this, "DANKO \nTimesheet entry saved to the database", Toast.LENGTH_SHORT ).show()
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this, "ERROR: ${err.message}", Toast.LENGTH_SHORT).show()
                }

        }//end_If

    }//end_saveToFirebase



    //METHOD -> to View Records -> View the items from the db
    fun fetchAndDisplay()
    {
        database.child("ChildNode-TaskItems").get().addOnSuccessListener { dataSnapshot ->

            if (dataSnapshot.exists())
            {
                val records = ArrayList<String>()
                dataSnapshot.children.forEach { snapshot ->
                    val task = snapshot.getValue(TaskModel::class.java)

                    task?.let { records.add(  "\nTask Name: ${it.taskName}," +
                            "\nDescription: ${it.taskDesc}," +
                            "\nCATEGORY: ${it.taskCat}," +

                            "\n\nDaily Min(Hrs): ${it.taskdailyMin}," +
                            "\nDaily Max(Hrs): ${it.taskdailyMax}," +

                            //"\n\nImage Url: ${it.imgUrl}," +


                            "\n\nStart Date: ${it.startDateString}," +
                            "\nStart Time: ${it.startTimeString}," +
                            "\nEnd Date: ${it.endDateString}," +
                            "\nEnd Time: ${it.endTimeString}," +
                            "\nTotal Time: ${it.totalTimeString}\n" )
                    }
                }

                //Call Method -> Fetch && Display Database Records
                displayDialog(records)

            } else {
                Toast.makeText(this, "No records Found", Toast.LENGTH_SHORT).show() }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to detc data", Toast.LENGTH_SHORT).show() }


    }//end View Method

    //METHOD -> Fetch && Display Database Records
    fun displayDialog(records: ArrayList<String>)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Database Records")

        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, records)
        builder.setAdapter (arrayAdapter, null)
        builder.setPositiveButton( "Ok", null)
        builder.show()

    }

//------------------New Task Entry VARIABLES --------------------------





//------------------ADVANCED CAMERA 2 --------------------------

    //METHOD -> to Choose an Image from device gallery
    fun selectImage()
    {
        val intent = Intent()

        //intent is for selecting an image file of any format.
        intent.type ="image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent,"Select image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        // calls the parent activity's
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null)
        {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
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
        //uses the hashMapOf function to create a map to store the image URL under the key "imageUrl"
        val imageMap = hashMapOf( "imageUrl" to imageURL)

        // Add the image data to the "ChildNode-images" collection in Firestore
        firestore.collection("ChildNode-images").add(imageMap)
            .addOnSuccessListener {
                // Document addition successful
                Toast.makeText(this, "Image Saved online(Firestore)", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener{
                // Document addition failed
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show() }
    }


    //METHOD -> Save Image To Firebase
    private fun saveImageToFirebase(imageBitmap: Bitmap) {

        // 1. Compress Image: - Convert Bitmap to byte array for efficient storage in Cloud Storage
        //Creates a B.A.O.S to hold the compressed image data. -> Compresses to a JPEG format  -> compressed data is written to the baos.
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100,baos)
        val data = baos.toByteArray()

        // 2. Generate Unique Image Name: - Use UUID to ensure uniqueness
        val imageName = UUID.randomUUID().toString() + ".jpg"

        // 3. Create Image Reference in Cloud Storage: - Reference child node "ChildNode-images" and append unique name
        val imageRef = storageReference.child("ChildNode-images/$imageName")

        // 4. Uploads the compressed image data to the Cloud Storage location
        imageRef.putBytes(data)
            .addOnSuccessListener {
                // 5. Get Download URL for uploaded image
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

// Uploads a selected image to Cloud Storage and then saves its download URL to Firestore

    //METHOD -> uploads Image to FireStore
    fun uploadImage() {
        // 1. Check for Selected Image:
        //   - Handle the case where no image is selected
        // Checks filePath for Selected Image
        filePath?.let { filePath ->
            if (contentResolver.openInputStream(filePath) != null) {

                //Creates a progress dialog to display upload progress.
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading ...")
                progressDialog.show()

                val ref = storageReference.child("ChildNode-images/${UUID.randomUUID()}.jpg")

                //Uploads the image file at filePath to the Cloud Storage location specified by
                ref.putFile(filePath)
                    .addOnSuccessListener { taskSnapshot -> progressDialog.dismiss()
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

//------------------ADVANCED CAMERA 2 --------------------------



} //END CLASS



data class TaskModel2(   var taskName: String? = null,
                        var taskDesc: String? = null,

                        var startDateString: String? = null,
                        var startTimeString: String? = null,
                        var endDateString: String? = null,
                        var endTimeString: String? = null,

                        var totalTimeString: String? = null,

                        var taskCat : String? = null,

                         //var imgUrl : String? = null,


                        var taskdailyMin : String? = null,
                        var taskdailyMax : String? = null )


//1. declare variable in model class
//2. Assign value to variable
//3. Pass value into database
