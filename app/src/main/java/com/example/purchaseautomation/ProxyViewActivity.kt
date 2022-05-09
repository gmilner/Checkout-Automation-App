package com.example.purchaseautomation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class ProxyViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proxy_view)
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