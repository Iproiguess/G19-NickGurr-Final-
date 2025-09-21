package com.example.grpmobile;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CampaignReviewAdapter extends RecyclerView.Adapter<CampaignReviewAdapter.CampaignReviewViewHolder> {

    private List<CampaignReviewItem> campaignReviewList;
    private OnCampaignActionListener listener;

    public interface OnCampaignActionListener {
        void onApprove(CampaignReviewItem campaign, int position);
        void onReject(CampaignReviewItem campaign, int position);
    }

    public CampaignReviewAdapter(List<CampaignReviewItem> campaignReviewList, OnCampaignActionListener listener) {
        this.campaignReviewList = campaignReviewList; // This list is shared with AdminActivity
        this.listener = listener;
    }

    @NonNull
    @Override
    public CampaignReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_campaign_review, parent, false);
        return new CampaignReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignReviewViewHolder holder, int position) {
        CampaignReviewItem currentCampaign = campaignReviewList.get(position);

        Log.d("CampaignReviewAdapter", "Binding campaign ID: " + currentCampaign.getId() + " at position " + position);
        Log.d("CampaignReviewAdapter", "Title: '" + currentCampaign.getTitle() + "'");
        Log.d("CampaignReviewAdapter", "Email: '" + currentCampaign.getContactEmail() + "'");
        Log.d("CampaignReviewAdapter", "Description: '" + currentCampaign.getDescription() + "'");
        Log.d("CampaignReviewAdapter", "Image URI: '" + currentCampaign.getImageUriString() + "'");
        Log.d("CampaignReviewAdapter", "Donation Goal: " + currentCampaign.getDonationGoal());

        holder.tvCampaignTitle.setText(currentCampaign.getTitle());
        holder.tvContactEmail.setText(currentCampaign.getContactEmail());
        holder.tvCampaignDescription.setText(currentCampaign.getDescription());

        String imageUriString = currentCampaign.getImageUriString();
        if (!TextUtils.isEmpty(imageUriString)) {
            try {
                Uri imageUri = Uri.parse(imageUriString);
                holder.ivCampaignImage.setImageURI(imageUri);
                holder.ivCampaignImage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("CampaignReviewAdapter", "Error loading image URI: " + imageUriString, e);
                holder.ivCampaignImage.setImageResource(R.drawable.ic_launcher_background); // Fallback
                holder.ivCampaignImage.setVisibility(View.VISIBLE);
            }
        } else {
            holder.ivCampaignImage.setImageResource(R.drawable.ic_launcher_background); // Default
            holder.ivCampaignImage.setVisibility(View.VISIBLE);
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedGoal = currencyFormat.format(currentCampaign.getDonationGoal());
        holder.tvDonationGoal.setText("Goal: " + formattedGoal);

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApprove(currentCampaign, holder.getAdapterPosition());
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(currentCampaign, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return campaignReviewList == null ? 0 : campaignReviewList.size();
    }

    // Corrected method to update the list
    public void updateList(List<CampaignReviewItem> newList) {
        // In the current setup, AdminActivity modifies the list instance that
        // this.campaignReviewList already points to.
        // So, this.campaignReviewList is already up-to-date when this method is called.
        // We just need to notify the adapter that the data has changed.
        // The check 'this.campaignReviewList != newList' handles cases where AdminActivity
        // might pass a completely new list instance in the future.
        if (this.campaignReviewList != newList) {
            // This case should ideally not happen with the current AdminActivity setup
            // where it passes the same list instance it holds.
            // If it does, we switch the adapter's reference to this new list.
            Log.d("CampaignReviewAdapter", "updateList: New list instance received. Switching reference.");
            this.campaignReviewList = newList;
        }
        Log.d("CampaignReviewAdapter", "updateList: Notifying dataset changed. List size: " + (this.campaignReviewList != null ? this.campaignReviewList.size() : 0));
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < campaignReviewList.size()) {
            campaignReviewList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, campaignReviewList.size());
        }
    }

    static class CampaignReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvCampaignTitle;
        TextView tvContactEmail;
        TextView tvCampaignDescription;
        ImageView ivCampaignImage;
        TextView tvDonationGoal;
        Button btnApprove;
        Button btnReject;

        public CampaignReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCampaignTitle = itemView.findViewById(R.id.tvItemCampaignTitle);
            tvContactEmail = itemView.findViewById(R.id.tvItemContactEmail);
            tvCampaignDescription = itemView.findViewById(R.id.tvItemCampaignDescription);
            ivCampaignImage = itemView.findViewById(R.id.ivItemCampaignImage);
            tvDonationGoal = itemView.findViewById(R.id.tvItemDonationGoal);
            btnApprove = itemView.findViewById(R.id.btnApproveCampaign);
            btnReject = itemView.findViewById(R.id.btnRejectCampaign);
        }
    }
}
