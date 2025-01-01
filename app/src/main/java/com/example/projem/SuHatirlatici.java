package com.example.projem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SuHatirlatici extends AppCompatActivity {
    RangeSlider slider;
    TextView deger, yuzde,totalml;
    ProgressBar progressBar;
    AppCompatButton btn1, btn2, btn3;
    static int totalsu = 0;
    static int i=1000;
    static int hedefdeger;
    ImageButton geri;
    ArrayList<Integer> reversesu;
    ArrayList<String> reversetarih;
    ListView listview;

    @SuppressLint({"ClickableViewAccessibility", "WrongThread"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_su_hatirlatici);

       Calendar calendar=Calendar.getInstance();
       int currentDay=calendar.get(Calendar.DAY_OF_MONTH);
       SharedPreferences settings=getSharedPreferences("PREFS",0);
       int lastDay=settings.getInt("day",0);
       if(lastDay!=currentDay){
           SharedPreferences.Editor editor= settings.edit();
           editor.putInt("day",currentDay);
           editor.commit();
           getContentResolver().delete(ContentProvider.CONTENT_URI4, null, null);

       }

        slider = findViewById(R.id.slider);
        deger = findViewById(R.id.slidertext);
        progressBar = findViewById(R.id.progressBar);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        yuzde = findViewById(R.id.yuzde);
        totalml=findViewById(R.id.totalml);
        listview=findViewById(R.id.listview);
        geri=findViewById(R.id.geritus);
        geri.setOnClickListener(v->{finish();});
        slider.setValues((float) i);
        deger.setText(String.valueOf(i));

        SharedPreferences sharedPreferences = getSharedPreferences("checkbox1", MODE_PRIVATE);
        hedefdeger = sharedPreferences.getInt("hedef", 1000);

        deger.setText(String.valueOf(hedefdeger));
        slider.setValues((float) hedefdeger);
        oku();
        listview.setAdapter(new SuHatirlatici.Adapter(getApplicationContext(), reversesu, reversetarih));
        LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams((int)(ViewGroup.LayoutParams.MATCH_PARENT),(int)(200*listview.getCount()));
        mParam.setMargins(75,30,75,90);
        listview.setLayoutParams(mParam);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues=new ContentValues();
                contentValues.put(ContentProvider.SUML,200);
                getContentResolver().insert(ContentProvider.CONTENT_URI4,contentValues);
                Toast.makeText(SuHatirlatici.this, "Kayıt eklendi.", Toast.LENGTH_SHORT).show();
                oku();
                listview.setAdapter(new SuHatirlatici.Adapter(getApplicationContext(), reversesu, reversetarih));
                LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams((int)(ViewGroup.LayoutParams.MATCH_PARENT),(int)(200*listview.getCount()));
                mParam.setMargins(75,30,75,90);
                listview.setLayoutParams(mParam);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues=new ContentValues();
                contentValues.put(ContentProvider.SUML,300);
                getContentResolver().insert(ContentProvider.CONTENT_URI4,contentValues);
                Toast.makeText(SuHatirlatici.this, "Kayıt eklendi.", Toast.LENGTH_SHORT).show();
                oku();
                listview.setAdapter(new SuHatirlatici.Adapter(getApplicationContext(), reversesu, reversetarih));
                LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams((int)(ViewGroup.LayoutParams.MATCH_PARENT),(int)(200*listview.getCount()));
                mParam.setMargins(75,30,75,90);
                listview.setLayoutParams(mParam);


            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues=new ContentValues();
                contentValues.put(ContentProvider.SUML,500);
                getContentResolver().insert(ContentProvider.CONTENT_URI4,contentValues);
                Toast.makeText(SuHatirlatici.this, "Kayıt eklendi.", Toast.LENGTH_SHORT).show();
                oku();
                listview.setAdapter(new SuHatirlatici.Adapter(getApplicationContext(), reversesu, reversetarih));
                LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams((int)(ViewGroup.LayoutParams.MATCH_PARENT),(int)(200*listview.getCount()));
                mParam.setMargins(75,30,75,90);
                listview.setLayoutParams(mParam);

            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ArrayList<String> sonuc = (ArrayList<String>) listview.getItemAtPosition(position);
                Integer suml = Integer.parseInt(sonuc.get(0));
                String sutarih = sonuc.get(1);

                String[] selectionArguments = {String.valueOf(suml), sutarih};
                getContentResolver().delete(ContentProvider.CONTENT_URI4, "suml=? and sutarih=?", selectionArguments);
                Toast.makeText(getApplicationContext(), "Kayıt silindi.", Toast.LENGTH_SHORT).show();
                oku();
                listview.setAdapter(new SuHatirlatici.Adapter(getApplicationContext(), reversesu, reversetarih));

                LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams((int)(ViewGroup.LayoutParams.MATCH_PARENT),(int)(200*listview.getCount()));
                mParam.setMargins(75,30,75,90);
                listview.setLayoutParams(mParam);

            }
        });


        slider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                i = Math.round(value);
                deger.setText(String.valueOf(i));
                hedefdeger=i;
                progressBar.setProgress((totalsu * 100) / hedefdeger);
                yuzde.setText("%"+(totalsu * 100) / hedefdeger);
                totalml.setText(totalsu +" ml");
                SharedPreferences sP = getSharedPreferences("checkbox1", MODE_PRIVATE);
                SharedPreferences.Editor editor = sP.edit();
                editor.putInt("hedef", i);
                editor.apply();
            }
        });


    }

    @SuppressLint("Range")
    protected void oku() {
        Uri uri = Uri.parse("content://com.example.projem.ContentProvider/su");
        ContentResolver contentResolver = getContentResolver();
        ArrayList<Integer> sulist= new ArrayList<>();
        ArrayList<String> sutarih=new ArrayList<>();

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                sulist.add(cursor.getInt(cursor.getColumnIndex(ContentProvider.SUML)));
                sutarih.add(cursor.getString(cursor.getColumnIndex(ContentProvider.SUTARIH)));
            }
        } else {
            System.out.println("Cursor null");
        }
        totalsu=0;
        for(int i=0;i<sulist.size();i++){
            totalsu+=sulist.get(i);
        }
        progressBar.setProgress((totalsu * 100) / hedefdeger);
        yuzde.setText("%"+(totalsu * 100) / hedefdeger);
        totalml.setText(totalsu +" ml");

        reversesu = new ArrayList<>(sulist);
        Collections.reverse(reversesu);
        reversetarih = new ArrayList<>(sutarih);
        Collections.reverse(reversetarih);

    }

    static class Adapter extends BaseAdapter {

        Context context;
        ArrayList<Integer> su;
        ArrayList<String> sutarih;
        private LayoutInflater inflater = null;

        public Adapter(Context context, ArrayList<Integer> su, ArrayList<String> sutarih) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.su = su;
            this.sutarih = sutarih;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return su.size();
        }

        @Override
        public ArrayList<String> getItem(int position) {
            ArrayList<String> sonuc = new ArrayList<>();
            sonuc.add(String.valueOf(su.get(position)));
            sonuc.add(sutarih.get(position));
            return sonuc;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vi = view;
            if (vi == null)
                vi = inflater.inflate(R.layout.listview_su, null);
            ImageView delicon=(ImageView) vi.findViewById(R.id.deleteicon);
            ImageView img= (ImageView) vi.findViewById(R.id.img);
            TextView text1 = (TextView) vi.findViewById(R.id.text1);
            TextView text2 = (TextView) vi.findViewById(R.id.text2);
            if(su.get(i)==200){
                img.setImageResource(R.drawable.icondrawglass);
            }
            if(su.get(i)==300){
                img.setImageResource(R.drawable.icondrawcup);
            }
            if(su.get(i)==500){
                img.setImageResource(R.drawable.icondrawdrink);
            }
            delicon.setColorFilter(Color.rgb(55,55,55));
            boolean gecemi = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
            if (gecemi) {
                text1.setTextColor(Color.WHITE);
                text2.setTextColor(Color.WHITE);
                delicon.setColorFilter(Color.WHITE);
            }
            text1.setText(su.get(i)+" ml");
            text2.setText(sutarih.get(i));
            return vi;
        }


    }





}