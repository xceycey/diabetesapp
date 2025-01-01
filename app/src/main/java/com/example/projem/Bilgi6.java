package com.example.projem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class Bilgi6 extends AppCompatActivity {
    ImageButton geri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bilgi6);
        geri=findViewById(R.id.geritus);
        geri.setOnClickListener(v->{
            startActivity(new Intent(Bilgi6.this,MainActivity.class));
        });
    }
}