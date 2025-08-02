package com.example.giaodien.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2;

    // Table and column names
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_LAST_LOGIN = "last_login";

    // SQL to create table
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                    COLUMN_PASSWORD_HASH + " TEXT NOT NULL," +
                    COLUMN_FULL_NAME + " TEXT," +
                    COLUMN_CREATED_AT + " INTEGER DEFAULT (strftime('%s','now'))," +
                    COLUMN_LAST_LOGIN + " INTEGER" +
                    ")";

    // Index for faster email lookups
    private static final String CREATE_EMAIL_INDEX =
            "CREATE INDEX idx_users_email ON " + TABLE_USERS + "(" + COLUMN_EMAIL + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_EMAIL_INDEX);
            db.setTransactionSuccessful();
            Log.i(TAG, "Database created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.beginTransaction();
            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " +
                        COLUMN_FULL_NAME + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " +
                        COLUMN_LAST_LOGIN + " INTEGER");
                db.execSQL(CREATE_EMAIL_INDEX);
            }
            db.setTransactionSuccessful();
            Log.i(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database", e);
            recreateDatabase(db);
        } finally {
            db.endTransaction();
        }
    }
    private void recreateDatabase(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error recreating database", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
