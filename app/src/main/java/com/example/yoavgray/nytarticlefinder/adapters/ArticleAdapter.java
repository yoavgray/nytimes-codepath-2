package com.example.yoavgray.nytarticlefinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yoavgray.nytarticlefinder.R;
import com.example.yoavgray.nytarticlefinder.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ArticleAdapter extends
        RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    public final static String NYTIMES_URL = "http://www.nytimes.com/";
    // Try loading different articles with different colors
    int[] materialColors =
            { R.color.materialBlue, R.color.materialCyan, R.color.materialDeepOrange,
                R.color.materialIndigo, R.color.materialPink, R.color.materialPurple, R.color.materialRed };
    Random rand = new Random();

    // Store a member variable for the contacts
    private List<Article> articles;
    private Context context;

    // Pass in the contact array into the constructor
    public ArticleAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.linear_layout_grid_item) RelativeLayout gridContainer;
        @BindView(R.id.text_view_article_title) TextView titleTextView;
        @BindView(R.id.image_view_article_thumbnail) ImageView thumbnailImageView;
        @BindView(R.id.text_view_article_publish_date) TextView publishDateTextView;
        @BindView(R.id.image_view_share_article) ImageView shareArticalImageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.article_grid_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        final Article article = articles.get(position);

        if (article.getThumbnailUrl() == null) {
            holder.thumbnailImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_nyt));
        } else {
            Picasso.with(context)
                    // Load poster image or backdrop according to votes on portrait orientation
                    .load(NYTIMES_URL + article.getThumbnailUrl())
                    .fit()
                    .placeholder(R.drawable.progress_image)
                    .transform(new RoundedCornersTransformation(20, 20))
                    .into(holder.thumbnailImageView);
        }
        // Set item views based on your views and data model
        holder.titleTextView.setText(article.getHeadline().getHeadline());
        String[] dateArray = article.getPubDate().split("T");
        holder.publishDateTextView.setText(dateArray[0]);
        holder.shareArticalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send an implicit intent to share the article
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Here's an interesting article:\n"
                        + article.getWebUrl());
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
