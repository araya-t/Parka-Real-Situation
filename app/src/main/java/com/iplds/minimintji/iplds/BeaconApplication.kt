package com.iplds.minimintji.iplds

import android.app.Application
import com.estimote.cloud_plugin.common.EstimoteCloudCredentials
import com.iplds.minimintji.iplds.manager.Contextor;
import com.estimote.indoorsdk_module.cloud.Location
import android.util.Log


/**
 * START YOUR JOURNEY HERE!
 * Main app class
 */

class BeaconApplication : Application() {

    // This is map for holding all locations from your account.
    // You can move it somewhere else, but for sake of simplicity we put it in here.
    val locationsById: MutableMap<String, Location> = mutableMapOf()

    // !!! ULTRA IMPORTANT !!!
    // Change your credentials below to have access to locations from your account.
    // Make sure you have any locations created in cloud!
    // If you don't have
    // your Estimote Cloud Account - go to https://cloud.estimote.com/ and create one :)
    val cloudCredentials = EstimoteCloudCredentials("create-parking-lot-model-4db", "c5b573016d1c2efc424b5aca8b0951aa")



    override fun onCreate() {
        super.onCreate()

        //Initialize thing(s) here
        Log.d("BeaconApplication", "BeaconApplication in onCreate")
        Contextor.getInstance().init(applicationContext)
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}
