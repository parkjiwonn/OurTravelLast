package com.example.ourtravel.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourtravel.Map.Model.ListClass;
import com.example.ourtravel.R;

import java.util.ArrayList;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder>{

    ArrayList<ListClass> list;
    int pos;

    public RecyclerviewAdapter(ArrayList<ListClass> list) {
        this.list = list;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }

    private RecyclerviewAdapter.OnItemClickListener listener = null;

    public void setOnItemClickListener(OnItemClickListener listener) { this.listener =listener;}

    @NonNull
    @Override
    public RecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerviewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.map_search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewAdapter.ViewHolder holder, int position) {

        holder.textview.setText(list.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textview = itemView.findViewById(R.id.textview);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION)
                    {
                        if(listener != null)
                        {
                            listener.onItemClick(view, pos);
                        }

                    }
                }
            });
        }
    }
}
