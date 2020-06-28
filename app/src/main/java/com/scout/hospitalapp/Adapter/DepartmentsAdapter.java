package com.scout.hospitalapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.scout.hospitalapp.R;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DepartmentsAdapter extends RecyclerView.Adapter<DepartmentsAdapter.viewHolder> {
    ArrayList<String> list;
    Context context;
    clickListener mListener;

    public DepartmentsAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setOnClickListener(clickListener mListener){
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_department, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        holder.departmentsName.setText(list.get(position));

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.removeDepartment(position);
            }
        });
    }

    @Override
    public int getItemCount()  {
        return(list!=null? list.size() : 0);
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.departmentsName)
        TextView departmentsName;
        @BindView(R.id.cancel)
        ImageView cancel;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface clickListener{
        void removeDepartment(int position);
    }
}
