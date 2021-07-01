package com.example.android.protoautotransfer2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Boolean firstTime;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);

        firstTime = sharedPreferences.getBoolean("firstTime", true);

        if(firstTime)
        {

            ImageView imageView1 = findViewById(R.id.welcome_screen_1);
            ImageView imageView2 = findViewById(R.id.welcome_screen_2);
            ImageView imageView3 = findViewById(R.id.welcome_screen_3);

            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                        imageView2.setVisibility(View.VISIBLE);
                        imageView1.setVisibility(View.GONE);
                }
            });

            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    imageView3.setVisibility(View.VISIBLE);
                    imageView2.setVisibility(View.GONE);
                }
            });

            imageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    firstTime = false;
                    editor.putBoolean("firstTime", firstTime);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }


    }
}