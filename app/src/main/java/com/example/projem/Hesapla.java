package com.example.projem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;

import static com.example.projem.Ayarlar.kdoranim;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class Hesapla extends AppCompatActivity {
    ListView liste;
    ImageButton kapat;
    TextView temizle, sonuc, doz;
    static ArrayList<String> reversead;
    static ArrayList<Integer> reversemiktar;
    static ArrayList<String> reverseporsiyon;
    static ArrayList<String> reversekarb;
    static ArrayList<Bitmap> reverseimg;
    private static final DecimalFormat df = new DecimalFormat("0.0");
    private static final DecimalFormat df2 = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hesapla);
        liste = findViewById(R.id.listview);
        sonuc = findViewById(R.id.sonuc);
        doz = findViewById(R.id.doz);
        kapat = findViewById(R.id.close);
        kapat.setOnClickListener(view -> finish());
        temizle = findViewById(R.id.txt_temizle);
        temizle = findViewById(R.id.txt_temizle);
        temizle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < reversead.size(); i++) {
                    String[] selectionArguments = {reversead.get(i)};
                    getContentResolver().delete(ContentProvider.CONTENT_URI3, "ogunad=?", selectionArguments);
                    Toast.makeText(getApplicationContext(), "Temizlendi.", Toast.LENGTH_SHORT).show();
                    liste.setAdapter(null);
                    sonuc.setText("0.0");
                    doz.setText("0.0");

                }

            }
        });

        oku();
        Hesapla.myAdapter adapter = new Hesapla.myAdapter(getApplicationContext(), reversead, reversemiktar, reverseporsiyon, reversekarb, reverseimg);
        liste.setAdapter(adapter);
        Hesap();

        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = Hesapla.this.getTheme();
                theme.resolveAttribute(R.attr.textrengi, typedValue, true);
                TypedArray arr =Hesapla.this.obtainStyledAttributes(typedValue.data, new int[]{
                        R.attr.textrengi});
                int primaryColor = arr.getColor(0, -1);
                AlertDialog.Builder builder = new AlertDialog.Builder(Hesapla.this,R.style.MyAlertDialogTheme);
                final View customLayout = getLayoutInflater().inflate(R.layout.newalertdialog, null);
                builder.setView(customLayout);
                TextView tv_title = new TextView(Hesapla.this);
                tv_title.setText("      Bu kaydı öğününüzden silmek\n      istiyor musunuz?");
                tv_title.setTextSize(18f);
                builder.setCustomTitle(tv_title);
                builder.setPositiveButton("SİL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        oku();
                        String[] selectionArguments = {reversead.get(position), String.valueOf(reversemiktar.get(position)),reverseporsiyon.get(position), String.valueOf(reversekarb.get(position))};
                        getContentResolver().delete(ContentProvider.CONTENT_URI3, "ogunad=? and ogunmiktar=? and ogunporsiyon=? and ogunkarb=?", selectionArguments);
                        Toast.makeText(getApplicationContext(), "Kayıt silindi.", Toast.LENGTH_SHORT).show();
                        liste.setAdapter(null);
                        oku();
                        Hesapla.myAdapter adapter=new Hesapla.myAdapter(getApplicationContext(), reversead, reversemiktar, reverseporsiyon,reversekarb,reverseimg);
                        liste.setAdapter(adapter);
                        Hesap();


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
                dialog.getWindow().setLayout(800, 500);
                TypedValue themedValue = new TypedValue();
                Hesapla.this.getTheme().resolveAttribute(R.attr.anatema, themedValue, true);
                Drawable mydrawable = AppCompatResources.getDrawable(Hesapla.this, themedValue.resourceId);
                dialog.getWindow().setBackgroundDrawable(mydrawable);

            }
        });



    }

    protected void Hesap() {
        oku();
        Hesapla.myAdapter adapter = new Hesapla.myAdapter(getApplicationContext(), reversead, reversemiktar, reverseporsiyon, reversekarb, reverseimg);
        liste.setAdapter(adapter);

        int yemeksay = liste.getCount();
        Double toplam = 0.0;
        for (int i = 0; i < yemeksay; i++) {
            int miktar=adapter.getMiktar(i);
            String karb = (String) liste.getItemAtPosition(i);
            Double doublekarb=Double.parseDouble(karb);
            Double carpim=doublekarb*miktar;
            String a = carpim.toString();
            toplam += Double.parseDouble(a);
        }

        sonuc.setText(df2.format(toplam));
        if (kdoranim == 15) {
            SharedPreferences karb = getSharedPreferences("checkbox1", MODE_PRIVATE);
            Float kdorani = karb.getFloat("oran", 15f);
            kdoranim = kdorani;

        }
        Double dozmiktar = toplam / kdoranim;
        System.out.println(kdoranim);
        doz.setText(df.format(dozmiktar));
    }


    @SuppressLint("Range")
    protected void oku() {
        final ArrayList<String> ogunadlist = new ArrayList<>();
        final ArrayList<Integer> ogunmiktarlist = new ArrayList<>();
        final ArrayList<String> ogunporsiyonlist = new ArrayList<>();
        final ArrayList<String> ogunkarblist = new ArrayList<>();
        final ArrayList<Bitmap> ogunimagelist = new ArrayList<>();
        Uri uri = Uri.parse("content://com.example.projem.ContentProvider/ogun");
        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ogunadlist.add(cursor.getString(cursor.getColumnIndex(ContentProvider.OGUNAD)));
                ogunmiktarlist.add(cursor.getInt(cursor.getColumnIndex(ContentProvider.OGUNMIKTAR)));
                ogunporsiyonlist.add(cursor.getString(cursor.getColumnIndex(ContentProvider.OGUNPORSIYON)));
                ogunkarblist.add(cursor.getString(cursor.getColumnIndex(ContentProvider.OGUNKARB)));
                byte[] imagebytes = cursor.getBlob(cursor.getColumnIndex(ContentProvider.OGUNIMAGE));
                Bitmap imagebitmap = BitmapFactory.decodeByteArray(imagebytes, 0, imagebytes.length);
                ogunimagelist.add(imagebitmap);


            }
        } else {
            System.out.println("Cursor null");
        }

        reversead = new ArrayList<>(ogunadlist);
        Collections.reverse(reversead);
        reversemiktar = new ArrayList<>(ogunmiktarlist);
        Collections.reverse(reversemiktar);
        reverseporsiyon = new ArrayList<>(ogunporsiyonlist);
        Collections.reverse(reverseporsiyon);
        reversekarb = new ArrayList<>(ogunkarblist);
        Collections.reverse(reversekarb);
        reverseimg = new ArrayList<>(ogunimagelist);
        Collections.reverse(reverseimg);
        for(String a:reversead){
            System.out.println(a);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        oku();
        Hesapla.myAdapter adapter = new Hesapla.myAdapter(getApplicationContext(), reversead, reversemiktar, reverseporsiyon, reversekarb, reverseimg);
        if (liste.getAdapter() == null) {
            liste.setAdapter(adapter);
        } else { //Already has an adapter
            liste.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            liste.invalidateViews();
            liste.refreshDrawableState();
        }


    }

    class myAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> ogunadlist;
        ArrayList<Integer> ogunmiktarlist;
        ArrayList<String> ogunporsiyonlist;
        ArrayList<String> ogunkarblist;
        ArrayList<Bitmap> ogunimagelist;
        private LayoutInflater inflater = null;


        public myAdapter(Context context, ArrayList<String> ogunadlist, ArrayList<Integer> ogunmiktarlist, ArrayList<String> ogunporsiyonlist, ArrayList<String> ogunkarblist, ArrayList<Bitmap> ogunimagelist) {
            this.context = context;
            this.ogunadlist = ogunadlist;
            this.ogunmiktarlist = ogunmiktarlist;
            this.ogunporsiyonlist = ogunporsiyonlist;
            this.ogunkarblist = ogunkarblist;
            this.ogunimagelist = ogunimagelist;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return ogunadlist.size();
        }

        public int getMiktar(int position){
            return ogunmiktarlist.get(position);
        }
        @Override
        public Object getItem(int position) {
            return ogunkarblist.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @SuppressLint({"SetTextI18n", "Range"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vi = view;

            if (vi == null)
                vi = inflater.inflate(R.layout.listviewogun, null);
            ImageView imageView = (ImageView) vi.findViewById(R.id.imageview);
            TextView text1 = (TextView) vi.findViewById(R.id.text1);
            TextView text2 = (TextView) vi.findViewById(R.id.text2);
            TextView text3 = (TextView) vi.findViewById(R.id.text3);

            Bitmap bmp = ogunimagelist.get(i);
            imageView.setImageBitmap(bmp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            boolean gecemi = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
            if (gecemi) {
                text1.setTextColor(Color.WHITE);
                text2.setTextColor(Color.WHITE);
                text3.setTextColor(Color.WHITE);
            }
            text1.setText(ogunadlist.get(i));
            text2.setText(ogunmiktarlist.get(i) + " " + ogunporsiyonlist.get(i));
            text3.setText("Birim karbonhidrat miktarı: " + ogunkarblist.get(i));

            return vi;
        }

    }
}