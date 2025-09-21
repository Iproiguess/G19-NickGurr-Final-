package com.example.grpmobile;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GrpMobile.db";
    // MODIFIED: Incremented database version for adding contact_email back
    private static final int DATABASE_VERSION = 5;

    // User Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email"; // User's login email
    public static final String COLUMN_PROFILE_IMAGE_URI = "profile_image_uri";
    public static final String COLUMN_ROLE = "role";

    // Campaign Table
    public static final String TABLE_CAMPAIGNS = "campaigns";
    public static final String COLUMN_CAMPAIGN_ID = "_id";
    public static final String COLUMN_CAMPAIGN_TITLE = "title";
    public static final String COLUMN_CAMPAIGN_DESCRIPTION = "description";
    public static final String COLUMN_CAMPAIGN_LOCATION = "location";
    public static final String COLUMN_CAMPAIGN_DATE = "date";
    public static final String COLUMN_CAMPAIGN_IMAGE_URI = "image_uri";
    public static final String COLUMN_CAMPAIGN_SUBMITTER_USERNAME = "submitter_username";
    public static final String COLUMN_CAMPAIGN_CONTACT_EMAIL = "contact_email"; // ADDED BACK: Campaign specific contact
    public static final String COLUMN_CAMPAIGN_PAYPAL_URL = "paypal_url";
    public static final String COLUMN_CAMPAIGN_DONATION_GOAL = "donation_goal";
    public static final String COLUMN_CAMPAIGN_STATUS = "status";
    public static final String COLUMN_CAMPAIGN_CURRENT_DONATION = "current_donation";


    // Create User Table SQL - CORRECTED SLASHES
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PROFILE_IMAGE_URI + " TEXT,"
            + COLUMN_ROLE + " TEXT NOT NULL"
            + ");";

    // Create Campaign Table SQL - CORRECTED SLASHES and ADDED contact_email back
    private static final String CREATE_TABLE_CAMPAIGNS = "CREATE TABLE " + TABLE_CAMPAIGNS + " ("
            + COLUMN_CAMPAIGN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CAMPAIGN_TITLE + " TEXT NOT NULL,"
            + COLUMN_CAMPAIGN_DESCRIPTION + " TEXT,"
            + COLUMN_CAMPAIGN_LOCATION + " TEXT,"
            + COLUMN_CAMPAIGN_DATE + " TEXT,"
            + COLUMN_CAMPAIGN_IMAGE_URI + " TEXT,"
            + COLUMN_CAMPAIGN_SUBMITTER_USERNAME + " TEXT,"
            + COLUMN_CAMPAIGN_CONTACT_EMAIL + " TEXT," // ADDED BACK
            + COLUMN_CAMPAIGN_PAYPAL_URL + " TEXT,"
            + COLUMN_CAMPAIGN_DONATION_GOAL + " REAL DEFAULT 0,"
            + COLUMN_CAMPAIGN_STATUS + " TEXT NOT NULL DEFAULT 'pending',"
            + COLUMN_CAMPAIGN_CURRENT_DONATION + " REAL DEFAULT 0"
            + ");";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper", "onCreate: Creating tables...");
        try {
            db.execSQL(CREATE_TABLE_USERS);
            Log.d("DBHelper", "onCreate: TABLE_USERS created.");
            db.execSQL(CREATE_TABLE_CAMPAIGNS);
            Log.d("DBHelper", "onCreate: TABLE_CAMPAIGNS created.");
        } catch (Exception e) {
            Log.e("DBHelper", "onCreate: Error creating tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DBHelper", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMPAIGNS);
        onCreate(db);
    }

    // User-related methods
    public boolean addUser(String username, String password, String email, String profileImageUri, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email); // User's login email
        values.put(COLUMN_PROFILE_IMAGE_URI, profileImageUri);
        values.put(COLUMN_ROLE, role);
        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_USERS, null, values);
            Log.d("DBHelper", "addUser: User " + username + " insert result: " + result);
        } catch (Exception e) {
            Log.e("DBHelper", "Error adding user: " + username, e);
        }
        return result != -1;
    }

    @SuppressLint("Range")
    public String getUserProfileImageUri(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String imageUri = null;
        if (username == null || username.isEmpty()) {
            Log.w("DBHelper", "getUserProfileImageUri: username is null or empty.");
            return null;
        }
        try {
            cursor = db.query(TABLE_USERS, new String[]{COLUMN_PROFILE_IMAGE_URI},
                    COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE_URI));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting user profile image URI for username: " + username, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imageUri;
    }

    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isValid = false;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                    COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                    new String[]{username, password}, null, null, null);
            isValid = (cursor != null && cursor.getCount() > 0);
        } catch (Exception e) {
            Log.e("DBHelper", "Error checking user credentials for " + username, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isValid;
    }

    @SuppressLint("Range")
    public String getUserRole(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String role = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COLUMN_ROLE},
                    COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting user role for " + username, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return role;
    }

    @SuppressLint("Range")
    public String getUserEmail(String username) { // Gets user's login email
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String email = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COLUMN_EMAIL},
                    COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting user email for username: " + username, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return email;
    }

    // Campaign-related methods
    // MODIFIED: Added contactEmail parameter back
    public boolean addCampaign(String title, String description, String location, String date,
                               String imageUri, String submitterUsername, String contactEmail, String paypalUrl,
                               double donationGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAMPAIGN_TITLE, title);
        values.put(COLUMN_CAMPAIGN_DESCRIPTION, description);
        values.put(COLUMN_CAMPAIGN_LOCATION, location);
        values.put(COLUMN_CAMPAIGN_DATE, date);
        values.put(COLUMN_CAMPAIGN_IMAGE_URI, imageUri);
        values.put(COLUMN_CAMPAIGN_SUBMITTER_USERNAME, submitterUsername);
        values.put(COLUMN_CAMPAIGN_CONTACT_EMAIL, contactEmail); // ADDED BACK
        values.put(COLUMN_CAMPAIGN_PAYPAL_URL, paypalUrl);
        values.put(COLUMN_CAMPAIGN_DONATION_GOAL, donationGoal);
        values.put(COLUMN_CAMPAIGN_STATUS, "pending"); // Default status
        values.put(COLUMN_CAMPAIGN_CURRENT_DONATION, 0); // Default current donation

        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_CAMPAIGNS, null, values);
            Log.i("DBHelper", "addCampaign: '" + title + "', status explicitly set to 'pending'. Insert result: " + result);
        } catch (Exception e) {
            Log.e("DBHelper", "Error adding campaign in addCampaign: " + title, e);
        }
        return result != -1;
    }

    // getPendingCampaigns and getApprovedCampaigns will now include contact_email and paypal_url
    // as they select all columns (null in query's projection)
    public Cursor getPendingCampaigns() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CAMPAIGN_STATUS + " = ?";
        String[] selectionArgs = {"pending"};
        return db.query(TABLE_CAMPAIGNS, null, selection, selectionArgs, null, null, COLUMN_CAMPAIGN_ID + " DESC");
    }

    public Cursor getApprovedCampaigns() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CAMPAIGN_STATUS + " = ?";
        String[] selectionArgs = {"approved"};
        return db.query(TABLE_CAMPAIGNS, null, selection, selectionArgs, null, null, COLUMN_CAMPAIGN_ID + " DESC");
    }

    public boolean updateCampaignStatus(int campaignId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAMPAIGN_STATUS, newStatus);
        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_CAMPAIGNS, values, COLUMN_CAMPAIGN_ID + "=?",
                    new String[]{String.valueOf(campaignId)});
        } catch (Exception e) {
            Log.e("DBHelper", "Error updating campaign status for ID " + campaignId, e);
        }
        return rowsAffected > 0;
    }

    @SuppressLint("Range")
    public CampaignReviewItem getCampaignById(int campaignId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        CampaignReviewItem campaign = null;
        try {
            // Ensure this query fetches all necessary columns, including contact_email and paypal_url
            cursor = db.query(TABLE_CAMPAIGNS, null, COLUMN_CAMPAIGN_ID + "=?",
                    new String[]{String.valueOf(campaignId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Assuming CampaignReviewItem constructor is updated to accept both contactEmail and paypalUrl
                campaign = new CampaignReviewItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_IMAGE_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_SUBMITTER_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_CONTACT_EMAIL)), // ADDED BACK
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_PAYPAL_URL)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_DONATION_GOAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_STATUS))
                );
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting campaign by ID: " + campaignId, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return campaign;
    }

    @SuppressLint("Range")
    public void logAllCampaigns() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Log.d("DBHelper_Dump", "--- Dumping ALL Campaigns ---");
        try {
            cursor = db.query(TABLE_CAMPAIGNS, null, null, null, null, null, COLUMN_CAMPAIGN_ID + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_TITLE));
                    String submitter = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_SUBMITTER_USERNAME));
                    String contactEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_CONTACT_EMAIL)); // ADDED BACK
                    String paypalUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_PAYPAL_URL));
                    double donationGoal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_DONATION_GOAL));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_STATUS));
                    double currentDonation = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CAMPAIGN_CURRENT_DONATION));

                    Log.d("DBHelper_Dump", "ID: " + id +
                            ", Title: '" + title + "'" +
                            ", Submitter: '" + submitter + "'" +
                            ", Contact Email: '" + contactEmail + "'" + // ADDED BACK
                            ", PayPal URL: '" + paypalUrl + "'" +
                            ", Goal: " + donationGoal +
                            ", Status: '" + status + "'" +
                            ", CurrentDon: " + currentDonation);
                } while (cursor.moveToNext());
                Log.d("DBHelper_Dump", "--- End of Campaign Dump (" + cursor.getCount() + " rows) ---");
            } else if (cursor != null) {
                Log.d("DBHelper_Dump", "No campaigns found in the table. Cursor count: 0");
            } else {
                Log.e("DBHelper_Dump", "Cursor is null when trying to dump all campaigns.");
            }
        } catch (Exception e) {
            Log.e("DBHelper_Dump", "Error dumping all campaigns", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean updateUserProfileImageUri(String username, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_IMAGE_URI, imageUri);
        int rowsAffected = 0;
        try {
            rowsAffected = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
            Log.d("DBHelper", "updateUserProfileImageUri for " + username + ": URI '" + imageUri + "'. Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.e("DBHelper", "Error updating profile image URI for user: " + username, e);
        }
        return rowsAffected > 0;
    }
}
