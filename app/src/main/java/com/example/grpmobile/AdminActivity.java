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
    private String currentAdminUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d(TAG, "onCreate called");

        currentAdminUsername = getIntent().getStringExtra("USERNAME");
        if (currentAdminUsername == null || currentAdminUsername.isEmpty()) {
            Log.e(TAG, "Username not passed (expected key 'USERNAME')! Using fallback.");
            Toast.makeText(this, "Error: Admin user data not loaded.", Toast.LENGTH_LONG).show();
            // finish(); // Consider finishing if critical
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

        if (profileImageViewAdmin != null) {
            profileImageViewAdmin.setImageResource(R.drawable.ic_launcher_background);
        }

        recyclerViewCampaignReviews.setLayoutManager(new LinearLayoutManager(this));
        campaignReviewAdapter = new CampaignReviewAdapter(pendingCampaignsList, this);
        recyclerViewCampaignReviews.setAdapter(campaignReviewAdapter);

        if (textViewAdminBack != null) {
            textViewAdminBack.setOnClickListener(v -> {
                Intent intent = new Intent(AdminActivity.this, UserActivity.class);
                intent.putExtra("USERNAME", currentAdminUsername);
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
                finish();
            });
        }

        if (profileImageViewAdmin != null) {
            profileImageViewAdmin.setOnClickListener(v -> {
                String usernameToPass = currentAdminUsername;
                if (usernameToPass == null || usernameToPass.isEmpty()) {
                    Log.w(TAG, "Admin username is null/empty when PFP clicked. Cannot proceed.");
                    Toast.makeText(AdminActivity.this, "Cannot load profile: User ID missing.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(AdminActivity.this, AccountSettingsActivity.class);
                intent.putExtra("USERNAME", usernameToPass);
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Calling logAllCampaigns from DBHelper and loading pending campaigns.");
        if (dbHelper != null) {
            // dbHelper.logAllCampaigns(); // You might want to ensure this logs both email and paypalUrl
            loadPendingCampaignsFromDb();
        }
    }

    private void loadPendingCampaignsFromDb() {
        Log.d(TAG, "loadPendingCampaignsFromDb: Clearing pendingCampaignsList.");
        pendingCampaignsList.clear();
        Cursor cursor = null;

        try {
            // Ensure dbHelper.getPendingCampaigns() returns a cursor that includes
            // both COLUMN_CAMPAIGN_CONTACT_EMAIL and COLUMN_CAMPAIGN_PAYPAL_URL
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

                        // MODIFIED: Fetch both contactEmail and paypalUrl
                        // Ensure these columns exist in your DBHelper.TABLE_CAMPAIGNS and are selected by getPendingCampaigns()
                        String contactEmail = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_CONTACT_EMAIL));
                        String paypalUrl = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_PAYPAL_URL));

                        double donationGoal = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_DONATION_GOAL));
                        String status = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAMPAIGN_STATUS));

                        // MODIFIED: Assuming CampaignReviewItem constructor now accepts both contactEmail and paypalUrl
                        // The order of contactEmail and paypalUrl here must match your CampaignReviewItem constructor.
                        pendingCampaignsList.add(new CampaignReviewItem(id, title, description, location, date,
                                imageUri, submitter, contactEmail, paypalUrl, donationGoal, status));
                    } while (cursor.moveToNext());
                }
            } else {
                Log.e(TAG, "loadPendingCampaignsFromDb: Cursor was null.");
            }
        } catch (Exception e) {
            Log.e(TAG, "loadPendingCampaignsFromDb: Exception while processing cursor.", e);
            // Example: If a column is missing, an IllegalArgumentException ("column ... does not exist") will be thrown here.
            Toast.makeText(this, "Error loading campaign data. Columns might be missing.", Toast.LENGTH_LONG).show();
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

    // ADDED: Implementation for onViewDetails from OnCampaignActionListener
    @Override
    public void onViewDetails(CampaignReviewItem campaign) {
        Log.d(TAG, "onViewDetails called for campaign: " + campaign.getTitle());
        Intent intent = new Intent(this, CampaignDetailActivity.class);
        intent.putExtra("title", campaign.getTitle());
        intent.putExtra("description", campaign.getDescription());
        intent.putExtra("location", campaign.getLocation());
        intent.putExtra("date", campaign.getDate());
        intent.putExtra("imageUriString", campaign.getImageUriString());

        // ADDED: Pass both contactEmail and paypalUrl
        // Ensure CampaignReviewItem has getContactEmail() and getPaypalUrl()
        intent.putExtra("contactEmail", campaign.getContactEmail());
        intent.putExtra("paypalUrl", campaign.getPaypalUrl());

        intent.putExtra("targetDonation", campaign.getDonationGoal());
        // You might also want to pass current donations if CampaignReviewItem holds it
        // intent.putExtra("currentDonation", campaign.getCurrentDonationAmount());
        // Pass any other necessary details

        startActivity(intent);
    }
}
