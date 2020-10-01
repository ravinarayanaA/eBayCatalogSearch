package com.example.ebaycatalogsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] sort_by = {"Best Match", "Price: highest first",
            "Price + Shipping: highest first",
            "Price + Shipping: lowest first"};
    HashMap<String, String> sort_by_mapping = new HashMap();
    String select_dropdown;
    public boolean error_flag = false;
    public boolean num_error_flag = false;
    public EditText keyword;
    public EditText minprice;
    public EditText maxprice;
    public CheckBox new_check;
    public CheckBox used_check;
    public CheckBox unspecified_check;

    public void toggle_view(TextView txtView, Boolean hide_or_show) {
        if (hide_or_show == true) {
            txtView.setVisibility(View.VISIBLE);
        } else {
            if (txtView.getVisibility() == View.VISIBLE) {
                txtView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Spinner spin = (Spinner) findViewById(R.id.dropdown);
        spin.setOnItemSelectedListener(this);


        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sort_by);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        sort_by_mapping.put("Best Match", "BestMatch");
        sort_by_mapping.put("Price: highest first", "CurrentPriceHighest");
        sort_by_mapping.put("Price + Shipping: highest first", "PricePlusShippingHighest");
        sort_by_mapping.put("Price + Shipping: lowest first", "PricePlusShippingLowest");

        keyword = (EditText) findViewById(R.id.editKeyword);
        minprice = (EditText) findViewById(R.id.minPrice);
        maxprice = (EditText) findViewById(R.id.maxPrice);
        new_check = (CheckBox) findViewById(R.id.new_check);
        used_check = (CheckBox) findViewById(R.id.used_check);
        unspecified_check = (CheckBox) findViewById(R.id.unspecified_check);

        Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                keyword.setText("");
                minprice.setText("");
                maxprice.setText("");
                new_check.setChecked(false);
                used_check.setChecked(false);
                unspecified_check.setChecked(false);
                toggle_view((TextView) findViewById(R.id.keyword_warning), false);
                toggle_view((TextView) findViewById(R.id.price_warning), false);
                spin.setSelection(0);
            }
        });

        Button mButton = (Button) findViewById(R.id.searchButton);
        final Context context = getApplicationContext();
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String query_string = "";


                        try {
                            if (Float.valueOf(minprice.getText().toString()) < 0 || Float.valueOf(maxprice.getText().toString()) < 0 || (Float.valueOf(maxprice.getText().toString()) < Float.valueOf(minprice.getText().toString()))) {
                                num_error_flag = true;
                                Toast.makeText(context, "Please fix all fields with error", Toast.LENGTH_SHORT).show();

                                toggle_view((TextView) findViewById(R.id.price_warning), true);
                            } else {
                                num_error_flag = false;
                                toggle_view((TextView) findViewById(R.id.price_warning), false);
                                Log.v("error", "Perfect!");
                            }
                        } catch (NumberFormatException ex) {
                            num_error_flag = false;
                            toggle_view((TextView) findViewById(R.id.price_warning), false);

                        }
                        if (keyword.getText().toString().trim().matches("")) {
                                error_flag = true;
                            Toast.makeText(context, "Please fix all fields with error", Toast.LENGTH_SHORT).show();
                            toggle_view((TextView) findViewById(R.id.keyword_warning), true);
                        } else {
                                error_flag = false;
                            toggle_view((TextView) findViewById(R.id.keyword_warning), false);
                        }
                        query_string += "keyword=" + keyword.getText().toString();
                        if (error_flag == false && num_error_flag==false) {
                            if (!minprice.getText().toString().trim().matches("")) {
                                query_string += "&price_lower=" + minprice.getText().toString();
                            } else {
                                query_string += "&price_lower=null";
                            }

                            if (!maxprice.getText().toString().trim().matches("")) {
                                query_string += "&price_upper=" + maxprice.getText().toString();
                            } else {
                                query_string += "&price_upper=null";
                            }

                            ArrayList<CheckBox> condition = new ArrayList<>();
                            condition.add(new_check);
                            condition.add(used_check);
                            condition.add(unspecified_check);
                            StringBuilder condtion_str = new StringBuilder();
                            for (int i = 0; i < condition.size(); i++) {
                                if (condition.get(i).isChecked() == true) {
                                    condtion_str.append(condition.get(i).getText());
                                    if (!(i == condition.size() - 1)) {
                                        condtion_str.append(",");
                                    }
                                }
                            }
                            if (!condtion_str.toString().matches("")) {
                                query_string += "&condition=" + condtion_str.toString();
                            } else {
                                query_string += "&condition=null";
                            }
                            try {
                                query_string += "&sortorder=" + URLEncoder.encode(select_dropdown, "utf-8");
                            } catch (
                                    UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(MainActivity.this, ContentCatalog.class);
                            intent.putExtra("query_string", query_string);

                            startActivity(intent);
                        }
                    }
                });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        select_dropdown = sort_by[position];
//        Toast.makeText(getApplicationContext(), sort_by[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}