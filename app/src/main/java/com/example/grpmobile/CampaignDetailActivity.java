package com.example.grpmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View; // Added for View.OnClickListener
import android.widget.Button;
// REMOVED: import android.widget.EditText;
import android.widget.ImageView;
// REMOVED: import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.NumberFormat;
import java.util.Locale;

public class CampaignDetailActivity extends AppCompatActivity {

    private ImageView ivCampaignDetailImage;
    private TextView tvCampaignDetailTitle;
    private TextView tvCampaignDetailDescription;
    private TextView tvCampaignDetailLocation;
    private TextView tvCampaignDetailDate;
    private TextView tvCampaignDetailDonationGoal;
    private Button btnBack;

    // private EditText etDonorName, etDonorContact, etDonationAmount;
    private Button btnDonate;

    private double currentDonation;
    private double targetDonation;
    private String campaignPaypalUrl;

    private static final String TAG = "CampaignDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_campaign_detail);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        ivCampaignDetailImage = findViewById(R.id.ivCampaignDetailImage);
        tvCampaignDetailTitle = findViewById(R.id.tvCampaignDetailTitle);
        tvCampaignDetailDescription = findViewById(R.id.tvCampaignDetailDescription);
        tvCampaignDetailLocation = findViewById(R.id.tvCampaignDetailLocation);
        tvCampaignDetailDate = findViewById(R.id.tvCampaignDetailDate);
        tvCampaignDetailDonationGoal = findViewById(R.id.tvCampaignDetailDonationGoal);
        btnBack = findViewById(R.id.btnBack);

        btnDonate = findViewById(R.id.btnDonate);

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String location = intent.getStringExtra("location");
            String date = intent.getStringExtra("date");
            String imageUriString = intent.getStringExtra("imageUriString");
            campaignPaypalUrl = intent.getStringExtra("paypalUrl");
            int imageResId = intent.getIntExtra("imageResId", 0);
            currentDonation = intent.getDoubleExtra("currentDonation", 0.0);
            targetDonation = intent.getDoubleExtra("targetDonation", 0.0);

            tvCampaignDetailTitle.setText(title);
            tvCampaignDetailDescription.setText(description);
            tvCampaignDetailLocation.setText(location);
            tvCampaignDetailDate.setText(date);

            updateDonationUI(); // Call this to set donation goal text

            if (!TextUtils.isEmpty(imageUriString)) {
                try {
                    Uri imageUri = Uri.parse(imageUriString);
                    ivCampaignDetailImage.setImageURI(imageUri);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading image URI: " + imageUriString, e);
                    if (imageResId != 0) {
                        ivCampaignDetailImage.setImageResource(imageResId);
                    } else {
                        ivCampaignDetailImage.setImageResource(R.drawable.ic_launcher_background);
                    }
                }
            } else if (imageResId != 0) {
                ivCampaignDetailImage.setImageResource(imageResId);
            } else {
                ivCampaignDetailImage.setImageResource(R.drawable.ic_launcher_background);
            }

        } else {
            Toast.makeText(this, "Could not load campaign details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // MODIFIED: OnClickListener for btnDonate - simplified
        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (campaignPaypalUrl != null && !campaignPaypalUrl.trim().isEmpty()) {
                    String url = campaignPaypalUrl.trim();
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://" + url;
                    }
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        Log.e(TAG, "Could not open PayPal URL: " + url, e);
                        Toast.makeText(CampaignDetailActivity.this, "Invalid donation link.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CampaignDetailActivity.this, "No donation link available for this campaign.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateDonationUI() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ms", "MY"));
        String formattedCurrentDonation = currencyFormat.format(currentDonation); // Still needed if showing current/target
        String formattedTargetDonation = currencyFormat.format(targetDonation);

        // If you want only target: tvCampaignDetailDonationGoal.setText("Goal: " + formattedTargetDonation);
        tvCampaignDetailDonationGoal.setText("Goal: " + formattedTargetDonation);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

