package com.example.ebaycatalogsearch;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private JSONArray mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    CustomAdapter(Context context, JSONArray data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rowlayout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.myTextView.setText(Html.fromHtml((mData.getJSONObject(position).getString("item_title"))));
            try {
                String conditoin_str = mData.getJSONObject(position).getString("item_condition");
                holder.myconditionView.setText(Html.fromHtml(conditoin_str));
            } catch (JSONException e) {
                holder.myconditionView.setText("N/A");
            }
            try {
                String top_rated = mData.getJSONObject(position).getString("item_top_rated_image");
                holder.topRatedView.setVisibility(View.VISIBLE);
                holder.topRatedView.setText(Html.fromHtml("Top Rated Listing"));
            } catch (JSONException e) {
                holder.topRatedView.setVisibility(View.GONE);
            }
            holder.mypriceView.setText(Html.fromHtml(mData.getJSONObject(position).getString("item_price")));
            String shipping_cost = mData.getJSONObject(position).getString("shipping_cost");
            if (shipping_cost.matches("0.0")) {
                holder.myshippingPriceView.setText(Html.fromHtml("<b>FREE</b> Shipping"));
            } else {
                holder.myshippingPriceView.setText(Html.fromHtml("Ships for <b>$" + shipping_cost + "</b>"));
            }
            String item_url = mData.getJSONObject(position).getString("item_image_url");
            if (item_url.matches("/assets/images/ebayDefault.png")) {
                Picasso.get().load(R.drawable.ebay_default).into(holder.myImageView);
            } else {
                Picasso.get().load(item_url).into(holder.myImageView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Picasso.get().load(R.drawable.ebay_default).into(holder.myImageView);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        final int limit = 50;
        if (mData.length() > limit) {
            return limit;
        } else {
            return mData.length();
        }
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView myconditionView;
        TextView mypriceView;
        TextView myshippingPriceView;
        TextView topRatedView;
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.info_text);
            myImageView = itemView.findViewById(R.id.image_container);
            myconditionView = itemView.findViewById(R.id.condition);
            myshippingPriceView = itemView.findViewById(R.id.shipping_price);
            mypriceView = itemView.findViewById(R.id.item_price);
            topRatedView = itemView.findViewById(R.id.top_rated);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                try {
                    mClickListener.onItemClick(view, getAdapterPosition());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // convenience method for getting data at click position
    Object getItem(int id) throws JSONException {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position) throws JSONException;
    }
}