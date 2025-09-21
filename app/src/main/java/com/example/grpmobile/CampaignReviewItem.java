package com.example.grpmobile;

public class CampaignReviewItem {
    private int id;
    private String title;
    private String description;
    private String location;
    private String date;
    private String imageUriString;
    private String submitterUsername;
    private String contactEmail; // Added
    private String paypalUrl;    // Added
    private double donationGoal;
    private String status;
    // Optional: if you also want to track current donations for review items
    // private double currentDonationAmount;

    // Constructor updated to accept 11 arguments
    public CampaignReviewItem(int id, String title, String description, String location, String date,
                              String imageUriString, String submitterUsername,
                              String contactEmail, String paypalUrl, // New fields added here
                              double donationGoal, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.imageUriString = imageUriString;
        this.submitterUsername = submitterUsername;
        this.contactEmail = contactEmail;
        this.paypalUrl = paypalUrl;
        this.donationGoal = donationGoal;
        this.status = status;
        // this.currentDonationAmount = 0; // Initialize if you add this field
    }

    // Getters
    public int getId() {
        return id;
    }

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

    public String getImageUriString() {
        return imageUriString;
    }

    public String getSubmitterUsername() {
        return submitterUsername;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getPaypalUrl() {
        return paypalUrl;
    }

    public double getDonationGoal() {
        return donationGoal;
    }

    public String getStatus() {
        return status;
    }

    // Optional: Getter for current donation amount if you add it
    // public double getCurrentDonationAmount() { return currentDonationAmount; }

    // Optional: Setters if needed, for example, if status could be updated on the item itself
    public void setStatus(String status) {
        this.status = status;
    }
}
