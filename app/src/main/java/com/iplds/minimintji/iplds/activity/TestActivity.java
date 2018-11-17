package com.iplds.minimintji.iplds.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iplds.minimintji.iplds.R;
import com.iplds.minimintji.iplds.dao.CarPositions.CarPositionCollection;
import com.iplds.minimintji.iplds.manager.Contextor;
import com.iplds.minimintji.iplds.manager.HttpManager;
import com.iplds.minimintji.iplds.manager.SessionManager;

import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_328, btn_329, btn_330, btn_337, btn_338, btn_339;
    private boolean is_328_available = true;
    private boolean is_329_available = true;
    private boolean is_330_available = true;
    private boolean is_337_available = true;
    private boolean is_338_available = true;
    private boolean is_339_available = true;
    private int positionId = 0;
    private String userToken,fcmToken = null;
    private Toolbar toolbarTest;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toast toast;
    private SessionManager sessionManager = null;
    private double x_position = 0.0;
    private double y_position = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initInstances();
    }

    private void initInstances() {
        Bundle extras = getIntent().getExtras();
        fcmToken = extras.getString("fcmToken");
        Log.d("fcmToken", "TestActivity || \n fcmToken: " + fcmToken);

        sessionManager = new SessionManager(getApplicationContext());
        userToken = sessionManager.getToken();
        Log.d("userToken","------------ user token: "+userToken);

        btn_328 = findViewById(R.id.btn_328);
        btn_329 = findViewById(R.id.btn_329);
        btn_330 = findViewById(R.id.btn_330);
        btn_337 = findViewById(R.id.btn_337);
        btn_338 = findViewById(R.id.btn_338);
        btn_339 = findViewById(R.id.btn_339);

        if(is_328_available){
            btn_328.setBackgroundColor(Color.GREEN);
            Log.d("TestActivity","btn_328 is available ");
        }else{
            btn_328.setBackgroundColor(Color.RED);
        }

        if(is_329_available){
            btn_329.setBackgroundColor(Color.GREEN);
        }else{
            btn_329.setBackgroundColor(Color.RED);
        }

        if(is_330_available){
            btn_330.setBackgroundColor(Color.GREEN);
        }else{
            btn_330.setBackgroundColor(Color.RED);
        }

        if(is_337_available){
            btn_337.setBackgroundColor(Color.GREEN);
        }else{
            btn_337.setBackgroundColor(Color.RED);
        }

        if(is_338_available){
            btn_338.setBackgroundColor(Color.GREEN);
        }else{
            btn_338.setBackgroundColor(Color.RED);
        }

        if(is_339_available){
            btn_339.setBackgroundColor(Color.GREEN);
        }else{
            btn_339.setBackgroundColor(Color.RED);
        }

        toolbarTest = findViewById(R.id.toolbarTest);
        setSupportActionBar(toolbarTest);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_328.setOnClickListener(this);
        btn_329.setOnClickListener(this);
        btn_330.setOnClickListener(this);
        btn_337.setOnClickListener(this);
        btn_338.setOnClickListener(this);
        btn_339.setOnClickListener(this);

    }

    int time=5000; // in milliseconds = 5 seconds

    @Override
    public void onClick(View v) {
        if(v == btn_328){
            positionId = 328;
            x_position = 17.95;
            y_position = 13.0;

            if(is_328_available == true){
                // drive in
                btn_328.setBackgroundColor(Color.RED);
                changeGmsStatus(positionId,true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 5 seconds
                        sendXYPositionToServer(x_position,y_position);
                    }
                }, time);

            }else{
                // drive out
                btn_328.setBackgroundColor(Color.GREEN);
                changeGmsStatus(positionId,false);
            }
        }

        if(v == btn_329){
            positionId = 329;
            x_position = 21;
            y_position = 12.05;

            if(is_329_available == true){
                // drive in
                btn_329.setBackgroundColor(Color.RED);
                changeGmsStatus(positionId,true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 5 seconds
                        sendXYPositionToServer(x_position,y_position);
                    }
                }, time);

            }else{
                // drive out
                btn_329.setBackgroundColor(Color.GREEN);
                changeGmsStatus(positionId,false);
            }
        }

        if(v == btn_330){
            positionId = 330;
            x_position = 23.75;
            y_position = 14.05;

            if(is_330_available == true){
                // drive in
                btn_330.setBackgroundColor(Color.RED);
                changeGmsStatus(positionId,true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 5 seconds
                        sendXYPositionToServer(x_position,y_position);
                    }
                }, time);

            }else{
                // drive out
                btn_330.setBackgroundColor(Color.GREEN);
                changeGmsStatus(positionId,false);
            }
        }

        if(v == btn_337){
            positionId = 337;
            x_position = 19;
            y_position = 1.5;

            if(is_337_available == true){
                // drive in
                btn_337.setBackgroundColor(Color.RED);
                changeGmsStatus(positionId,true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 5 seconds
                        sendXYPositionToServer(x_position,y_position);
                    }
                }, time);

            }else{
                // drive out
                btn_337.setBackgroundColor(Color.GREEN);
                changeGmsStatus(positionId,false);
            }
        }

        if(v == btn_338){
            positionId = 338;
            x_position = 22;
            y_position = 2.5;

            if(is_338_available == true){
                x_position = 19;
                y_position = 1.5;

                // drive in
                btn_338.setBackgroundColor(Color.RED);
                changeGmsStatus(positionId,true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 5 seconds
                        sendXYPositionToServer(x_position,y_position);
                    }
                }, time);

            }else{
                // drive out
                btn_338.setBackgroundColor(Color.GREEN);
                changeGmsStatus(positionId,false);
            }
        }

        if(v == btn_339){
            positionId = 339;
            x_position = 23;
            y_position = 3.5;

            if(is_339_available == true){
                // drive in
                btn_339.setBackgroundColor(Color.RED);
                changeGmsStatus(positionId,true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 5 seconds
                        sendXYPositionToServer(x_position,y_position);
                    }
                }, time);

            }else{
                // drive out
                btn_339.setBackgroundColor(Color.GREEN);
                changeGmsStatus(positionId,false);
            }
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void changeGmsStatus(final int positionId, boolean isDriveIn){
        if(isDriveIn){
            // change status from available to unavailable
            retrofit2.Call<Void> callGMS = HttpManager.getInstance()
                       .getServiceGMS().changeStatus(positionId);
            Log.d("sendDataTrigger", " drive in @ position id = " + positionId);

            //call GMS server
            callGMS.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("responseSuccess", " send trigger to GMS SUCCESS");

                        toast = Toast.makeText(TestActivity.this,
                                                        "Send trigger to GMS"
                                                        + "\n, change to unavailable \n==> SUCCESS", Toast.LENGTH_SHORT);
                        showSuccessToast();

                    } else {
                        Log.d("responseSuccess", "\ncan connect with server but failed"
                                + "\n 'Failed' response.message() -----> " + response.message());

                        toast = Toast.makeText(TestActivity.this,
                                    response.message(), Toast.LENGTH_SHORT);
                        showFailedToast();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    Log.d("responseSuccess", "Failed Error message is " + t);
                    toast = Toast.makeText(TestActivity.this, "Send trigger to GMS ==> FAILED", Toast.LENGTH_SHORT);
                    showFailedToast();
                }
            });
        }else{
            retrofit2.Call<Void> callGMS = HttpManager.getInstance()
                    .getServiceGMS().changeStatus(positionId);

            callGMS.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("responseSuccess", "send trigger to GMS SUCCESS");

                        String text = "Position '" + positionId + "' is available";
                        toast = Toast.makeText(TestActivity.this, text, Toast.LENGTH_SHORT);
                        showSuccessToast();

                    } else {
                        Log.d("responseSuccess", "\ncan connect with server but failed"
                                + "\n 'Failed' response.message() -----> " + response.message());

                        toast = Toast.makeText(TestActivity.this,
                                response.message(), Toast.LENGTH_SHORT);
                        showFailedToast();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    Log.d("responseSuccess", "Failed Error message is " + t);
                    toast = Toast.makeText(TestActivity.this, "Change status to available ==> FAILED", Toast.LENGTH_SHORT);
                    showFailedToast();
                }
            });
        }
    }

    private void sendXYPositionToServer(double x_position, double y_position){
        long timeStampLong1000 = System.currentTimeMillis()/1000L;
        retrofit2.Call<CarPositionCollection> callParka = HttpManager.getInstance()
                .getServiceParka()
                .sendXYPosition(userToken,
                        x_position,
                        y_position,
                        5018,
                        fcmToken,
                        timeStampLong1000);

        callParka.enqueue(new Callback<CarPositionCollection>() {
            @Override
            public void onResponse(retrofit2.Call<CarPositionCollection> call, Response<CarPositionCollection> response) {

                if(response.isSuccessful()){
                    Log.d("responseSuccess", "Send (x,y) position to server ==> SUCCESS");

                    toast = Toast.makeText(
                            Contextor.getInstance().getContext(),
                            "Send (x,y) position to server ==> SUCCESS",
                            Toast.LENGTH_SHORT);
                    showSuccessToast();
                }else{
                    Log.d("responseSuccess", "\ncan connect with server but failed"
                            + "\n 'Failed' response.message() -----> " + response.message());

                    toast = Toast.makeText(
                            TestActivity.this,
                            response.message(),
                            Toast.LENGTH_SHORT);
                    showFailedToast();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CarPositionCollection> call, Throwable t) {
                Log.d("responseSuccess", "Failed Error message is " + t);

                toast = Toast.makeText(
                        TestActivity.this,
                        "Can not connect Server ==> FAILED \n" + t.toString(),
                        Toast.LENGTH_SHORT);
                showFailedToast();
            }
        });
    }

    private void showSuccessToast() {
        View view = toast.getView();
        int backgroundColor = ContextCompat.getColor(TestActivity.this, R.color.mint_cocktail);
        view.setBackgroundColor(backgroundColor);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        int textColor = ContextCompat.getColor(TestActivity.this, R.color.black);
        text.setTextColor(textColor);
        toast.show();
    }

    private void showFailedToast() {
        View view = toast.getView();
        int backgroundColor = ContextCompat.getColor(TestActivity.this, R.color.red);
        view.setBackgroundColor(backgroundColor);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        int textColor = ContextCompat.getColor(TestActivity.this, R.color.black);
        text.setTextColor(textColor);
        toast.show();
    }
}
