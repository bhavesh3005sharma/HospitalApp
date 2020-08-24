package com.scout.hospitalapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
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
    private Context context;
    private ArrayList<ModelAppointment> list;
    private ArrayList<ModelAppointment> filteredList;
    private interfaceClickListener mListener;
    private Boolean isIncreasingSortingOrder;
    private int check=0;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_appointment, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ModelAppointment appointment = filteredList.get(position);
        holder.date.setText(context.getString(R.string.mdtp_date)+" - "+appointment.getAppointmentDate());
        holder.time.setText(context.getString(R.string.mdtp_time)+" - "+appointment.getAppointmentTime());
        holder.doctorName.setText(appointment.getDoctorName()+" ("+context.getString(R.string.doctor_name)+")");
        holder.textViewSerialNo.setText(context.getString(R.string.serial_number)+" "+appointment.getSerialNumber());

        if (appointment.getStatus().equals(context.getString(R.string.accepted))) {
            holder.textChangeStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemSelected(appointment.getAppointmentId().getId(),position);
                }
            });
            holder.textChangeStatus.setText(context.getString(R.string.change_status));
        }
        else
            holder.textChangeStatus.setText(appointment.getStatus());
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
                        String[] filterData = charString.split("#");
                        String date = filterData[0];
                        String time = "";
                        if (filterData.length>1)
                          time = filterData[1];

                        if (!time.isEmpty() && date.equals(row.getAppointmentDate()) && isAFilteredTime(row.getAppointmentTime(),time))
                            listFilterByQuery.add(row);
                        else if(time.isEmpty() && date.equals(row.getAppointmentDate()))
                            listFilterByQuery.add(row);


                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
//                        if (row.getStatus().toLowerCase().contains(charString.toLowerCase()) || row.getAppointmentDate().toLowerCase().contains(charString.toLowerCase()) || row.getDisease().toLowerCase().contains(charString.toLowerCase())
//                                || row.getAppointmentTime().toLowerCase().contains(charString.toLowerCase()) || row.getDoctorName().toLowerCase().contains(charString.toLowerCase())) {
//                            listFilterByQuery.add(row);
//                        }
                    }
                    filteredList = listFilterByQuery;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results.values != null) {
                    filteredList = (ArrayList<ModelAppointment>) results.values;
                    SortDateWise();
                    if (++check>1 && filteredList.size()<5) {
                        Log.d("getAppointmentsList","loadMoreData");
                        mListener.loadMoreData();
                    }
                }
            }
        };
    }

    private boolean isAFilteredTime(String appointmentTime, String time) {
        String[] result;
        result = appointmentTime.split("-");

        Log.d("First",""+getTimeDifference(result[0]+"-"+time));
        Log.d("Second",""+getTimeDifference(time+"-"+result[1]));
        if(getTimeDifference(result[0]+"-"+time)>=0 && getTimeDifference(time+"-"+result[1])>=0)
            return true;
        else
            return false;
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.text_date) TextView date;
        @BindView(R.id.text_time) TextView time;
        @BindView(R.id.text_doctor_name) TextView doctorName;
        @BindView(R.id.textViewSerialNo) TextView textViewSerialNo;
        @BindView(R.id.textChangeStatus) TextView textChangeStatus;
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

        void onItemSelected(String appointmentId, int position);

        void loadMoreData();
    }
}
