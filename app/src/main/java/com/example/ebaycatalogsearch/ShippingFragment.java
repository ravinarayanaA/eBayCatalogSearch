package com.example.ebaycatalogsearch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShippingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShippingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private JSONObject json;
    private JSONObject old_json;

    public ShippingFragment() {
        // Required empty public constructor
    }
    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ShippingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShippingFragment newInstance(String param1, String old) {
        ShippingFragment fragment = new ShippingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, old);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                json = new JSONObject(getArguments().getString(ARG_PARAM1));
                old_json = new JSONObject(getArguments().getString(ARG_PARAM2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shipping, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView shipping_field = view.findViewById(R.id.shipping_info);

        JSONObject shipping = null;
        try {
            JSONArray ship_array = (JSONArray) old_json.get("shippingInfo");
            shipping = (JSONObject) ship_array.get(0);
            Iterator<String> keys = shipping.keys();
            String return_text = "";
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    JSONArray ship_info = (JSONArray) shipping.get(key);
                    String text_field;
                    JSONObject text_field_obj;
                    if (key.matches("shippingServiceCost")) {
                        text_field_obj = (JSONObject) ship_info.get(0);
                        text_field = "$" + text_field_obj.get("__value__");
                    } else if (ship_info.get(0).toString().matches("true")) {
                        text_field = "Yes";
                    } else if (ship_info.get(0).toString().matches("false")) {
                        text_field = "No";
                    } else {
                        text_field = ship_info.get(0).toString();
                    }
                    String[] r = key.split("(?=\\p{Upper})");
                    String joined = String.join(" ", r);
                    return_text += "<li>&nbsp;<b>" +  toTitleCase(joined) + "</b>: " + text_field + "</li>";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            shipping_field.setText(Html.fromHtml(return_text));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}