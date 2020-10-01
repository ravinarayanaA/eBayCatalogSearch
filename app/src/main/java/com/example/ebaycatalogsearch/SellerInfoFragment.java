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
 * Use the {@link SellerInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerInfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private JSONObject json;
    private JSONObject old_json;


    public SellerInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SellerInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerInfoFragment newInstance(String param1, String old) {
        SellerInfoFragment fragment = new SellerInfoFragment();
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
        return inflater.inflate(R.layout.fragment_seller_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView return_policy_field = view.findViewById(R.id.return_policy);
        TextView seller_field = view.findViewById(R.id.seller_information);

        JSONObject return_policy = null;
        try {
            return_policy = (JSONObject) json.get("ReturnPolicy");
            Iterator<String> keys = return_policy.keys();
            String return_text = "";
            while (keys.hasNext()) {
                String key = keys.next();
                String[] r = key.split("(?=\\p{Upper})");
                String joined = String.join(" ", r);
                try {
                    return_text += "<li>&nbsp;<b>" + joined + "</b>: " + return_policy.get(key) + "</li>";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return_policy_field.setText(Html.fromHtml(return_text));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject seller = null;
            seller = (JSONObject) json.get("Seller");
            Iterator<String> keys_seller = seller.keys();
            String seller_text = "<ul>";
            while (keys_seller.hasNext()) {
                String key = keys_seller.next();
                try {
                    String[] r = key.split("(?=\\p{Upper})");
                    String joined = String.join(" ", r);
                    seller_text += "<li>&nbsp;<b>" + joined + "</b>: " + seller.get(key) + "</li>";

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            seller_field.setText(Html.fromHtml(seller_text));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}