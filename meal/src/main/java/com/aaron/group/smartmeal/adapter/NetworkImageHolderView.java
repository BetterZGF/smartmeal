package com.aaron.group.smartmeal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.aaron.group.smartmeal.R;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;


public class NetworkImageHolderView implements Holder<String> {

    private View view;
    @Override
    public View createView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_banner_item, null, false);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int i, String s) {
        if(view instanceof ImageView)
        {
            ImageView imageView = (ImageView) view;
            Glide.with(context).load(s).dontAnimate().centerCrop().placeholder(R.mipmap.ic_launcher).into(imageView);
        }
    }
}
