package com.example.grpmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grpmobile.R;

public class RegistrationActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, tvLocation, tvDate, tvStatus;  // TextViews to display activity details
    private ImageView imageActivity;  // ImageView to display activity image
    private EditText etName, etContact, etDonationAmount;  // EditText fields for user input (name, contact, donation)
    private Button btnSubmit;  // Button to submit the registration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);  // Set the content view

        // Initialize the views
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvDate = findViewById(R.id.tvDate);
        tvStatus = findViewById(R.id.tvStatus);
        imageActivity = findViewById(R.id.imageActivity);

        etName = findViewById(R.id.etName);
        etContact = findViewById(R.id.etContact);
        etDonationAmount = findViewById(R.id.etDonationAmount);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Get data passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tvTitle.setText(extras.getString("title"));
            tvDescription.setText(extras.getString("description"));
            tvLocation.setText("Location: " + extras.getString("location"));
            tvDate.setText("Date: " + extras.getString("date"));
            tvStatus.setText(extras.getString("status"));
            imageActivity.setImageResource(extras.getInt("imageResId"));
        }

        // Set click listener for the submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String name = etName.getText().toString().trim();
                String contact = etContact.getText().toString().trim();
                String donationStr = etDonationAmount.getText().toString().trim();

                // Input validation
                if (name.isEmpty()) {
                    etName.setError("Please enter your name");
                    etName.requestFocus();
                    return;
                }

                if (contact.isEmpty()) {
                    etContact.setError("Please enter your contact number");
                    etContact.requestFocus();
                    return;
                }

                if (donationStr.isEmpty()) {
                    etDonationAmount.setError("Please enter donation amount");
                    etDonationAmount.requestFocus();
                    return;
                }

                // Parse donation amount to double
                double donation;
                try {
                    donation = Double.parseDouble(donationStr);
                } catch (NumberFormatException e) {
                    etDonationAmount.setError("Invalid amount");
                    etDonationAmount.requestFocus();
                    return;
                }

                // Format the donation amount to 2 decimal places
                String formattedAmount = String.format("%.2f", donation);

                // Show a thank you message with the donation details
                Toast.makeText(RegistrationActivity.this,
                        "Thank you " + name + " for donating RM" + formattedAmount +
                                ". We may contact you at " + contact,
                        Toast.LENGTH_LONG).show();

                // Close the current activity after submission
                finish();
            }
        });
    }
}
