package com.example.quizz

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

private lateinit var m_buttonSubmit: Button
private lateinit var sharedPreferences: SharedPreferences


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        editTextUsername.setText(username)

        m_buttonSubmit = findViewById(R.id.buttonSubmit)

        m_buttonSubmit.setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java).apply {
                putExtra("username", editTextUsername.text.toString())
            }
            startActivity(intent)

            val editor = sharedPreferences.edit()
            editor.putString("username", username)
            editor.apply()
        }
    }
}