package com.example.ebaycatalogsearch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 3;
    private String json_text_var;
    private String old_json;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,String json_text, String advanced_data) {
        super(fragmentActivity);
        json_text_var =json_text;
        old_json = advanced_data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return ProductFragment.newInstance(json_text_var,old_json);
        }
        else if(position==1){
            return SellerInfoFragment.newInstance(json_text_var,old_json);
        }
        else if(position==2){
            return ShippingFragment.newInstance(json_text_var,old_json);
        }
        return ProductFragment.newInstance(json_text_var,old_json);
    }

    @Override
    public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}

