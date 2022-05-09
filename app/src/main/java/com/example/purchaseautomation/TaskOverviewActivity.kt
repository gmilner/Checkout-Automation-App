package com.example.purchaseautomation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class TaskOverviewActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var profiles: Array<Profile>
    private lateinit var tasks: Array<Task>
    private lateinit var settings: Setting
    private lateinit var job: Job
    private val gson = GsonBuilder().create()
    private val gsonNull = GsonBuilder().serializeNulls().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskAdapter = TaskAdapter(mutableListOf())
        rvTaskList.adapter = taskAdapter
        rvTaskList.layoutManager = LinearLayoutManager(this)

        checkFiles()

        btnStartTask.setOnClickListener {
            makeRequest()
        }
    }

    override fun onResume() {
        super.onResume()
        checkFiles()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.taskoverview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteTask_item -> {
                File(filesDir, "tasks.txt").delete()
                Log.d("Debug", "Deleted tasks")
                checkFiles()
                onResume()
            }
            R.id.addTask_item -> startActivity(Intent(this, CreateTaskActivity::class.java))
            R.id.tasks_item -> startActivity(Intent(this, TaskOverviewActivity::class.java))
            R.id.profiles_item -> startActivity(Intent(this, ProfileViewActivity::class.java))
            R.id.proxies_item -> startActivity(Intent(this, ProxyViewActivity::class.java))
            R.id.settings_item -> startActivity(Intent(this, SettingViewActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkFiles() : String {
        if (File("$filesDir/profiles.txt").exists()) {
            val fileInputStream = openFileInput("profiles.txt")
            val readJson = fileInputStream.bufferedReader().readText()
            profiles = gson.fromJson(readJson, Array<Profile>::class.java)
            println("Got profiles.txt")
        }
        else {
            return "profiles"
        }

        if (File("$filesDir/tasks.txt").exists()) {
            taskAdapter.clearTasks()
            val fileInputStream = openFileInput("tasks.txt")
            val readJson = fileInputStream.bufferedReader().readText()
            tasks = gson.fromJson(readJson, Array<Task>::class.java)
            tasks.forEach {
                taskAdapter.addTask(it)
                Log.d("InsertedTask","$it")
            }
        }
        else {
            return "tasks"
        }

        if (File("$filesDir/settings.txt").exists()) {
            val fileInputStream = openFileInput("settings.txt")
            val readJson = fileInputStream.bufferedReader().readText()
            settings = gson.fromJson(readJson, Setting::class.java)
        }
        else {
            return "settings"
        }

        return "safe"
    }

    private fun makeRequest() {
        when (checkFiles()) {
            "safe" -> {
                for (loadedTask in tasks) {
                    val loadedProfile = profiles[loadedTask.profileID]
                    val variant = loadedTask.variant
                    val webhookUrl = settings.webhookURL
                    val scope = CoroutineScope(Dispatchers.IO + CoroutineName("requestScope"))
                    job = scope.launch {
                        val userAgent =
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
                        var currentUrl = "https://smets.lu/cart/add.js?quantity=1&id=$variant"
                        val addressPayload = listOf(
                            "utf8" to "\u2713",
                            "_method" to "patch",
                            "authenticity_token" to "",
                            "previous_step" to "contact_information",
                            "step" to "shipping_method",
                            "checkout[email]" to loadedTask.email,
                            "checkout[buyer_accepts_marketing]" to "0",
                            "checkout[pick_up_in_store][selected]" to "false",
                            "checkout[id]" to "delivery-shipping",
                            "checkout[shipping_address][country]" to loadedProfile.country,
                            "checkout[shipping_address][first_name]" to loadedProfile.firstName,
                            "checkout[shipping_address][last_name]" to loadedProfile.lastName,
                            "checkout[shipping_address][address1]" to loadedProfile.address1,
                            "checkout[shipping_address][address2]" to loadedProfile.address2,
                            "checkout[shipping_address][city]" to loadedProfile.city,
                            "checkout[shipping_address][zip]" to loadedProfile.postcode,
                            "checkout[shipping_address][phone]" to loadedProfile.phone,
                            "checkout[remember_me]" to "0",
                            "checkout[client_details][browser_width]" to "1903",
                            "checkout[client_details][browser_height]" to "1009",
                            "checkout[client_details][javascript_enabled]" to "1",
                            "checkout[client_details][color_depth]" to "24",
                            "checkout[client_details][java_enabled]" to "false",
                            "checkout[client_details][browser_tz]" to "0"
                        )
                        val ratesPayload = listOf(
                            "utf8" to "\u2713",
                            "_method" to "patch",
                            "authenticity_token" to "",
                            "previous_step" to "shipping_method",
                            "step" to "payment_method",
                            "checkout[shipping_rate][id]" to "shopify-EXPRESS%20WORLDWIDE%20EU-20.00",
                            "checkout[client_details][browser_width]" to "1903",
                            "checkout[client_details][browser_height]" to "1009",
                            "checkout[client_details][javascript_enabled]" to "1",
                            "checkout[client_details][color_depth]" to "24",
                            "checkout[client_details][java_enabled]" to "false",
                            "checkout[client_details][browser_tz]" to "0"
                        )
                        val paymentPayload = listOf(
                            "utf8" to "\u2713",
                            "_method" to "patch",
                            "authenticity_token" to "",
                            "previous_step" to "payment_method",
                            "step" to "",
                            "s" to "",
                            "checkout[payment_gateway]" to "26884931638",
                            "checkout[different_billing_address]" to "false",
                            "checkout[total_price]" to "13966",
                            "complete" to "1",
                            "checkout[client_details][browser_width]" to "1903",
                            "checkout[client_details][browser_height]" to "1009",
                            "checkout[client_details][javascript_enabled]" to "1",
                            "checkout[client_details][color_depth]" to "24",
                            "checkout[client_details][java_enabled]" to "false",
                            "checkout[client_details][browser_tz]" to "0"
                        )
                        Fuel.get(currentUrl).responseString { request, response, result ->
                            Log.d("TaskStatus", "Carted")
                            var cookies = response.headers["Set-Cookie"]
                            currentUrl = response.url.toString()
                            Log.d("URL", currentUrl)
                            taskAdapter.updateStatus("Carted", loadedTask.ID)
                            Fuel.get("https://smets.lu/checkout.json")
                                .header(Headers.COOKIE to cookies)
                                .responseString { request, response, result ->
                                    currentUrl = response.url.toString()
                                    Log.d("TaskStatus", "Setting shipping info")
                                    taskAdapter.updateStatus("Setting Shipping Info", loadedTask.ID)
                                    cookies = response.headers["Set-Cookie"]
                                    Log.d("URL", currentUrl)
                                    Fuel.upload(currentUrl, Method.POST, addressPayload)
                                        .header(Headers.COOKIE to cookies)
                                        .responseString { request, response, result ->
                                            Log.d("TaskStatus", "Getting shipping prices")
                                            taskAdapter.updateStatus("Getting Shipping Prices", loadedTask.ID)
                                            cookies = response.headers["Set-Cookie"]
                                            currentUrl = response.url.toString()
                                            if (!currentUrl.contains("&step=shipping_method")) {
                                                taskAdapter.updateStatus("Error with profile", loadedTask.ID)
                                                Log.d("TaskStatus", "Profile error")
                                            }
                                            else if (currentUrl.contains("stock_problems")) {
                                                taskAdapter.updateStatus("Out of stock", loadedTask.ID)
                                                Log.d("TaskStatus", "Out of stock")
                                            }
                                            else {
                                                Log.d("URL", currentUrl)
                                                Fuel.upload(currentUrl, Method.POST, ratesPayload)
                                                    .header(
                                                        Headers.COOKIE to cookies,
                                                        Headers.USER_AGENT to userAgent
                                                    )
                                                    .responseString { request, response, result ->
                                                        taskAdapter.updateStatus("Getting payment page", loadedTask.ID)
                                                        Log.d("TaskStatus", "Getting payment page")
                                                        cookies = response.headers["Set-Cookie"]
                                                        currentUrl = response.url.toString()
                                                        Log.d("URL", currentUrl)
                                                        currentUrl =
                                                            currentUrl.plus("?step=payment_method")
                                                        Fuel.upload(currentUrl, Method.GET).header(
                                                            Headers.COOKIE to cookies,
                                                            Headers.USER_AGENT to userAgent
                                                        ).responseString { request, response, result ->
                                                            cookies = response.headers["Set-Cookie"]
                                                            currentUrl = response.url.toString()
                                                            Log.d("URL", currentUrl)
                                                            Fuel.upload(
                                                                currentUrl,
                                                                Method.POST,
                                                                paymentPayload
                                                            )
                                                                .header(Headers.COOKIE to cookies)
                                                                .responseString { request, response, result ->
                                                                    currentUrl = response.url.toString()
                                                                    Log.d("URL", currentUrl)
                                                                    val thirdLayer = fields(
                                                                        "PayPal Link",
                                                                        "[LINK]($currentUrl)"
                                                                    )
                                                                    val secondLayer = embeds(
                                                                        null,
                                                                        arrayOf(thirdLayer)
                                                                    )
                                                                    val embed = Embed(
                                                                        null, arrayOf(secondLayer),
                                                                        null
                                                                    )
                                                                    val embedJson =
                                                                        gsonNull.toJson(embed)
                                                                    Fuel.upload(webhookUrl, Method.POST)
                                                                        .jsonBody(embedJson)
                                                                        .header(Headers.ACCEPT to "application/json")
                                                                        .responseString { request, response, result ->
                                                                            taskAdapter.updateStatus("Finished - check Discord", loadedTask.ID)
                                                                            Log.d(
                                                                                "TaskStatus",
                                                                                "PayPal link sent to Discord"
                                                                            )
                                                                            Log.d("json", embedJson)
                                                                        }
                                                                }
                                                        }
                                                    }
                                            }

                                        }
                                }
                        }
                    }
                }
            }
            "profiles" -> {
                Toast.makeText(this, "No profiles created", Toast.LENGTH_SHORT).show()
                Log.d("Error", "No profiles created")
            }
            "tasks" -> {
                Toast.makeText(this, "No tasks created", Toast.LENGTH_SHORT).show()
                Log.d("Error", "No tasks created")
            }
            "settings" -> {
                Toast.makeText(this, "No settings created", Toast.LENGTH_SHORT).show()
                Log.d("Error", "No settings created")
            }
        }
    }
}