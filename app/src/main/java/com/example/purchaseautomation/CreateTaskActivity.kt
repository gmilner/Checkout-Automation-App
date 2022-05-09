package com.example.purchaseautomation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.android.synthetic.main.activity_create_task.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var fileInputStream: FileInputStream
    private lateinit var fileOutputStream: FileOutputStream
    private var profiles = arrayOf<Profile>()
    private var profileNameList = mutableListOf<String>()
    private var profileNameArray = arrayOf<String>()
    private var tasks = arrayOf<Task>()
    private var convertedTasks = mutableListOf<Task>()
    private var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        taskAdapter = TaskAdapter(mutableListOf())

        btnAddTask.setOnClickListener {
            val profileSpinner: Spinner = findViewById(R.id.spTProfileName)
            File("$filesDir/tasks.txt").createNewFile()
            fileInputStream = openFileInput("tasks.txt")
            try {
                val readJson = fileInputStream.bufferedReader().readText()
                tasks = gson.fromJson(readJson, Array<Task>::class.java)
            }
            catch (e: Exception) {
                println("Exception: $e")
            }

            var id = 0
            if (!tasks.isNullOrEmpty()) {
                id = (tasks[tasks.size - 1].ID + 1)
            }
            var taskName = etTaskName.text.toString()
            var variant = etVariant.text.toString()
            var email = etEmail.text.toString()
            var profileID = profileSpinner.selectedItemPosition
            var proxyListID = 0
            if (taskName == "" || variant == "" || email == "" || profileID.toString() == "") { // Check that all necessary fields have content
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // Check that the email matches the pre-defined Android standard
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            }
            else if (id == 5) {
                Toast.makeText(this, "Task limit of 5 reached", Toast.LENGTH_SHORT).show()
            }
            else {
                var task = Task(id, taskName, variant, email, profileID, proxyListID, "Stopped")

                convertedTasks = tasks.toMutableList()
                convertedTasks.add(task)
                val newJson = gson.toJson(convertedTasks)
                fileOutputStream = openFileOutput("tasks.txt", Context.MODE_PRIVATE)
                fileOutputStream.write(newJson.toByteArray())
                println("Added task: $newJson")
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadSpinner()
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

    fun loadSpinner() : Spinner {
        profileNameList.clear()
        // Fills profiles object from file
        if (File("$filesDir/profiles.txt").exists()) {
            fileInputStream = openFileInput("profiles.txt")
            val readJson = fileInputStream.bufferedReader().readText()
            profiles = gson.fromJson(readJson, Array<Profile>::class.java)
        }

        for (profile in profiles) {
            profileNameList.add(profile.profileName)
        }
        profileNameArray = profileNameList.toTypedArray()

        var profileSpinner: Spinner = findViewById(R.id.spTProfileName)
        var arrAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, profileNameArray)
        arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        profileSpinner.adapter = arrAdapter
        return profileSpinner
    }


}