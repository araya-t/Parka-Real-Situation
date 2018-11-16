package com.iplds.minimintji.iplds.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;

import com.iplds.minimintji.iplds.R;
import com.iplds.minimintji.iplds.manager.SessionManager;

public class WelcomeActivity2 extends Activity {
    private static int SPLASH_TIME_OUT = 3000; // 3s

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final SessionManager sessionManager = new SessionManager(WelcomeActivity2.this);


        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                String token = prefs.getString("userToken", null);

                Bundle extras = getIntent().getExtras();
                String test = null;
                if (extras != null) {
                    test = getIntent().getExtras().getString("test");
                }

                if (token != null) {
                    if (test != null) {
                        Intent anotherIntent = new Intent(WelcomeActivity2.this, CheckIsDriveOutActivity.class);
                        startActivity(anotherIntent);
                        finish();
                    } else {
                        Intent welcomeIntent1 = new Intent(WelcomeActivity2.this, HomeActivity.class);
                        welcomeIntent1.putExtra("userToken", sessionManager.getToken());
                        startActivity(welcomeIntent1);
                        finish();
                    }
                } else {
                    Intent welcomeIntent2 = new Intent(WelcomeActivity2.this, MainActivity.class);
                    startActivity(welcomeIntent2);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);

    }

}