package com.example.projem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Bilgi1 extends AppCompatActivity {
    ImageButton geri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bilgi1);
        geri=findViewById(R.id.geritus);
        geri.setOnClickListener(v->{
            startActivity(new Intent(Bilgi1.this,MainActivity.class));
        });
    }
}