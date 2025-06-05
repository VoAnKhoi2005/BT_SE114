package com.example.to_do_app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.to_do_app.domain.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Todo.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "contacts";
    public static final String COLUMN_PHONE = "phone"; //PK
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_WORK_ID = "work_id";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_PHONE + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_WORK_ID + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_WORK_ID + ") REFERENCES " +
                    WorkDBHelper.TABLE_NAME + "(" + WorkDBHelper.COLUMN_ID + ") ON DELETE CASCADE" + ")";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public ContactDBHelper(Context context) {
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

    public void addContact(Contact contact, String workId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, contact.phone);
        values.put(COLUMN_NAME, contact.name);
        values.put(COLUMN_WORK_ID, workId);
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public boolean deleteContact(String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, COLUMN_PHONE + " = ?", new String[]{phone});
        db.close();
        return rows > 0;
    }

    public List<Contact> getContactsForWork(String workId) {
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_NAME, COLUMN_PHONE},
                COLUMN_WORK_ID + " = ?",
                new String[]{workId},
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String phone = cursor.getString(1);
                contacts.add(new Contact(name, phone));
            }
            cursor.close();
        }

        db.close();
        return contacts;
    }

    public void deleteContactsForWork(String workId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_WORK_ID + " = ?", new String[]{workId});
        db.close();
    }
}
