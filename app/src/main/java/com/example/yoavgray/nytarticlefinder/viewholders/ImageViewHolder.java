package com.example.yoavgray.nytarticlefinder.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yoavgray.nytarticlefinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.linear_layout_grid_item) RelativeLayout gridContainer;
    @BindView(R.id.text_view_article_title) TextView titleTextView;
    @BindView(R.id.image_view_article_thumbnail) ImageView thumbnailImageView;
    @BindView(R.id.text_view_article_publish_date) TextView publishDateTextView;
    @BindView(R.id.image_view_share_article) ImageView shareArticalImageView;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public RelativeLayout getGridContainer() {
        return gridContainer;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public ImageView getThumbnailImageView() {
        return thumbnailImageView;
    }

    public TextView getPublishDateTextView() {
        return publishDateTextView;
    }

    public ImageView getShareArticalImageView() {
        return shareArticalImageView;
    }
}