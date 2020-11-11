package com.example.androidassignments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChatDatabaseHelper extends SQLiteOpenHelper {
    ChatDatabaseHelper myDb;
    public static final String DATABASE_NAME = "Messages.db";
    public static final int DATABASE_VERSION = 3;
    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String COL_1 = "KEY_ID";
    public static final String COL_2 = "KEY_MESSAGE";




    public ChatDatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("ChatDatabaseHelper", "Calling onCreate");
        String sql="CREATE TABLE TABLE_NAME (KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, KEY_MESSAGE TEXT)";
        db.execSQL(sql);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("ChatDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + "newVersion=" + newVersion);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, message);
        long result = db.insert(TABLE_NAME, null, values);
        if(result==-1){
            Log.d("ChatDatabaseHelper", "Failed to insert data to database");
            return false;
        }
        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor answer = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return  answer;
    }




}
