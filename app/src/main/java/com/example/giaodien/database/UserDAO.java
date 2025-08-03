package com.example.giaodien.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.giaodien.model.User;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserDAO {
    private static final String TAG = "UserDAO";
    private final DatabaseHelper dbHelper;
    private static final int BCRYPT_COST = 12;

    // Constructor cũ: Dùng để tương thích ngược nếu cần
    public UserDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // Constructor mới: Dùng để tuân thủ Dependency Injection (khuyến nghị)
    public UserDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
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
        } catch (SQLiteException e) {
            Log.e(TAG, "Error registering user", e);
        } finally {
            db.close();
        }
        return result;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                new String[]{
                        DatabaseHelper.COLUMN_USER_ID,
                        DatabaseHelper.COLUMN_EMAIL,
                        DatabaseHelper.COLUMN_PASSWORD_HASH,
                        DatabaseHelper.COLUMN_FULL_NAME
                },
                DatabaseHelper.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                String storedHash = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD_HASH));
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);

                if (result.verified) {
                    user = new User();
                    user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)));
                    user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
                    user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULL_NAME)));

                    // Cập nhật last login time, tái sử dụng kết nối database
                    updateLastLogin(db, email);
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error logging in user", e);
        } finally {
            db.close();
        }
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
        boolean exists = false;
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_USER_ID},
                DatabaseHelper.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null
        )) {
            exists = cursor.getCount() > 0;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error checking for existing email", e);
        } finally {
            db.close();
        }
        return exists;
    }

    // Phương thức mới để lấy thông tin người dùng bằng email
    public User getUserInfoByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        String[] columns = {
                DatabaseHelper.COLUMN_USER_ID,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_FULL_NAME
        };
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                columns,
                DatabaseHelper.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
                user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULL_NAME)));
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting user info", e);
        } finally {
            db.close();
        }
        return user;
    }
}