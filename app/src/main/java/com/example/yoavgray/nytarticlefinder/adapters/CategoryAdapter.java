package com.example.yoavgray.nytarticlefinder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yoavgray.nytarticlefinder.R;
import com.example.yoavgray.nytarticlefinder.models.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class CategoryAdapter extends
        RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.categories = categories;
        this.context = context;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_category) TextView categoryTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View categoryView = inflater.inflate(R.layout.category_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(categoryView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Category category = categories.get(position);

        holder.categoryTextView.setText(category.getName());
        if (category.isIncluded()) {
            holder.categoryTextView.setBackgroundColor(context.getResources()
                    .getColor(R.color.whiteColor));
            holder.categoryTextView.setTextColor(context.getResources()
                    .getColor(R.color.blackColor));
        } else {
            holder.categoryTextView.setBackground(context.getResources()
                    .getDrawable(R.drawable.category_list_item_background));
            holder.categoryTextView.setTextColor(context.getResources()
                    .getColor(R.color.whiteColor));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

}