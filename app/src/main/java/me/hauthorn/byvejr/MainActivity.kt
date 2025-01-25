package me.hauthorn.byvejr

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {
    private val zipKey = "zip"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()

        // Load zip from shared prefs
        val sharedPref = getSharedPreferences("me.hauthorn.byvejr", Context.MODE_PRIVATE)
        val zip = sharedPref.getString(zipKey, "7500") ?: "7500"

        loadImage(zip)

        val zipEditText = findViewById<EditText>(R.id.zip_edit_text)
        zipEditText.setText(zip)

        val zipButton = findViewById<Button>(R.id.save_zip_button)
        zipButton.setOnClickListener {
            val newZip = zipEditText.text.toString()
            // Store it in shared preferences
            with(sharedPref.edit()) {
                putString(zipKey, newZip)
                apply()
            }
            loadImage(newZip)
            // Issue intent to update the widget
            val intent = Intent(this, WeatherWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(
                ComponentName(this, WeatherWidget::class.java)
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            sendBroadcast(intent)
        }
    }

    private fun loadImage(zip: String) {
        Glide.with(this)
            .load("https://www.klartvejr.dk/kort/$zip")
            .into(findViewById(R.id.weatherImageView))
    }
}