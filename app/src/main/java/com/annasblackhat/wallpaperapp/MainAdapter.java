package com.annasblackhat.wallpaperapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annasblackhat.wallpaperapp.databinding.ListItemImageBinding;

import java.util.List;

/**
 * Created by Git Solution on 01/08/2017.
 */

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {

    private List<String> wallpapers ;

    public MainAdapter(List<String> wallpapers) {
        this.wallpapers = wallpapers;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemImageBinding binding = ListItemImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MainViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        holder.getBinding().setVariable(com.annasblackhat.wallpaperapp.BR.image, wallpapers.get(position));
        holder.getBinding().executePendingBindings();

        ((ListItemImageBinding)holder.getBinding()).btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)holder.getContext()).setWallpaper(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }
}
