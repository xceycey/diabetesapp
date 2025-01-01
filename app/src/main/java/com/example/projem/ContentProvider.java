package com.example.projem;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class ContentProvider extends android.content.ContentProvider {
    static final String PROVIDER_NAME="com.example.projem.ContentProvider";
    static final String URL="content://"+ PROVIDER_NAME+"/seker";
    static final Uri CONTENT_URI=Uri.parse(URL);
    static final String URL2="content://"+ PROVIDER_NAME+"/yemek";
    static final Uri CONTENT_URI2=Uri.parse(URL2);
    static final String URL3="content://"+ PROVIDER_NAME+"/ogun";
    static final Uri CONTENT_URI3=Uri.parse(URL3);
    static final String URL4="content://"+ PROVIDER_NAME+"/su";
    static final Uri CONTENT_URI4=Uri.parse(URL4);
    static final String SUML="suml";
    static final String SUTARIH="sutarih";

    static final String OGUNAD="ogunad";
    static final String OGUNMIKTAR="ogunmiktar";
    static final String OGUNPORSIYON="ogunporsiyon";
    static final String OGUNKARB="ogunkarb";
    static final String OGUNIMAGE="ogunimage";

    static final String DEGER="deger";
    static final String TARIH="tarih";
    static final String SAAT="saat";
    static final String DURUM="durum";

    static final String AD="yemekadi";
    static final String MIKTAR="miktar";
    static final String PORSIYON="porsiyon";
    static final String KARB="karb";
    static final String IMAGE="image";
    static final int Degerler=1,Yemekler=2,Ogunler=3,Sular=4;
    static final UriMatcher uriMatcher;
    static{
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"seker",Degerler);
        uriMatcher.addURI(PROVIDER_NAME,"yemek",Yemekler);
        uriMatcher.addURI(PROVIDER_NAME,"ogun",Ogunler);
        uriMatcher.addURI(PROVIDER_NAME,"su",Sular);
    }
    private static HashMap<String,String> SEKER_PROJECTION_MAP;
    //Database
    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME="diyabetapp.db";
    static final String TABLE_NAME="Tbldegerler";
    static final String TABLE_NAME2="Tblyemekler";
    static final String TABLE_NAME3="Tblogunum";
    static final String TABLE_NAME4="Tblsu";

    static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+"(deger varchar(5),"+
            "tarih date,"+"saat time,"+"durum varchar(30))";

    static final String CREATE_TABLE2="CREATE TABLE "+TABLE_NAME2+"(yemekadi varchar(20),"+
            "miktar int,"+"porsiyon varchar(10),"+"karb varchar(10),"+"image blob)";

    static final String CREATE_TABLE3="CREATE TABLE "+TABLE_NAME3+"(ogunad varchar(20),"+
            "ogunmiktar int,"+"ogunporsiyon varchar(10),"+"ogunkarb varchar(10),"+"ogunimage blob)";


    static final String CREATE_TABLE4="CREATE TABLE "+TABLE_NAME4+"(suml int,"+
            "sutarih TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
    public static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_TABLE2);
            db.execSQL(CREATE_TABLE3);
            db.execSQL(CREATE_TABLE4);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME3);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME4);
            onCreate(db);
        }
        public void clear(){

        }
    }
    @Override
    public boolean onCreate() {
        Context context=getContext();
        DatabaseHelper databaseHelper=new DatabaseHelper(context);
        sqLiteDatabase=databaseHelper.getWritableDatabase();
        return sqLiteDatabase!=null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder=new SQLiteQueryBuilder();
        Cursor cursor = null;

        switch (uriMatcher.match(uri)){
            case Degerler:
                sqLiteQueryBuilder.setTables(TABLE_NAME);
                sqLiteQueryBuilder.setProjectionMap(SEKER_PROJECTION_MAP);
                if(sortOrder==null || sortOrder.matches("")){
                    sortOrder="tarih" + ", " +"saat" +" ASC";
                }
                cursor= sqLiteQueryBuilder.query(sqLiteDatabase,projection,selection,selectionArgs,null,null,sortOrder);

                break;
            case Yemekler:
                sqLiteQueryBuilder.setTables(TABLE_NAME2);
                sqLiteQueryBuilder.setProjectionMap(SEKER_PROJECTION_MAP);
                cursor= sqLiteQueryBuilder.query(sqLiteDatabase,projection,selection,selectionArgs,null,null,null);

                break;
            case Ogunler:
                sqLiteQueryBuilder.setTables(TABLE_NAME3);
                sqLiteQueryBuilder.setProjectionMap(SEKER_PROJECTION_MAP);
                cursor= sqLiteQueryBuilder.query(sqLiteDatabase,projection,selection,selectionArgs,null,null,null);

                break;
            case Sular:
                sqLiteQueryBuilder.setTables(TABLE_NAME4);
                sqLiteQueryBuilder.setProjectionMap(SEKER_PROJECTION_MAP);
                cursor= sqLiteQueryBuilder.query(sqLiteDatabase,projection,selection,selectionArgs,null,null,null);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri _uri = null;
        switch (uriMatcher.match(uri)){
            case Degerler:
                long rowID=sqLiteDatabase.insert(TABLE_NAME,"",values);
                if(rowID>0){
                    _uri= ContentUris.withAppendedId(CONTENT_URI,rowID);
                    getContext().getContentResolver().notifyChange(_uri,null);
                }
                break;
            case Yemekler:
                long rowID2=sqLiteDatabase.insert(TABLE_NAME2,"",values);
                if(rowID2>0){
                    _uri= ContentUris.withAppendedId(CONTENT_URI2,rowID2);
                    getContext().getContentResolver().notifyChange(_uri,null);
                }
                break;
            case Ogunler:
                long rowID3=sqLiteDatabase.insert(TABLE_NAME3,"",values);
                if(rowID3>0){
                    _uri= ContentUris.withAppendedId(CONTENT_URI3,rowID3);
                    getContext().getContentResolver().notifyChange(_uri,null);
                }
                break;
            case Sular:
                long rowID4=sqLiteDatabase.insert(TABLE_NAME4,"",values);
                if(rowID4>0){
                    _uri= ContentUris.withAppendedId(CONTENT_URI4,rowID4);
                    getContext().getContentResolver().notifyChange(_uri,null);
                }
                break;
            default: throw new SQLException("Failed to insert row into " + uri);
        }
        return _uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int effectedrowcount=0;
        switch(uriMatcher.match(uri)){
            case Degerler:
                effectedrowcount= sqLiteDatabase.delete(TABLE_NAME,selection,selectionArgs);
                break;
            case Yemekler:
                effectedrowcount= sqLiteDatabase.delete(TABLE_NAME2,selection,selectionArgs);
                System.out.println(effectedrowcount);
                break;
            case Ogunler:
                effectedrowcount= sqLiteDatabase.delete(TABLE_NAME3,selection,selectionArgs);

                break;
            case Sular:
                effectedrowcount= sqLiteDatabase.delete(TABLE_NAME4,selection,selectionArgs);

                break;

            default:
                throw new IllegalArgumentException("Failed matching uri");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return effectedrowcount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int effectedrowcount=0;
        switch(uriMatcher.match(uri)){
            case Degerler:
                effectedrowcount= sqLiteDatabase.update(TABLE_NAME,values,selection,selectionArgs);
                break;
            case Yemekler:
                effectedrowcount= sqLiteDatabase.update(TABLE_NAME2,values,selection,selectionArgs);
                break;
            case Ogunler:
                effectedrowcount= sqLiteDatabase.update(TABLE_NAME3,values,selection,selectionArgs);
                break;
            case Sular:
                effectedrowcount= sqLiteDatabase.update(TABLE_NAME4,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed matching uri");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return effectedrowcount;
    }
}
