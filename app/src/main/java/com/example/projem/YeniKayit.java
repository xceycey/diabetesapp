package com.example.projem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class YeniKayit extends AppCompatActivity {
    ImageButton kapat;
    DatePicker mydatepicker;
    TimePicker mytimepicker;
    Button kaydet,sil;
    EditText deger;
    TextInputLayout til;
    String item="";
    AutoCompleteTextView act;
    String[] secenekler = { "Kahvaltıdan Önce", "Kahvaltıdan Sonra", "Öğle Yemeğinden Önce",
            "Öğle Yemeğinden Sonra", "Akşam Yemeğinden Önce", "Akşam Yemeğinden Sonra", "Uyumadan Önce" };
    ArrayAdapter<String> arrayAdapter;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeni_kayit);



        TextView txt=findViewById(R.id.txtkayit);
        kaydet=findViewById(R.id.kaydet);
        sil=findViewById(R.id.sil);
        deger=findViewById(R.id.edt_deger);
        til=findViewById(R.id.menu);
        act=findViewById(R.id.autoCompleteTextView);
        mydatepicker=findViewById(R.id.mydatepicker);
        mytimepicker=findViewById(R.id.mytimepicker);

        Intent al=getIntent();
        String check=al.getStringExtra("check");

        if(check.equals("eski")){
            kapat=findViewById(R.id.close);
            kapat.setOnClickListener(v->{
                finish();
            });
            String gelendeger=al.getStringExtra("deger");
            String gelentarih=al.getStringExtra("tarih");
            String gelensaat=al.getStringExtra("saat");
            String gelendurum=al.getStringExtra("durum");


            txt.setText("Düzenle");
            act.setText(gelendurum);
            act.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.filter_spinner_dropdown_bg));
            arrayAdapter=new ArrayAdapter<>(getApplicationContext(),R.layout.item_autocomplete,R.id.tvCustom,secenekler);
            act.setAdapter(arrayAdapter);
            item=gelendurum;
            act.setOnItemClickListener((parent, arg1, pos, id) -> {
                item = (String) parent.getItemAtPosition(pos);
                // Toast.makeText(YeniKayit.this, "Selected Item: "+item, Toast.LENGTH_SHORT).show();
            });

            deger.setText(gelendeger);
            sil.setVisibility(View.VISIBLE);
            sil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] selectionArguments = {gelendeger, gelentarih, gelensaat, gelendurum};
                    getContentResolver().delete(ContentProvider.CONTENT_URI, "deger=? and tarih=? and saat=? and durum=?", selectionArguments);
                    Toast.makeText(getApplicationContext(), "Kayıt silindi.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), SekerTakipSayfa.class));
                }
            });
            String[] gelentarihsplit=gelentarih.split("-");
            mydatepicker.setMaxDate(System.currentTimeMillis() - 1000);
            mydatepicker.updateDate(Integer.parseInt(gelentarihsplit[0]),Integer.parseInt(gelentarihsplit[1])-1,
                    Integer.parseInt(gelentarihsplit[2]));
            String[] gelensaatsplit=gelensaat.split(":");
            mytimepicker.setIs24HourView(true);
            mytimepicker.setHour(Integer.parseInt(gelensaatsplit[0]));
            mytimepicker.setMinute(Integer.parseInt(gelensaatsplit[1]));
            kaydet.setOnClickListener(v -> {
                String strTime="";
                String strDate;
                int gun=mydatepicker.getDayOfMonth();
                int ay=mydatepicker.getMonth();
                int yil=mydatepicker.getYear();

                int hour=mytimepicker.getHour();
                int min =mytimepicker.getMinute();
                if(min<10 && hour>=10){
                    strTime=hour+":0"+min;
                }
                if(hour<10 && min>=10){
                    strTime="0"+hour+":"+min;
                }
                if(min<10 && hour<10){
                    strTime="0"+hour+":0"+min;
                }
                if(min>=10 && hour>=10){
                    strTime=hour+":"+min;
                }


                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, yil);
                cal.set(Calendar.MONTH, ay);
                cal.set(Calendar.DAY_OF_MONTH, gun);
                Date dateRepresentation = cal.getTime();
                strDate = dateFormatter.format(dateRepresentation);
                //Toast.makeText(this, "Seçilen tarih ve saat: "+strDate+" "+strTime+" "+olcum+" "+item, Toast.LENGTH_LONG).show();


                String olcum=deger.getText().toString();
                if(olcum.isEmpty()){
                    deger.setError("Lütfen bir değer girin");
                }
                else{
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(ContentProvider.DEGER,olcum);
                    contentValues.put(ContentProvider.TARIH,strDate);
                    contentValues.put(ContentProvider.SAAT,strTime);
                    contentValues.put(ContentProvider.DURUM,item);
                    String [] selectionArguments={gelendeger,gelentarih,gelensaat};
                    getContentResolver().update(ContentProvider.CONTENT_URI,contentValues,"deger=? and tarih=? and saat=?",selectionArguments);
                    Toast.makeText(this, "Kayıt güncellendi.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }
        else{
            txt.setText("Yeni Kayıt");
            act.setText("Seçim Yapın");
            act.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.filter_spinner_dropdown_bg));
            arrayAdapter=new ArrayAdapter<>(getApplicationContext(),R.layout.item_autocomplete,R.id.tvCustom,secenekler);
            act.setAdapter(arrayAdapter);
            act.setOnItemClickListener((parent, arg1, pos, id) -> {
                item = (String) parent.getItemAtPosition(pos);
                // Toast.makeText(YeniKayit.this, "Selected Item: "+item, Toast.LENGTH_SHORT).show();
            });

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            long now = System.currentTimeMillis() - 1000;
            mydatepicker.setMaxDate(now);
            mydatepicker.init(year, month, day, null);

            mytimepicker.setIs24HourView(true);
            mytimepicker.setHour(c.get(Calendar.HOUR_OF_DAY));
            mytimepicker.setMinute(c.get(Calendar.MINUTE));

            kapat=findViewById(R.id.close);
            kapat.setOnClickListener(v->{
                finish();
            });

            kaydet.setOnClickListener(a->{

                String strTime="";
                String strDate;
                int gun=mydatepicker.getDayOfMonth();
                int ay=mydatepicker.getMonth();
                int yil=mydatepicker.getYear();

                int hour=mytimepicker.getHour();
                int min =mytimepicker.getMinute();
                if(min<10 && hour>=10){
                    strTime=hour+":0"+min;
                }
                if(hour<10 && min>=10){
                    strTime="0"+hour+":"+min;
                }
                if(min<10 && hour<10){
                    strTime="0"+hour+":0"+min;
                }
                if(min>=10 && hour>=10){
                    strTime=hour+":"+min;
                }


                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, yil);
                cal.set(Calendar.MONTH, ay);
                cal.set(Calendar.DAY_OF_MONTH, gun);
                Date dateRepresentation = cal.getTime();
                strDate = dateFormatter.format(dateRepresentation);
                //Toast.makeText(this, "Seçilen tarih ve saat: "+strDate+" "+strTime+" "+item, Toast.LENGTH_LONG).show();
                System.out.println(strDate+" "+strTime);

                String olcum=deger.getText().toString();
                if(olcum.isEmpty()){
                    deger.setError("Lütfen bir değer girin");
                }
                else{
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(ContentProvider.DEGER,olcum);
                    contentValues.put(ContentProvider.TARIH,strDate);
                    contentValues.put(ContentProvider.SAAT,strTime);
                    contentValues.put(ContentProvider.DURUM,item);

                    getContentResolver().insert(ContentProvider.CONTENT_URI,contentValues);
                    Toast.makeText(this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }


    }


}