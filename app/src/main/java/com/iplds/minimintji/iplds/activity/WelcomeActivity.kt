package com.iplds.minimintji.iplds.activity

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
import com.iplds.minimintji.iplds.manager.SessionManager


/**
 * Simple splash screen to load the data from cloud.
 * Make sure to initialize EstimoteSDK with your APP ID and APP TOKEN in {@link BeaconApplication} class.
 * You can get those credentials from your Estimote Cloud account :)
 */

class WelcomeActivity : AppCompatActivity() {
    private lateinit var fcmToken:String
    private val SPLASH_TIME_OUT = 3000 // 3s

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make actionbar invisible.
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        supportActionBar?.hide()
        setContentView(R.layout.activity_welcome)

        Log.d("WelcomeActivity","-------------- in WelcomeActivity --------- ")

        val sessionManager = SessionManager(this@WelcomeActivity)

        /** ------------ Init token from FCM -------------------------------------------------------------- **/
        initFcmToken()
        /** ----------------------------------------------------------------------------------------------- **/

        // Create object for communicating with Estimote cloud.
        // IMPORTANT - you need to put here your Estimote Cloud credentials.
        // We daclared them in BeaconApplication.kt class
        val cloudManager = IndoorCloudManagerFactory().create(applicationContext, (application as BeaconApplication).cloudCredentials)
        Log.d("WelcomeActivity", "after cloudManager = " + cloudManager)



        // Launch request for all locations connected to your account.
        // If you don't see any - check your cloud account - maybe you should create those locations first?

        Log.d("WelcomeActivity", "before cloudManager.getAllLocations")

        cloudManager.getAllLocations(object : CloudCallback<List<Location>> {
            override fun success(locations: List<Location>) {
                // Take location objects and map them to their identifiers
                val locationIds = locations.associateBy { it.identifier }
                Log.d("WelcomeActivity", "locationIds = " + locationIds)

                // save mapped locations to global pseudo "storage". You can do this in many various way :)
                (application as BeaconApplication).locationsById.putAll(locationIds)

                Toast.makeText(this@WelcomeActivity, "Ready to use Beacon", Toast.LENGTH_LONG).show()
                // If all is fine, go ahead and launch activity with list of your locations :)
                Log.d("WelcomeActivity"," ======== cloudManager.getAllLocations SUCCESS")
            }

            override fun failure(serverException: EstimoteCloudException) {
                // For the sake of this demo, you need to make sure you have an internet connection and AppID/AppToken set :)
                Toast.makeText(this@WelcomeActivity, "Unable to fetch location data from cloud. " +
                        "Check your internet connection and make sure you initialised our SDK with your AppId/AppToken", Toast.LENGTH_LONG).show()
            }
        })

        val locationId = "six-slots-only--floor-10b"

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
                    if (test.equals("test")) {
                        /** drive out **/
                        val anotherIntent = Intent(this@WelcomeActivity, CheckIsDriveOutActivity::class.java)
                        anotherIntent.putExtra("fcmToken",fcmToken)

                        startActivity(anotherIntent)
                        finish()
                    }else{
                        /** drive in **/
                        val welcomeIntent1 = Intent(this@WelcomeActivity, HomeActivityNew::class.java)
                        welcomeIntent1.putExtra("userToken", sessionManager.token)
                        welcomeIntent1.putExtra("fcmToken",fcmToken)

                        startActivity(HomeActivityNew.Companion.createIntent(this, locationId, fcmToken))
                        finish()
                    }
                } else {
                    /** go to HomeActivityNew **/
                    val welcomeIntent1 = Intent(this@WelcomeActivity, HomeActivityNew::class.java)
                    welcomeIntent1.putExtra("userToken", sessionManager.token)
                    welcomeIntent1.putExtra("fcmToken",fcmToken)

                    startActivity(HomeActivityNew.Companion.createIntent(this, locationId, fcmToken))
                    finish()
                }
            } else {
                /** user token == null
                    user must login **/
                val welcomeIntent2 = Intent(this@WelcomeActivity, MainActivity::class.java)
                welcomeIntent2.putExtra("fcmToken",fcmToken)

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
                        Toast.makeText(this@WelcomeActivity, "getInstanceId failed: " + task.exception!!, Toast.LENGTH_LONG).show()
                        return@OnCompleteListener
                    }

                    //Get new instance ID token
                    fcmToken = task.result!!.token
//                    Toast.makeText(this@WelcomeActivity, "getInstanceId Token: $fcmToken", Toast.LENGTH_LONG).show()
                    Log.i("fcmToken", "WelcomeActivity || \n fcmToken: " + fcmToken)

                })

    }

}