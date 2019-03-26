package se.ju.stos1605.quizzz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.Nullable;



public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME = 4000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /**Code to start timer and take action after the timer ends
         * This 'finish()' is for exiting the app when back button pressed from startpage which is MainActivity
         * SPLASH_TIME is constant that decides how long the splash-screen will show */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mySuperIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mySuperIntent);
                finish();
            }
        }, SPLASH_TIME);
    }

}

