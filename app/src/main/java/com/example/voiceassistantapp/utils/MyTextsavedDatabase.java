package com.example.voiceassistantapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.voiceassistantapp.models.MyTextSaved;

import java.util.ArrayList;
import java.util.List;

public class MyTextsavedDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "textsaveds_manager";
    private static final String TABLE_NAME = "textsaveds";
    private static final String MYTEXT = "myText";
    private static final String NAMETEXT = "nameText";
    private static final String DATETEXT = "dateText";
    private static final int VERSION = 1;
    private Context context;
    private String SQLQuery = "CREATE TABLE " + TABLE_NAME + " (" +
            MYTEXT + " TEXT, " +
            NAMETEXT + " TEXT primary key, " +
            DATETEXT + " TEXT)";

    public MyTextsavedDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addAlbum(MyTextSaved myTextSaved) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MYTEXT, myTextSaved.getMyText());
        values.put(NAMETEXT, myTextSaved.getNameMyText());
        values.put(DATETEXT, myTextSaved.getDateMyText());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public List<MyTextSaved> getAllMyTextSaved() {
        List<MyTextSaved> myTextSaveds = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MyTextSaved myTextSaved = new MyTextSaved();
                myTextSaved.setMyText(cursor.getString(0));
                myTextSaved.setNameMyText(cursor.getString(1));
                myTextSaved.setDateMyText(cursor.getString(2));

                myTextSaveds.add(myTextSaved);
            } while (cursor.moveToNext());
        }
        db.close();
        return myTextSaveds;
    }
    public void deleteTextsaved(String nameText) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, NAMETEXT + "=?", new String[]{nameText});
    }
    public void UpdateMyTextSaved(MyTextSaved myTextSaved) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MYTEXT, myTextSaved.getMyText());
        contentValues.put(NAMETEXT, myTextSaved.getNameMyText());
        contentValues.put(DATETEXT,myTextSaved.getDateMyText());
        db.update(TABLE_NAME, contentValues, NAMETEXT + "=?", new String[]{myTextSaved.getNameMyText()});
    }
}
