package com.example.yogendra.nplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Yogendra on 30-01-2016.
 */

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        System.out.println(".......................................Starting Player...........................................");

        Thread sTimer =new Thread(){
        public void run() {
            try {
                sleep(3000);
            } catch (Exception ex) {
                System.out.println("Cannot Splash It"+ex);
            } finally {
                Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
            }
        }

        };
        sTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
