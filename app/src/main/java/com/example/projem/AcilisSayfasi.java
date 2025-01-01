package com.example.projem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class AcilisSayfasi extends AppCompatActivity {
    ProgressBar progress;
    ImageView logo;
    int sayac=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acilis_sayfasi);
        SharedPreferences settings = getSharedPreferences("theme", 0);
        //"theme" is the same key 0 is the default value
        boolean theme = settings.getBoolean("theme", false);
        if(theme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        logo=findViewById(R.id.acilisresmi);
        progress=findViewById(R.id.yukleniyor);
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(2500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        logo.startAnimation(animation);

        Timer zamanlayici=new Timer();
        TimerTask zamanlayicigorevi= new TimerTask() {
            @Override
            public void run() {
                sayac++;
                progress.setProgress(sayac);
                if(sayac==100){
                    zamanlayici.cancel();
                    Intent gecis=new Intent(AcilisSayfasi.this,LoginSayfa.class);
                    startActivity(gecis);
                }


            }
        };
        zamanlayici.schedule(zamanlayicigorevi,0,50);
    }
}