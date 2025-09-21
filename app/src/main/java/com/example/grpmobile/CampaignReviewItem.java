package com.example.grpmobile;

public class CampaignReviewItem {
    private int id;
    private String title;
    private String description;
    private String location;
    private String date;
    private String contactEmail;
    private String imageUriString;
    private double donationGoal;
    private String submitterUsername;
    private String status; // Added status field

    public CampaignReviewItem(int id, String title, String description, String location, String date,
                              String imageUriString, String submitterUsername, String contactEmail,
                              double donationGoal, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.imageUriString = imageUriString;
        this.submitterUsername = submitterUsername;
        this.contactEmail = contactEmail;
        this.donationGoal = donationGoal;
        this.status = status; // Assign status
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getImageUriString() { return imageUriString; } // Getter for image URI
    public String getSubmitterUsername() { return submitterUsername; }
    public String getContactEmail() { return contactEmail; }
    public double getDonationGoal() { return donationGoal; }
    public String getStatus() { return status; } // Getter for status
}
