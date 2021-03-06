package com.iplds.minimintji.iplds.etc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.widget.Toast

import com.iplds.minimintji.iplds.BeaconApplication
import com.iplds.minimintji.iplds.R
import com.estimote.indoorsdk_module.cloud.CloudCallback
import com.estimote.indoorsdk_module.cloud.EstimoteCloudException
import com.estimote.indoorsdk_module.cloud.IndoorCloudManagerFactory
import com.estimote.indoorsdk_module.cloud.Location

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.iplds.minimintji.iplds.activity.CheckIsDriveOutActivity
import com.iplds.minimintji.iplds.activity.MainActivity
import com.iplds.minimintji.iplds.manager.SessionManager


/**
 * Simple splash screen to load the data from cloud.
 * Make sure to initialize EstimoteSDK with your APP ID and APP TOKEN in {@link BeaconApplication} class.
 * You can get those credentials from your Estimote Cloud account :)
 */

class FetchCloudDataActivity : AppCompatActivity() {
    private lateinit var fcmToken:String
    private val SPLASH_TIME_OUT = 3000 // 3s

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make actionbar invisible.
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        supportActionBar?.hide()
        setContentView(R.layout.activity_welcome)

        val sessionManager = SessionManager(this@FetchCloudDataActivity)

        /** ------------ Init token from FCM -------------------------------------------------------------- **/
        initFcmToken()
        /** ----------------------------------------------------------------------------------------------- **/

        // Create object for communicating with Estimote cloud.
        // IMPORTANT - you need to put here your Estimote Cloud credentials.
        // We daclared them in BeaconApplication.kt class
        val cloudManager = IndoorCloudManagerFactory().create(applicationContext, (application as BeaconApplication).cloudCredentials)

        // Launch request for all locations connected to your account.
        // If you don't see any - check your cloud account - maybe you should create those locations first?
        cloudManager.getAllLocations(object : CloudCallback<List<Location>> {
            override fun success(locations: List<Location>) {
                // Take location objects and map them to their identifiers
                val locationIds = locations.associateBy { it.identifier }

                // save mapped locations to global pseudo "storage". You can do this in many various way :)
                (application as BeaconApplication).locationsById.putAll(locationIds)

                // If all is fine, go ahead and launch activity with list of your locations :)
                Log.d("FetchCloudDataActivity"," ======== in FetchCloudDataActivity")
            }

            override fun failure(serverException: EstimoteCloudException) {
                // For the sake of this demo, you need to make sure you have an internet connection and AppID/AppToken set :)
                Toast.makeText(this@FetchCloudDataActivity, "Unable to fetch location data from cloud. " +
                        "Check your internet connection and make sure you initialised our SDK with your AppId/AppToken", Toast.LENGTH_LONG).show()
            }
        })

        //check which activity should start
        Handler().postDelayed({
            val prefs = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
            val token = prefs.getString("userToken", null)

            val extras = intent.extras
            var test: String? = null
            if (extras != null) {
                test = intent.extras!!.getString("test")
            }

            if (token != null) {
                if (test != null) {
                    val anotherIntent = Intent(this@FetchCloudDataActivity, CheckIsDriveOutActivity::class.java)
                    addFcmTokenTpIntent(anotherIntent)
                    startActivity(anotherIntent)
                    finish()
                } else {
                    val welcomeIntent1 = Intent(this@FetchCloudDataActivity, HomeActivity::class.java)
                    welcomeIntent1.putExtra("userToken", sessionManager.token)
                    addFcmTokenTpIntent(welcomeIntent1)
                    startActivity(welcomeIntent1)
                    finish()
                }
            } else {
                val welcomeIntent2 = Intent(this@FetchCloudDataActivity, MainActivity::class.java)
                addFcmTokenTpIntent(welcomeIntent2)
                startActivity(welcomeIntent2)
                finish()
            }
        }, SPLASH_TIME_OUT.toLong())
    }

    private fun initFcmToken(){
        //Init token from FCM
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("TAG token failed", "getInstanceId failed", task.exception)
                        Toast.makeText(this@FetchCloudDataActivity, "getInstanceId failed: " + task.exception!!, Toast.LENGTH_LONG).show()
                        return@OnCompleteListener
                    }

                    //Get new instance ID token
                    fcmToken = task.result!!.token
                    Toast.makeText(this@FetchCloudDataActivity, "getInstanceId Token: $fcmToken", Toast.LENGTH_LONG).show()
                    Log.d("TagToken", "token: $fcmToken")

                })
    }

    private fun addFcmTokenTpIntent(intent:Intent){
        var locationId = "six-slots-only--floor-10b"
        intent.putExtra("fcmToken",fcmToken)
        intent.putExtra("locationId", locationId)
    }

//    private fun startHomeActivity(){
////        startActivity(Intent(this, LocationListActivity::class.java))
//        var locationId = "six-slots-only--floor-10b"
//
//        val intent = Intent(this, HomeActivity::class.java)
//        val extra = Bundle()
//        extra.putString("fcmToken",fcmToken)
//        extra.putString("locationId", locationId)
//        intent.putExtras(extra)
//
//        startActivity(intent)
//    }



}