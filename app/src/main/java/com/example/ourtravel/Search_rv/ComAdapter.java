package com.example.ourtravel.Search_rv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourtravel.R;

import java.util.ArrayList;

public class ComAdapter extends RecyclerView.Adapter<ComAdapter.ViewHolder> {

    private final ArrayList<ComData> comDataArrayList;
    private Context context;
    private final String TAG = this.getClass().getSimpleName();
    int pos;

    public ComAdapter(ArrayList<ComData> comDataArrayList, Context context) {
        this.comDataArrayList = comDataArrayList;
        this.context = context;
    }

    // OnClickListener Custom --------------------------
    public interface OnItemClickListener{
        void onItemClick(int pos);
    }

    public OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }
    //---------------------------------------------------


    @NonNull
    @Override
    public ComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "태그 onCreateViewHolder 들어옴");

        Context Rcontext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) Rcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recent_research_item, parent, false);

        ComAdapter.ViewHolder vh = new ComAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ComData comData = comDataArrayList.get(position);

        holder.tx_research.setText(comData.getResearch());
        holder.tx_date.setText(comData.getDate());

        context = holder.itemView.getContext();

    }

    @Override
    public int getItemCount() {
        return comDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tx_research, tx_date;
        public ImageButton btn_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tx_research = itemView.findViewById(R.id.tx_research);
            this.tx_date = itemView.findViewById(R.id.tx_date);
            this.btn_delete = itemView.findViewById(R.id.btn_delete);
            // 검색어 개별 삭제 기능 넣어야 함.
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        onItemClickListener.onItemClick(pos);
                    }
                }
            });

        }
    }
}
