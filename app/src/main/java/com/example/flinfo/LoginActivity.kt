package com.example.flinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import retrofit2.Call
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.flinfo.retrofit.AuthenticationResponse
import com.example.flinfo.retrofit.RetrofitHelper
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Callback

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView
    private lateinit var forgotPasswordText: TextView
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)
        signupText = findViewById(R.id.signup_text)
        forgotPasswordText = findViewById(R.id.forgot_password_text)
        progressBar = findViewById(R.id.progress_bar)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            login(email, password)
        }

        signupText.setOnClickListener {
            // TODO: Implement signup logic
        }

        forgotPasswordText.setOnClickListener {
            // TODO: Implement forgot password logic
        }
    }

    private fun login(email: String, password: String) {
        val token = "d3dyLXVzZXI6JDBtZXdoYXRDMG1wbGV4UGEkJHcwcmQ="
        val credentials = HashMap<String, String>()
        credentials["token"] = token
        credentials["email"] = email
        credentials["password"] = password

        // Convert the HashMap to a RequestBody
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            Gson().toJson(credentials)
        )
        progressBar.visibility = View.VISIBLE

        val userApi = RetrofitHelper.getUserApiService()
        val call = userApi.authenticate(requestBody)

        call.enqueue(object : Callback<AuthenticationResponse> {
            override fun onResponse(
                call: Call<AuthenticationResponse>,
                response: Response<AuthenticationResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    authResponse?.let {
                        RetrofitHelper.setAuthorizationToken(authResponse.flinfoToken)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()                    }
                } else {
                    // TODO: Handle error response
                }
            }

            override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                // TODO: Handle network error
            }
        })
    }


}
