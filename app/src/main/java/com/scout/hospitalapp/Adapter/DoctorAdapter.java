package com.scout.hospitalapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.viewHolder> implements Filterable {
    ArrayList<ModelDoctorInfo> filteredList;
    ArrayList<ModelDoctorInfo> list;
    Context context;
    clickListener mListener;

    public DoctorAdapter(ArrayList<ModelDoctorInfo> list, Context context) {
        this.list = list;
        this.filteredList = list;
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
        Log.d("Adapter",filteredList.toString());
        ModelDoctorInfo doctorInfo = filteredList.get(position);
        if (doctorInfo!=null) {
            holder.doctorName.setText(doctorInfo.getName());
            holder.department.setText(doctorInfo.getDepartment());
            holder.location.setText(doctorInfo.getAddress());
            holder.text_phoneNo.setText(doctorInfo.getPhone_no());
        }

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
        return(filteredList !=null? filteredList.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = list;
                } else {
                    ArrayList<ModelDoctorInfo> listFilterByQuery = new ArrayList<>();
                    for (ModelDoctorInfo row : list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getDepartment().toLowerCase().contains(charString.toLowerCase())) {
                            listFilterByQuery.add(row);
                        }
                    }
                    filteredList = listFilterByQuery;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<ModelDoctorInfo>) results.values;
                notifyDataSetChanged();
            }
        };
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
        ImageView profileImage;
        @BindView(R.id.cancel)
        ImageView cancel;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface clickListener{
        void removeDoctor(int position);

        void onItemClick(int adapterPosition);
    }
}
