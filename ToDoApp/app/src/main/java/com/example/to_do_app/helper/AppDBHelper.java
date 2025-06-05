package com.example.to_do_app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.to_do_app.domain.Contact;
import com.example.to_do_app.domain.Work;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Todo.db";
    public static final int DATABASE_VERSION = 3;

    // Table: work
    public static final String TABLE_WORKS = "works";
    public static final String COLUMN_WORK_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_IS_DONE = "is_done";

    // Table: contacts
    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FOREIGN_WORK_ID = "work_id";

    public AppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create works table
        db.execSQL("CREATE TABLE " + TABLE_WORKS + " (" +
                COLUMN_WORK_ID + " TEXT PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DEADLINE + " INTEGER, " +
                COLUMN_IS_DONE + " INTEGER)");

        // Create contacts table
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + " (" +
                COLUMN_PHONE + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_FOREIGN_WORK_ID + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_FOREIGN_WORK_ID + ") REFERENCES " +
                TABLE_WORKS + "(" + COLUMN_WORK_ID + ") ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKS);
        onCreate(db);
    }

    public void addWork(Work work) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORK_ID, work.getId());
        values.put(COLUMN_TITLE, work.getTitle());
        values.put(COLUMN_DESCRIPTION, work.getDesc());
        values.put(COLUMN_DEADLINE, work.getDeadLine().getTime());
        values.put(COLUMN_IS_DONE, work.isDone() ? 1 : 0);
        db.insert(TABLE_WORKS, null, values);
        db.close();
    }

    public boolean updateWork(Work work) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, work.getTitle());
        values.put(COLUMN_DESCRIPTION, work.getDesc());
        values.put(COLUMN_DEADLINE, work.getDeadLine().getTime());
        values.put(COLUMN_IS_DONE, work.isDone() ? 1 : 0);
        int rows = db.update(TABLE_WORKS, values, COLUMN_WORK_ID + " = ?", new String[]{work.getId()});
        db.close();
        return rows > 0;
    }

    public boolean deleteWork(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_WORKS, COLUMN_WORK_ID + " = ?", new String[]{id});
        db.close();
        return rows > 0;
    }

    public Work getWorkById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKS,
                null,
                COLUMN_WORK_ID + " = ?",
                new String[]{id}, null, null, null);

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            Date deadline = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)));
            boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DONE)) == 1;
            cursor.close();
            db.close();
            return new Work(id, title, desc, deadline, isDone);
        }

        cursor.close();
        db.close();
        return null;
    }

    public ArrayList<Work> getAllWorks() {
        ArrayList<Work> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKS, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WORK_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            Date deadline = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)));
            boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DONE)) == 1;
            list.add(new Work(id, title, desc, deadline, isDone));
        }

        cursor.close();
        db.close();
        return list;
    }

    // --- CONTACT CRUD ---

    public void addContact(Contact contact, String workId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, contact.phone);
        values.put(COLUMN_NAME, contact.name);
        values.put(COLUMN_FOREIGN_WORK_ID, workId);
        db.insertWithOnConflict(TABLE_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public boolean deleteContact(String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_CONTACTS, COLUMN_PHONE + " = ?", new String[]{phone});
        db.close();
        return rows > 0;
    }

    public void deleteContactsForWork(String workId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_FOREIGN_WORK_ID + " = ?", new String[]{workId});
        db.close();
    }

    public List<Contact> getContactsForWork(String workId) {
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS,
                new String[]{COLUMN_NAME, COLUMN_PHONE},
                COLUMN_FOREIGN_WORK_ID + " = ?",
                new String[]{workId}, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            contacts.add(new Contact(name, phone));
        }

        cursor.close();
        db.close();
        return contacts;
    }
}
