package com.example.tasktwo_tk

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
    lateinit var spinner: Spinner
    lateinit var captureImgButton: Button
    lateinit var edName: EditText
    lateinit var edDesc: EditText
    lateinit var startDateBtn: Button
    lateinit var startTimeBtn: Button
    lateinit var endDateBtn: Button
    lateinit var endTimeBtn: Button
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

        database = FirebaseDatabase.getInstance().reference



        //connects the spinner with its adapter
        val spinnerAdapter = ArrayAdapter.createFromResource(
                                this, R.array.tk_spinner_items_Array,
                                android.R.layout.simple_spinner_dropdown_item )

        // -> spinner to display data using the adapter's contents.
        spinner.adapter = spinnerAdapter


        //OnClickListener EVENTS ->  Call BTNS
        startDateBtn.setOnClickListener{showDatePicker((startDatelistener))}
        endDateBtn.setOnClickListener{showDatePicker((endDateListener))}
        startTimeBtn.setOnClickListener{showTimePicker((startTimeListener))}
        endTimeBtn.setOnClickListener{showTimePicker((endTimeListener))}

        //Firebase BTN
        captureImgButton.setOnClickListener{
            val selectedItem = spinner.selectedItem as String
            val taskName = edName.text.toString()
            val taskDesc = edDesc.text.toString()



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
            saveToFirebase(selectedItem,taskName, taskDesc)

        }//end_Firebase_BTN



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



    //1.LISTENER -> startDate Btn

    //FORMAT --> fb --> date --> date util
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
    //string format start time
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
    fun saveToFirebase(item:String, taskName:String, taskDesc:String) {

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
                                    endTimeString, totalTimeString, taskCat )

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
}

data class TaskModel(

    var taskName: String? = null,
    var taskDesc: String? = null,
    var startDateString: String? = null,
    var startTimeString: String? = null,
    var endDateString: String? = null,
    var endTimeString: String? = null,
    var totalTimeString: String? = null,

    //1. declare variable in Model Class
    var taskCat : String? = null

    )
