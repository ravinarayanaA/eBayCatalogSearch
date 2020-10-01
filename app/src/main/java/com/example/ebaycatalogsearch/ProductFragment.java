package com.example.ebaycatalogsearch;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.StreamHandler;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {
    private static final String ARG_COUNT = "param1";
    private static final String ARG_COUNT1 = "param2";
    private JSONObject json;
    private JSONObject old_json;

    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance(String json, String old) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COUNT, json);
        args.putString(ARG_COUNT1, old);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                json = new JSONObject(getArguments().getString(ARG_COUNT));
                old_json = new JSONObject(getArguments().getString(ARG_COUNT1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout horizontal_view = view.findViewById(R.id.horizontal_view);
        JSONArray pictures = new JSONArray();
        try {
            pictures = (JSONArray) json.get("PictureURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < pictures.length(); i++) {
            try {
                String url_image = (String) pictures.get(i);
                ImageView imgView = new ImageView(getContext());
                imgView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                imgView.setVisibility(View.VISIBLE);
                Picasso.get().load(url_image).resize(1000, 1000).into(imgView);
                horizontal_view.addView(imgView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        TextView title = view.findViewById(R.id.title);
        TextView price = view.findViewById(R.id.product_price);
        TextView brand = view.findViewById(R.id.brand);
        TextView subtitle_view = view.findViewById(R.id.subtitle);
        TextView specification = view.findViewById(R.id.specification);
        JSONObject price_value = new JSONObject();
        try {
            price_value = (JSONObject) json.get("CurrentPrice");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray name_value = new JSONArray();
        JSONObject item_specifics = null;
        String specification_text = "";
        String brand_name = "";
        try {
            item_specifics = (JSONObject) json.get("ItemSpecifics");
            name_value = (JSONArray) item_specifics.get("NameValueList");
            for (int i = 0; i < name_value.length(); i++) {
                JSONObject name_vlaue_obj = (JSONObject) name_value.get(i);
                if (!name_vlaue_obj.get("Name").toString().matches("Brand")) {
                    JSONArray value = (JSONArray) name_vlaue_obj.get("Value");
                    specification_text += "<li>&nbsp;" + value.get(0) + "</li>";
                } else {
                    JSONArray value = (JSONArray) name_vlaue_obj.get("Value");
                    brand_name = (String) value.get(0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        specification.setText(Html.fromHtml(specification_text));
        if (specification.getText().toString().matches("")) {
            specification.setVisibility(View.GONE);
            view.findViewById(R.id.specification_header).setVisibility(View.GONE);
            view.findViewById(R.id.specification_line).setVisibility(View.GONE);
        }
        String subtitle;
        try {
            subtitle = json.get("Subtitle").toString();
        } catch (JSONException e) {
            subtitle = "";
        }
        if (!brand_name.matches("") && !subtitle.matches("")) {
            subtitle_view.setText(Html.fromHtml(subtitle));
            brand.setText(Html.fromHtml(brand_name));
        } else if (brand_name.matches("") && !subtitle.matches("")) {
            subtitle_view.setText(Html.fromHtml(subtitle));
            brand.setVisibility(View.GONE);
            view.findViewById(R.id.brand_head).setVisibility(View.GONE);
        } else if (!brand_name.matches("") && subtitle.matches("")) {
            brand.setText(Html.fromHtml(brand_name));
            subtitle_view.setVisibility(View.GONE);
            view.findViewById(R.id.subtitle_head).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.subtitle).setVisibility(View.GONE);
            view.findViewById(R.id.subtitle_head).setVisibility(View.GONE);
            view.findViewById(R.id.brand_head).setVisibility(View.GONE);
            view.findViewById(R.id.brand).setVisibility(View.GONE);
            view.findViewById(R.id.line).setVisibility(View.GONE);
            view.findViewById(R.id.features_product).setVisibility(View.GONE);
        }
        JSONObject shipping = null;
        try {
            JSONArray ship_array = (JSONArray) old_json.get("shippingInfo");
            shipping = (JSONObject) ship_array.get(0);

            JSONArray ship_obj = (JSONArray) shipping.get("shippingServiceCost");
            JSONObject text_field_obj = (JSONObject) ship_obj.get(0);
            title.setText((String) json.get("Title"));
            if (text_field_obj.get("__value__").toString().matches("0.0")) {
                price.setText(Html.fromHtml("<span style='color:#84AB57'><b>$" + String.valueOf(price_value.get("Value")) + "</b></span> <small>FREE shipping</small>"));
            } else {
                price.setText(Html.fromHtml("<span style='color:#84AB57'><b>$" + String.valueOf(price_value.get("Value")) + "</b></span> <small>Ships for $" + text_field_obj.get("__value__").toString()+"</small>"));
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
        }


    }
}