package com.example.grpmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);  // Set the content view to activity_about.xml layout file

        // Get the TextView components from the layout
        TextView aboutText = findViewById(R.id.aboutText);
        TextView contactEmail = findViewById(R.id.contactEmail);
        TextView contactPhone = findViewById(R.id.contactPhone);
        Button btnBackToAccountSettings = findViewById(R.id.btnBackToAccountSettings);

        // Set the text content for the About section
        String aboutContent = "UniteFund is a global leader in fundraising platforms, dedicated to using innovative technology to help organizations, social enterprises, and individuals raise funds. Our mission is to connect donors with those in need, making a wider social impact.\n\n" +
                "We offer various fundraising tools, including online donations, charity events, and investment platforms. We believe every act of kindness can bring about meaningful change in the world.";

        // Set the text for the "About Us" section
        aboutText.setText(aboutContent);

        // Set the email contact info
        contactEmail.setText("Contact Us: support@unitefund.com");

        // Set the phone number contact info
        contactPhone.setText("Phone: +60-11114567");

        if (btnBackToAccountSettings != null) {
            btnBackToAccountSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Simply finish this activity to go back to the previous one
                }
            });
        }
    }
}
