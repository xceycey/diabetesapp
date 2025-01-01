package com.example.projem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.example.projem.KarbSayfa.bmp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class YemekEkle extends AppCompatActivity {
    ImageButton kapat;
    ExtendedFloatingActionButton foto;
    AppCompatButton kaydet, sil, ogunekle;
    ImageView imageView;
    Bitmap bitmap;
    TextView yiyecek, txtfoto;
    EditText yemekad, miktar, servis, karb;
    boolean fotosecildimi;

    Uri staticuri;
    int tut=1;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fotosecildimi = false;
        setContentView(R.layout.activity_yemek_ekle);
        kapat = findViewById(R.id.close);
        ogunekle = findViewById(R.id.ogunekle);
        imageView = findViewById(R.id.imageview);
        yiyecek = findViewById(R.id.yiyecek);
        kaydet = findViewById(R.id.kaydet);
        yemekad = findViewById(R.id.yemekadi);
        miktar = findViewById(R.id.miktar);
        servis = findViewById(R.id.servis);
        karb = findViewById(R.id.karb);
        txtfoto = findViewById(R.id.fototxt);
        sil = findViewById(R.id.sil);
        kapat.setOnClickListener(view -> finish());
        foto = findViewById(R.id.foto);

        Intent al = getIntent();
        String check = al.getStringExtra("check");

        if (check.equals("eski")) {
            //Kayıt Güncelleme
            yiyecek.setText("Düzenle");
            kapat = findViewById(R.id.close);
            kapat.setOnClickListener(v -> {
                finish();
            });
            String gelenyemek = al.getStringExtra("yemek");
            Integer gelenmiktar = al.getIntExtra("miktar", 0);
            String gelenporsiyon = al.getStringExtra("porsiyon");
            String gelenkarb = al.getStringExtra("karb");
            foto.setVisibility(View.GONE);
            txtfoto.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bmp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            yemekad.setText(gelenyemek);
            miktar.setText(String.valueOf(gelenmiktar));
            servis.setText(gelenporsiyon);
            karb.setText(gelenkarb);
            sil.setVisibility(View.VISIBLE);
            sil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] selectionArguments = {gelenyemek, String.valueOf(gelenmiktar), gelenporsiyon,gelenkarb};
                    getContentResolver().delete(ContentProvider.CONTENT_URI2, "yemekadi=? and miktar=? and porsiyon=? and karb=?", selectionArguments);
                    Toast.makeText(getApplicationContext(), "Kayıt silindi.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), KarbSayfa.class));
                }
            });
            ogunekle.setVisibility(View.VISIBLE);
            ogunekle.setOnClickListener(new View.OnClickListener() {

                @SuppressLint("Range")
                @Override
                public void onClick(View view) {
                    TypedValue typedValue = new TypedValue();
                    Resources.Theme theme = YemekEkle.this.getTheme();
                    theme.resolveAttribute(R.attr.textrengi, typedValue, true);
                    TypedArray arr =YemekEkle.this.obtainStyledAttributes(typedValue.data, new int[]{
                            R.attr.textrengi});
                    int primaryColor = arr.getColor(0, -1);
                    AlertDialog.Builder builder = new AlertDialog.Builder(YemekEkle.this,R.style.MyAlertDialogTheme);
                    final View customLayout = getLayoutInflater().inflate(R.layout.foodalertdialog, null);
                    builder.setView(customLayout);
                    TextView tv_title = new TextView(YemekEkle.this);
                    tv_title.setText("      Eklemek istediğiniz miktar nedir?\n  (Adet sayısı porsiyon miktarı ile çarpılır.)");
                    tv_title.setTextSize(18f);
                    builder.setCustomTitle(tv_title);
                    EditText adet = customLayout.findViewById(R.id.edtadet);
                    ImageButton up=customLayout.findViewById(R.id.btnup);
                    ImageButton down=customLayout.findViewById(R.id.btndown);
                    up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tut=Integer.parseInt(adet.getText().toString());
                            tut++;
                            adet.setText(String.valueOf(tut));
                        }
                    });
                    down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int tut=Integer.parseInt(adet.getText().toString());
                            tut--;
                            if(tut>0){
                                adet.setText(String.valueOf(tut));
                            }

                        }
                    });
                    builder.setPositiveButton("ONAYLA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String yemek = yemekad.getText().toString();
                            String miktari = miktar.getText().toString();
                            String porsiyon = servis.getText().toString();
                            String karbo = karb.getText().toString();
                            int kontrol = 1;
                            if (yemek.isEmpty()) {
                                kontrol = 0;
                                yemekad.setError("Yemek adı giriniz.");
                            }
                            if (karbo.isEmpty()) {
                                kontrol = 0;
                                karb.setError("Bir karbonhidrat değeri giriniz");
                            }
                            if (miktari.isEmpty()) {
                                kontrol = 0;
                                miktar.setError("Bir değer giriniz.");
                            }
                            if (porsiyon.isEmpty()) {
                                kontrol = 0;
                                servis.setError("Bir birim giriniz.");
                            }
                            if (kontrol == 1) {
                                Uri uri1 = Uri.parse("content://com.example.projem.ContentProvider/yemek");
                                ContentResolver contentResolver1 = getContentResolver();
                                String[] selectionargs1 = {yemek,miktari, porsiyon, karbo};
                                Cursor cursor1 = contentResolver1.query(uri1, null, "yemekadi=? and miktar=? and porsiyon=? and karb=?", selectionargs1, null);
                                int kendimiktari=gelenmiktar;
                                if (cursor1 != null && cursor1.getCount() > 0) {
                                    while (cursor1.moveToNext()) {
                                        kendimiktari=cursor1.getInt(cursor1.getColumnIndex(ContentProvider.MIKTAR));
                                    }
                                }

                                Uri uri = Uri.parse("content://com.example.projem.ContentProvider/ogun");
                                ContentResolver contentResolver = getContentResolver();
                                String[] selectionargs = {yemek, porsiyon, karbo};
                                Cursor cursor = contentResolver.query(uri, null, "ogunad=? and ogunporsiyon=? and ogunkarb=?", selectionargs, null);
                                int cekilenmiktar=1;
                                if (cursor != null && cursor.getCount() > 0) {
                                    System.out.println("kayıt bulundu.");
                                    while (cursor.moveToNext()) {
                                        cekilenmiktar=cursor.getInt(cursor.getColumnIndex(ContentProvider.OGUNMIKTAR));
                                    }
                                    ContentValues contentValues3= new ContentValues();
                                    contentValues3.put(ContentProvider.OGUNMIKTAR, cekilenmiktar+(tut*kendimiktari));
                                    String[] selectionArguments3 ={yemek, String.valueOf(cekilenmiktar), porsiyon, karbo};
                                    getContentResolver().update(ContentProvider.CONTENT_URI3, contentValues3, "ogunad=? and ogunmiktar=? and ogunporsiyon=? and ogunkarb=?", selectionArguments3);
                                    Toast.makeText(getApplicationContext(), "Öğüne Eklendi.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    System.out.println("kayıt bulunamadi.");
                                    ContentValues contentValues2 = new ContentValues();
                                    contentValues2.put(ContentProvider.OGUNAD, yemek);
                                    contentValues2.put(ContentProvider.OGUNMIKTAR, kendimiktari*tut);
                                    contentValues2.put(ContentProvider.OGUNPORSIYON, porsiyon);
                                    contentValues2.put(ContentProvider.OGUNKARB, karbo);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    contentValues2.put(ContentProvider.OGUNIMAGE, byteArray);
                                    getContentResolver().insert(ContentProvider.CONTENT_URI3, contentValues2);
                                    Toast.makeText(getApplicationContext(), "Öğüne Eklendi.", Toast.LENGTH_SHORT).show();
                                }
                                finish();


                            }
                        }
                    });
                    builder.setNegativeButton("İptal", null);

                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.turuncu));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(primaryColor);
                            //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
                        }
                    });
                    dialog.show();
                    dialog.getWindow().setLayout(900, 550);
                    TypedValue themedValue = new TypedValue();
                    YemekEkle.this.getTheme().resolveAttribute(R.attr.anatema, themedValue, true);
                    Drawable mydrawable = AppCompatResources.getDrawable(YemekEkle.this, themedValue.resourceId);
                    dialog.getWindow().setBackgroundDrawable(mydrawable);

                }
            });

            kaydet.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("Range")
                @Override
                public void onClick(View view) {

                    String yemek = yemekad.getText().toString();
                    String miktari = miktar.getText().toString();
                    String porsiyon = servis.getText().toString();
                    String karbo = karb.getText().toString();
                    int kontrol = 1;
                    if (yemek.isEmpty()) {
                        kontrol = 0;
                        yemekad.setError("Yemek adı giriniz.");
                    }
                    if (karbo.isEmpty()) {
                        kontrol = 0;
                        karb.setError("Bir karbonhidrat değeri giriniz");
                    }
                    if (miktari.isEmpty()) {
                        kontrol = 0;
                        miktar.setError("Bir değer giriniz.");
                    }
                    if (porsiyon.isEmpty()) {
                        kontrol = 0;
                        servis.setError("Bir birim giriniz.");
                    }
                    if (kontrol == 1) {
                        //System.out.println("Yemek adı:"+yemek+" "+miktari+" "+porsiyon+" Karbonhidrat:"+karbo);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ContentProvider.AD, yemek);
                        contentValues.put(ContentProvider.MIKTAR, Integer.parseInt(miktari));
                        contentValues.put(ContentProvider.PORSIYON, porsiyon);
                        contentValues.put(ContentProvider.KARB, karbo);


                        System.out.println(gelenyemek+gelenmiktar+gelenporsiyon+gelenkarb);
                        String[] selectionArguments = {gelenyemek, String.valueOf(gelenmiktar), gelenporsiyon,gelenkarb};
                        getContentResolver().update(ContentProvider.CONTENT_URI2, contentValues, "yemekadi=? and miktar=? and porsiyon=? and karb=?", selectionArguments);
                        Toast.makeText(getApplicationContext(), "Kayıt güncellendi.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });


        } else {
            //Yeni Kayıt
            foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 2);

                }
            });
            kaydet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String yemek = yemekad.getText().toString();
                    String miktari = miktar.getText().toString();
                    String porsiyon = servis.getText().toString();
                    String karbo = karb.getText().toString();
                    int kontrol = 1;
                    if (yemek.isEmpty()) {
                        kontrol = 0;
                        yemekad.setError("Yemek adı giriniz.");
                    }
                    if (karbo.isEmpty()) {
                        kontrol = 0;
                        karb.setError("Bir karbonhidrat değeri giriniz");
                    }
                    if (miktari.isEmpty()) {
                        kontrol = 0;
                        miktar.setError("Bir değer giriniz.");
                    }
                    if (porsiyon.isEmpty()) {
                        kontrol = 0;
                        servis.setError("Bir birim giriniz.");
                    }
                    if (kontrol == 1) {
                        System.out.println("Yemek adı:"+yemek+" "+miktari+" "+porsiyon+" Karbonhidrat:"+karbo);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ContentProvider.AD, yemek);
                        contentValues.put(ContentProvider.MIKTAR, Integer.parseInt(miktari));
                        contentValues.put(ContentProvider.PORSIYON, porsiyon);
                        contentValues.put(ContentProvider.KARB, karbo);
                        if (staticuri != null) {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                            byte[] bytes = outputStream.toByteArray();
                            contentValues.put(ContentProvider.IMAGE, bytes);
                            System.out.println("Foto seçtin");
                        } else {
                            System.out.println("Foto seçmedin");
                            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            contentValues.put(ContentProvider.IMAGE, byteArray);
                        }


                        getContentResolver().insert(ContentProvider.CONTENT_URI2, contentValues);
                        Toast.makeText(YemekEkle.this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent photo = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photo, 2);
            }

        }
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            staticuri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), staticuri);
                //System.out.println("bitmap: " + bitmap);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(YemekEkle.this, "Bir şeyler ters gitti", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }else {
            Toast.makeText(YemekEkle.this, "Fotoğraf seçmedin",Toast.LENGTH_LONG).show();
        }
    }

}