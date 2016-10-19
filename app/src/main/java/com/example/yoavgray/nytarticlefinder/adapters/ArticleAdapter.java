package com.example.yoavgray.nytarticlefinder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yoavgray.nytarticlefinder.R;
import com.example.yoavgray.nytarticlefinder.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleAdapter extends
        RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    public final static String NYTIMES_URL = "http://www.nytimes.com/";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_article_title) TextView titleTextView;
        @BindView(R.id.image_view_article_thumbnail) ImageView thumbnailImageView;
        @BindView(R.id.text_view_article_author) TextView authorTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Store a member variable for the contacts
    private List<Article> articles;
    private Context context;

    // Pass in the contact array into the constructor
    public ArticleAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.context = context;
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
        View contactView = inflater.inflate(R.layout.article_grid_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Article article = articles.get(position);

        if (article.getThumbnailUrl() == null) {
            holder.thumbnailImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_nyt, null));
        } else {
            Picasso.with(context)
                    // Load poster image or backdrop according to votes on portrait orientation
                    .load(NYTIMES_URL + article.getThumbnailUrl())
                    .fit()
                    .placeholder(R.drawable.progress_image)
                    .into(holder.thumbnailImageView);
        }
        // Set item views based on your views and data model
        holder.titleTextView.setText(article.getHeadline().getHeadline());
        holder.authorTextView.setText("Sir John Smith");
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
