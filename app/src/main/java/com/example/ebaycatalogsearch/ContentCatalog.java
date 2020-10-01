package com.example.ebaycatalogsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentCatalog extends AppCompatActivity implements CustomAdapter.ItemClickListener {
    JSONArray data;
    CustomAdapter adapter;

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return false;
    }

    public void create_recyclerview() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(ContentCatalog.this, numberOfColumns));
        adapter = new CustomAdapter(ContentCatalog.this, data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        return;
    }
    public void refresh_request(){
        String query_string = getIntent().getStringExtra("query_string");
        final RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        final Context context = getApplicationContext();
        String url = null;
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        url = "https://androidebayserver.wl.r.appspot.com/api/get_data?" + query_string + "&seller=&shipping=";
        Log.v("URL",url);
        JsonObjectRequest json_obj_req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    data = (JSONArray) response.get("items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TextView noresults = findViewById(R.id.noresults);
                if (data.length() == 0) {
                    findViewById(R.id.noresults).setVisibility(View.VISIBLE);
                    noresults.setText("No Records");
                    Toast.makeText(context, "No Records", Toast.LENGTH_SHORT).show();
                }
                else {
                    findViewById(R.id.second_page).setBackgroundColor(Color.parseColor("#F7F4F4"));

                    int num_results_int = 0;
                    if (data.length() > 50) {
                        num_results_int = 50;
                    } else {
                        try {
                            num_results_int = ((JSONArray) response.get("items")).length();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    TextView num_results = findViewById(R.id.num_results);
                    String text_num = null;
                    try {
                        text_num = "Showing <span style='color:blue'>" + num_results_int + "</span> results for <span style='color:blue'>" + (String) response.get("keyword")+"</span>";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    num_results.setText(Html.fromHtml(text_num));
                    create_recyclerview();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mRequestQueue.add(json_obj_req);

    }
    public void volley_request(){
        String query_string = getIntent().getStringExtra("query_string");
        final RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.loading_image);
        final Context context = getApplicationContext();
        mProgressBar.setVisibility(View.VISIBLE);
        String url = null;
//        url = "https://ebaynodejsserver.wl.r.appspot.com/api/get_data?" + query_string + "&seller=&shipping=";
        url = "https://androidebayserver.wl.r.appspot.com/api/get_data?" + query_string + "&seller=&shipping=";
        Log.v("URL",url);
        JsonObjectRequest json_obj_req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    data = (JSONArray) response.get("items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressBar.setVisibility(View.GONE);
                findViewById(R.id.loading_text).setVisibility(View.GONE);
                TextView noresults = findViewById(R.id.noresults);
                if (data.length() == 0) {
                    findViewById(R.id.noresults).setVisibility(View.VISIBLE);
                    noresults.setText("No Records");
                    Toast.makeText(context, "No Records", Toast.LENGTH_SHORT).show();
                }
                else {
                    findViewById(R.id.second_page).setBackgroundColor(Color.parseColor("#F7F4F4"));

                    int num_results_int = 0;
                    if (data.length() > 50) {
                        num_results_int = 50;
                    } else {
                        try {
                            num_results_int = ((JSONArray) response.get("items")).length();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    TextView num_results = findViewById(R.id.num_results);
                    String text_num = null;
                    try {
                        text_num = "Showing <span style='color:blue'>" + num_results_int + "</span> results for <span style='color:blue'>" + (String) response.get("keyword")+"</span>";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    num_results.setText(Html.fromHtml(text_num));
                    create_recyclerview();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                findViewById(R.id.loading_text).setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        mRequestQueue.add(json_obj_req);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_catalog);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search Results");

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh_request();
                pullToRefresh.setRefreshing(false);
            }
        });

        volley_request();

    }

    @Override
    public void onItemClick(View view, int position) throws JSONException {
        Log.i("TAG", "You clicked number, which is at cell position " + position);
        Intent intent = new Intent(ContentCatalog.this, ProductDescriptionActivity.class);
        intent.putExtra("data", data.get(position).toString());
        startActivity(intent);
    }
}