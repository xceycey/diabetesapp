package com.example.projem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;


public class KarbSayfa extends AppCompatActivity {
    BottomNavigationView bnv;
    EditText editText;
    ImageButton ara;
    ListView listView;
    TextView temizle;
    ArrayList<String> reversead;
    ArrayList<Integer> reversemiktar;
    ArrayList<String> reverseporsiyon;
    ArrayList<String> reversekarb;
    ArrayList<Bitmap> reverseimg;
    Button hesap;
    public static Bitmap bmp;

    ExtendedFloatingActionButton yemekekle;

    @SuppressLint({"ClickableViewAccessibility", "NonConstantResourceId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karb_sayfa);
        hesap = findViewById(R.id.hesapla);
        hesap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Hesapla.class));
            }
        });
        temizle = findViewById(R.id.txt_temizle);
        editText = findViewById(R.id.edttext);
        ara = findViewById(R.id.ara);
        yemekekle = findViewById(R.id.floatbtn_ekle);
        listView = findViewById(R.id.listview);
        listView.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        });

        final ArrayList<String> yemekadlist = new ArrayList<>();
        final ArrayList<Integer> miktarlist = new ArrayList<>();
        final ArrayList<String> porsiyonlist = new ArrayList<>();
        final ArrayList<String> karblist = new ArrayList<>();
        final ArrayList<Bitmap> imagelist = new ArrayList<>();
        oku(yemekadlist, miktarlist, porsiyonlist, karblist, imagelist);
        myAdapter adapter = new myAdapter(getApplicationContext(), reversead, reversemiktar, reverseporsiyon, reversekarb, reverseimg);
        if (listView.getAdapter() == null) { //Adapter not set yet.
            listView.setAdapter(adapter);
        } else { //Already has an adapter
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.invalidateViews();
            listView.refreshDrawableState();
        }

        //listView.setAdapter(new KarbSayfa.myAdapter(getApplicationContext(), yemekadlist, miktarlist, porsiyonlist,karblist,imagelist));

        yemekekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gonder = new Intent(getApplicationContext(), YemekEkle.class);
                gonder.putExtra("check", "yeni");
                startActivity(gonder);
            }
        });


        ara.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                String aranacak = editText.getText().toString();
                System.out.println(aranacak);
                final ArrayList<String> yemekadlist = new ArrayList<>();
                final ArrayList<Integer> miktarlist = new ArrayList<>();
                final ArrayList<String> porsiyonlist = new ArrayList<>();
                final ArrayList<String> karblist = new ArrayList<>();
                final ArrayList<Bitmap> imagelist = new ArrayList<>();
                if (aranacak.matches("")) {
                    oku(yemekadlist, miktarlist, porsiyonlist, karblist, imagelist);
                    myAdapter adapter = new myAdapter(getApplicationContext(), reversead, reversemiktar, reverseporsiyon, reversekarb, reverseimg);
                    if (listView.getAdapter() == null) { //Adapter not set yet.
                        listView.setAdapter(adapter);
                    } else { //Already has an adapter
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        listView.invalidateViews();
                        listView.refreshDrawableState();
                    }
                } else {
                    Uri uri = Uri.parse("content://com.example.projem.ContentProvider/yemek");
                    ContentResolver contentResolver = getContentResolver();
                    String[] selectionargs = {"%" + aranacak + "%"};
                    //içinde aranan geçecek şekilde düzenle
                    Cursor cursor = contentResolver.query(uri, null, "yemekadi like ?", selectionargs, null);
                    if (cursor != null && cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            yemekadlist.add(cursor.getString(cursor.getColumnIndex(ContentProvider.AD)));
                            miktarlist.add(cursor.getInt(cursor.getColumnIndex(ContentProvider.MIKTAR)));
                            porsiyonlist.add(cursor.getString(cursor.getColumnIndex(ContentProvider.PORSIYON)));
                            karblist.add(cursor.getString(cursor.getColumnIndex(ContentProvider.KARB)));
                            byte[] imagebytes = cursor.getBlob(cursor.getColumnIndex(ContentProvider.IMAGE));

                            Bitmap imagebitmap = BitmapFactory.decodeByteArray(imagebytes, 0, imagebytes.length);
                            imagelist.add(imagebitmap);


                        }
                    } else {
                        System.out.println("Cursor null");
                        Toast.makeText(getApplicationContext(), "Kayıt bulunamadı.", Toast.LENGTH_SHORT).show();
                    }
                    ArrayList<String> newad = new ArrayList<>(yemekadlist);
                    Collections.reverse(newad);
                    ArrayList<Integer> newmiktar = new ArrayList<>(miktarlist);
                    Collections.reverse(newmiktar);
                    ArrayList<String> newporsiyon = new ArrayList<>(porsiyonlist);
                    Collections.reverse(newporsiyon);
                    ArrayList<String> newkarb = new ArrayList<>(karblist);
                    Collections.reverse(newkarb);
                    ArrayList<Bitmap> newimg = new ArrayList<>(imagelist);
                    Collections.reverse(newimg);

                    myAdapter adapter2 = new myAdapter(getApplicationContext(), newad, newmiktar, newporsiyon, newkarb, newimg);
                    listView.setAdapter(adapter2);
                }




                /*final ArrayList<Foods> foodlist = new ArrayList<>();

                String query = editText.getText().toString();

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();

                Request request = new Request.Builder()
                        .url("https://trackapi.nutritionix.com/v2/search/instant?query="+query+"&detailed=true")
                        .get()
                        .addHeader("x-app-id", "ff0ccea8")
                        .addHeader("x-app-key", "605660a17994344157a78f518a111eda")
                        .addHeader("x-remote-user-id","7a43c5ba-50e7-44fb-b2b4-bbd1b7d22632")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String mresponse = response.body().string();
                            KarbSayfa.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject obj=new JSONObject(mresponse);
                                        JSONArray arr=obj.getJSONArray("common");
                                        for(int i=0;i<arr.length();i++){
                                            JSONObject object = arr.getJSONObject(i);
                                            System.out.println(object);
                                            String name=object.getString("food_name");
                                            String gram=object.getString("serving_weight_grams");
                                            String qty=object.getString("serving_qty");
                                            String unit=object.getString("serving_unit");
                                            JSONObject objphoto=object.getJSONObject("photo");
                                            String photo=objphoto.getString("thumb");
                                            JSONArray nutr=object.getJSONArray("full_nutrients");
                                            String carb="";
                                            for(int j=0;j<nutr.length();j++){
                                                JSONObject objnutr=nutr.getJSONObject(j);
                                                String id=objnutr.getString("attr_id");
                                                if(id.equals("205")){
                                                    carb=objnutr.getString("value");
                                                }
                                            }
                                            System.out.println(name+" "+carb+" "+qty+" "+unit+" "+gram);
                                            foodlist.add(new Foods(name,carb,unit,qty,photo,gram));

                                        }




                                        JSONObject obj1=new JSONObject(mresponse);
                                        JSONArray arr1=obj1.getJSONArray("branded");
                                        for(int i=0;i<arr1.length();i++){
                                            JSONObject object = arr1.getJSONObject(i);
                                            System.out.println(object);
                                            String name=object.getString("food_name");
                                            String gram=object.getString("serving_weight_grams");
                                            String qty=object.getString("serving_qty");
                                            String unit=object.getString("serving_unit");
                                            String marka=object.getString("brand_name");
                                            JSONObject objphoto=object.getJSONObject("photo");
                                            String photo=objphoto.getString("thumb");
                                            JSONArray nutr=object.getJSONArray("full_nutrients");
                                            String carb="";
                                            for(int j=0;j<nutr.length();j++){
                                                JSONObject objnutr=nutr.getJSONObject(j);
                                                String id=objnutr.getString("attr_id");
                                                if(id.equals("205")){
                                                    carb=objnutr.getString("value");
                                                }
                                            }
                                            //System.out.println(name+" "+carb+" "+qty+" "+unit+" "+gram);
                                            foodlist.add(new Foods(name+" ("+marka+")",carb,unit,qty,photo,gram));

                                        }








                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }


                                }

                            });
                        }
                        if(!response.isSuccessful()){
                            int responseCode = response.code();
                            System.out.println(responseCode);
                        }
                    }
                });

                adapter=new FoodAdapter(foodlist,getApplicationContext());
                LinearLayoutManager manager=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Foods foods, int position) {
                        //
                    }
                });
*/
            }

        });
        temizle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                final ArrayList<String> yemekadlist = new ArrayList<>();
                final ArrayList<Integer> miktarlist = new ArrayList<>();
                final ArrayList<String> porsiyonlist = new ArrayList<>();
                final ArrayList<String> karblist = new ArrayList<>();
                final ArrayList<Bitmap> imagelist = new ArrayList<>();
                oku(yemekadlist, miktarlist, porsiyonlist, karblist, imagelist);
                myAdapter adapter = new myAdapter(getApplicationContext(), reversead, reversemiktar, reverseporsiyon, reversekarb, reverseimg);
                if (listView.getAdapter() == null) { //Adapter not set yet.
                    listView.setAdapter(adapter);
                } else { //Already has an adapter
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                }
            }
        });

        bnv = findViewById(R.id.navigation);
        bnv.setSelectedItemId(R.id.nav_rest);
        bnv.setOnItemSelectedListener(item ->

        {
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
                    return true;
                case R.id.nav_person:
                    startActivity(new Intent(getApplicationContext(), Ayarlar.class));
                    overridePendingTransition(0, 0);
                    return true;

            }
            return false;
        });


    }

    @SuppressLint("Range")
    protected void oku(ArrayList<String> yemekadlist, ArrayList<Integer> miktarlist, ArrayList<String> porsiyonlist, ArrayList<String> karblist, ArrayList<Bitmap> imagelist) {
        Uri uri = Uri.parse("content://com.example.projem.ContentProvider/yemek");
        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                yemekadlist.add(cursor.getString(cursor.getColumnIndex(ContentProvider.AD)));
                miktarlist.add(cursor.getInt(cursor.getColumnIndex(ContentProvider.MIKTAR)));
                porsiyonlist.add(cursor.getString(cursor.getColumnIndex(ContentProvider.PORSIYON)));
                karblist.add(cursor.getString(cursor.getColumnIndex(ContentProvider.KARB)));
                byte[] imagebytes = cursor.getBlob(cursor.getColumnIndex(ContentProvider.IMAGE));

                Bitmap imagebitmap = BitmapFactory.decodeByteArray(imagebytes, 0, imagebytes.length);
                imagelist.add(imagebitmap);


            }
        } else {
            System.out.println("Cursor null");
        }

        reversead = new ArrayList<>(yemekadlist);
        Collections.reverse(reversead);
        reversemiktar = new ArrayList<>(miktarlist);
        Collections.reverse(reversemiktar);
        reverseporsiyon = new ArrayList<>(porsiyonlist);
        Collections.reverse(reverseporsiyon);
        reversekarb = new ArrayList<>(karblist);
        Collections.reverse(reversekarb);
        reverseimg = new ArrayList<>(imagelist);
        Collections.reverse(reverseimg);


    }


    @SuppressLint("Range")
    @Override
    protected void onResume() {
        super.onResume();

        final ArrayList<String> yemekadlist = new ArrayList<>();
        final ArrayList<Integer> miktarlist = new ArrayList<>();
        final ArrayList<String> porsiyonlist = new ArrayList<>();
        final ArrayList<String> karblist = new ArrayList<>();
        final ArrayList<Bitmap> imagelist = new ArrayList<>();
        oku(yemekadlist, miktarlist, porsiyonlist, karblist, imagelist);
        myAdapter adapter = new myAdapter(getApplicationContext(), reversead, reversemiktar, reverseporsiyon, reversekarb, reverseimg);
        if (listView.getAdapter() == null) { //Adapter not set yet.
            listView.setAdapter(adapter);
        } else { //Already has an adapter
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.invalidateViews();
            listView.refreshDrawableState();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ArrayList<String> A = (ArrayList<String>) listView.getItemAtPosition(position);
                String ad = A.get(0);
                int miktar = Integer.parseInt(A.get(1));
                String porsiyon = A.get(2);
                String karb = (A.get(3));
                Intent a = new Intent(getApplicationContext(), YemekEkle.class);
                a.putExtra("check", "eski");
                a.putExtra("yemek", ad);
                a.putExtra("miktar", miktar);
                a.putExtra("porsiyon", porsiyon);
                a.putExtra("karb", karb);
                startActivity(a);

            }
        });

    }

    static class myAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> yemekadlist;
        ArrayList<Integer> miktarlist;
        ArrayList<String> porsiyonlist;
        ArrayList<String> karblist;
        ArrayList<Bitmap> imagelist;
        private LayoutInflater inflater = null;


        public myAdapter(Context context, ArrayList<String> yemekadlist, ArrayList<Integer> miktarlist, ArrayList<String> porsiyonlist, ArrayList<String> karblist, ArrayList<Bitmap> imagelist) {
            this.context = context;
            this.yemekadlist = yemekadlist;
            this.miktarlist = miktarlist;
            this.porsiyonlist = porsiyonlist;
            this.karblist = karblist;
            this.imagelist = imagelist;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return yemekadlist.size();
        }

        @Override
        public ArrayList<String> getItem(int position) {
            ArrayList<String> sonuc = new ArrayList<>();
            sonuc.add(yemekadlist.get(position));
            sonuc.add(String.valueOf(miktarlist.get(position)));
            sonuc.add(porsiyonlist.get(position));
            sonuc.add(String.valueOf(karblist.get(position)));
            bmp = imagelist.get(position);
            return sonuc;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vi = view;

            if (vi == null)
                vi = inflater.inflate(R.layout.listviewkarb, null);
            ImageView imageView = (ImageView) vi.findViewById(R.id.imageview);
            TextView text1 = (TextView) vi.findViewById(R.id.text1);
            TextView text2 = (TextView) vi.findViewById(R.id.text2);
            TextView text3 = (TextView) vi.findViewById(R.id.text3);
            Bitmap bmp = imagelist.get(i);
            imageView.setImageBitmap(bmp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            boolean gecemi = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
            if (gecemi) {
                text1.setTextColor(Color.WHITE);
                text2.setTextColor(Color.WHITE);
                text3.setTextColor(Color.WHITE);
            }
            text1.setText(yemekadlist.get(i));
            text2.setText(miktarlist.get(i) + " " + porsiyonlist.get(i));
            text3.setText("Karbonhidrat miktarı: " + karblist.get(i));

            return vi;
        }

    }
}