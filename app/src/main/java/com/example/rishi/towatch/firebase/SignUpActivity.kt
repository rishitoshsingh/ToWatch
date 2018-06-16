package com.example.rishi.towatch.firebase

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.rishi.towatch.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var mSharedPreferences:SharedPreferences
    private lateinit var mSharedPreferencesEditor: SharedPreferences.Editor

    private val RC_SIGN_IN = 9001


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        register.setOnClickListener {

            val email = email.text.toString()
            val password = password.text.toString()

            if (email == "") {
                Snackbar.make(new_linear_layout, "Enter Email ID", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password == "") {
                Snackbar.make(new_linear_layout, "Enter Password", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("CreateUser", "createUserWithEmail:success")
                            val user = mAuth?.getCurrentUser()
                            Snackbar.make(new_linear_layout, "Registered", Snackbar.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Log.w("CreateUser", "createUserWithEmail:failure", task.getException())
                            Snackbar.make(new_linear_layout, "Registration Unsuccessful", Snackbar.LENGTH_SHORT).show()
                        }

                    }
        }

        log_in.setOnClickListener {

            val email = email.text.toString()
            val password = password.text.toString()

            if (email == "") {
                Snackbar.make(new_linear_layout, "Enter Email ID", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password == "") {
                Snackbar.make(new_linear_layout, "Enter Password", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("onCompleteListner", "signInWithEmail:success")
                            val user = mAuth?.getCurrentUser()
                            Snackbar.make(new_linear_layout, "Login Successful", Snackbar.LENGTH_SHORT).show()
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("onCompleteListner", "signInWithEmail:failure", task.exception)
                            Snackbar.make(new_linear_layout, "Login Unsuccessful", Snackbar.LENGTH_SHORT).show()

                        }
                    }
        }

        forgot.setOnClickListener {
            Snackbar.make(new_linear_layout, "Currently this feature is not available", Snackbar.LENGTH_SHORT).show()
//            TODO("Forgot Password")
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignIn.setOnClickListener {
            val signInIntent:Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                mSharedPreferencesEditor = mSharedPreferences.edit()
                mSharedPreferencesEditor.putBoolean("account",true)
                mSharedPreferencesEditor.putString("GivenName",account.givenName)
                mSharedPreferencesEditor.putString("DisplayName",account.displayName)
                mSharedPreferencesEditor.putString("Email",account.email)
                mSharedPreferencesEditor.putString("Id",account.id)
                mSharedPreferencesEditor.putString("PersonPhotoUrl", account.photoUrl.toString())
                mSharedPreferencesEditor.commit()
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("SignIn Activity", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        finish()
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.exception)
                        Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
                    }

                    // ...
                }
    }

}
