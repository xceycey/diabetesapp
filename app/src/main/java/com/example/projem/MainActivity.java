package com.example.projem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnv;
    TextView durum,seker,ortseker,birim1,birim2;
    ExtendedFloatingActionButton ekle;
    LinearLayout kutum1,kutum2,kutum3,kutum4,kutum5,kutum6;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ekle=findViewById(R.id.floatbtn_ekle);
        ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gonder=new Intent(getApplicationContext(),YeniKayit.class);
                gonder.putExtra("check","yeni");
                startActivity(gonder);
            }
        });


        kutum1=findViewById(R.id.kutu1);
        kutum2=findViewById(R.id.kutu2);
        kutum3=findViewById(R.id.kutu3);
        kutum4=findViewById(R.id.kutu4);
        kutum5=findViewById(R.id.kutu5);
        kutum6=findViewById(R.id.kutu6);
        bnv=findViewById(R.id.navigation);
        bnv.setSelectedItemId(R.id.nav_home);
        bnv.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.nav_home:
                    return true;
                case R.id.nav_blood:
                    startActivity(new Intent(getApplicationContext(),SekerTakipSayfa.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_rest:
                    startActivity(new Intent(getApplicationContext(),KarbSayfa.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_person:
                    startActivity(new Intent(getApplicationContext(),Ayarlar.class));
                    overridePendingTransition(0,0);
                    return true;

            }
            return false;
        });

        kutum1.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,Bilgi1.class));
        });
        kutum2.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,Bilgi2.class));
        });
        kutum3.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,Bilgi3.class));
        });
        kutum4.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,Bilgi4.class));
        });
        kutum5.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,Bilgi5.class));
        });
        kutum6.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,Bilgi6.class));
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @SuppressLint("Range")
    @Override
    protected void onResume() {
        super.onResume();
        durum=findViewById(R.id.txtdurum);
        seker=findViewById(R.id.txtseker);
        birim1=findViewById(R.id.txtbirim);
        birim2=findViewById(R.id.txtbirim2);
        ortseker=findViewById(R.id.txtseker2);
        final ArrayList<String> degerList= new ArrayList<>();
        final ArrayList<String> durumList=new ArrayList<>();

        Uri uri = Uri.parse("content://com.example.projem.ContentProvider/seker");
        ContentResolver contentResolver=getContentResolver();
        String order = "tarih" + " desc, " +"saat "+ "desc";
        Cursor cursor =contentResolver.query(uri,null,null,null,order);
        if(cursor != null && cursor.getCount() > 0){
            while(cursor.moveToNext()){
                degerList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.DEGER)));
                durumList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.DURUM)));

            }
        }
        if(degerList.size()>0){
            String sonuncudeger=degerList.get(0);
            String sonuncudurum=durumList.get(0);
            seker.setText(sonuncudeger);
            durum.setText(sonuncudurum);

        }


        final ArrayList<String> degerListnew= new ArrayList<>();
        String order2 = "tarih" + " desc, " +"saat "+ "desc LIMIT 30";
        Cursor cursor2 =contentResolver.query(uri,null,null,null,order2);
        if(cursor2 != null && cursor2.getCount() > 0){
            while(cursor2.moveToNext()){
                degerListnew.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.DEGER)));

            }
        }
        System.out.println(degerListnew);
        Integer total=0;
        Integer ort;
        for(int i=0;i<degerListnew.size();i++){
            total+=Integer.parseInt(degerListnew.get(i));
        }
        System.out.println(total);
        if(degerListnew.size()>0){
            ort=total/degerListnew.size();
            System.out.println(ort);
            ortseker.setText(ort.toString());
        }


    }
}

