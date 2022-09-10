package com.ahmed_badr.to_do_manager.ui.elements.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ahmed_badr.to_do_manager.R
import com.ahmed_badr.to_do_manager.databinding.ActivityAuthenticationBinding
import com.ahmed_badr.to_do_manager.ui.view_models.AuthenticationViewModel
import com.ahmed_badr.to_do_manager.ui.view_models.UserViewModel
import com.ahmed_badr.to_do_manager.utils.response.AuthResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding

    private lateinit var googleSignInClient: GoogleSignInClient

    private val authenticationViewModel : AuthenticationViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()

    // Sign in intent result launcher
    private val signInIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result!!.data)
                handleGoogleSignInTask(task)
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }
    }

    private fun handleGoogleSignInTask(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            // Show Progress bar
            binding.signInProgressBar.visibility = View.VISIBLE

            val account = task.result
            applySignInRequest(account)
        } else {
            val error = task.exception!!.message.toString()
            showToast(error)
        }
    }

    // send sign in request to firebase authentication if the account is not null
    private fun applySignInRequest(account: GoogleSignInAccount?) {
        account?.let {
            authenticationViewModel.signInWithGoogle(account)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.apply {
            buttonSignIn.setOnClickListener {
                sendSignInWithGoogleIntent()
            }
        }

        // Trigger any change in user authentication state
        authenticationViewModel.userAuthResult.observe(this) { authResult ->
            when (authResult) {
                is AuthResult.Success -> {
                    val user = authResult.data!!

                    val isNewUser = authResult.isNewUser
                    if (isNewUser) userViewModel.addUser(user)

                    navigateToMain()
                }
                is AuthResult.Error -> {
                    val errorMessage = authResult.message.toString()
                    showToast(errorMessage)
                }
                else -> {
                    val errorMessage = getString(R.string.something_wont_wrong)
                    showToast(errorMessage)
                }
            }
        }
    }

    // choose account intent
    private fun sendSignInWithGoogleIntent() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webClientId))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        signInIntentLauncher.launch(signInIntent)
    }

    // navigate to the main activity
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // show toast text
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}