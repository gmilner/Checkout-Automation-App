package com.example.purchaseautomation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_setting_view.*
import java.io.FileOutputStream

class SettingViewActivity : AppCompatActivity() {

    private lateinit var fileOutputStream: FileOutputStream
    private val gson = GsonBuilder().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_view)

        btnSave.setOnClickListener {
            var webhookUrl = etWebhook.text.toString()
            var delay = 0 //To be added

            if (webhookUrl == "" || !android.util.Patterns.WEB_URL.matcher(webhookUrl).matches()) {
                Toast.makeText(this, "Invalid webhook URL", Toast.LENGTH_SHORT).show()
            }
            else {
                val settings = Setting(webhookUrl, delay)
                val jsonSettings = gson.toJson(settings)
                fileOutputStream = openFileOutput("settings.txt", Context.MODE_PRIVATE)
                fileOutputStream.write(jsonSettings.toByteArray())
                Log.d("Log", "Save settings: $jsonSettings")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.other_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tasks_item -> startActivity(Intent(this, TaskOverviewActivity::class.java))
            R.id.profiles_item -> startActivity(Intent(this, ProfileViewActivity::class.java))
            R.id.proxies_item -> startActivity(Intent(this, ProxyViewActivity::class.java))
            R.id.settings_item -> startActivity(Intent(this, SettingViewActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}