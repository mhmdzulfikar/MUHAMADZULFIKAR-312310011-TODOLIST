package com.example.uastugas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    // Nama database dan versi
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    // Nama tabel dan kolom
    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK_NAME = "taskName";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_NOTE = "note";

    // Query untuk membuat tabel
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_NOTE + " TEXT);";

    // Konstruktor
    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE); // Membuat tabel
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // Hapus tabel lama
        onCreate(db); // Buat tabel baru
    }

    // Menambahkan tugas ke database
    public void addTask(String taskName, String time, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_NOTE, note);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Mendapatkan semua tugas
    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    // Menghapus tugas berdasarkan nama
    public void deleteTask(String taskName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_TASK_NAME + "=?", new String[]{taskName});
        db.close();
    }
}
