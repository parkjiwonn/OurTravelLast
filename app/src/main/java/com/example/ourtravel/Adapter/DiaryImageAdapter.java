package com.example.ourtravel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ourtravel.R;
import com.example.ourtravel.RecyclerView.DiaryImageData;

import java.util.ArrayList;

public class DiaryImageAdapter extends RecyclerView.Adapter<DiaryImageAdapter.ViewHolder> {

    private final ArrayList<DiaryImageData> diaryImageDataArrayList ;
    private Context Context;
    int pos;

    public DiaryImageAdapter(ArrayList<DiaryImageData> diaryImageDataArrayList, Context context){
        this.diaryImageDataArrayList = diaryImageDataArrayList;
        this.Context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        // context에서 LayoutInflater 객체를 얻는다.

        View view = inflater.inflate(R.layout.diaryimageitem, parent, false) ;

        DiaryImageAdapter.ViewHolder vh = new DiaryImageAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DiaryImageData diaryImageData = diaryImageDataArrayList.get(position);

        String imagerul = diaryImageData.getPhoto();

        String url = "http://3.39.168.165/diaryimg/" + imagerul;
        Glide.with(holder.itemView.getContext()).load(url).into(holder.image);

        Context = holder.itemView.getContext();
    }

    @Override
    public int getItemCount() {
        return diaryImageDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.diaryimage);
        }
    }
}
