package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        file_name.text = when (MainActivity.URL) {
            "https://github.com/bumptech/glide" -> resources.getString(R.string.glide_text)
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
            -> resources.getString(R.string.udacity_text)
            else -> resources.getString(R.string.retrofit_text)
        }

        if (MainActivity.isFailed) {
            status.text = getString(R.string.failed)
            status.setTextColor(Color.RED)
        } else {
            status.text = getString(R.string.succeeded)
        }

        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }

}
