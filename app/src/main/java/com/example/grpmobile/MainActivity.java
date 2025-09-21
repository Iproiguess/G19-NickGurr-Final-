package com.example.grpmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
// Removed unused ContentValues and SQLiteDatabase imports as DBHelper handles it now
import android.os.Bundle;
import android.util.Log; // Added for potential logging
import android.util.Patterns; // For email validation
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtName, edtPassword, edtConfirmPassword, edtEmail; // Added edtEmail
    private RadioGroup radioGroup;
    private Button btnLoginOrSignup; // Renamed for clarity, text changes (Login/Sign Up)
    private TextView tvToggleMode; // Renamed for clarity, text changes
    private DBHelper dbHelper;
    private boolean isLoginMode = true;  // Default is login mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtEmail = findViewById(R.id.edtEmail); // Initialize edtEmail
        radioGroup = findViewById(R.id.radioGroup);
        btnLoginOrSignup = findViewById(R.id.btnLogin); // ID from XML is btnLogin
        tvToggleMode = findViewById(R.id.tvGoToSignup); // ID from XML is tvGoToSignup

        dbHelper = new DBHelper(this);

        // Set initial UI state (Login mode)
        switchToLoginMode(); // Default to Login mode

        // Set up the main button click listener
        btnLoginOrSignup.setOnClickListener(v -> {
            if (isLoginMode) {
                loginUser();
            } else {
                signUpUser();
            }
        });

        // Set up "Go to Sign Up" / "Back to Login" TextView click listener
        tvToggleMode.setOnClickListener(v -> {
            if (isLoginMode) {
                switchToSignUpMode();
            } else {
                switchToLoginMode();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void switchToLoginMode() {
        isLoginMode = true;
        btnLoginOrSignup.setText("Login");
        tvToggleMode.setText("Create an account (Sign Up)");
        edtName.setText("");
        edtPassword.setText("");
        edtConfirmPassword.setText("");
        edtEmail.setText(""); // Clear email field
        radioGroup.setVisibility(View.GONE);
        edtConfirmPassword.setVisibility(View.GONE);
        edtEmail.setVisibility(View.GONE); // Hide email field
        edtName.setError(null); // Clear any previous errors
        edtPassword.setError(null);
        edtName.requestFocus();
    }

    @SuppressLint("SetTextI18n")
    private void switchToSignUpMode() {
        isLoginMode = false;
        btnLoginOrSignup.setText("Sign Up");
        tvToggleMode.setText("Already have an account? Login");
        edtName.setText("");
        edtPassword.setText("");
        edtConfirmPassword.setText("");
        edtEmail.setText(""); // Clear email field
        radioGroup.setVisibility(View.VISIBLE);
        edtConfirmPassword.setVisibility(View.VISIBLE);
        edtEmail.setVisibility(View.VISIBLE);
        edtName.setError(null);
        edtPassword.setError(null);
        edtConfirmPassword.setError(null);
        edtEmail.setError(null);
        edtName.requestFocus();
    }

    private void loginUser() {
        String name = edtName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Please enter your name");
            edtName.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Please enter your password");
            edtPassword.requestFocus();
            return;
        }

        if (dbHelper.checkUserCredentials(name, password)) {
            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
            String role = dbHelper.getUserRole(name);
            String email = dbHelper.getUserEmail(name); // Fetch the user's email

            Intent intent;
            if ("User".equals(role)) {
                intent = new Intent(MainActivity.this, UserActivity.class);
                if (email != null) {
                    intent.putExtra("USER_EMAIL", email); // Pass email to UserActivity
                } else {
                    Log.w("MainActivity", "User email is null for username: " + name);
                    intent.putExtra("USER_EMAIL", "N/A");
                }
            } else if ("Admin".equals(role)) {
                intent = new Intent(MainActivity.this, AdminActivity.class);
                // No need to pass email specifically for admin here as AccountSettings fetches it
            } else {
                Toast.makeText(MainActivity.this, "Login failed: Unknown role.", Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra("USERNAME", name); // Pass username to the next activity
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            edtPassword.setText("");
            edtPassword.requestFocus();
        }
    }

    private void signUpUser() {
        String name = edtName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Please enter a name");
            edtName.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Please enter a password");
            edtPassword.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.setError("Please confirm your password");
            edtConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Passwords do not match");
            edtConfirmPassword.requestFocus();
            edtConfirmPassword.setText("");
            return;
        }
        if (email.isEmpty()) {
            edtEmail.setError("Please enter your email");
            edtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Please enter a valid email address");
            edtEmail.requestFocus();
            return;
        }

        int selectedRoleId = radioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(MainActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String role = selectedRoleButton.getText().toString();

        // Pass null for profileImageUri for now
        boolean success = dbHelper.addUser(name, password, email, null, role);

        if (success) {
            Toast.makeText(MainActivity.this, "Sign up successful! Please log in.", Toast.LENGTH_LONG).show();
            switchToLoginMode();
        } else {
            Toast.makeText(MainActivity.this, "Sign up failed. Username or email may already exist.", Toast.LENGTH_LONG).show();
        }
    }
}
