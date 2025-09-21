package com.example.grpmobile;

// No special Android imports needed for this POJO unless using Parcelable

public class ActivityItem {
    // Existing fields based on ActivityAdapter usage
    private String title;
    private String description;
    private String location;
    private String date;
    private String status;
    private String imageUriString; // For URI from database/gallery
    private int imageResId;      // For drawable resource ID (e.g., local placeholders)
    private double currentDonation;
    private double targetDonation;
    // int donationProgress; // This can be calculated, or stored if fetched directly

    // New fields
    private String contactEmail;
    private String paypalUrl;

    // Constructor
    public ActivityItem(String title, String description, String location, String date, String status,
                        String imageUriString, int imageResId,
                        double currentDonation, double targetDonation,
                        String contactEmail, String paypalUrl) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.status = status;
        this.imageUriString = imageUriString;
        this.imageResId = imageResId;
        this.currentDonation = currentDonation;
        this.targetDonation = targetDonation;
        // this.donationProgress = calculateProgress(currentDonation, targetDonation);

        // Initialize new fields
        this.contactEmail = contactEmail;
        this.paypalUrl = paypalUrl;
    }

    // Getters for all fields
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUriString() {
        return imageUriString;
    }

    public int getImageResId() {
        return imageResId;
    }

    public double getCurrentDonation() {
        return currentDonation;
    }

    public double getTargetDonation() {
        return targetDonation;
    }

    // Getter for donationProgress - can be calculated on the fly
    public int getDonationProgress() {
        if (targetDonation <= 0) {
            return 0;
        }
        double progress = (currentDonation / targetDonation) * 100;
        return (int) Math.min(progress, 100); // Cap at 100
    }

    // Getters for new fields
    public String getContactEmail() {
        return contactEmail;
    }

    public String getPaypalUrl() {
        return paypalUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    // Helper method to calculate progress (if not storing it directly)
    // private int calculateProgress(double current, double target) {
    //     if (target <= 0) {
    //         return 0;
    //     }
    //     double progress = (current / target) * 100;
    //     return (int) Math.min(progress, 100); // Cap at 100
    // }
}
