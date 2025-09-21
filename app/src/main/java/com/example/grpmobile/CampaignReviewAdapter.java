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
        void onViewDetails(CampaignReviewItem campaign);
    }

    public CampaignReviewAdapter(List<CampaignReviewItem> campaignReviewList, OnCampaignActionListener listener) {
        this.campaignReviewList = campaignReviewList;
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
        Log.d("CampaignReviewAdapter", "Contact Email: '" + currentCampaign.getContactEmail() + "'");
        Log.d("CampaignReviewAdapter", "PayPal URL: '" + currentCampaign.getPaypalUrl() + "'");
        Log.d("CampaignReviewAdapter", "Description: '" + currentCampaign.getDescription() + "'");


        holder.tvCampaignTitle.setText(currentCampaign.getTitle());

        String contactEmail = currentCampaign.getContactEmail();
        if (contactEmail != null && !contactEmail.isEmpty()) {
            holder.tvContactEmail.setText("Email: " + contactEmail);
            holder.tvContactEmail.setVisibility(View.VISIBLE);
        } else {
            holder.tvContactEmail.setText("Email: N/A");
        }

        String paypalUrl = currentCampaign.getPaypalUrl();
        if (holder.tvPaypalUrl != null) {
            if (paypalUrl != null && !paypalUrl.isEmpty()) {
                holder.tvPaypalUrl.setText("PayPal: " + paypalUrl);
                holder.tvPaypalUrl.setVisibility(View.VISIBLE);
            } else {
                holder.tvPaypalUrl.setText("PayPal: N/A");
            }
        }


        holder.tvCampaignDescription.setText(currentCampaign.getDescription());

        String imageUriString = currentCampaign.getImageUriString();
        if (!TextUtils.isEmpty(imageUriString)) {
            try {
                Uri imageUri = Uri.parse(imageUriString);
                holder.ivCampaignImage.setImageURI(imageUri);
                holder.ivCampaignImage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("CampaignReviewAdapter", "Error loading image URI: " + imageUriString, e);
                holder.ivCampaignImage.setImageResource(R.drawable.ic_launcher_background);
                holder.ivCampaignImage.setVisibility(View.VISIBLE);
            }
        } else {
            holder.ivCampaignImage.setImageResource(R.drawable.ic_launcher_background);
            holder.ivCampaignImage.setVisibility(View.VISIBLE);
        }

        // MODIFIED: Changed Locale for NumberFormat to display RM
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ms", "MY"));
        String formattedGoal = currencyFormat.format(currentCampaign.getDonationGoal());
        // The prefix "Goal: " is still here. NumberFormat should handle the "RM"
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetails(currentCampaign);
            }
        });
    }

    @Override
    public int getItemCount() {
        return campaignReviewList == null ? 0 : campaignReviewList.size();
    }

    public void updateList(List<CampaignReviewItem> newList) {
        if (this.campaignReviewList != newList) {
            this.campaignReviewList = newList;
        }
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
        TextView tvPaypalUrl;

        public CampaignReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCampaignTitle = itemView.findViewById(R.id.tvItemCampaignTitle);
            tvContactEmail = itemView.findViewById(R.id.tvItemContactEmail);
            tvCampaignDescription = itemView.findViewById(R.id.tvItemCampaignDescription);
            ivCampaignImage = itemView.findViewById(R.id.ivItemCampaignImage);
            tvDonationGoal = itemView.findViewById(R.id.tvItemDonationGoal);
            btnApprove = itemView.findViewById(R.id.btnApproveCampaign);
            btnReject = itemView.findViewById(R.id.btnRejectCampaign);
            tvPaypalUrl = itemView.findViewById(R.id.tvItemPaypalUrl);
        }
    }
}
