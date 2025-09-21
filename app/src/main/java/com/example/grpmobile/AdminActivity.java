package com.example.grpmobile;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity implements CampaignReviewAdapter.OnCampaignActionListener {

    private static final String TAG = "AdminActivity";

    private RecyclerView recyclerViewCampaignReviews;
    private CampaignReviewAdapter campaignReviewAdapter;
    private List<CampaignReviewItem> pendingCampaignsList;
    private DBHelper dbHelper;
    private TextView tvNoPendingCampaigns;
    private TextView textViewAdminBack;
    private CircleImageView profileImageViewAdmin;
    private String currentAdminUsername; // Added to store the admin username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d(TAG, "onCreate called");

        // Retrieve the admin username passed with the key "USERNAME"
        currentAdminUsername = getIntent().getStringExtra("USERNAME");
        if (currentAdminUsername == null || currentAdminUsername.isEmpty()) {
            Log.e(TAG, "Username not passed (expected key 'USERNAME')! Using fallback.");
            Toast.makeText(this, "Error: Admin user data not loaded.", Toast.LENGTH_LONG).show();
            // Consider finishing the activity if the username is critical and not available
            // finish();
            // return;
        } else {
            Log.d(TAG, "Admin username received (key 'USERNAME'): " + currentAdminUsername);
        }

        dbHelper = new DBHelper(this);
        pendingCampaignsList = new ArrayList<>();

        recyclerViewCampaignReviews = findViewById(R.id.recyclerViewAdminCampaigns);
        tvNoPendingCampaigns = findViewById(R.id.tvNoPendingCampaigns);
        textViewAdminBack = findViewById(R.id.textViewAdminBack);
        profileImageViewAdmin = findViewById(R.id.profileImageViewAdmin);

        // Set the default profile picture
        if (profileImageViewAdmin != null) {
            profileImageViewAdmin.setImageResource(R.drawable.ic_launcher_background); // Set default image
        }

        recyclerViewCampaignReviews.setLayoutManager(new LinearLayoutManager(this));
        campaignReviewAdapter = new CampaignReviewAdapter(pendingCampaignsList, this);
        recyclerViewCampaignReviews.setAdapter(campaignReviewAdapter);

        if (textViewAdminBack != null) {
            textViewAdminBack.setOnClickListener(v -> {
                Intent intent = new Intent(AdminActivity.this, UserActivity.class);
                // Pass the username to UserActivity if needed, though it might get its own from login
                intent.putExtra("USERNAME", currentAdminUsername);
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
                finish();
            });
        }

        if (profileImageViewAdmin != null) {
            profileImageViewAdmin.setOnClickListener(v -> {
                String usernameToPass;
                if (currentAdminUsername != null && !currentAdminUsername.isEmpty()) {
                    usernameToPass = currentAdminUsername;
                } else {
                    Log.w(TAG, "Admin username is null/empty when PFP clicked. Cannot proceed.");
                    Toast.makeText(AdminActivity.this, "Cannot load profile: User ID missing.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(AdminActivity.this, AccountSettingsActivity.class);
                intent.putExtra("USERNAME", usernameToPass);
                intent.putExtra("IS_ADMIN", true); // <<< ADD THIS LINE
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Calling logAllCampaigns from DBHelper and loading pending campaigns.");
        if (dbHelper != null) { // Check if dbHelper is initialized
            dbHelper.logAllCampaigns();
            loadPendingCampaignsFromDb();
        }
    }

    private void loadPendingCampaignsFromDb() {
        Log.d(TAG, "loadPendingCampaignsFromDb: Clearing pendingCampaignsList.");
        pendingCampaignsList.clear();
        Cursor cursor = null;

        try {
            cursor = dbHelper.getPendingCampaigns();
            if (cursor != null) {
                Log.d(TAG, "loadPendingCampaignsFromDb: Cursor count: " + cursor.getCount());
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_ID));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_TITLE));
                        String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_DESCRIPTION));
                        String location = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_LOCATION));
                        String date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_DATE));
                        String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_IMAGE_URI));
                        String submitter = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_SUBMITTER_USERNAME));
                        String contactEmail = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_CONTACT_EMAIL));
                        double donationGoal = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_DONATION_GOAL));
                        String status = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_STATUS));

                        pendingCampaignsList.add(new CampaignReviewItem(id, title, description, location, date,
                                imageUri, submitter, contactEmail, donationGoal, status));
                    } while (cursor.moveToNext());
                }
            } else {
                Log.e(TAG, "loadPendingCampaignsFromDb: Cursor was null.");
            }
        } catch (Exception e) {
            Log.e(TAG, "loadPendingCampaignsFromDb: Exception while processing cursor.", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        campaignReviewAdapter.updateList(pendingCampaignsList);

        if (pendingCampaignsList.isEmpty()) {
            if (tvNoPendingCampaigns != null) tvNoPendingCampaigns.setVisibility(View.VISIBLE);
            if (recyclerViewCampaignReviews != null) recyclerViewCampaignReviews.setVisibility(View.GONE);
        } else {
            if (tvNoPendingCampaigns != null) tvNoPendingCampaigns.setVisibility(View.GONE);
            if (recyclerViewCampaignReviews != null) recyclerViewCampaignReviews.setVisibility(View.VISIBLE);
        }
        Log.i(TAG, "loadPendingCampaignsFromDb: Finished. Loaded " + pendingCampaignsList.size() + " campaigns.");
    }

    @Override
    public void onApprove(CampaignReviewItem campaign, int position) {
        Log.d(TAG, "onApprove called for campaign: " + campaign.getTitle());
        boolean success = dbHelper.updateCampaignStatus(campaign.getId(), "approved");
        if (success) {
            Toast.makeText(this, "Campaign Approved: " + campaign.getTitle(), Toast.LENGTH_SHORT).show();
            campaignReviewAdapter.removeItem(position);
            if (campaignReviewAdapter.getItemCount() == 0) {
                if (tvNoPendingCampaigns != null) tvNoPendingCampaigns.setVisibility(View.VISIBLE);
                if (recyclerViewCampaignReviews != null) recyclerViewCampaignReviews.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Failed to approve campaign: " + campaign.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReject(CampaignReviewItem campaign, int position) {
        Log.d(TAG, "onReject called for campaign: " + campaign.getTitle());
        boolean success = dbHelper.updateCampaignStatus(campaign.getId(), "rejected");
        if (success) {
            Toast.makeText(this, "Campaign Rejected: " + campaign.getTitle(), Toast.LENGTH_SHORT).show();
            campaignReviewAdapter.removeItem(position);
            if (campaignReviewAdapter.getItemCount() == 0) {
                if (tvNoPendingCampaigns != null) tvNoPendingCampaigns.setVisibility(View.VISIBLE);
                if (recyclerViewCampaignReviews != null) recyclerViewCampaignReviews.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Failed to reject campaign: " + campaign.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }
}
