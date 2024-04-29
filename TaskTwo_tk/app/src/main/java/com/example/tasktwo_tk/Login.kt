package com.example.tasktwo_tk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tasktwo_tk.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {


    //Using Binding -> Gradle Script
    private lateinit var binding: ActivityLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)


        //Alternative to typecasting -> Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //initialize
        firebaseAuth = FirebaseAuth.getInstance()


        //Event Handler -> Binding when clicking on tv Link
        binding.tvRegisterLink.setOnClickListener{

            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }



        //Event Handler -> Binding when clicking on BTN
        binding.btnLogin.setOnClickListener{

            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()


            //CHECKS
            if(email.isNotEmpty() && password.isNotEmpty()  ){

                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{

                        if(it.isSuccessful){

                            Toast.makeText(this, "Successful Login ->> NavigationDraweViewsActivity -> MainActivity", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Login, MainActivity::class.java)
                            startActivity(intent)

                        }else{  //it.is NOT Successful

                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }

                    }//listener

            } else{ //1 of da Fields is EMPTY

                Toast.makeText(this, "Email or password is Empty", Toast.LENGTH_SHORT).show()
            }
            
        }//end_onClick_Listen




    }
}