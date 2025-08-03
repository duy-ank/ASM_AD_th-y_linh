package com.example.giaodien.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.giaodien.Expense;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "FinanceManager.db";
    private static final int DATABASE_VERSION = 4;

    // Table Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_LAST_LOGIN = "last_login";

    // Table Expenses
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSE_ID = "expense_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PAYMENT_METHOD = "payment_method";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_USER_FK = "user_id";

    // SQL statements
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                    COLUMN_PASSWORD_HASH + " TEXT NOT NULL," +
                    COLUMN_FULL_NAME + " TEXT," +
                    COLUMN_CREATED_AT + " INTEGER DEFAULT (strftime('%s','now'))," +
                    COLUMN_LAST_LOGIN + " INTEGER" +
                    ")";

    private static final String CREATE_TABLE_EXPENSES =
            "CREATE TABLE " + TABLE_EXPENSES + "(" +
                    COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_AMOUNT + " REAL NOT NULL," +
                    COLUMN_CATEGORY + " TEXT NOT NULL," +
                    COLUMN_PAYMENT_METHOD + " TEXT NOT NULL," +
                    COLUMN_DESCRIPTION + " TEXT," +
                    COLUMN_DATE + " INTEGER DEFAULT (strftime('%s','now'))," +
                    COLUMN_USER_FK + " INTEGER," +
                    "FOREIGN KEY(" + COLUMN_USER_FK + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE" +
                    ")";

    // Indexes
    private static final String CREATE_EMAIL_INDEX =
            "CREATE INDEX idx_users_email ON " + TABLE_USERS + "(" + COLUMN_EMAIL + ")";

    private static final String CREATE_EXPENSE_DATE_INDEX =
            "CREATE INDEX idx_expenses_date ON " + TABLE_EXPENSES + "(" + COLUMN_DATE + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeTransaction(db, () -> {
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_EXPENSES);
            db.execSQL(CREATE_EMAIL_INDEX);
            db.execSQL(CREATE_EXPENSE_DATE_INDEX);
            Log.i(TAG, "Database created successfully");
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        executeTransaction(db, () -> {
            if (oldVersion < 2) {
                upgradeToVersion2(db);
            }
            if (oldVersion < 3) {
                upgradeToVersion3(db);
            }
            if (oldVersion < 4) {
                upgradeToVersion4(db);
            }
            Log.i(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
        });
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_FULL_NAME + " TEXT");
        db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_LAST_LOGIN + " INTEGER");
        db.execSQL(CREATE_EMAIL_INDEX);
    }

    private void upgradeToVersion3(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EXPENSES);
        db.execSQL(CREATE_EXPENSE_DATE_INDEX);
    }

    private void upgradeToVersion4(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_EXPENSES);
    }

    private void executeTransaction(SQLiteDatabase db, Runnable operations) {
        db.beginTransaction();
        try {
            operations.run();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e(TAG, "Database operation failed", e);
            recreateDatabase(db);
        } finally {
            db.endTransaction();
        }
    }

    private void recreateDatabase(SQLiteDatabase db) {
        executeTransaction(db, () -> {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        });
    }

    // Expense operations
    public long addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_CATEGORY, expense.getCategory());
        values.put(COLUMN_PAYMENT_METHOD, expense.getPaymentMethod());
        values.put(COLUMN_DESCRIPTION, expense.getDescription());
        values.put(COLUMN_DATE, expense.getTimestamp());

        long result = db.insert(TABLE_EXPENSES, null, values);
        db.close();
        return result;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                COLUMN_EXPENSE_ID,
                COLUMN_AMOUNT,
                COLUMN_CATEGORY,
                COLUMN_PAYMENT_METHOD,
                COLUMN_DESCRIPTION,
                COLUMN_DATE
        };

        try (Cursor cursor = db.query(
                TABLE_EXPENSES,
                columns,
                null,
                null,
                null,
                null,
                COLUMN_DATE + " DESC")) {

            while (cursor.moveToNext()) {
                Expense expense = new Expense(
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                );
                expenses.add(expense);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting all expenses", e);
        } finally {
            db.close();
        }
        return expenses;
    }

    public List<Expense> getRecentExpenses(int limit) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_EXPENSE_ID,
                COLUMN_AMOUNT,
                COLUMN_CATEGORY,
                COLUMN_PAYMENT_METHOD,
                COLUMN_DESCRIPTION,
                COLUMN_DATE
        };

        try (Cursor cursor = db.query(
                TABLE_EXPENSES,
                columns,
                null,
                null,
                null,
                null,
                COLUMN_DATE + " DESC",
                String.valueOf(limit)
        )) {
            if (cursor.moveToFirst()) {
                do {
                    Expense expense = new Expense(
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                    );
                    expenses.add(expense);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting recent expenses", e);
        } finally {
            db.close();
        }
        return expenses;
    }

    public List<Expense> getExpensesByUser(long userId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COLUMN_EXPENSE_ID,
                COLUMN_AMOUNT,
                COLUMN_CATEGORY,
                COLUMN_PAYMENT_METHOD,
                COLUMN_DESCRIPTION,
                COLUMN_DATE
        };

        try (Cursor cursor = db.query(
                TABLE_EXPENSES,
                columns,
                COLUMN_USER_FK + " = ?",
                new String[]{String.valueOf(userId)},
                null, null,
                COLUMN_DATE + " DESC")) {

            while (cursor.moveToNext()) {
                Expense expense = new Expense(
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                );
                expenses.add(expense);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting expenses", e);
        } finally {
            db.close();
        }
        return expenses;
    }

    public int deleteExpense(long expenseId) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete(
                    TABLE_EXPENSES,
                    COLUMN_EXPENSE_ID + " = ?",
                    new String[]{String.valueOf(expenseId)}
            );
        } catch (SQLiteException e) {
            Log.e(TAG, "Error deleting expense", e);
            return 0;
        } finally {
            db.close();
        }
    }

    // User operations
    public long addUser(String email, String passwordHash, String fullName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD_HASH, passwordHash);
        values.put(COLUMN_FULL_NAME, fullName);

        try {
            return db.insert(TABLE_USERS, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error adding user", e);
            return -1;
        } finally {
            db.close();
        }
    }


    public Cursor getUserInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE username = ?";
        return db.rawQuery(query, new String[]{username});
    }

}