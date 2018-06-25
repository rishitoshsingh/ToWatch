package com.example.rishi.towatch.firebase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
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
import java.util.*


//import javax.swing.text.StyleConstants.getBackground


class SignUpActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mSharedPreferencesEditor: SharedPreferences.Editor

    private val RC_SIGN_IN = 9001

    public override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val texts = arrayOf("Watch Trailers", "Check Facts", "Get Recommendations", "Find Similar Movies", "Discover Movies", "Get info of movies from Youtube Movie Trailers")
        var tipPosition = 0

        val fadingText = findViewById<TextView>(R.id.fadingTextView)
        val faddingTextHandler = Handler()
        val fadingTextRunnable = object : Runnable {
            override fun run() {
                //set number of tip(randon/another way)
                runOnUiThread {
                    fadingText.text = texts[tipPosition++]
                    fadingText.visibility = View.VISIBLE
                    fadingText.setTextColor(resources.getColor(R.color.colorAccent))
                }
                if (tipPosition == 6) tipPosition = 0
                faddingTextHandler.postDelayed(this, 3000)
            }
        }
        faddingTextHandler.post(fadingTextRunnable)

        val gradients = arrayOf(resources.getDrawable(R.drawable.pacific_dream), resources.getDrawable(R.drawable.venice), resources.getDrawable(R.drawable.can_you_feel_the_love_tonight), resources.getDrawable(R.drawable.the_blue_lagoon))
        var nextPosition = 1
        var previousPosition = 0
        val colorChangeHandler = Handler()
        val colorChangingRunnable = object : Runnable {
            override fun run() {
                val color = arrayOf(gradients[previousPosition++], gradients[nextPosition++])
                val trans = TransitionDrawable(color)
                runOnUiThread {
                    iconBackground.background = trans
                    trans.startTransition(3000)
                }
                if (nextPosition == 3) nextPosition = 0
                if (previousPosition == 3) previousPosition = 0
                colorChangeHandler.postDelayed(this, 3000)
            }
        }
        colorChangeHandler.post(colorChangingRunnable)
//


        mAuth = FirebaseAuth.getInstance()
        register.setOnClickListener {

            val email = email.text.toString()
            val password = password.text.toString()

            if (email == "") {
                Snackbar.make(signInContainer, "Enter Email ID", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password == "") {
                Snackbar.make(signInContainer, "Enter Password", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("CreateUser", "createUserWithEmail:success")
                            val user = mAuth?.getCurrentUser()
                            Snackbar.make(signInContainer, "Registered", Snackbar.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Log.w("CreateUser", "createUserWithEmail:failure", task.getException())
                            Snackbar.make(signInContainer, "Registration Unsuccessful", Snackbar.LENGTH_SHORT).show()
                        }
                    }
        }

        log_in.setOnClickListener {

            val email = email.text.toString()
            val password = password.text.toString()

            if (email == "") {
                Snackbar.make(signInContainer, "Enter Email ID", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password == "") {
                Snackbar.make(signInContainer, "Enter Password", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("onCompleteListner", "signInWithEmail:success")
                            val user = mAuth?.getCurrentUser()
                            Snackbar.make(signInContainer, "Login Successful", Snackbar.LENGTH_SHORT).show()
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("onCompleteListner", "signInWithEmail:failure", task.exception)
                            Snackbar.make(signInContainer, "Login Unsuccessful", Snackbar.LENGTH_SHORT).show()

                        }
                    }
        }

        forgot.setOnClickListener {
            Snackbar.make(signInContainer, "Currently this feature is not available", Snackbar.LENGTH_SHORT).show()
//            TODO("Forgot Password")
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignIn.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
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
                mSharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
                mSharedPreferencesEditor = mSharedPreferences.edit()
                mSharedPreferencesEditor.putBoolean("account", true)
                mSharedPreferencesEditor.putString("GivenName", account.givenName)
                mSharedPreferencesEditor.putString("DisplayName", account.displayName)
                mSharedPreferencesEditor.putString("Email", account.email)
                mSharedPreferencesEditor.putString("Id", account.id)
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
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    }

                    // ...
                }
    }

}
