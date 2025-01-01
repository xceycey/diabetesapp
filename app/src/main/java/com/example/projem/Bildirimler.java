package com.example.projem;

import static com.android.volley.VolleyLog.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Bildirimler extends AppCompatActivity {
    ImageButton geri;
    TextView txtsaat;
    EditText bildirimad;
    Button btnsaat;
    AppCompatButton kaydet,sil;
    int hour, min;
    String saat;
    private Calendar calendar;
    static private AlarmManager alarmManager;
    static private PendingIntent pendingIntent=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bildirimler);
        geri = findViewById(R.id.geritus);
        geri.setOnClickListener(v -> {
            finish();
        });
        txtsaat = findViewById(R.id.txtsaat);
        bildirimad = findViewById(R.id.bildirimad);
        btnsaat = findViewById(R.id.btnsaat);
        kaydet = findViewById(R.id.kaydet);
        sil=findViewById(R.id.sil);

        Calendar calendar1 = Calendar.getInstance();
        MaterialTimePicker mtp = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setHour(calendar1.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar1.get(Calendar.MINUTE)).build();
        hour = calendar1.get(Calendar.HOUR_OF_DAY);
        calendar1.get(Calendar.HOUR_OF_DAY);
        min = calendar1.get(Calendar.MINUTE);
        SharedPreferences sharedPreferences = getSharedPreferences("checkbox1", MODE_PRIVATE);
        String gelentxt = sharedPreferences.getString("txt","");
        bildirimad.setText(gelentxt);

        SharedPreferences sharedPreferences1 = getSharedPreferences("checkbox1", MODE_PRIVATE);
        String gelensaat = sharedPreferences1.getString("saat","");
        if(!gelensaat.isEmpty()){
            txtsaat.setText(gelensaat);
        }
        else{
            if (min < 10 && hour >= 10) {
                saat = hour + ":0" + min;
                txtsaat.setText(saat);
            } else if (hour < 10 && min >= 10) {
                saat = "0" + hour + ":" + min;
                txtsaat.setText(saat);
            } else if (min < 10 && hour < 10) {
                saat = "0" + hour + ":0" + min;
                txtsaat.setText(saat);
            } else {
                saat = hour + ":" + min;
                txtsaat.setText(saat);
            }
        }



        btnsaat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtp.show(getSupportFragmentManager(), "Saat");
                mtp.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hour = mtp.getHour();
                        min = mtp.getMinute();
                        System.out.println(hour);
                        System.out.println(min);
                        if (min<10 && hour>=10) {
                            saat = hour + ":0" + min;
                            txtsaat.setText(saat);
                        } else if (hour < 10 && min >= 10) {
                            saat = "0" + hour + ":" + min;
                            txtsaat.setText(saat);
                        } else if (min < 10 && hour < 10) {
                            saat = "0" + hour + ":0" + min;
                            txtsaat.setText(saat);
                        } else {
                            saat = hour + ":" + min;
                            txtsaat.setText(saat);
                        }


                    }
                });
            }
        });

        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bildirimtext = bildirimad.getText().toString();
                SharedPreferences sP = getSharedPreferences("checkbox1", MODE_PRIVATE);
                SharedPreferences.Editor editor = sP.edit();
                editor.putString("txt",bildirimtext);
                editor.apply();

                SharedPreferences sP1 = getSharedPreferences("checkbox1", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sP1.edit();
                editor1.putString("saat",txtsaat.getText().toString());
                editor1.apply();

                if (bildirimtext.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lütfen bildirim adı girin.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent _intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    _intent.putExtra("text", bildirimtext);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, _intent, PendingIntent.FLAG_MUTABLE);
                    }
                    else
                    {
                        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, _intent, PendingIntent.FLAG_ONE_SHOT);
                    }
                    alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, min);
                    calendar.set(Calendar.SECOND, 0);
                    alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);

                    Toast.makeText(getApplicationContext(), "Bildirim Ayarlandı.", Toast.LENGTH_SHORT).show();
                    System.out.println("bildirimtxt: " + bildirimtext);

                    finish();
                }

            }
        });
        sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alarmManager!= null) {
                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(getApplicationContext(), "Alarm silindi.", Toast.LENGTH_SHORT).show();
                }else{
                    System.out.println("Alarm silinemedi.");
                }
            }
        });

    }

}



