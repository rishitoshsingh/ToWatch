package com.example.rishi.towatch.firebase

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.rishi.towatch.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        register.setOnClickListener {

            val email = email.text.toString()
            val password = password.text.toString()

            if (email == "") {
                Snackbar.make(new_linear_layout,"Enter Email ID",Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password == "") {
                Snackbar.make(new_linear_layout,"Enter Password",Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful){
                            Log.d("CreateUser", "createUserWithEmail:success")
                            val user = mAuth?.getCurrentUser()
                            Snackbar.make(new_linear_layout,"Registered",Snackbar.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Log.w("CreateUser", "createUserWithEmail:failure", task.getException())
                            Snackbar.make(new_linear_layout,"Registration Unsuccessful",Snackbar.LENGTH_SHORT).show()
                        }

                    }
        }

        log_in.setOnClickListener {

            val email = email.text.toString()
            val password = password.text.toString()

            if (email == "") {
                Snackbar.make(new_linear_layout,"Enter Email ID",Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password == "") {
                Snackbar.make(new_linear_layout,"Enter Password",Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("onCompleteListner", "signInWithEmail:success")
                            val user = mAuth?.getCurrentUser()
                            Snackbar.make(new_linear_layout,"Login Successful",Snackbar.LENGTH_SHORT).show()
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("onCompleteListner", "signInWithEmail:failure", task.exception)
                            Snackbar.make(new_linear_layout,"Login Unsuccessful",Snackbar.LENGTH_SHORT).show()

                        }
                    }
        }

        forgot.setOnClickListener {
            Snackbar.make(new_linear_layout,"Currently this feature is not available",Snackbar.LENGTH_SHORT).show()
//            TODO("Forgot Password")
        }

    }
}
