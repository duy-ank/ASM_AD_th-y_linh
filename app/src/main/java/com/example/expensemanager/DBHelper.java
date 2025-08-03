package com.example.expensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expenses.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "expenses";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "amount REAL, " +
                "category TEXT, " +
                "note TEXT, " +
                "date TEXT, " +
                "payment_method TEXT, " +
                "image_path TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN payment_method TEXT");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN image_path TEXT");
        }
    }

    public boolean insertExpense(double amount, String category, String note, String date, String paymentMethod, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", amount);
        cv.put("category", category);
        cv.put("note", note);
        cv.put("date", date);
        cv.put("payment_method", paymentMethod);
        cv.put("image_path", imagePath);
        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    public Cursor getAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY date DESC", null);
    }

    public Cursor getExpenseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = ?", new String[]{String.valueOf(id)});
    }

    public boolean deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean updateExpense(int id, double amount, String category, String note, String date, String paymentMethod, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", amount);
        cv.put("category", category);
        cv.put("note", note);
        cv.put("date", date);
        cv.put("payment_method", paymentMethod);
        cv.put("image_path", imagePath);
        int rows = db.update(TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
}
