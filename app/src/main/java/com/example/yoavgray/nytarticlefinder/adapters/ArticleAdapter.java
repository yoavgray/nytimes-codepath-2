package com.example.yoavgray.nytarticlefinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.yoavgray.nytarticlefinder.R;
import com.example.yoavgray.nytarticlefinder.models.Article;
import com.example.yoavgray.nytarticlefinder.viewholders.ImageViewHolder;
import com.example.yoavgray.nytarticlefinder.viewholders.TextViewHolder;

import java.util.List;

public class ArticleAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static String NYTIMES_URL = "http://www.nytimes.com/";

    private final int TEXT = 0, IMAGE = 1;

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
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (articles.get(position).getThumbnailUrl() == null) {
            return TEXT;
        } else {
            return IMAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case TEXT:
                View v1 = inflater.inflate(R.layout.article_text_grid_item, viewGroup, false);
                viewHolder = new TextViewHolder(v1);
                break;
            case IMAGE:
                View v2 = inflater.inflate(R.layout.article_thumbnail_grid_item, viewGroup, false);
                viewHolder = new ImageViewHolder(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.article_text_grid_item, viewGroup, false);
                viewHolder = new TextViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case TEXT:
                TextViewHolder textViewHolder = (TextViewHolder) viewHolder;
                configureTextViewHolder(textViewHolder, position);
                break;
            case IMAGE:
                ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;
                configureImageViewHolder(imageViewHolder, position);
                break;
        }
    }

    private void configureTextViewHolder(TextViewHolder holder, int position) {
        final Article article = articles.get(position);

        if (article != null) {
            // Set item views based on your views and data model
            holder.getTitleTextView().setText(article.getHeadline().getHeadline());
            holder.getLeadingParagraphTextView().setText(article.getLeadParagraph());
            if (article.getPubDate() != null) {
                String[] dateArray = article.getPubDate().split("T");
                holder.getPublishDateTextView().setText(dateArray[0]);
            }
            // Using RetroLambda!!
            holder.getShareArticalImageView().setOnClickListener((view) -> {
                    // Send an implicit intent to share the article
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Here's an interesting article:\n"
                            + article.getWebUrl());
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
            });
        }
    }

    private void configureImageViewHolder(ImageViewHolder holder, int position) {
        final Article article = articles.get(position);

        if (article != null) {
            Glide.with(context)
                    // Load poster image or backdrop according to votes on portrait orientation
                    .load(NYTIMES_URL + article.getThumbnailUrl())
                    .override(250, 200)
                    .placeholder(R.drawable.progress_image)
                    .into(holder.getThumbnailImageView());

            // Set item views based on your views and data model
            holder.getTitleTextView().setText(article.getHeadline().getHeadline());
            String[] dateArray = article.getPubDate().split("T");
            holder.getPublishDateTextView().setText(dateArray[0]);
            holder.getShareArticalImageView().setOnClickListener((view) -> {
                    // Send an implicit intent to share the article
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Here's an interesting article:\n"
                            + article.getWebUrl());
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
            });
        }
    }
}
