package com.example.tasktwo_tk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner

class CreateCategoryActivity : AppCompatActivity() {

    //variables
    lateinit var spinner: Spinner
    lateinit var edUserInput: EditText
    lateinit var btnSave : Button

    lateinit var categoriesArrayList : ArrayList<String>
    lateinit var adapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        //TYPECASTING
        spinner = findViewById(R.id.spinner)
        edUserInput = findViewById(R.id.edTextViewCategory)
        btnSave = findViewById(R.id.btnSave)

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



    }//on Create

}