package com.example.grpmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri; // Keep this
import android.os.Bundle;
import android.provider.MediaStore; // Add this import
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable; // Add this import
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker

    private TextView tvUsernameDisplay;
    private TextView tvEmailDisplay;
    private CircleImageView profileImage;
    private DBHelper dbHelper;
    private String currentUsername;
    private boolean isAdmin;

    private Button btnChangeProfilePicture; // Declare the new button
    private Button btnRequestCampaign;
    private Button btnAboutUs;
    private Button btnLogout;
    private Button btnBackToAdminDashboard;
    private Button btnBack;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        dbHelper = new DBHelper(this);
        currentUsername = getIntent().getStringExtra("USERNAME");

        if (currentUsername != null && !currentUsername.isEmpty()) {
            String userRole = dbHelper.getUserRole(currentUsername);
            isAdmin = (userRole != null && userRole.equalsIgnoreCase("admin"));
            Log.d(TAG, "Username: " + currentUsername + ", Role from DB: " + userRole + ", isAdmin determined as: " + isAdmin);
        } else {
            isAdmin = false;
            Log.d(TAG, "Current username is null or empty. isAdmin set to false.");
            currentUsername = "Guest";
        }

        profileImage = findViewById(R.id.profile_image);
        tvUsernameDisplay = findViewById(R.id.tvUsernameDisplay);
        tvEmailDisplay = findViewById(R.id.tvEmailDisplay);

        btnChangeProfilePicture = findViewById(R.id.btnChangeProfilePicture); // Initialize the button
        btnRequestCampaign = findViewById(R.id.btnRequestCampaign);
        btnAboutUs = findViewById(R.id.btnAboutUs);
        btnLogout = findViewById(R.id.btnLogout);
        btnBackToAdminDashboard = findViewById(R.id.btnBackToAdminDashboard);
        btnBack = findViewById(R.id.btnBack);

        if (!currentUsername.equals("Guest")) {
            tvUsernameDisplay.setText(currentUsername);
            String email = dbHelper.getUserEmail(currentUsername);
            if (email != null && !email.isEmpty()) {
                tvEmailDisplay.setText(email);
            } else {
                tvEmailDisplay.setText("Email not found");
            }
            loadProfileImage();
        } else {
            tvUsernameDisplay.setText("N/A");
            tvEmailDisplay.setText("N/A");
            if (profileImage != null) {
                profileImage.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }

        // Configure button visibility based on isAdmin status
        if (isAdmin) {
            Log.d(TAG, "User IS Admin. Configuring admin view.");
            if (btnBackToAdminDashboard != null) {
                btnBackToAdminDashboard.setVisibility(View.VISIBLE);
                btnBackToAdminDashboard.setOnClickListener(v -> {
                    Intent intent = new Intent(AccountSettingsActivity.this, AdminActivity.class);
                    if (currentUsername != null && !currentUsername.equals("Guest")) {
                        intent.putExtra("USERNAME", currentUsername);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                });
            } else {
                Log.e(TAG, "btnBackToAdminDashboard is NULL.");
            }
        } else {
            Log.d(TAG, "User IS NOT Admin. Configuring regular user view.");
            if (btnBackToAdminDashboard != null) {
                btnBackToAdminDashboard.setVisibility(View.GONE);
            }
        }

        // Set click listeners
        if (btnChangeProfilePicture != null) {
            btnChangeProfilePicture.setOnClickListener(v -> {
                if (currentUsername.equals("Guest")) {
                    Toast.makeText(AccountSettingsActivity.this, "Please log in to change picture.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Consider using ACTION_GET_CONTENT for broader compatibility if ACTION_PICK has issues
                // Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                // pickImageIntent.setType("image/*");
                startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
            });
        }

        if (btnRequestCampaign != null) {
            btnRequestCampaign.setOnClickListener(v -> {
                Intent intent = new Intent(AccountSettingsActivity.this, RequestCampaignActivity.class);
                if (currentUsername != null && !currentUsername.equals("Guest")) {
                    intent.putExtra("USERNAME", currentUsername);
                    if (tvEmailDisplay.getText() != null && !tvEmailDisplay.getText().toString().equals("Email not found") && !tvEmailDisplay.getText().toString().equals("N/A")) {
                        intent.putExtra("USER_EMAIL", tvEmailDisplay.getText().toString());
                    }
                } else {
                    Toast.makeText(AccountSettingsActivity.this, "Please log in to request a campaign.", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(intent);
            });
        }

        if (btnAboutUs != null) {
            btnAboutUs.setOnClickListener(v -> {
                Intent intent = new Intent(AccountSettingsActivity.this, AboutActivity.class);
                startActivity(intent);
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Toast.makeText(AccountSettingsActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AccountSettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAffinity();
            });
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadProfileImage() {
        if (profileImage == null || currentUsername == null || currentUsername.equals("Guest")) {
            if (profileImage != null) {
                profileImage.setImageResource(android.R.drawable.sym_def_app_icon);
            }
            return;
        }

        String imageUriString = dbHelper.getUserProfileImageUri(currentUsername);
        if (imageUriString != null && !imageUriString.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(imageUriString);
                profileImage.setImageURI(imageUri);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing or loading image URI: " + imageUriString, e);
                profileImage.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        } else {
            profileImage.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            try {
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);

                if (profileImage != null) {
                    profileImage.setImageURI(selectedImageUri);
                }

                // Save the new URI string to the database
                if (currentUsername != null && !currentUsername.equals("Guest")) {
                    boolean success = dbHelper.updateUserProfileImageUri(currentUsername, selectedImageUri.toString());
                    if (success) {
                        Toast.makeText(this, "Profile picture updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save picture.", Toast.LENGTH_SHORT).show();
                        // Optionally, revert the image view if save fails by reloading the old one
                        // loadProfileImage();
                    }
                }
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException taking persistable URI permission: " + e.getMessage());
                Toast.makeText(this, "Could not get permission for the selected image.", Toast.LENGTH_LONG).show();
                // Optionally, revert image or show placeholder
                if (profileImage != null) {
                    loadProfileImage(); // Revert to old/placeholder if permission fails
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing selected image: " + e.getMessage());
                Toast.makeText(this, "Error processing image.", Toast.LENGTH_SHORT).show();
                if (profileImage != null) {
                    loadProfileImage(); // Revert to old/placeholder if processing fails
                }
            }
        }
    }

}

