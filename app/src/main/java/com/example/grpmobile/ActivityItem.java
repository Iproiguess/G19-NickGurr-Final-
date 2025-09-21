package com.example.grpmobile;

public class ActivityItem {
    private String title;
    private String description;
    private String location;
    private String date;
    private String status;
    private int imageResId;          // For drawable resources (like default images)
    private String imageUriString;   // New: For URI of uploaded images (can be null)
    private int currentDonation;
    private double targetDonation;     // Changed from int to double

    // Constructor to handle both imageResId (for defaults/placeholders) and imageUriString
    public ActivityItem(String title, String description, String location, String date,
                        String status, int imageResId, String imageUriString,
                        int currentDonation, double targetDonation) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.status = status;
        this.imageResId = imageResId;
        this.imageUriString = imageUriString;
        this.currentDonation = currentDonation;
        this.targetDonation = targetDonation;
    }

    // Overloaded constructor for cases where only imageResId is provided (e.g., old sample data)
    // Or when an uploaded image is not available and we must use a resource ID.
    public ActivityItem(String title, String description, String location, String date,
                        String status, int imageResId, int currentDonation, double targetDonation) {
        this(title, description, location, date, status, imageResId, null, currentDonation, targetDonation);
    }


    // Getter methods
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

    public int getImageResId() {
        return imageResId;
    }

    public String getImageUriString() { // New getter
        return imageUriString;
    }

    public int getCurrentDonation() {
        return currentDonation;
    }

    public double getTargetDonation() { // Return type changed to double
        return targetDonation;
    }

    // Setter methods
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setImageUriString(String imageUriString) { // New setter
        this.imageUriString = imageUriString;
    }

    public void setCurrentDonation(int currentDonation) {
        this.currentDonation = currentDonation;
    }

    public void setTargetDonation(double targetDonation) { // Parameter type changed to double
        this.targetDonation = targetDonation;
    }

    public int getDonationProgress() {
        if (targetDonation == 0) {
            return 0;
        }
        return (int) ((double) currentDonation / targetDonation * 100);
    }
}
