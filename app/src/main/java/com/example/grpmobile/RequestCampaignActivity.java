package com.example.grpmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
// Removed: import android.provider.MediaStore; // No longer used after changing image picker action
import android.text.TextUtils;
import android.util.Log; // For logging
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RequestCampaignActivity extends AppCompatActivity {

    private EditText etCampaignTitle, etCampaignDescription, etCampaignLocation, etCampaignDate, etPaypalUrl;
    private EditText etDonationGoal;
    private ImageView ivCampaignImagePreview;
    private Button btnSelectImage;
    private Button btnSubmitRequest, btnCancelRequest;

    private Uri selectedImageUri = null;
    private String currentUsername;
    private DBHelper dbHelper;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_campaign);

        dbHelper = new DBHelper(this);

        currentUsername = getIntent().getStringExtra("USERNAME");
        // currentUserEmail was removed, we will fetch it using currentUsername if needed

        etCampaignTitle = findViewById(R.id.etCampaignTitle);
        etCampaignDescription = findViewById(R.id.etCampaignDescription);
        etCampaignLocation = findViewById(R.id.etCampaignLocation);
        etCampaignDate = findViewById(R.id.etCampaignDate);
        etPaypalUrl = findViewById(R.id.etPaypalUrl);
        etDonationGoal = findViewById(R.id.etDonationGoal);
        ivCampaignImagePreview = findViewById(R.id.ivCampaignImagePreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnCancelRequest = findViewById(R.id.btnCancelRequest);

        ivCampaignImagePreview.setImageResource(R.drawable.ic_launcher_background); // Default placeholder

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        btnSubmitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCampaignRequest();
            }
        });

        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivCampaignImagePreview.setImageURI(selectedImageUri);

            try {
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                Log.i("RequestCampaign", "Successfully took persistable URI permission for: " + selectedImageUri);
            } catch (SecurityException e) {
                Log.e("RequestCampaign", "SecurityException: Failed to take persistable URI permission.", e);
                Toast.makeText(this, "Failed to get persistent image access. Image might not be available later.", Toast.LENGTH_LONG).show();
            }
        } else {
            if (selectedImageUri == null) {
                ivCampaignImagePreview.setImageResource(R.drawable.ic_launcher_background); // Reset to placeholder if selection cancelled
            }
        }
    }

    private void submitCampaignRequest() {
        String title = etCampaignTitle.getText().toString().trim();
        String description = etCampaignDescription.getText().toString().trim();
        String location = etCampaignLocation.getText().toString().trim();
        String date = etCampaignDate.getText().toString().trim();
        String paypalUrl = etPaypalUrl.getText().toString().trim();
        String donationGoalStr = etDonationGoal.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(location) ||
                TextUtils.isEmpty(date) || TextUtils.isEmpty(donationGoalStr)) {
            Toast.makeText(this, "Please fill all required fields (Title, Description, Location, Date, Donation Goal).", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUsername == null || currentUsername.isEmpty() || "N/A".equals(currentUsername)) {
            Toast.makeText(this, "User information missing. Cannot submit campaign. Please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        // MODIFIED: Fetch the submitter's email to use as campaign contact email
        String contactEmail = dbHelper.getUserEmail(currentUsername);
        if (contactEmail == null || contactEmail.isEmpty()) {
            Log.e("RequestCampaign", "Could not retrieve email for user: " + currentUsername + ". Campaign will be submitted without contact email.");
            Toast.makeText(this, "Could not retrieve user email. Submitting without contact email.", Toast.LENGTH_LONG).show();
        }


        double donationGoal = 0;
        try {
            donationGoal = Double.parseDouble(donationGoalStr);
            if (donationGoal <= 0) {
                Toast.makeText(this, "Donation goal must be a positive number.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid donation goal format.", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageUriString = null;
        if (selectedImageUri != null) {
            imageUriString = selectedImageUri.toString();
        }

        // MODIFIED: dbHelper.addCampaign now includes contactEmail
        // Signature: (title, description, location, date, imageUri, submitterUsername, contactEmail, paypalUrl, donationGoal)
        boolean success = dbHelper.addCampaign(title, description, location, date,
                imageUriString, currentUsername, contactEmail, paypalUrl, donationGoal);

        if (success) {
            Toast.makeText(this, "Campaign request submitted!", Toast.LENGTH_LONG).show();
            Log.i("RequestCampaign", "Campaign Submission added: " + title + " by " + currentUsername + " with contact email: " + contactEmail);
            finish();
        } else {
            Toast.makeText(this, "Failed to submit campaign request.", Toast.LENGTH_SHORT).show();
            Log.e("RequestCampaign", "Failed to add campaign to DB: " + title);
        }
    }
}
