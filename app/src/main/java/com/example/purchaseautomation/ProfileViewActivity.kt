package com.example.purchaseautomation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile_view.*
import java.io.File
import java.io.FileInputStream


class ProfileViewActivity : AppCompatActivity() {

    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var fileInputStream: FileInputStream
    private var profiles = arrayOf<Profile>()
    private var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view)

        profileAdapter = ProfileAdapter(mutableListOf())
        rvProfileList.adapter = profileAdapter
        rvProfileList.layoutManager = LinearLayoutManager(this)

        getProfiles()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.taskoverview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteTask_item -> {
                File(filesDir, "profiles.txt").delete()
                File(filesDir, "tasks.txt").delete()
                println("Deleted profile")
                getProfiles()
                onResume()
            }
            R.id.addTask_item -> startActivity(Intent(this, CreateProfileActivity::class.java))
            R.id.tasks_item -> startActivity(Intent(this, TaskOverviewActivity::class.java))
            R.id.profiles_item -> startActivity(Intent(this, ProfileViewActivity::class.java))
            R.id.proxies_item -> startActivity(Intent(this, ProxyViewActivity::class.java))
            R.id.settings_item -> startActivity(Intent(this, SettingViewActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        getProfiles()
        println("Tried to fill profiles")
    }

    private fun getProfiles() {
        if (File("$filesDir/profiles.txt").exists()) {
            profileAdapter.clearProfiles()
            fileInputStream = openFileInput("profiles.txt")
            val readJson = fileInputStream.bufferedReader().readText()
            profiles = gson.fromJson(readJson, Array<Profile>::class.java)
            profiles.forEach {
                println("Inserted profile: $it")
                profileAdapter.addProfile(it)
            }
        }
    }
}