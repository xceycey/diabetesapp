package com.example.projem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;


public class Ayarlar extends AppCompatActivity {
    BottomNavigationView bnv;
    TextView cikis,feedback,oran,suhatirlat,bildirim;
    FirebaseAuth yetki;
    SwitchCompat mode;
    FirebaseUser currentuser;
    public static Float kdoranim=15f;
    private static final DecimalFormat df = new DecimalFormat("0");
    TextView hesapbil,hesapbil2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ayarlar);
        bildirim=findViewById(R.id.bildirim);
        bildirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Bildirimler.class));
            }
        });
        suhatirlat=findViewById(R.id.suhatirlatici);
        suhatirlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SuHatirlatici.class));
            }
        });
        feedback=findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Feedback.class));
            }
        });
        oran=findViewById(R.id.oran);
        oran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = Ayarlar.this.getTheme();
                theme.resolveAttribute(R.attr.textrengi, typedValue, true);
                TypedArray arr = Ayarlar.this.obtainStyledAttributes(typedValue.data, new int[]{
                        R.attr.textrengi});
                int primaryColor = arr.getColor(0, -1);
                AlertDialog.Builder builder = new AlertDialog.Builder(Ayarlar.this, R.style.MyAlertDialogTheme);
                final View customLayout = getLayoutInflater().inflate(R.layout.customalertdialog, null);
                builder.setView(customLayout);
                TextView tv_title = new TextView(Ayarlar.this);
                tv_title.setText("      1 doz için karbonhidrat oranınız\n      nedir?");
                tv_title.setTextSize(15f);
                //tv_title.setTextColor(getResources().getColor(R.color.white));
                Typeface typeface = ResourcesCompat.getFont(Ayarlar.this, R.font.montserratextrabold);
                tv_title.setTypeface(typeface);
                builder.setCustomTitle(tv_title);
                builder.setMessage(" (Varsayılan 15 olarak alınır.)");
                EditText editText = customLayout.findViewById(R.id.edt);

                SharedPreferences karb = getSharedPreferences("checkbox1", MODE_PRIVATE);
                Float kdorani = karb.getFloat("oran", 15f);
                editText.setText(df.format(kdorani));

                builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String edt = editText.getText().toString();
                        if (!edt.isEmpty()) {
                            kdoranim = Float.parseFloat(edt);
                            SharedPreferences karb = getSharedPreferences("checkbox1", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = karb.edit();
                            editor1.putFloat("oran", Float.parseFloat(editText.getText().toString()));
                            editor1.apply();
                        } else {
                            finish();
                        }

                    }
                });
                builder.setNegativeButton("İptal", null);

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                        //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(800, 650);
                TypedValue themedValue = new TypedValue();
                Ayarlar.this.getTheme().resolveAttribute(R.attr.anatema, themedValue, true);
                Drawable mydrawable = AppCompatResources.getDrawable(Ayarlar.this, themedValue.resourceId);
                dialog.getWindow().setBackgroundDrawable(mydrawable);
            }
        });

        mode=findViewById(R.id.switchmode);
        boolean isnightmodeon=AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES;
        mode.setChecked(isnightmodeon);

        SharedPreferences settings = getSharedPreferences("theme", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("theme", isnightmodeon);
        editor.commit();

        hesapbil=findViewById(R.id.txthesapbil);
        hesapbil2=findViewById(R.id.txthesapbil2);
        yetki=FirebaseAuth.getInstance();
        currentuser=yetki.getCurrentUser();
        String mail =currentuser.getEmail();
        String adsoyad=currentuser.getDisplayName();

        hesapbil.setText(adsoyad);
        hesapbil2.setText(mail);
        cikis = findViewById(R.id.cikisyap);
        bnv = findViewById(R.id.navigation);
        bnv.setSelectedItemId(R.id.nav_person);
        bnv.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_blood:
                    startActivity(new Intent(getApplicationContext(), SekerTakipSayfa.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_rest:
                    startActivity(new Intent(getApplicationContext(), KarbSayfa.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_person:
                    return true;

            }
            return false;
        });

        cikis.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Ayarlar.this,LoginSayfa.class));
            Toast.makeText(this, "Başarıyla çıkış yapıldı.", Toast.LENGTH_SHORT).show();
            finishAffinity();
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mode=findViewById(R.id.switchmode);
        mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                }
            }
        });

        boolean isnightmodeon=AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES;
        mode.setChecked(isnightmodeon);
    }
}