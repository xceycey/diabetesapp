package com.example.projem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.protobuf.Value;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class SekerTakipSayfa extends AppCompatActivity {
    ExtendedFloatingActionButton ekle;
    ListView listView;
    LineChart chart;
    BottomNavigationView bnv;
    private static long timestampStart = 0;

    TextInputLayout til;
    String item = "";
    AutoCompleteTextView act;
    String[] secenekler = {"Tüm Türler", "Kahvaltıdan Önce", "Kahvaltıdan Sonra", "Öğle Yemeğinden\nÖnce",
            "Öğle Yemeğinden\nSonra", "Akşam Yemeğinden\nÖnce", "Akşam Yemeğinden\nSonra", "Uyumadan Önce"};
    ArrayAdapter<String> arrayadp;

    @SuppressLint({"Range", "ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seker_takip_sayfa);
        Uri uri = Uri.parse("content://com.example.projem.ContentProvider/seker");
        ContentResolver contentResolver = getContentResolver();
        String order = "tarih" + " desc, " + "saat " + "desc";

        til = findViewById(R.id.menu);
        act = findViewById(R.id.autoCompleteTextView);
        listView = findViewById(R.id.listview);

        final ArrayList<String> degerList = new ArrayList<>();
        final ArrayList<String> tarihList = new ArrayList<>();
        final ArrayList<String> tarihvesaatList = new ArrayList<>();
        final ArrayList<String> saatList = new ArrayList<>();
        final ArrayList<String> durumList = new ArrayList<>();
        final ArrayList<String> sonuc = new ArrayList<>();

        //ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.listview_layout, sonuc);
        //listView.setAdapter(arrayAdapter);
        listView.setAdapter(new yourAdapter(this, degerList, tarihvesaatList, durumList));

        Cursor cursor = contentResolver.query(uri, null, null, null, order);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                degerList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.DEGER)));
                tarihList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.TARIH)));
                saatList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.SAAT)));
                durumList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.DURUM)));
                tarihvesaatList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.TARIH)) + " " +
                        cursor.getString(cursor.getColumnIndex(ContentProvider.SAAT)));
                sonuc.add(cursor.getString(cursor.getColumnIndex(ContentProvider.DEGER)) + " "
                        + cursor.getString(cursor.getColumnIndex(ContentProvider.TARIH)) + " " +
                        cursor.getString(cursor.getColumnIndex(ContentProvider.SAAT)) + " " +
                        cursor.getString(cursor.getColumnIndex(ContentProvider.DURUM)));
                //arrayAdapter.notifyDataSetChanged();
            }
        } else {
            System.out.println("Cursor null");
        }

        arrayadp = new ArrayAdapter<>(getApplicationContext(), R.layout.item_autocomplete, R.id.tvCustom, secenekler);
        act.setAdapter(arrayadp);

        act.setOnItemClickListener((parent, arg1, pos, id) -> {
            item = (String) parent.getItemAtPosition(pos);

            switch (item) {
                case "Tüm Türler":
                    listView.setAdapter(new yourAdapter(this, degerList, tarihvesaatList, durumList));
                    list(degerList,tarihList,saatList,durumList);
                    break;
                case "Kahvaltıdan Önce":
                    final ArrayList<String> kahondeger = new ArrayList<>();
                    final ArrayList<String> kahontarihsaat = new ArrayList<>();
                    final ArrayList<String> kahondurum = new ArrayList<>();
                    final ArrayList<String> kahontarih = new ArrayList<>();
                    final ArrayList<String> kahonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, kahondeger, kahontarihsaat, kahondurum));

                    Cursor cursor2 = contentResolver.query(uri, null, null, null, order);
                    if (cursor2 != null && cursor2.getCount() > 0) {
                        while (cursor2.moveToNext()) {
                            if (cursor2.getString(cursor2.getColumnIndex(ContentProvider.DURUM)).equals("Kahvaltıdan Önce")) {
                                kahondeger.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.DEGER)));
                                kahontarih.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.TARIH)));
                                kahonsaat.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.SAAT)));
                                kahontarihsaat.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor2.getString(cursor2.getColumnIndex(ContentProvider.SAAT)));
                                kahondurum.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.DURUM)));
                            }

                        }
                    }
                    list(kahondeger,kahontarih,kahonsaat,kahondurum);
                    break;
                case "Kahvaltıdan Sonra":
                    final ArrayList<String> kahsondeger = new ArrayList<>();
                    final ArrayList<String> kahsontarihsaat = new ArrayList<>();
                    final ArrayList<String> kahsondurum = new ArrayList<>();
                    final ArrayList<String> kahsontarih = new ArrayList<>();
                    final ArrayList<String> kahsonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, kahsondeger, kahsontarihsaat, kahsondurum));
                    Cursor cursor3 = contentResolver.query(uri, null, null, null, order);
                    if (cursor3 != null && cursor3.getCount() > 0) {
                        while (cursor3.moveToNext()) {
                            if (cursor3.getString(cursor3.getColumnIndex(ContentProvider.DURUM)).equals("Kahvaltıdan Sonra")) {
                                kahsondeger.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.DEGER)));
                                kahsontarih.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.TARIH)));
                                kahsonsaat.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.SAAT)));
                                kahsontarihsaat.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor3.getString(cursor3.getColumnIndex(ContentProvider.SAAT)));
                                kahsondurum.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(kahsondeger,kahsontarih,kahsonsaat,kahsondurum);
                    break;
                case "Öğle Yemeğinden\nÖnce":
                    final ArrayList<String> ogondeger = new ArrayList<>();
                    final ArrayList<String> ogontarihsaat = new ArrayList<>();
                    final ArrayList<String> ogondurum = new ArrayList<>();
                    final ArrayList<String> ogontarih = new ArrayList<>();
                    final ArrayList<String> ogonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, ogondeger, ogontarihsaat, ogondurum));
                    Cursor cursor4 = contentResolver.query(uri, null, null, null, order);
                    if (cursor4 != null && cursor4.getCount() > 0) {
                        while (cursor4.moveToNext()) {
                            if (cursor4.getString(cursor4.getColumnIndex(ContentProvider.DURUM)).equals("Öğle Yemeğinden Önce")) {
                                ogondeger.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.DEGER)));
                                ogontarih.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.TARIH)));
                                ogonsaat.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.SAAT)));
                                ogontarihsaat.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor4.getString(cursor4.getColumnIndex(ContentProvider.SAAT)));
                                ogondurum.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(ogondeger,ogontarih,ogonsaat,ogondurum);
                    break;
                case "Öğle Yemeğinden\nSonra":
                    final ArrayList<String> ogsondeger = new ArrayList<>();
                    final ArrayList<String> ogsontarihsaat = new ArrayList<>();
                    final ArrayList<String> ogsondurum = new ArrayList<>();
                    final ArrayList<String> ogsontarih = new ArrayList<>();
                    final ArrayList<String> ogsonsaat = new ArrayList<>();


                    listView.setAdapter(new yourAdapter(this, ogsondeger, ogsontarihsaat, ogsondurum));
                    Cursor cursor5 = contentResolver.query(uri, null, null, null, order);
                    if (cursor5 != null && cursor5.getCount() > 0) {
                        while (cursor5.moveToNext()) {
                            if (cursor5.getString(cursor5.getColumnIndex(ContentProvider.DURUM)).equals("Öğle Yemeğinden Sonra")) {
                                ogsondeger.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.DEGER)));
                                ogsontarih.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.TARIH)));
                                ogsonsaat.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.SAAT)));
                                ogsontarihsaat.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor5.getString(cursor5.getColumnIndex(ContentProvider.SAAT)));
                                ogsondurum.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(ogsondeger,ogsontarih,ogsonsaat,ogsondurum);
                    break;
                case "Akşam Yemeğinden\nÖnce":
                    final ArrayList<String> akondeger = new ArrayList<>();
                    final ArrayList<String> akontarihsaat = new ArrayList<>();
                    final ArrayList<String> akondurum = new ArrayList<>();
                    final ArrayList<String> akontarih = new ArrayList<>();
                    final ArrayList<String> akonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, akondeger, akontarihsaat, akondurum));
                    Cursor cursor6 = contentResolver.query(uri, null, null, null, order);
                    if (cursor6 != null && cursor6.getCount() > 0) {
                        while (cursor6.moveToNext()) {
                            if (cursor6.getString(cursor6.getColumnIndex(ContentProvider.DURUM)).equals("Akşam Yemeğinden Önce")) {
                                akondeger.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.DEGER)));
                                akontarih.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.TARIH)));
                                akonsaat.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.SAAT)));
                                akontarihsaat.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor6.getString(cursor6.getColumnIndex(ContentProvider.SAAT)));
                                akondurum.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(akondeger,akontarih,akonsaat,akondurum);
                    break;
                case "Akşam Yemeğinden\nSonra":
                    final ArrayList<String> aksondeger = new ArrayList<>();
                    final ArrayList<String> aksontarihsaat = new ArrayList<>();
                    final ArrayList<String> aksondurum = new ArrayList<>();
                    final ArrayList<String> aksontarih= new ArrayList<>();
                    final ArrayList<String> aksonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, aksondeger, aksontarihsaat, aksondurum));
                    Cursor cursor7 = contentResolver.query(uri, null, null, null, order);
                    if (cursor7 != null && cursor7.getCount() > 0) {
                        while (cursor7.moveToNext()) {
                            if (cursor7.getString(cursor7.getColumnIndex(ContentProvider.DURUM)).equals("Akşam Yemeğinden Sonra")) {
                                aksondeger.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.DEGER)));
                                aksontarih.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.TARIH)));
                                aksonsaat.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.SAAT)));
                                aksontarihsaat.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor7.getString(cursor7.getColumnIndex(ContentProvider.SAAT)));
                                aksondurum.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(aksondeger,aksontarih,aksonsaat,aksondurum);
                    break;
                case "Uyumadan Önce":
                    final ArrayList<String> uyondeger = new ArrayList<>();
                    final ArrayList<String> uyontarihsaat = new ArrayList<>();
                    final ArrayList<String> uyondurum = new ArrayList<>();
                    final ArrayList<String> uyontarih = new ArrayList<>();
                    final ArrayList<String> uyonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, uyondeger, uyontarihsaat, uyondurum));
                    Cursor cursor8 = contentResolver.query(uri, null, null, null, order);
                    if (cursor8 != null && cursor8.getCount() > 0) {
                        while (cursor8.moveToNext()) {
                            if (cursor8.getString(cursor8.getColumnIndex(ContentProvider.DURUM)).equals("Uyumadan Önce")) {
                                uyondeger.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.DEGER)));
                                uyontarih.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.TARIH)));
                                uyonsaat.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.SAAT)));
                                uyontarihsaat.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor8.getString(cursor8.getColumnIndex(ContentProvider.SAAT)));
                                uyondurum.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(uyondeger,uyontarih,uyonsaat,uyondurum);
                    break;

                default:
                    break;

            }

            // Toast.makeText(YeniKayit.this, "Selected Item: "+item, Toast.LENGTH_SHORT).show();
        });

        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
            }
        });

        ekle = findViewById(R.id.floatbtn_ekle);
        ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gonder = new Intent(getApplicationContext(), YeniKayit.class);
                gonder.putExtra("check", "yeni");
                startActivity(gonder);
            }
        });
        bnv = findViewById(R.id.navigation);
        bnv.setSelectedItemId(R.id.nav_blood);
        bnv.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_blood:
                    return true;
                case R.id.nav_rest:
                    startActivity(new Intent(getApplicationContext(), KarbSayfa.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_person:
                    startActivity(new Intent(getApplicationContext(), Ayarlar.class));
                    overridePendingTransition(0, 0);
                    return true;

            }
            return false;
        });


        chart = findViewById(R.id.chart);

        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setScaleXEnabled(true);
        chart.setScaleYEnabled(true);
        chart.setPinchZoom(true);


        ValueFormatter formatter = new ValueFormatter() {
            private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM\nHH:mm");

            @Override
            public String getFormattedValue(float value) {
                long a = (long) value;
                String dateString = simpleDateFormat.format(a * 1000 + timestampStart);
                //System.out.println(String.format("Date: %s", dateString));
                return String.format(dateString);
            }
        };

        boolean gecemi= AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES;

        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();
            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 5f, 0f);
            xAxis.setGranularity(1f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawLabels(true);
            xAxis.setValueFormatter(formatter);
            //xAxis.setSpaceMin(5f);

            if(gecemi){
                xAxis.setTextColor(Color.WHITE);
                System.out.println("xAxis beyaz");
            }else{
                xAxis.setTextColor(Color.BLACK);
                System.out.println("xAxis siyah");
            }
            //xAxis.setLabelCount(5);

        }
        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(350f);
            yAxis.setAxisMinimum(30f);
            if(gecemi){
                yAxis.setTextColor(Color.WHITE);
            }else{
                yAxis.setTextColor(Color.BLACK);
            }

        }
        {   // // Create Limit Lines // //
            /*LimitLine llXAxis = new LimitLine(9f, "Index 10");
            llXAxis.setLineWidth(4f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);*/
            //xAxis.addLimitLine(llXAxis);

            LimitLine ll1 = new LimitLine(180f, "Hiperglisemi");
            ll1.setLineWidth(1f);
            ll1.enableDashedLine(10f, 0f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
            ll1.setTextSize(10f);
            if(gecemi){
                ll1.setTextColor(Color.WHITE);
            }else{
                ll1.setTextColor(Color.BLACK);
            }


            LimitLine ll2 = new LimitLine(70f, "Hipoglisemi");
            ll2.setLineWidth(1f);
            ll2.enableDashedLine(10f, 0f, 0f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);
            if(gecemi){
                ll2.setTextColor(Color.WHITE);
            }else{
                ll2.setTextColor(Color.BLACK);
            }

            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);
            // add limit lines
            yAxis.addLimitLine(ll1);
            yAxis.addLimitLine(ll2);

            ArrayList<String> reversedeger = new ArrayList<>(degerList);
            Collections.reverse(reversedeger);
            ArrayList<String> reversetarih = new ArrayList<>(tarihList);
            Collections.reverse(reversetarih);
            ArrayList<String> reversesaat = new ArrayList<>(saatList);
            Collections.reverse(reversesaat);
            if (reversedeger.size() > 0) {
                setData(reversedeger.size(), reversetarih, reversedeger, reversesaat);
            }
            chart.setVisibleXRangeMaximum(80000f);


            Legend l = chart.getLegend();

            l.setForm(Legend.LegendForm.CIRCLE);
            l.setTextSize(11f);
            if(gecemi){
                l.setTextColor(Color.WHITE);
            }else{
                l.setTextColor(Color.BLACK);
            }
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setYOffset(2f);

            chart.setVisibleXRangeMaximum(55000f);
            chart.setViewPortOffsets(80, 20, 50, 120);

        }

        list(degerList,tarihList,saatList,durumList);


    }

    private void list(ArrayList<String> degerList,ArrayList<String> tarihList,ArrayList<String> saatList,ArrayList<String> durumList){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent a = new Intent(getApplicationContext(), YeniKayit.class);
                a.putExtra("check", "eski");
                a.putExtra("deger", degerList.get(position));
                a.putExtra("tarih", tarihList.get(position));
                a.putExtra("saat", saatList.get(position));
                a.putExtra("durum", durumList.get(position));
                System.out.println("deneme");
                startActivity(a);
            }
        });
    }

    private void setData(int count, ArrayList<String> tarihList, ArrayList<String> degerList, ArrayList<String> saatList) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            float val = Float.parseFloat(degerList.get(i));
            String tarih = tarihList.get(i);
            String saat = saatList.get(i);
            String strdate = tarih + "/" + saat;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd/HH:mm", Locale.getDefault());
            long xnew = 0;
            try {
                Date date = format.parse(strdate);
                long xold = date.getTime();
                if (timestampStart == 0) {
                    timestampStart = xold;
                }

                xnew = (xold - timestampStart);
                System.out.println(tarih + " " + saat + " " + xold + " " + val);
                //System.out.println(xnew+" "+val);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            values.add(new Entry((xnew / 1000f), val));
        }

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);

            ValueFormatter valueFormatter=new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf(Math.round(value));
                }
            };
            set1.setValueFormatter(valueFormatter);
            chart.animateX(1000);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();


            //chart.setVisibleXRangeMaximum(5); // allow 5 values to be displayed
        } else {
            boolean gecemi= AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES;
            set1 = new LineDataSet(values, "Kan şekeri (mg/dL)");
            set1.setDrawIcons(false);
            //set1.enableDashedLine(10f, 0f, 0f);
            if (gecemi){
                set1.setColor(Color.WHITE);
                set1.setValueTextColor(Color.WHITE);
                set1.setCircleColor(Color.WHITE);
            }else{
                set1.setColor(Color.BLACK);
                set1.setValueTextColor(Color.BLACK);
                set1.setCircleColor(Color.BLACK);
            }



            set1.setLineWidth(1f);
            set1.setCircleRadius(5f);
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

            set1.setDrawCircleHole(true);
            set1.setFormLineWidth(1f);
            //set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setValueTextSize(9f);
            //set1.enableDashedHighlightLine(10f, 5f, 0f);
            //set1.setDrawFilled(false);
            set1.setDrawHighlightIndicators(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
            chart.getXAxis().setLabelCount(5);
            chart.setVisibleXRangeMaximum(100000f);
            int index = data.getEntryCount();
            if (degerList.size() > 0) {
                chart.moveViewTo(data.getXMax(), Integer.parseInt(degerList.get(index - 1)), chart.getAxisLeft().getAxisDependency());
            }

        }
    }


    @SuppressLint("Range")
    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = Uri.parse("content://com.example.projem.ContentProvider/seker");
        ContentResolver contentResolver = getContentResolver();
        String order = "tarih" + " desc, " + "saat " + "desc";
        listView = findViewById(R.id.listview);
        til = findViewById(R.id.menu);
        act = findViewById(R.id.autoCompleteTextView);
        act.setText("Tüm Türler");

        final ArrayList<String> degerList = new ArrayList<>();
        final ArrayList<String> tarihList = new ArrayList<>();
        final ArrayList<String> tarihvesaatList = new ArrayList<>();
        final ArrayList<String> saatList = new ArrayList<>();
        final ArrayList<String> durumList = new ArrayList<>();
        final ArrayList<String> sonuc = new ArrayList<>();

        listView.setAdapter(new yourAdapter(this, degerList, tarihvesaatList, durumList));

        Cursor cursor = contentResolver.query(uri, null, null, null, order);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                degerList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.DEGER)));
                tarihList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.TARIH)));
                saatList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.SAAT)));
                durumList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.DURUM)));
                tarihvesaatList.add(cursor.getString(cursor.getColumnIndex(ContentProvider.TARIH)) + " " +
                        cursor.getString(cursor.getColumnIndex(ContentProvider.SAAT)));
                sonuc.add(cursor.getString(cursor.getColumnIndex(ContentProvider.DEGER)) + " "
                        + cursor.getString(cursor.getColumnIndex(ContentProvider.TARIH)) + " " +
                        cursor.getString(cursor.getColumnIndex(ContentProvider.SAAT)) + " " +
                        cursor.getString(cursor.getColumnIndex(ContentProvider.DURUM)));
            }
        } else {
            System.out.println("Cursor null");
        }


        arrayadp = new ArrayAdapter<>(getApplicationContext(), R.layout.item_autocomplete, R.id.tvCustom, secenekler);
        act.setAdapter(arrayadp);

        act.setOnItemClickListener((parent, arg1, pos, id) -> {
            item = (String) parent.getItemAtPosition(pos);

            switch (item) {
                case "Tüm Türler":
                    listView.setAdapter(new yourAdapter(this, degerList, tarihvesaatList, durumList));
                    list(degerList,tarihList,saatList,durumList);
                    break;
                case "Kahvaltıdan Önce":
                    final ArrayList<String> kahondeger = new ArrayList<>();
                    final ArrayList<String> kahontarihsaat = new ArrayList<>();
                    final ArrayList<String> kahondurum = new ArrayList<>();
                    final ArrayList<String> kahontarih = new ArrayList<>();
                    final ArrayList<String> kahonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, kahondeger, kahontarihsaat, kahondurum));

                    Cursor cursor2 = contentResolver.query(uri, null, null, null, order);
                    if (cursor2 != null && cursor2.getCount() > 0) {
                        while (cursor2.moveToNext()) {
                            if (cursor2.getString(cursor2.getColumnIndex(ContentProvider.DURUM)).equals("Kahvaltıdan Önce")) {
                                kahondeger.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.DEGER)));
                                kahontarih.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.TARIH)));
                                kahonsaat.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.SAAT)));
                                kahontarihsaat.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor2.getString(cursor2.getColumnIndex(ContentProvider.SAAT)));
                                kahondurum.add(cursor2.getString(cursor2.getColumnIndex(ContentProvider.DURUM)));
                            }

                        }
                    }
                    list(kahondeger,kahontarih,kahonsaat,kahondurum);
                    break;
                case "Kahvaltıdan Sonra":
                    final ArrayList<String> kahsondeger = new ArrayList<>();
                    final ArrayList<String> kahsontarihsaat = new ArrayList<>();
                    final ArrayList<String> kahsondurum = new ArrayList<>();
                    final ArrayList<String> kahsontarih = new ArrayList<>();
                    final ArrayList<String> kahsonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, kahsondeger, kahsontarihsaat, kahsondurum));
                    Cursor cursor3 = contentResolver.query(uri, null, null, null, order);
                    if (cursor3 != null && cursor3.getCount() > 0) {
                        while (cursor3.moveToNext()) {
                            if (cursor3.getString(cursor3.getColumnIndex(ContentProvider.DURUM)).equals("Kahvaltıdan Sonra")) {
                                kahsondeger.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.DEGER)));
                                kahsontarih.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.TARIH)));
                                kahsonsaat.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.SAAT)));
                                kahsontarihsaat.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor3.getString(cursor3.getColumnIndex(ContentProvider.SAAT)));
                                kahsondurum.add(cursor3.getString(cursor3.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(kahsondeger,kahsontarih,kahsonsaat,kahsondurum);
                    break;
                case "Öğle Yemeğinden\nÖnce":
                    final ArrayList<String> ogondeger = new ArrayList<>();
                    final ArrayList<String> ogontarihsaat = new ArrayList<>();
                    final ArrayList<String> ogondurum = new ArrayList<>();
                    final ArrayList<String> ogontarih = new ArrayList<>();
                    final ArrayList<String> ogonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, ogondeger, ogontarihsaat, ogondurum));
                    Cursor cursor4 = contentResolver.query(uri, null, null, null, order);
                    if (cursor4 != null && cursor4.getCount() > 0) {
                        while (cursor4.moveToNext()) {
                            if (cursor4.getString(cursor4.getColumnIndex(ContentProvider.DURUM)).equals("Öğle Yemeğinden Önce")) {
                                ogondeger.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.DEGER)));
                                ogontarih.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.TARIH)));
                                ogonsaat.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.SAAT)));
                                ogontarihsaat.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor4.getString(cursor4.getColumnIndex(ContentProvider.SAAT)));
                                ogondurum.add(cursor4.getString(cursor4.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(ogondeger,ogontarih,ogonsaat,ogondurum);
                    break;
                case "Öğle Yemeğinden\nSonra":
                    final ArrayList<String> ogsondeger = new ArrayList<>();
                    final ArrayList<String> ogsontarihsaat = new ArrayList<>();
                    final ArrayList<String> ogsondurum = new ArrayList<>();
                    final ArrayList<String> ogsontarih = new ArrayList<>();
                    final ArrayList<String> ogsonsaat = new ArrayList<>();


                    listView.setAdapter(new yourAdapter(this, ogsondeger, ogsontarihsaat, ogsondurum));
                    Cursor cursor5 = contentResolver.query(uri, null, null, null, order);
                    if (cursor5 != null && cursor5.getCount() > 0) {
                        while (cursor5.moveToNext()) {
                            if (cursor5.getString(cursor5.getColumnIndex(ContentProvider.DURUM)).equals("Öğle Yemeğinden Sonra")) {
                                ogsondeger.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.DEGER)));
                                ogsontarih.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.TARIH)));
                                ogsonsaat.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.SAAT)));
                                ogsontarihsaat.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor5.getString(cursor5.getColumnIndex(ContentProvider.SAAT)));
                                ogsondurum.add(cursor5.getString(cursor5.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(ogsondeger,ogsontarih,ogsonsaat,ogsondurum);
                    break;
                case "Akşam Yemeğinden\nÖnce":
                    final ArrayList<String> akondeger = new ArrayList<>();
                    final ArrayList<String> akontarihsaat = new ArrayList<>();
                    final ArrayList<String> akondurum = new ArrayList<>();
                    final ArrayList<String> akontarih = new ArrayList<>();
                    final ArrayList<String> akonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, akondeger, akontarihsaat, akondurum));
                    Cursor cursor6 = contentResolver.query(uri, null, null, null, order);
                    if (cursor6 != null && cursor6.getCount() > 0) {
                        while (cursor6.moveToNext()) {
                            if (cursor6.getString(cursor6.getColumnIndex(ContentProvider.DURUM)).equals("Akşam Yemeğinden Önce")) {
                                akondeger.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.DEGER)));
                                akontarih.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.TARIH)));
                                akonsaat.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.SAAT)));
                                akontarihsaat.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor6.getString(cursor6.getColumnIndex(ContentProvider.SAAT)));
                                akondurum.add(cursor6.getString(cursor6.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(akondeger,akontarih,akonsaat,akondurum);
                    break;
                case "Akşam Yemeğinden\nSonra":
                    final ArrayList<String> aksondeger = new ArrayList<>();
                    final ArrayList<String> aksontarihsaat = new ArrayList<>();
                    final ArrayList<String> aksondurum = new ArrayList<>();
                    final ArrayList<String> aksontarih= new ArrayList<>();
                    final ArrayList<String> aksonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, aksondeger, aksontarihsaat, aksondurum));
                    Cursor cursor7 = contentResolver.query(uri, null, null, null, order);
                    if (cursor7 != null && cursor7.getCount() > 0) {
                        while (cursor7.moveToNext()) {
                            if (cursor7.getString(cursor7.getColumnIndex(ContentProvider.DURUM)).equals("Akşam Yemeğinden Sonra")) {
                                aksondeger.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.DEGER)));
                                aksontarih.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.TARIH)));
                                aksonsaat.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.SAAT)));
                                aksontarihsaat.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor7.getString(cursor7.getColumnIndex(ContentProvider.SAAT)));
                                aksondurum.add(cursor7.getString(cursor7.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(aksondeger,aksontarih,aksonsaat,aksondurum);
                    break;
                case "Uyumadan Önce":
                    final ArrayList<String> uyondeger = new ArrayList<>();
                    final ArrayList<String> uyontarihsaat = new ArrayList<>();
                    final ArrayList<String> uyondurum = new ArrayList<>();
                    final ArrayList<String> uyontarih = new ArrayList<>();
                    final ArrayList<String> uyonsaat = new ArrayList<>();

                    listView.setAdapter(new yourAdapter(this, uyondeger, uyontarihsaat, uyondurum));
                    Cursor cursor8 = contentResolver.query(uri, null, null, null, order);
                    if (cursor8 != null && cursor8.getCount() > 0) {
                        while (cursor8.moveToNext()) {
                            if (cursor8.getString(cursor8.getColumnIndex(ContentProvider.DURUM)).equals("Uyumadan Önce")) {
                                uyondeger.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.DEGER)));
                                uyontarih.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.TARIH)));
                                uyonsaat.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.SAAT)));
                                uyontarihsaat.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.TARIH)) + " " +
                                        cursor8.getString(cursor8.getColumnIndex(ContentProvider.SAAT)));
                                uyondurum.add(cursor8.getString(cursor8.getColumnIndex(ContentProvider.DURUM)));

                            }

                        }
                    }
                    list(uyondeger,uyontarih,uyonsaat,uyondurum);
                    break;

                default:
                    break;

            }

            // Toast.makeText(YeniKayit.this, "Selected Item: "+item, Toast.LENGTH_SHORT).show();
        });
        list(degerList,tarihList,saatList,durumList);

        ArrayList<String> reversedeger = new ArrayList<>(degerList);
        Collections.reverse(reversedeger);
        ArrayList<String> reversetarih = new ArrayList<>(tarihList);
        Collections.reverse(reversetarih);
        ArrayList<String> reversesaat = new ArrayList<>(saatList);
        Collections.reverse(reversesaat);
        if(reversedeger.size()>0){
            setData(reversedeger.size(), reversetarih, reversedeger, reversesaat);
        }




    }



    static class yourAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> deger;
        ArrayList<String> tarihvesaat;
        ArrayList<String> durum;
        private LayoutInflater inflater = null;

        public yourAdapter(Context context, ArrayList<String> deger, ArrayList<String> tarihvesaat, ArrayList<String> durum) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.deger = deger;
            this.tarihvesaat = tarihvesaat;
            this.durum = durum;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return deger.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return deger.get(position);
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
                vi = inflater.inflate(R.layout.listview_row, null);
            TextView text = (TextView) vi.findViewById(R.id.txt);
            TextView text1 = (TextView) vi.findViewById(R.id.text1);
            TextView text2 = (TextView) vi.findViewById(R.id.text2);
            text.setText(deger.get(i));
            text1.setText(tarihvesaat.get(i));
            text2.setText(durum.get(i));
            return vi;
        }


    }

}