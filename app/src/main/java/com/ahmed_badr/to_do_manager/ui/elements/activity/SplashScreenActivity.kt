package com.ahmed_badr.to_do_manager.ui.elements.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ahmed_badr.to_do_manager.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    companion object {
        // splash screen waiting time
        private const val SPLASH_TIME = 3000L
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // check if the user is authenticated or not
        val isAuthenticatedUser = Firebase.auth.currentUser != null

        // wait 3 seconds for the splash screen
        Handler(Looper.myLooper()!!).postDelayed({
            if (isAuthenticatedUser) navigateToMain() // is authenticated go to the Main Activity
            else navigateToAuthentication() // if not authenticated user go to Authentication Activity
        }, SPLASH_TIME)

    }

    // navigate to authentication activity
    private fun navigateToAuthentication() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        startActivity(intent)
        finish()
    }

    // navigate to main activity
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}