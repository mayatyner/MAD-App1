package com.example.app1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_success)

        //Get the text views
        val congrats: TextView = findViewById<TextView>(R.id.loginMessage)

        //Get the starter intent
        val receivedIntent = intent

        //Set the text views
        var fName: String? = receivedIntent.getStringExtra("FN_DATA")
        var lName: String? = receivedIntent.getStringExtra("LN_DATA")

        congrats.text = fName + " " + lName + " is logged in!"
    }

}