package com.example.tasktwo_tk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tasktwo_tk.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    //Using Binding -> Gradle Script
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //intialize
        firebaseAuth = FirebaseAuth.getInstance()



        //Event Handler -> Binding when clicking on tv Link
        binding.tvLoginLink.setOnClickListener{

            //start next activity
            val intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
            //finish()
        }


        //Event Handler -> Binding when clicking on BTN
        binding.btnRegister.setOnClickListener{

            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            val confirmPassword = binding.edPasswordConfirm.text.toString()


            //CHECKS
            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() ){

                if(password == confirmPassword){

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener{

                            if(it.isSuccessful){


                                Toast.makeText(this, "Successful Sign Up ->>>> LoginActivity", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Register, Login::class.java)
                                startActivity(intent)

                            }else{  //it.is NOT Successful

                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            }

                        }//listener


                } else{  // password != confirmPassword

                    Toast.makeText(this, "Passwords Are Not Matching 2", Toast.LENGTH_SHORT).show()
                }



            } else{ //1 of da Fields is EMPTY

                Toast.makeText(this, "Email or password is Empty", Toast.LENGTH_SHORT).show()
            }


        }//end_onClick_Listen




    }
}