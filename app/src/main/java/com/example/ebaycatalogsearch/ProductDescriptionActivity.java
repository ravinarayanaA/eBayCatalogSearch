package com.example.ebaycatalogsearch;

import android.app.MediaRouteButton;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProductDescriptionActivity extends AppCompatActivity {
    private ViewPagerAdapter viewPagerAdapter;
    String product_data;
    ProgressBar mProgressBar;
    JSONObject data;
    String data_str;

    ViewPager2 viewPager;
    TabLayout tabLayout;

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);

        data_str = getIntent().getStringExtra("data");
        try {
            data = new JSONObject(data_str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        View view = getSupportActionBar().getCustomView();
        TextView name = view.findViewById(R.id.title_action);
        try {
            name.setText((CharSequence) data.get("item_title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageView redirect = view.findViewById(R.id.redirect_image);
        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = null;
                try {
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) data.get("item_ebay_link")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(browserIntent);
            }
        });
        String url = null;
        try {
            url = "https://androidebayserver.wl.r.appspot.com/api/get_data/product?product_id=" + data.get("item_id");
            Log.d("Single ITem",url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tabLayout.addTab(tabLayout.newTab().setText("PRODUCT"));
        tabLayout.addTab(tabLayout.newTab().setText("SELLER INFO"));
        tabLayout.addTab(tabLayout.newTab().setText("SHIPPING"));

        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant_selected);
        tabLayout.getTabAt(1).setIcon(R.drawable.shop_ic);
        tabLayout.getTabAt(2).setIcon(R.drawable.truck);

        final RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest json_obj_req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    product_data = ((JSONObject) response).get("Item").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressBar = findViewById(R.id.loading_product);
                mProgressBar.setVisibility(View.GONE);
                findViewById(R.id.loading_text_product).setVisibility(View.GONE);
                viewPager.setAdapter(createCardAdapter());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                findViewById(R.id.loading_text_product).setVisibility(View.GONE);
                mProgressBar = findViewById(R.id.loading_product);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        mRequestQueue.add(json_obj_req);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

    }

    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, product_data, data_str);
        return adapter;
    }
}