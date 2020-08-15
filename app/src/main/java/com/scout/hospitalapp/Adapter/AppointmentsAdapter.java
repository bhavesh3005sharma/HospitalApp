package com.scout.hospitalapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.viewHolder> implements Filterable {
    Context context;
    ArrayList<ModelAppointment> list;
    ArrayList<ModelAppointment> filteredList;
    interfaceClickListener mListener;
    Boolean isIncreasingSortingOrder;

    public AppointmentsAdapter(Context context, ArrayList<ModelAppointment> list, Boolean isIncreasingSortingOrder) {
        this.context = context;
        this.list = list;
        this.filteredList = list;
        this.isIncreasingSortingOrder = isIncreasingSortingOrder;
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

    private void SortDateWise() {
        Collections.sort(filteredList,new Comparator<ModelAppointment>() {
            @Override
            public int compare (ModelAppointment o1, ModelAppointment o2){
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date date1 = new Date(), date2 = new Date();
                try {
                    date1 = sdf.parse(o1.getAppointmentDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    date2 = sdf.parse(o2.getAppointmentDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int check = 1;
                if (!isIncreasingSortingOrder)
                    check = -1;

                if ((date1).compareTo(date2)==0){
                    return getTimeDifference(o1.getAppointmentTime()+" - "+o2.getAppointmentTime())*check;
                }else
                    return ((date1).compareTo(date2))* check;
            }
        });
        notifyDataSetChanged();
    }

    private int getTimeDifference(String s) {
        String[] result,time1,time2;
        result = s.split("-");
        time1 = result[0].split(":");
        time2 = result[1].split(":");
        time1[0] = time1[0].replaceAll("\\s+", "");
        time2[0] = time2[0].replaceAll("\\s+", "");
        time1[1] = time1[1].replaceAll("\\s+", "");
        time2[1] = time2[1].replaceAll("\\s+", "");
        int h1,h2,m1,m2;
        h1 = Integer.valueOf(time1[0]);
        h2 = Integer.valueOf(time2[0]);
        m1 = Integer.valueOf(time1[1]);
        m2 = Integer.valueOf(time2[1]);

        if(h2>h1 && m2<m1){
            h2--;
            m2+=60;
        }
        return ((h2-h1)*60)+(m2-m1);
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
                SortDateWise();
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
