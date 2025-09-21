package com.example.grpmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private ProgressBar pbCampaignDetailProgress;
    private Button btnBack;

    private EditText etDonorName, etDonorContact, etDonationAmount;
    private Button btnDonate;

    private double currentDonation;
    private double targetDonation;

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
        pbCampaignDetailProgress = findViewById(R.id.pbCampaignDetailProgress);
        btnBack = findViewById(R.id.btnBack);

        etDonorName = findViewById(R.id.etDonorName);
        etDonorContact = findViewById(R.id.etDonorContact);
        etDonationAmount = findViewById(R.id.etDonationAmount);
        btnDonate = findViewById(R.id.btnDonate);

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String location = intent.getStringExtra("location");
            String date = intent.getStringExtra("date");
            String imageUriString = intent.getStringExtra("imageUriString");
            int imageResId = intent.getIntExtra("imageResId", 0);
            currentDonation = intent.getDoubleExtra("currentDonation", 0.0);
            targetDonation = intent.getDoubleExtra("targetDonation", 0.0);

            tvCampaignDetailTitle.setText(title);
            tvCampaignDetailDescription.setText(description);
            tvCampaignDetailLocation.setText(location);
            tvCampaignDetailDate.setText(date);

            updateDonationUI();

            // Image loading logic
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

        btnDonate.setOnClickListener(v -> handleDonation());
    }

    private void updateDonationUI() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ms", "MY"));
        String formattedCurrentDonation = currencyFormat.format(currentDonation);
        String formattedTargetDonation = currencyFormat.format(targetDonation);

        tvCampaignDetailDonationGoal.setText(
                String.format(Locale.getDefault(),
                        "Goal: %s / %s",
                        formattedCurrentDonation,
                        formattedTargetDonation));

        if (targetDonation > 0) {
            int progress = (int) ((currentDonation / targetDonation) * 100);
            pbCampaignDetailProgress.setProgress(Math.min(progress, 100));
        } else {
            pbCampaignDetailProgress.setProgress(0);
        }
    }

    private void handleDonation() {
        String name = etDonorName.getText().toString().trim();
        String contact = etDonorContact.getText().toString().trim();
        String amountStr = etDonationAmount.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(contact)) {
            Toast.makeText(this, "Please enter your contact number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Please enter donation amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double donationAmount;
        try {
            donationAmount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid donation amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (donationAmount <= 0) {
            Toast.makeText(this, "Donation must be greater than RM 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update donation total
        currentDonation += donationAmount;
        updateDonationUI();

        String msg = String.format(Locale.getDefault(),
                "Thank you %s!\nDonation: RM%.2f\nContact: %s",
                name, donationAmount, contact);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        // Clear inputs
        etDonorName.setText("");
        etDonorContact.setText("");
        etDonationAmount.setText("");
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
