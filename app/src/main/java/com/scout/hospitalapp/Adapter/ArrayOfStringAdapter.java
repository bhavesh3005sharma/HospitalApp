package com.scout.hospitalapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.scout.hospitalapp.R;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArrayOfStringAdapter extends RecyclerView.Adapter<ArrayOfStringAdapter.viewHolder> {
    ArrayList<String> list;
    Context context;
    clickListener mListener;

    public ArrayOfStringAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setOnClickListener(clickListener mListener){
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_string_title, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        holder.title.setText(list.get(position));

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
                if(mListener!=null)
                mListener.removeItem(position);
            }
        });
    }

    @Override
    public int getItemCount()  {
        return(list!=null? list.size() : 0);
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.cancel)
        ImageView cancel;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface clickListener{
        void removeItem(int position);
    }
}
