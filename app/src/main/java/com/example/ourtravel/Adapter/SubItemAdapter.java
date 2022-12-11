package com.example.ourtravel.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ourtravel.R;

import java.util.ArrayList;

public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {

    private ArrayList<String> subItemList;

    SubItemAdapter(ArrayList<String> subItemList) {
        this.subItemList = subItemList;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_sub_item, viewGroup, false);

        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemViewHolder subItemViewHolder, int i) {
        String subItem = subItemList.get(i);

        String url = "http://3.39.168.165/diaryimg/" + subItem;
        Glide.with(subItemViewHolder.itemView.getContext()).load(url).into(subItemViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return subItemList.size();
    }

    public class SubItemViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public SubItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.rv_img);
        }
    }
}
