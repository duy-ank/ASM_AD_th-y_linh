package com.example.giaodien.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.giaodien.model.User;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserDAO {
    private static final String TAG = "UserDAO";
    private final DatabaseHelper dbHelper;
    private static final int BCRYPT_COST = 12;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long registerUser(String email, String password, String fullName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String hashedPassword = BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());

        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PASSWORD_HASH, hashedPassword);
        values.put(DatabaseHelper.COLUMN_FULL_NAME, fullName);

        long result = -1;
        try {
            result = db.insertOrThrow(DatabaseHelper.TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error registering user", e);
        } finally {
            db.close();
        }
        return result;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                new String[]{
                        DatabaseHelper.COLUMN_ID,
                        DatabaseHelper.COLUMN_EMAIL,
                        DatabaseHelper.COLUMN_PASSWORD_HASH,
                        DatabaseHelper.COLUMN_FULL_NAME
                },
                DatabaseHelper.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null
        );

        if (cursor.moveToFirst()) {

            String storedHash = cursor.getString(2);
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);

            if (result.verified) {
                user = new User();
                user.setId(cursor.getLong(0));
                user.setEmail(cursor.getString(1));
                user.setFullName(cursor.getString(3));

                // Update last login time
                updateLastLogin(db, email);
            }
        }

        cursor.close();
        db.close();
        return user;
    }

    private void updateLastLogin(SQLiteDatabase db, String email) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LAST_LOGIN, System.currentTimeMillis() / 1000);

        db.update(
                DatabaseHelper.TABLE_USERS,
                values,
                DatabaseHelper.COLUMN_EMAIL + " = ?",
                new String[]{email}
        );
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_ID},
                DatabaseHelper.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}