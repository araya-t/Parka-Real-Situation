package com.iplds.minimintji.iplds.activity

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.estimote.indoorsdk.IndoorLocationManagerBuilder
import com.estimote.indoorsdk_module.algorithm.OnPositionUpdateListener
import com.estimote.indoorsdk_module.algorithm.ScanningIndoorLocationManager
import com.estimote.indoorsdk_module.cloud.Location
import com.estimote.indoorsdk_module.cloud.LocationPosition
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.iplds.minimintji.iplds.BeaconApplication
import com.iplds.minimintji.iplds.R
import com.iplds.minimintji.iplds.adapter.ViewPagerAdapter
import com.iplds.minimintji.iplds.dao.CarPositions.CarPositionCollection
import com.iplds.minimintji.iplds.dao.User
import com.iplds.minimintji.iplds.fragment.HomeFragment
import com.iplds.minimintji.iplds.fragment.ShowStatusFragment
import com.iplds.minimintji.iplds.manager.Contextor
import com.iplds.minimintji.iplds.manager.HttpManager
import com.iplds.minimintji.iplds.manager.SessionManager
import com.iplds.minimintji.iplds.utils.CsvRow
import libs.mjn.prettydialog.PrettyDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.ArrayList

// don't forget to implement nav select
class HomeActivityNew : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var toolbarHome: Toolbar
    private val btnLogout: Button? = null
    private val btnHelp: Button? = null
    private lateinit var tvfirstname: TextView
    private lateinit var tvlastname:TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var userToken: String
    private lateinit var sessionManager: SessionManager

    //-----------------------------------------------------------------------------------------------
    private lateinit var notification: Notification
    private lateinit var location: Location
    private lateinit var indoorLocationManager: ScanningIndoorLocationManager
    private lateinit var onPositionUpdateListener: OnPositionUpdateListener

    private var locationPosition_x:Double = 0.0
    private var locationPosition_y:Double = 0.0
    private var countIndex = 0;

    private var sensorManager: SensorManager? = null
    private var accelSensor: Sensor? = null

    private val dcm = DecimalFormat("0.000000")
    private val dcmBeacon = DecimalFormat("0.0000")
    private val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
    private val sdfTimeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSSSS")

    private var startTime: Long = 0
    private var fileName: String? = null
    private var directory: String = ""
    private var isStill: Boolean = false
    private var isStopEngine: Boolean = false
    private var isChangeGmsStatus: Boolean = false
    private var timeStampAcce: Long = 0
    private var milliSecAcce:Long = 0
    private var listenerSampling = -1

    private var isReadFinish: Boolean = false

    private var previousAcceX = 1.0
    private var previousAcceY = 1.0
    private var previousAcceZ = 1.0
    private var csvRowsForCheckStill = emptyList<CsvRow>().toMutableList()
    private var csvRowsForCheckStop = emptyList<CsvRow>().toMutableList()
    private var csvRowRealSituation = emptyList<CsvRow>().toMutableList()
    internal var countIsStill = 0
    internal var countIsStopEngine = 0
    internal var previousCountRowStill = 0
    internal var previousCountRowStop = 0
    internal var isFirstValueStill = true
    internal var isFirstValueStop = true
    internal var isCarStopped = false
    private  var indexStillStartWhenDetectStop:Int = 0
    private  var indexStop:Int = 0

    private lateinit var fcmToken:String
    private var toast: Toast? = null

    companion object {
        val intentKeyLocationId = "location_id"
        val intentFcmToken = "fcmToken"

        fun createIntent(context: Context, locationId: String, fcmToken: String): Intent {
            val intent = Intent(context, HomeActivityNew::class.java)
            intent.putExtra(intentKeyLocationId, locationId)
            intent.putExtra(intentFcmToken, fcmToken)

            Log.i("LocationID","location id: "+ locationId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        notification = Notification.Builder(this)
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentTitle("Estimote Inc. \u00AE")
                .setContentText("Indoor location is running...")
                .setPriority(Notification.PRIORITY_HIGH)
                .build()
        //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        setupLocation()
        initInstances()

        // Give location object to your view to draw it on your screen
//        indoorLocationView.setLocation(location)


        /* Create IndoorManager object.
        Long story short - it takes list of scanned beacons, does the magic and returns estimated position (x,y)
        You need to setup it with your app context,  location data object,
        and your cloud credentials that you declared in BeaconApplication.kt file
        we are using .withScannerInForegroundService(notification)
        this will allow for scanning in background and will ensura that the system won't kill the scanning.
        You can also use .withSimpleScanner() that will be handled without service. */

        Log.d("HomeActivityNew","------ before ------ indoorLocationManager = IndoorLocationManagerBuilder")

        indoorLocationManager = IndoorLocationManagerBuilder(this@HomeActivityNew, location,
                (application as BeaconApplication).cloudCredentials)
                .withScannerInForegroundService(notification).build()

        /** ------------ Hook the listener for position update events -----------------------------------**/

        onPositionUpdateListener = object : OnPositionUpdateListener {
            override fun onPositionOutsideLocation() {
//                indoorLocationView.hidePosition()
            }

            override fun onPositionUpdate(locationPosition: LocationPosition) {
                /** code for updateing view**/
//                println(" ================================================= in onPositionUpdate =====================================")
//                indoorLocationView.updatePosition(locationPosition)
                locationPosition_x = locationPosition.x + 17
                locationPosition_y = locationPosition.y

            }

        }

        indoorLocationManager.setOnPositionUpdateListener(onPositionUpdateListener)

        /** Check if bluetooth is enabled, location permissions are granted, etc. **/
        RequirementsWizardFactory.createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        onRequirementsFulfilled = {
                            indoorLocationManager.startPositioning()
                        },
                        onRequirementsMissing = {
                            Toast.makeText(applicationContext, "Unable to scan for beacons. Requirements missing: ${it.joinToString()}", Toast.LENGTH_SHORT)
                                    .show()
                        },
                        onError = {
                            Toast.makeText(applicationContext, "Unable to scan for beacons. Error: ${it.message}", Toast.LENGTH_SHORT)
                                    .show()
                        })
    }

    private fun initInstances() {
        /** ------ Beacon and Sensor ----------- **/

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        accelSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        registerListener()

        // Init indoor location view here
//        indoorLocationView = findViewById(R.id.indoor_view)

        csvRowRealSituation= ArrayList()
        csvRowsForCheckStill = ArrayList()
        csvRowsForCheckStop = ArrayList()

        locationPosition_x = 100.0
        locationPosition_y = 100.0
        isChangeGmsStatus = false
        isStopEngine = false
        isReadFinish = false
        isStill = false
        fileName = null
        directory = Environment.getExternalStorageDirectory().toString() +
                    "/_Parka/BeaconSensorCsvFile"

//        val intent = intent
//        val extra = intent.extras
        fcmToken = intent.extras.getString("fcmToken")
        Log.i("fcmToken", "HomeActivityNew || \n fcmToken: " + fcmToken)

        /** ---------------------------------------------------------------------- **/

        //----- Toolbar -----
        toolbarHome = findViewById<View>(R.id.toolbarHome) as Toolbar
        setSupportActionBar(toolbarHome)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //----- drawer menu -----
        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        actionBarDrawerToggle = ActionBarDrawerToggle(
                this@HomeActivityNew,
                drawerLayout,
                toolbarHome,
                R.string.open_drawer,
                R.string.cloes_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        val header = navigationView.getHeaderView(0)
        tvfirstname = header.findViewById<View>(R.id.tvfirstname) as TextView
        tvlastname = header.findViewById<View>(R.id.tvlastname) as TextView

        /*
        SharedPreferences prefs = getBaseContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String userToken = prefs.getString("UserToken", null);
        */

        sessionManager = SessionManager(baseContext)
        userToken = sessionManager.token as String

//        userToken = getIntent().getExtras().getString("userToken");
        Log.d("TOken", "User TOken is :" + userToken)

        getUserInfo("" + userToken)

        //----------------------------
        tabLayout = findViewById<View>(R.id.tablayout_id) as TabLayout
        viewPager = findViewById<View>(R.id.viewpager_id) as ViewPager

        val adapter = ViewPagerAdapter(supportFragmentManager)

        // set fcmToken into Bundle ---------
        val bundle = Bundle()
        bundle.putString("fcmToken", fcmToken)

        // set HomeFragment Arguments
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle

        // set ShowStatusFragment Arguments
        val showStatusFragment = ShowStatusFragment()
        showStatusFragment.arguments = bundle

        // add fragments
        adapter.AddFragment(HomeFragment(), "Parking Position")
        adapter.AddFragment(ShowStatusFragment(), "Show Available")

        // Adapter setting
        viewPager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewPager)
        //----------------------------


    }

    fun getFcmToken(): String {
        return fcmToken
    }

    /** ---------------------- Set up Location ----------------------------------------------------- **/

    private fun setupLocation() {
        // get id of location to show from intent
        val locationId = intent.extras.getString(HomeActivityNew.intentKeyLocationId)
        // get object of location. If something went wrong, we build empty location with no data.
        location = (application as BeaconApplication).locationsById[locationId] ?: buildEmptyLocation()
        // Set the Activity title to you location name
        title = location.name
    }

    private fun buildEmptyLocation(): Location {
        return Location("", "", true, "", 0.0, emptyList(), emptyList(), emptyList())
    }

    /** ------------------------ Sensor ------------------------------------------------------------ **/
    private fun registerListener(): Boolean {
        // Register sensor listeners
        Log.d("SensorSampling","-registerListener() sampling = " + listenerSampling)

        var isSuccess = false

        if (listenerSampling == -1) {
            listenerSampling = SensorManager.SENSOR_DELAY_NORMAL

            Log.d("SensorSampling","--------- SensorManager.SENSOR_DELAY_FASTEST = " + listenerSampling)
        } else {
            isSuccess = true
        }
        sensorManager!!.registerListener(accelListener, accelSensor, listenerSampling)

        return isSuccess
    }

    private fun unregisterListener() {
        sensorManager!!.unregisterListener(accelListener)
    }

    private val accelListener = object : SensorEventListener {
        override fun onSensorChanged(eventAcce: SensorEvent) {
            val acc_x = eventAcce.values[0].toDouble()
            val acc_y = eventAcce.values[1].toDouble()
            val acc_z = eventAcce.values[2].toDouble()

            val acc_x_formatted = dcm.format(acc_x)
            val acc_y_formatted = dcm.format(acc_y)
            val acc_z_formatted = dcm.format(acc_z)

            milliSecAcce = System.currentTimeMillis() - startTime
            timeStampAcce = System.currentTimeMillis()
            val timestampLong = timeStampAcce

            val differentValueX = Math.abs(previousAcceX - acc_x)
            val differentValueY = Math.abs(previousAcceY - acc_y)
            val differentValueZ = Math.abs(previousAcceZ - acc_z)

            var temp:CsvRow = CsvRow(
                    countIndex++, milliSecAcce.toString(),
                    sdfTimeStamp.format(timeStampAcce), timestampLong
                    , acc_x_formatted, acc_y_formatted, acc_z_formatted
                    ,null,null
                    , locationPosition_x.toString(),locationPosition_y.toString()
                    ,null)

            csvRowRealSituation.add(temp)
            checkIsCarStillStop(differentValueX, differentValueY, differentValueZ, countIndex, temp)

            previousAcceX = acc_x
            previousAcceY = acc_y
            previousAcceZ = acc_z

        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Log.i("onAccuracyChanged"," -------> accuracy = " + accuracy)
        }
    }

    /** ------------------------ Check car still stop ------------------------------------------------------------ **/
    private fun checkIsCarStillStop(differentValueX: Double, differentValueY: Double, differentValueZ: Double, currentCountRow: Int, csvRow: CsvRow) {
        // currentCountRow value - previous value
        // The main variable that we will use is acce_x and acce_y
        // initial value of previousAcceX, previousAcceY, previousAcceZ = 1

        /** Check whether car is stopped or not */
        if (differentValueX < 0.05 && differentValueY < 0.05) {

            Log.d("differentValue", "differentValueX: $differentValueX || differentValueY: $differentValueY")
            Log.d("CarIsStop", "-----------> Row $$countIndex | car IS STOPPP")
//            Toast.makeText(this, "in car is stopping", Toast.LENGTH_SHORT).show()


            if (isFirstValueStop) {
                countIsStopEngine++
                csvRowsForCheckStop.add(csvRow)
                isFirstValueStop = false
                previousCountRowStop = currentCountRow

                //                Log.d("checkIsCarStill", "------------- First Value -------------");

            } else {

                Log.d("previousCountRow", "currentCountRow - previousCountRowStop = " + (currentCountRow - previousCountRowStop))

                if (currentCountRow - previousCountRowStop == 1) {
                    countIsStopEngine++
                    csvRowsForCheckStop.add(csvRow)
                    previousCountRowStop = currentCountRow

                    Log.d("checkIsCarStillStop", " row(" + currentCountRow
                            + ")------------- (" + differentValueX + ", " + differentValueY + " )countIsStopEngine (" + countIsStopEngine + ") Value -------------")

                } else if (currentCountRow - previousCountRowStop >= 3) {
                    csvRowsForCheckStop.clear()
                    countIsStopEngine = 0
                    previousCountRowStop = 0
                    isFirstValueStop = true
                }else{
                    csvRowsForCheckStop.add(csvRow)
                }

            }

            if (countIsStopEngine == 16 && isCarStopped == false) {

//                if(locationPosition_x == 100.0){
//
//                }

                isCarStopped = true
                Toast.makeText(this, "-- The car is STOP --", Toast.LENGTH_SHORT).show()
                Log.d("checkIsCarStillStop", "--------------- The car is STOP ---------------")

                /***********  implement code to sen x,y to server here  ************/
                Toast.makeText(this, "Car's engine is stopped", Toast.LENGTH_SHORT).show()

                if(csvRowsForCheckStill.size != 0){
                    indexStillStartWhenDetectStop = csvRowsForCheckStill.get(0).countRow - 1;
                }else{
                    Log.d("indexStillStartWhenStop", "--------------- csvRowsForCheckStill.size == 0 ---------------")
                    indexStillStartWhenDetectStop = countIndex - 15
                }

                indexStop = countIndex - 1

                var averageXPosition = calculateAverageXPosition(indexStillStartWhenDetectStop, indexStop)
                var averageYPosition = calculateAverageYPosition(indexStillStartWhenDetectStop, indexStop)

//--------- send data to server

                var csvRowStop:CsvRow = csvRowRealSituation.get(indexStop)
                val timestampLong1000 = csvRowStop.timeStampLong / 1000L
                var floor_id = 5018
//                val x_position = csvRowStop.x_position
//                val y_position = csvRowStop.y_position

                Log.d("sendDataToAppServer", "timestampLong1000 = " + timestampLong1000
                        + "\n, averageXPosition = " + averageXPosition
                        + "\n, averageYPosition = " + averageYPosition + "\n fcmToken = " + fcmToken)

                //set data to send for processing position that user parked
                val callParka = HttpManager.getInstance()
                        .serviceParka
                        .sendXYPosition(
                                userToken, averageXPosition, averageYPosition, floor_id, fcmToken, timestampLong1000)

                //call Parka server
                callParka.enqueue( object: Callback<CarPositionCollection?> {
                    override fun onResponse(call: Call<CarPositionCollection?>?, response: Response<CarPositionCollection?>?) {
                        if (response!!.isSuccessful) {
                            Log.d("responseSuccess", "Send data to 'Parka' ==> SUCCESS")

                            val dao: CarPositionCollection? = response.body()

                            Log.d("responseSuccess", "dao message = "+ dao!!.message)
                            Log.d("responseSuccess", "dao carPositions = " + dao!!.carPositions)
                            Log.d("responseSuccess", "response message = " + response.message())

                            toast = Toast.makeText(
                                    Contextor.getInstance().context,
                                    "Send data to 'Parka' ==> SUCCESS",
                                    Toast.LENGTH_SHORT)
                            showSuccessToast()

                        } else {
                            Log.d("responseSuccess", "\ncan connect with server but failed"
                                    + "\n 'Failed' response.message() -----> " + response.message())

                            toast = Toast.makeText(
                                    this@HomeActivityNew,
                                    response!!.message(),
                                    Toast.LENGTH_SHORT)
                            showFailedToast()
                        }
                    }

                    override fun onFailure(call: Call<CarPositionCollection?>?, t: Throwable?) {
                        Log.d("responseSuccess", "Failed Error message is " + t)

                        toast = Toast.makeText(
                                this@HomeActivityNew,
                                "Can not contact Server ==> FAILED \n" + t.toString(),
                                Toast.LENGTH_SHORT)
                        showFailedToast()
                    }
                })

            }


            /** Check whether car is still or not */
        } else if (((differentValueX <= 0.16) && differentValueY <= 0.16) || differentValueY <= 0.16) {

            Log.d("differentValue", "differentValueX: $differentValueX || differentValueY: $differentValueY")
            Log.d("CarIsStill", "-----------> Row $countIndex | car IS STILL")
//            Toast.makeText(this, "in car is still", Toast.LENGTH_SHORT).show()


            if (isFirstValueStill) {
                countIsStill++
                csvRowsForCheckStill.add(csvRow)
                isFirstValueStill = false
                previousCountRowStill = currentCountRow
            } else {

                Log.d("previousCountRow", "currentCountRow - previousCountRow = " + (currentCountRow - previousCountRowStill))

                if (currentCountRow - previousCountRowStill <= 1) {
                    countIsStill++
                    csvRowsForCheckStill.add(csvRow)
                    previousCountRowStill = currentCountRow

                    //                    Log.d("checkIsCarStillStop", " row(" + currentCountRow + ")------------- countIsStill (" + countIsStill + ") Value -------------");
                    Log.d("checkIsCarStillStop", " row(" + currentCountRow
                            + ")------------- (" + differentValueX + ", " + differentValueY + " )countIsStill (" + countIsStill + ") Value -------------")
                } else if (currentCountRow - previousCountRowStill > 5)  {
                    Log.d("checkIsCarStillStop", "--------------- the || Clear csvRowsForCheckStill")
                    csvRowsForCheckStill.clear()
                    countIsStill = 0
                    previousCountRowStill = 0
                    isFirstValueStill = true
                }else{
                    csvRowsForCheckStill.add(csvRow)
                }

            }

            if (countIsStill == 16 && isCarStopped == false) {
                Toast.makeText(this, "-- The car is STILL --", Toast.LENGTH_SHORT).show()
                Log.d("checkIsCarStillStop", "--------------- The car is STILL ---------------")
            }
        }
    }

    private fun calculateAverageXPosition(indexStillStartWhenDetectStop : Int,indexStop: Int) : Double {
        var i = indexStillStartWhenDetectStop
        var countAmount = 0;
        var sum = 0.0
        var averageX = -100.0

        while ( i <= indexStop) {
            sum += csvRowRealSituation.get(i).x_position
            Log.d("calculateAverage","Row " + i
                                                + "| countAmount="+ (countAmount+1)
                                                + " |x_position = " + csvRowRealSituation.get(i).x_position
                                                + " | sum = " + sum)
            i++
            countAmount++
        }

        if(sum != 0.0){
            averageX = sum/countAmount
        }

        return averageX
    }

    private fun calculateAverageYPosition(indexStillStartWhenDetectStop : Int,indexStop: Int) : Double{
        var i = indexStillStartWhenDetectStop
        var countAmount = 0;
        var sum = 0.0
        var averageY = -100.0

        while ( i <= indexStop) {
            Log.d("calculateAverage","Row " + i
                                            + "| countAmount="+ (countAmount + 1)
                                            + " |y_position = " + csvRowRealSituation.get(i).y_position
                                            + " | sum = " + sum)
            sum += csvRowRealSituation.get(i).y_position
            i++
            countAmount++
        }

        if(sum != 0.0){
            averageY = sum/countAmount
        }

        return averageY
    }

    private fun showSuccessToast() {
        val view = toast!!.getView()
        val backgroundColor = ContextCompat.getColor(this@HomeActivityNew, R.color.mint_cocktail)
        view.setBackgroundColor(backgroundColor)
        val text = view.findViewById<TextView>(android.R.id.message)
        val textColor = ContextCompat.getColor(this@HomeActivityNew, R.color.black)
        text.setTextColor(textColor)
        toast!!.show()
    }

    private fun showFailedToast() {
        val view = toast!!.getView()
        val backgroundColor = ContextCompat.getColor(this@HomeActivityNew, R.color.red)
        view.setBackgroundColor(backgroundColor)
        val text = view.findViewById<TextView>(android.R.id.message)
        val textColor = ContextCompat.getColor(this@HomeActivityNew, R.color.black)
        text.setTextColor(textColor)
        toast!!.show()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_testing -> {
                val intent = Intent(this@HomeActivityNew, TestActivity::class.java)
                intent.putExtra("fcmToken",fcmToken)
                startActivity(intent)
            }

            R.id.nav_help ->{
                val intent = Intent(this@HomeActivityNew, HelpActivity::class.java)
                intent.putExtra("fcmToken",fcmToken)
                startActivity(intent)
            }


            R.id.nav_logout ->
                /*
                new SessionManager(HomeActivity.this).removeUser();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                */
                CreateDialog()
        }

        return true
    }

    private fun getUserInfo(userToken: String) {
        val call = HttpManager.getInstance()
                .serviceParka
                .getUserInfo(userToken)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val userInfo = response.body()
                if (response.isSuccessful && userInfo != null) {
                    Log.d("UserInfo", "------------ UserInfo" + userInfo!!)
                    // ----- waiting for fragment -----
                    //tvName.setText(userInfo.getName());
                    //tvSurname.setText(userInfo.getSurname());

                    tvfirstname.setText(userInfo.name)
                    tvlastname.setText(userInfo.surname)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@HomeActivityNew, "Error: " + t.message.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }

    fun CreateDialog() {
        val pDialog = PrettyDialog(this)
        pDialog.setTitle("Do you want to sign out?")
                //.setMessage("555555")
                .addButton(
                        "Yes", // button text
                        R.color.pdlg_color_white, // button text color
                        R.color.colorAccent // button background color
                ) {
                    // Do what you gotta do
                    //                                final SharedPreferences prefs = getBaseContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                    //------------------
                    //                                new SessionManager(HomeActivity.this).removeUser();
                    //                                Toast.makeText(HomeActivity.this, "Token : "+ userToken,Toast.LENGTH_SHORT).show();

                    //------------------
                    //                                sessionManager = new SessionManager(HomeActivity.this);
                    //                                sessionManager.removeUser();
                    val prefs = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                    val edit = prefs.edit()
                    edit.remove("userToken")
                    edit.apply()

                    val intent = Intent(this@HomeActivityNew, MainActivity::class.java)
                    intent.putExtra("fcmToken",fcmToken)
                    startActivity(intent)
                    finish()
                }

                .addButton(
                        "Cancel",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red
                ) { pDialog.dismiss() }

                .setIcon(R.drawable.exclamation_mark_512)

                .show()
    }

    override fun onResume() {
        super.onResume()
        registerListener()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterListener()
    }

}
