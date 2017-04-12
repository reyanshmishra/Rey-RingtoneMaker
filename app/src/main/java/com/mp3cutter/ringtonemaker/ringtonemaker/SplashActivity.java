package com.mp3cutter.ringtonemaker.ringtonemaker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mp3cutter.ringtonemaker.R;
import com.mp3cutter.ringtonemaker.Ringdroid.RingdroidSelectActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView mBellImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        mBellImageView = (ImageView) findViewById(R.id.image_view_bell);
        mBellImageView.startAnimation(shake);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBellImageView.clearAnimation();
                startActivity(new Intent(SplashActivity.this, RingdroidSelectActivity.class));
            }
        }, 1000);
    }
}
