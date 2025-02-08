package com.devhima.datatracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_DATA_USAGE = "data_usage";
    private static final String COLUMN_BEFORE = "before";
    private static final String COLUMN_AFTER = "after";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(" +
				   COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				   COLUMN_USERNAME + " TEXT," +
				   COLUMN_DATA_USAGE + " INTEGER," +
                    COLUMN_BEFORE + " INTEGER," +
                    COLUMN_AFTER + " INTEGER)");
				   
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean insertUser(String username, long dataUsage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_DATA_USAGE, dataUsage);
        contentValues.put(COLUMN_BEFORE, 0);
        contentValues.put(COLUMN_AFTER, 0);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    public boolean updateUserUsage(String username, long dataUsage, long before, long after) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATA_USAGE, dataUsage);
        contentValues.put(COLUMN_BEFORE, before);
        contentValues.put(COLUMN_AFTER, after);
        int result = db.update(TABLE_USERS, contentValues, COLUMN_USERNAME + " = ?", new String[]{username});
        return result > 0;
    }
    
    public Integer deleteNote(String user){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS,"username = ?",new String[] {user});
    }
}
