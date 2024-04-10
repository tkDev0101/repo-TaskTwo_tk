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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //VRIABLES
    lateinit var spinner: Spinner
    lateinit var capButton: Button
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
        capButton = findViewById(R.id.btnCapture)
        database = FirebaseDatabase.getInstance().reference

        //spinner Typecasting?
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this, R.array.tk_spinner_items,
            android.R.layout.simple_spinner_dropdown_item )
        spinner.adapter = spinnerAdapter


        //OnClickListener EVENTS ->  btn pull
        startDateBtn.setOnClickListener{showDatePicker((startDatelistener))}
        endDateBtn.setOnClickListener{showDatePicker((endDateListener))}
        startTimeBtn.setOnClickListener{showTimePicker((startTimeListener))}
        endTimeBtn.setOnClickListener{showTimePicker((endTimeListener))}

        //Firebase BTN
        capButton.setOnClickListener{
            val selectedItem = spinner.selectedItem as String
            val taskName = edName.text.toString()
            val taskDesc = edDesc.text.toString()



            if(taskName.isEmpty())
            {
                edName.error = "Please enter timetable name"
                return@setOnClickListener
            }

            if(taskDesc.isEmpty())
            {
                edDesc.error = "Please enter time sheet description"
                return@setOnClickListener
            }

            saveToFirebase(selectedItem,taskName, taskDesc)

        }



    }//end_onCreate


    //METHOD -> Pick Date
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

    //METHOD -> Pick Time
    fun showTimePicker (timeSetListener: TimePickerDialog.OnTimeSetListener)
    {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this,timeSetListener, hour, minute,true)
        timePickerDialog.show()
    }



    //startDate Btn LISTENER

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


    //endDate Btn LISTENER

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


    //startTime Btn LISTENER

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


    //endTime LISTENER
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



    //METHOD -> Firebase
    fun saveToFirebase(item:String, taskName:String, taskDesc:String) {
        //FORMATS

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        //2. Asign value to varible
        val taskCat = spinner.selectedItem.toString()



        //fetch the values from the local btns text
        val startDateString = startDateBtn.text.toString()
        val startTimeString = startTimeBtn.text.toString()
        val endDateString = endDateBtn.text.toString()
        val endTimeString = endTimeBtn.text.toString()

        //parse values for firbase
        val startDate = dateFormat.parse(startDateString)
        val startTime = timeFormat.parse(startTimeString)
        val endDate = dateFormat.parse(endDateString)
        val endTime = timeFormat.parse(endTimeString)

        //calcs --> Optional
        val totalTimeinMillis = endDate.time - startDate.time + endTime.time - startTime.time
        val totalMinutes = totalTimeinMillis / (1000 * 60)
        val totalHours = totalMinutes / 60
        val minutesRemaining = totalMinutes % 60
        val totalTimeString = String.format(
            Locale.getDefault(), "%02d:%02d", totalHours, minutesRemaining )

        val key = database.child("items").push().key
        if (key != null) {
            val task = TaskModel(                                                                                   //3. Pass value into database
                taskName,taskDesc,startDateString, startTimeString, endDateString, endTimeString, totalTimeString, taskCat )

            database.child("items").child(key).setValue(task)
                .addOnSuccessListener {
                    Toast.makeText(this, "Timesheet entry saved to the database", Toast.LENGTH_SHORT ).show()
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this, "ERROR: ${err.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }
}

data class TaskModel(
    var taskName: String? = null,
    var taskDesc: String? = null,
    var startDateString: String? = null,
    var startTimeString: String? = null,
    var endDateString: String? = null,
    var endTimeString: String? = null,
    var totalTimeString: String? = null,

    //1. declare variable in model class
    var taskCat : String? = null


)


//1. declare variable in model class
//2. Asign value to varible
//3. Pass value into database