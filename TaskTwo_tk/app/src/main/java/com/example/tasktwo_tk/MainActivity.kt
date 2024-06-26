package com.example.tasktwo_tk

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //VARIABLES
    lateinit var edName: EditText
    lateinit var edDesc: EditText
    lateinit var startDateBtn: Button
    lateinit var startTimeBtn: Button
    lateinit var endDateBtn: Button
    lateinit var endTimeBtn: Button

    lateinit var takePicBtn: Button
    lateinit var btnAdvCam: Button
    lateinit var btnViewRec: Button

    lateinit var captureImgButton: Button


    //1. declare Variables
    lateinit var edTvDailyMin: EditText
    lateinit var edTvDailyMax: EditText

    lateinit var spinner: Spinner
    lateinit var edUserInput: EditText
    lateinit var btnSave : Button

    lateinit var categoriesArrayList : ArrayList<String>
    lateinit var adapter : ArrayAdapter<String>



    lateinit var database: DatabaseReference

    //GLOBALS
    var startDate: Date?=null
    var startTime: Date?=null
    var endDate: Date?=null
    var endTime: Date?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TYPECASTING
        edName = findViewById(R.id.edName)
        edDesc = findViewById(R.id.edDescription)
        spinner = findViewById(R.id.spinner)
        startDateBtn = findViewById(R.id.btnStartDate)
        startTimeBtn = findViewById(R.id.btnStartTime)
        endDateBtn = findViewById(R.id.btnEndDate)
        endTimeBtn = findViewById(R.id.btnEndTime)
        captureImgButton = findViewById(R.id.btnCapture)

        takePicBtn =findViewById(R.id.btnKamera)
        btnAdvCam =findViewById(R.id.btnAdvCam)
        btnViewRec =findViewById(R.id.btnViewRec)

        //2. Typecasting
        edTvDailyMin =findViewById(R.id.edTvDailyMin)
        edTvDailyMax =findViewById(R.id.edTvDailyMax)


        //SPINNER
        spinner = findViewById(R.id.spinner)
        edUserInput = findViewById(R.id.edTextViewCategory)
        btnSave = findViewById(R.id.btnSaveUser)


        database = FirebaseDatabase.getInstance().reference


        //simple code to populate the spinner - without using android resource array file
        categoriesArrayList = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesArrayList)
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        // BTN Listener ->save user inout from edit text to spinner
        btnSave.setOnClickListener{

            val newCategory = edUserInput.text.toString()
            if (newCategory.isNotEmpty() && !categoriesArrayList.contains(newCategory))
            {
                categoriesArrayList.add(newCategory)
                adapter.notifyDataSetChanged()
                edUserInput.text.clear()
            }
        }




        //OnClickListener EVENTS ->  Call BTNS
        startDateBtn.setOnClickListener{showDatePicker((startDatelistener))}
        endDateBtn.setOnClickListener{showDatePicker((endDateListener))}
        startTimeBtn.setOnClickListener{showTimePicker((startTimeListener))}
        endTimeBtn.setOnClickListener{showTimePicker((endTimeListener))}

        // BTN Listener ->  Save Entry 2 Firebase
        captureImgButton.setOnClickListener{

            val selectedItem = spinner.selectedItem as String
            val taskName = edName.text.toString()
            val taskDesc = edDesc.text.toString()

            val taskdailyMin = edTvDailyMin.text.toString()
            val taskdailyMax = edTvDailyMax.text.toString()



            if(taskName.isEmpty())
            {
                edName.error = "Asseblief, Please enter a Task Name Bru "
                return@setOnClickListener
            }

            if(taskDesc.isEmpty())
            {
                edDesc.error = "Asseblief, Please enter a description my Guy!!!"
                return@setOnClickListener
            }

            //ELSE -> Call 3.Method -> Save 2 Firebase
            saveToFirebase(selectedItem,taskName, taskDesc, taskdailyMin, taskdailyMax)

        }//end_Firebase_BTN


        // BTN Listener -> Call Method -> View Records
        btnViewRec.setOnClickListener() {
            fetchAndDisplay()
        }

        // BTN Listener -> TAKE PIC STUFF
        takePicBtn.setOnClickListener{
            val intent = Intent(this, Kamera::class.java)
            startActivity(intent)
        }

        // BTN Listener -> ADVANCED TAKE PIC STUFF
        btnAdvCam.setOnClickListener{
            val intentCamTwo = Intent(this, NewTaskEntry::class.java)
            startActivity(intentCamTwo)
        }

/*
        // BTN Listener -> Go 2 Navigation Drawer Activity
        btn_navDrawer.setOnClickListener() {
            val intentCamTwo = Intent(this, NavigationDraweViewsActivity::class.java)
            startActivity(intentCamTwo)
        }
*/

    }//end_onCreate


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
            val task = TaskModel( taskName,taskDesc,startDateString, startTimeString, endDateString,
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
                                                        "\nCATEGORY: ${it.taskCat}" +

                                                        "\n\ntaskdailyMin: ${it.taskdailyMin}" +
                                                        "\ntaskdailyMax: ${it.taskdailyMax}" +

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

}//class ends



    data class TaskModel(   var taskName: String? = null,
                            var taskDesc: String? = null,
                            var startDateString: String? = null,
                            var startTimeString: String? = null,
                            var endDateString: String? = null,
                            var endTimeString: String? = null,
                            var totalTimeString: String? = null,

                            //1. declare variable in Model Class
                            var taskCat : String? = null,
                            //var imgUrl : String? = null,

                            var taskdailyMin : String? = null,
                            var taskdailyMax : String? = null )

    //1. declare variable in model class
    //2. Assign value to variable
    //3. Pass value into database


