package com.example.to_do_app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.to_do_app.domain.Work;

import java.util.ArrayList;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Todo.db";
    public static final String TABLE_NAME = "todos";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_IS_DONE = "is_done";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            COLUMN_ID + " TEXT PRIMARY KEY, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_DEADLINE + " INTEGER, " +
            COLUMN_IS_DONE + " INTEGER)";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    public void addNew(Work work) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, work.getId());
        values.put(COLUMN_TITLE, work.getTitle());
        values.put(COLUMN_DESCRIPTION, work.getDesc());
        values.put(COLUMN_DEADLINE, work.getDeadLine().getTime());
        values.put(COLUMN_IS_DONE, work.isDone() ? 1 : 0);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean update(Work work) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, work.getTitle());
        values.put(COLUMN_DESCRIPTION, work.getDesc());
        values.put(COLUMN_DEADLINE, work.getDeadLine().getTime());
        values.put(COLUMN_IS_DONE, work.isDone() ? 1 : 0);
        int rowAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{work.getId()});
        db.close();
        return rowAffected > 0;
    }

    public boolean delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowAffected = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{id});
        db.close();
        return rowAffected > 0;
    }

    public ArrayList<Work> loadAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DESCRIPTION,
                COLUMN_DEADLINE,
                COLUMN_IS_DONE
        };
        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);
        ArrayList<Work> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            Date deadline = new Date(cursor.getLong(3));
            boolean isDone = cursor.getInt(4) == 1;
            items.add(new Work(id, title, desc, deadline, isDone));
        }
        cursor.close();
        db.close();
        return items;
    }

    public Work getById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DESCRIPTION,
                COLUMN_DEADLINE,
                COLUMN_IS_DONE
        };
        Cursor cursor = db.query(TABLE_NAME, projection,
                COLUMN_ID + " = ?", new String[]{id}, null, null, null);

        if (cursor.moveToFirst()) {
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            Date deadline = new Date(cursor.getLong(3));
            boolean isDone = cursor.getInt(4) == 1;
            cursor.close();
            db.close();
            return new Work(id, title, desc, deadline, isDone);
        }

        cursor.close();
        db.close();
        return null;
    }
}

