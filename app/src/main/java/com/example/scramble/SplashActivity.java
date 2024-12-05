package com.example.scramble;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Play Animation before fade
        new Handler().postDelayed(() -> {
            ImageView logo = findViewById(R.id.app_logo);
            ProgressBar progressBar = findViewById(R.id.app_progress);

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shrink_n_translate);
            logo.startAnimation(animation);
            animation = AnimationUtils.loadAnimation(this, R.anim.translate_out);
            progressBar.startAnimation(animation);
        }, 2250);

        // Splash screen wait, then fade to home screen
        new Handler().postDelayed(() -> {
            Intent intent;
            intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, R.anim.fade_in);
            finish();
        }, 3000);
    }
}