package com.example.voiceassistantapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDictionaryDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dictionary_manager";
    private static final String TABLE_NAME = "dictionary";
    private static final String YOUSAY = "youSay";
    private static final String APPUNDERSTAND = "appUnderstand";
    private static final int VERSION = 1;
    private Context context;
    private String SQLQuery = "CREATE TABLE " + TABLE_NAME + " (" +
            YOUSAY + " TEXT primary key, " +
            APPUNDERSTAND + " TEXT )";


    public MyDictionaryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addDictionary(MyDictionary myDictionary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(YOUSAY, myDictionary.getYouSay());
        values.put(APPUNDERSTAND, myDictionary.getAppUnderstand());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public List<MyDictionary> getAllMyDictionary() {
        List<MyDictionary> myDictionaries = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MyDictionary myDictionary = new MyDictionary();
                myDictionary.setYouSay(cursor.getString(0));
                myDictionary.setAppUnderstand(cursor.getString(1));

                myDictionaries.add(myDictionary);
            } while (cursor.moveToNext());
        }
        db.close();
        return myDictionaries;
    }
    public void deleteMyDictionary(String nameText) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, YOUSAY + "=?", new String[]{nameText});
    }
    public void UpdateDictionary(String yousay,String appunderstand, String yousayold) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(YOUSAY, yousay);
        contentValues.put(APPUNDERSTAND, appunderstand);
        db.update(TABLE_NAME, contentValues, YOUSAY + "=?", new String[]{yousayold});
    }
}
