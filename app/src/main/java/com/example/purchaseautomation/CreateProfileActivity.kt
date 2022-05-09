package com.example.purchaseautomation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_profile.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var fileInputStream: FileInputStream
    private lateinit var fileOutputStream: FileOutputStream
    private var profiles = arrayOf<Profile>()
    private var convertedProfiles = mutableListOf<Profile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        var gson = Gson()
        profileAdapter = ProfileAdapter(mutableListOf())

        btnAddProfile.setOnClickListener {
            File("$filesDir/profiles.txt").createNewFile()
            fileInputStream = openFileInput("profiles.txt")
            try {
                val readJson = fileInputStream.bufferedReader().readText()
                profiles = gson.fromJson(readJson, Array<Profile>::class.java)
            }
            catch (e: Exception) {
                println("Exception: $e")
            }
            var id = 0
            if (!profiles.isNullOrEmpty()) {
                id = (profiles[profiles.size - 1].ID + 1)
            }
            var profileName = etProfileName.text.toString()
            var firstName = etFirstName.text.toString()
            var lastName = etLastName.text.toString()
            var address1 = etAddress1.text.toString()
            var address2 = etAddress2.text.toString()
            var city = etCity.text.toString()
            var county = etCounty.text.toString()
            var postcode = etPostcode.text.toString()
            var country = etCountry.text.toString()
            var phone = etPhone.text.toString()
            var cardNum = etCardNum.text.toString()
            var cardMonth = etCardMonth.text.toString()
            var cardYear = etCardYear.text.toString()
            var cardCVV = etCardCVV.text.toString()
            if (profileName == "" || firstName == "" || lastName == "" || address1 == "" || city == ""
                || postcode == "" || country == "" || phone == "") {
                Toast.makeText(this, "Please fill all necessary fields", Toast.LENGTH_SHORT).show()
            }
            else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
                Toast.makeText(this, "Please fill a valid phone number", Toast.LENGTH_SHORT).show()
            }
            else {
                var profile = Profile(id, profileName, firstName, lastName, address1, address2,
                    city, county, postcode, country, phone, cardNum, cardMonth, cardYear, cardCVV)

                convertedProfiles = profiles.toMutableList()
                convertedProfiles.add(profile)
                val newJson = gson.toJson(convertedProfiles)
                fileOutputStream = openFileOutput("profiles.txt", Context.MODE_PRIVATE)
                fileOutputStream.write(newJson.toByteArray())
                Log.d("Log", "Create profile: $newJson")
                finish()
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