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

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.viewHolder> {
    ArrayList<ModelDoctorInfo> list;
    Context context;
    clickListener mListener;

    public DoctorAdapter(ArrayList<ModelDoctorInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setOnClickListener(clickListener mListener){
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_doctor, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        ModelDoctorInfo doctorInfo = list.get(position);
        holder.doctorName.setText(doctorInfo.getName());
        holder.department.setText(doctorInfo.getDepartment());
        holder.location.setText(doctorInfo.getAddress());
        holder.text_phoneNo.setText(doctorInfo.getPhone_no());

        holder.location.setVisibility(View.GONE);
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.removeDoctor(position);
            }
        });
    }

    @Override
    public int getItemCount()   {
        return(list!=null? list.size() : 0);
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.text_doctor_name)
        TextView doctorName;
        @BindView(R.id.text_department)
        TextView department;
        @BindView(R.id.text_location)
        TextView location;
        @BindView(R.id.text_phoneNo)
        TextView text_phoneNo;
        @BindView(R.id.profileImage)
        CircularImageView profileImage;
        @BindView(R.id.cancel)
        ImageView cancel;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface clickListener{
        void removeDoctor(int position);
    }
}
