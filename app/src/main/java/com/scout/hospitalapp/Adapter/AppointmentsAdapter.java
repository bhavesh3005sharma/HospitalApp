package com.scout.hospitalapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.R;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.viewHolder> implements Filterable {
    Context context;
    ArrayList<ModelAppointment> list;
    ArrayList<ModelAppointment> filteredList;
    interfaceClickListener mListener;

    public AppointmentsAdapter(Context context, ArrayList<ModelAppointment> list) {
        this.context = context;
        this.list = list;
        this.filteredList = list;
    }

    public void  setUpOnClickListener(interfaceClickListener mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialogue_appointment_details, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ModelAppointment appointment = filteredList.get(position);
        holder.date.setText(appointment.getAppointmentDate());
        holder.time.setText(appointment.getAppointmentTime());
        holder.doctorName.setText(appointment.getDoctorName());
        holder.textViewName.setText(appointment.getPatientName());
        holder.textViewAge.setText(appointment.getAge());
        holder.textViewDisease.setText(appointment.getDisease());
        holder.selectionSpinner.setVisibility(View.GONE);

        holder.parentCard.setRadius(20);
        if (appointment.getStatus().equals(context.getString(R.string.accepted))) {
            holder.selectionSpinner.setVisibility(View.VISIBLE);
            holder.selectionSpinner.setSelection(0);
            holder.selectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    mListener.onItemSelected(pos, position, parent.getItemAtPosition(pos));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        else{
            holder.status.setVisibility(View.VISIBLE);
            if (appointment.getStatus().equals(context.getString(R.string.accepted)) || appointment.getStatus().equals(context.getString(R.string.completed))){
                holder.status.setText(appointment.getStatus());
                holder.status.setTextColor(Color.WHITE);
                holder.status.setBackgroundResource(R.drawable.accepted_backgrounded);
            }
            if (appointment.getStatus().equals(context.getString(R.string.rejected))){
                holder.status.setText(appointment.getStatus());
                holder.status.setTextColor(Color.WHITE);
                holder.status.setBackgroundResource(R.drawable.rejected_backgrounded);
            }
            if (appointment.getStatus().equals(context.getString(R.string.pending)) || appointment.getStatus().equals(context.getString(R.string.not_attempted))){
                holder.status.setText(appointment.getStatus());
                holder.status.setTextColor(Color.BLACK);
                holder.status.setBackgroundResource(R.drawable.pending_backgrounded);
            }
        }
    }

    @Override
    public int getItemCount() {
        return(filteredList!=null? filteredList.size() : 0);
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
                    ArrayList<ModelAppointment> listFilterByQuery = new ArrayList<>();
                    for (ModelAppointment row : list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getStatus().toLowerCase().contains(charString.toLowerCase()) || row.getAppointmentDate().toLowerCase().contains(charString.toLowerCase()) || row.getDisease().toLowerCase().contains(charString.toLowerCase())
                                || row.getAppointmentTime().toLowerCase().contains(charString.toLowerCase()) || row.getDoctorName().toLowerCase().contains(charString.toLowerCase())) {
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
                filteredList = (ArrayList<ModelAppointment>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.textViewDate) TextView date;
        @BindView(R.id.textViewTime) TextView time;
        @BindView(R.id.textViewAge) TextView textViewAge;
        @BindView(R.id.textViewDisease) TextView textViewDisease;
        @BindView(R.id.textViewDoctorHospitalName) TextView doctorName;
        @BindView(R.id.textViewName) TextView textViewName;
        @BindView(R.id.textViewStatus) TextView status;
        @BindView(R.id.selectionSpinner) Spinner selectionSpinner;
        @BindView(R.id.parentCard) CardView parentCard;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null)
                    mListener.holderClick(getAdapterPosition());
                }
            });
        }
    }

    public interface interfaceClickListener{
        void holderClick(int position);

        void onItemSelected(int pos, int adapterPosition, Object itemAtPosition);
    }
}
