package com.example.electricitybill;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.ivSplashLogo);
        TextView appName = findViewById(R.id.tvSplashAppName);
        TextView tagline = findViewById(R.id.tvSplashTagline);

        logo.animate()
                .alpha(1f)
                .setDuration(500)
                .start();

        appName.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(200)
                .start();

        tagline.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(400)
                .start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}