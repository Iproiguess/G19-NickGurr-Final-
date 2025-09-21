package com.example.grpmobile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window; // Added
import android.view.WindowManager; // Added
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtLoginName, edtLoginPassword;
    private Button btnLogin, btnSignup;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Add these lines for fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        edtLoginName = findViewById(R.id.edtLoginName);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Login button listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtLoginName.getText().toString().trim();
                String password = edtLoginPassword.getText().toString().trim();

                if (name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both name and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isUserValid(name, password)) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    String role = getUserRole(name, password);
                    if ("User".equals(role)) {
                        Intent userIntent = new Intent(LoginActivity.this, UserActivity.class);
                        // Consider passing username to UserActivity as well if needed there
                        // userIntent.putExtra("USER_USERNAME", name); 
                        startActivity(userIntent);
                    } else if ("Admin".equals(role)) {
                        Intent adminIntent = new Intent(LoginActivity.this, AdminActivity.class);
                        adminIntent.putExtra("ADMIN_USERNAME", name); // Pass admin username
                        startActivity(adminIntent);
                    } else if (role.isEmpty()){
                        Toast.makeText(LoginActivity.this, "User role not found or invalid.", Toast.LENGTH_SHORT).show();
                    }
                    if ("User".equals(role) || "Admin".equals(role)) {
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(signupIntent);
            }
        });
    }

    private boolean isUserValid(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"username"};
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = null;
        boolean isValid = false;
        try {
            cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
            isValid = cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("LoginActivity", "Error querying user validity", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isValid;
    }

    private String getUserRole(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"role"};
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = null;
        String role = "";
        try {
            cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            } else {
                Log.w("LoginActivity", "User " + username + " found, but no role information.");
            }
        } catch (IllegalArgumentException e) {
            Log.e("LoginActivity", "Critical error: 'role' column not found in users table.", e);
        } catch (Exception e) {
            Log.e("LoginActivity", "Error querying user role", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return role;
    }
}
