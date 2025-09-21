package com.example.grpmobile;

import android.content.Intent;
import android.database.Cursor; // Added for DBHelper
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // Added for logging
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
// Removed: import android.widget.TextView; // Was for an optional welcome message
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// Removed: import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements ActivityAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ActivityAdapter activityAdapter;
    private List<ActivityItem> activityList;
    private EditText etSearch;
    private CircleImageView profileImageView;
    private DBHelper dbHelper;

    // Added fields to store user information
    private String currentUsername;
    private String currentUserEmail; // User's login email, not campaign contact email

    // Optional: If you have a TextView for a welcome message
    // private TextView tvWelcomeUser;
    // Removed: private FloatingActionButton fabRequestCampaign;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        dbHelper = new DBHelper(this);
        activityList = new ArrayList<>();

        // Retrieve USERNAME and USER_EMAIL from Intent
        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("USERNAME");
        currentUserEmail = intent.getStringExtra("USER_EMAIL");

        // Log the received values to help with debugging
        Log.d("UserActivity", "Received USERNAME: " + currentUsername);
        Log.d("UserActivity", "Received USER_EMAIL (login email): " + currentUserEmail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        profileImageView = findViewById(R.id.profile_image_view);
        // Removed: fabRequestCampaign = findViewById(R.id.fabRequestCampaign);

        if (profileImageView != null) {
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent accountSettingsIntent = new Intent(UserActivity.this, AccountSettingsActivity.class);
                    // Pass the username and email to AccountSettingsActivity
                    accountSettingsIntent.putExtra("USERNAME", currentUsername);
                    accountSettingsIntent.putExtra("USER_EMAIL", currentUserEmail); // User's login email
                    startActivity(accountSettingsIntent);
                }
            });
        }

        // Removed OnClickListener for fabRequestCampaign

        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerViewActivities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityAdapter = new ActivityAdapter(activityList, this); // 'this' refers to UserActivity implementing OnItemClickListener
        recyclerView.setAdapter(activityAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterActivities(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApprovedCampaignsFromDb();
    }

    private void loadApprovedCampaignsFromDb() {
        activityList.clear();
        Cursor cursor = null; // Initialize cursor to null
        try { // Add try-finally block for cursor management
            cursor = dbHelper.getApprovedCampaigns();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Fetch all necessary columns from DBHelper for campaigns
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_DESCRIPTION));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_LOCATION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_DATE));
                    String imageUriStringFromDb = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_IMAGE_URI));
                    double targetDonation = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_DONATION_GOAL));
                    // Assuming current donation is stored as REAL or INTEGER, getDouble for flexibility
                    double currentDonationFromDb = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_CURRENT_DONATION));

                    // MODIFIED: Fetch contactEmail and paypalUrl
                    String contactEmail = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_CONTACT_EMAIL));
                    String paypalUrl = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_PAYPAL_URL));

                    // MODIFIED: Constructor call for ActivityItem to include new fields
                    // The order must match the ActivityItem constructor:
                    // (title, desc, loc, date, status, imageUri, imageResId, currentDon, targetDon, contactEmail, paypalUrl)
                    ActivityItem item = new ActivityItem(
                            title,
                            description,
                            location,
                            date,
                            "Ongoing", // Status for display - you might want to fetch actual status if it can vary for approved items
                            imageUriStringFromDb, // imageUriString
                            0,      // imageResId (using URI string instead, so 0 is placeholder if not applicable)
                            currentDonationFromDb,
                            targetDonation,
                            contactEmail, // new field
                            paypalUrl     // new field
                    );
                    activityList.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("UserActivity", "Error loading approved campaigns from DB", e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure cursor is closed
            }
        }


        activityAdapter.updateList(activityList); // Update adapter with new list

        if (activityList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            // Consider showing a TextView here indicating no campaigns are available.
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
        Log.d("UserActivity", "Loaded " + activityList.size() + " approved campaigns.");
    }

    private void filterActivities(String query) {
        List<ActivityItem> filteredList = new ArrayList<>();
        if (activityList != null) { // Check if activityList is initialized
            for (ActivityItem item : activityList) {
                // Check if title, description, or location are not null before calling toLowerCase()
                if (item.getTitle() != null && item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription() != null && item.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                        item.getLocation() != null && item.getLocation().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        if (activityAdapter != null) { // Check if adapter is initialized
            activityAdapter.updateList(filteredList);
        }
    }

    // MODIFIED: onItemClick method to include contactEmail and paypalUrl in intent extras
    @Override
    public void onItemClick(ActivityItem item) {
        Intent intent = new Intent(UserActivity.this, CampaignDetailActivity.class);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("location", item.getLocation());
        intent.putExtra("date", item.getDate());
        intent.putExtra("imageUriString", item.getImageUriString());
        intent.putExtra("imageResId", item.getImageResId());
        intent.putExtra("currentDonation", item.getCurrentDonation()); // Passed as double
        intent.putExtra("targetDonation", item.getTargetDonation());

        // ADDED: Pass contactEmail and paypalUrl to CampaignDetailActivity
        // These getters must exist in ActivityItem.java
        intent.putExtra("contactEmail", item.getContactEmail());
        intent.putExtra("paypalUrl", item.getPaypalUrl());

        startActivity(intent);
    }
}
