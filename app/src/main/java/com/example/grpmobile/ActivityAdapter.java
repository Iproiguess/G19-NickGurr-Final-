package com.example.grpmobile;

import android.net.Uri; // Added for Uri
import android.text.TextUtils; // Added for TextUtils
import android.util.Log; // Added for logging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<ActivityItem> activityList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ActivityItem item);
    }

    public ActivityAdapter(List<ActivityItem> activityList, OnItemClickListener listener) {
        this.activityList = activityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_card, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityItem item = activityList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvLocation.setText("Location: " + item.getLocation());
        holder.tvDate.setText("Date: " + item.getDate());
        holder.tvStatus.setText(item.getStatus());

        if ("Ongoing".equalsIgnoreCase(item.getStatus()) || "Upcoming".equalsIgnoreCase(item.getStatus())) { // Added Upcoming
            holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        } else { // Completed or other statuses
            holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(android.R.color.darker_gray));
        }

        // Image Loading Logic
        String imageUriString = item.getImageUriString();
        if (!TextUtils.isEmpty(imageUriString) && item.getImageResId() == 0) { // Prioritize URI if imageResId suggests it (e.g. is 0)
            try {
                Uri imageUri = Uri.parse(imageUriString);
                holder.imageActivity.setImageURI(imageUri);
            } catch (Exception e) {
                Log.e("ActivityAdapter", "Error loading image URI: " + imageUriString, e);
                // Fallback to imageResId if URI loading fails or if URI is valid but res ID is also set (e.g. default)
                if (item.getImageResId() != 0) {
                    holder.imageActivity.setImageResource(item.getImageResId());
                } else {
                    holder.imageActivity.setImageResource(R.drawable.ic_launcher_background); // Ultimate fallback
                }
            }
        } else if (item.getImageResId() != 0) { // If no URI or URI is not prioritized, use imageResId
            holder.imageActivity.setImageResource(item.getImageResId());
        } else {
            // If both URI is empty/not prioritized and imageResId is 0, set a final default
            holder.imageActivity.setImageResource(R.drawable.ic_launcher_background);
        }


        // Update Progress Text and Bar
        String progressText = String.format(Locale.US, "RM%d / RM%.0f",
                item.getCurrentDonation(), item.getTargetDonation());
        holder.tvProgressText.setText(progressText);
        holder.progressBar.setProgress(item.getDonationProgress());


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Add null check for safety
        return activityList != null ? activityList.size() : 0;
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvLocation, tvDate, tvStatus, tvProgressText; // tvProgressText was already there
        ImageView imageActivity;
        ProgressBar progressBar; // progressBar was progressBarActivity in XML

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvActivityTitle);
            tvDescription = itemView.findViewById(R.id.tvActivityDescription);
            tvLocation = itemView.findViewById(R.id.tvActivityLocation);
            tvDate = itemView.findViewById(R.id.tvActivityDate);
            tvStatus = itemView.findViewById(R.id.tvActivityStatus);
            imageActivity = itemView.findViewById(R.id.imageActivity);
            tvProgressText = itemView.findViewById(R.id.tvProgressText);
            progressBar = itemView.findViewById(R.id.progressBarActivity); // Corrected ID
        }
    }

    public void updateList(List<ActivityItem> filteredList) {
        activityList = filteredList;
        notifyDataSetChanged();
    }
}
