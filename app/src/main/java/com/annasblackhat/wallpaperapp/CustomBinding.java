package com.annasblackhat.wallpaperapp;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


/**
 * Created by Git Solution on 01/08/2017.
 */

public class CustomBinding {
    @BindingAdapter({"imageUrl"})
    public static void loadImageUrl(ImageView imageView, String url) {
        System.out.println("xxx load image : "+url);

        Glide.with(imageView.getContext())
                .load(url)
//                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
    }

    @BindingAdapter({"android:src"})
    public static void setImageUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }
}
