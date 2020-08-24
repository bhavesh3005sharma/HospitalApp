package com.scout.hospitalapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

public class AppointmentsRequestAdapter extends RecyclerView.Adapter<AppointmentsRequestAdapter.viewHolder> implements Filterable {
    Context context;
    ArrayList<ModelAppointment> list;
    ArrayList<ModelAppointment> filteredList;
    interfaceClickListener mListener;

    public AppointmentsRequestAdapter(Context context, ArrayList<ModelAppointment> list) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_appointment_request, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ModelAppointment appointment = filteredList.get(position);
        holder.date.setText(context.getString(R.string.mdtp_date)+" : "+appointment.getAppointmentDate());
        holder.time.setText(context.getString(R.string.mdtp_time)+" - "+appointment.getAppointmentTime());
        holder.doctorName.setText(appointment.getDoctorName()+" (Doctor Name)");
        holder.textViewStatus.setVisibility(View.GONE);

        holder.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.buttonConfirm.setVisibility(View.GONE);
                holder.buttonReject.setVisibility(View.GONE);
                holder.textViewStatus.setVisibility(View.VISIBLE);

                holder.textViewStatus.setText(R.string.appointment_confirmed);
                holder.textViewStatus.setVisibility(View.VISIBLE);
                mListener.onAcceptingAppointment(position);
            }
        });

        holder.buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.buttonConfirm.setVisibility(View.GONE);
                holder.buttonReject.setVisibility(View.GONE);
                holder.textViewStatus.setVisibility(View.VISIBLE);

                holder.textViewStatus.setText(R.string.appointment_rejected);
                holder.textViewStatus.setVisibility(View.VISIBLE);
                mListener.onRejectingAppointment(position);
            }
        });
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
                if ((date1).compareTo(date2)==0){
                    return getTimeDifference(o1.getAppointmentTime()+" - "+o2.getAppointmentTime());
                }else
                    return ((date1).compareTo(date2));
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
                }
                else {
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
        @BindView(R.id.text_date) TextView date;
        @BindView(R.id.text_time) TextView time;
        @BindView(R.id.buttonConfirm) Button buttonConfirm;
        @BindView(R.id.buttonCancel) Button buttonReject;
        @BindView(R.id.text_doctor_name) TextView doctorName;
        @BindView(R.id.textViewSerialNo) TextView textViewStatus;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.holderClick(getAdapterPosition());
                }
            });
        }
    }

    public interface interfaceClickListener{
        void holderClick(int position);

        void onAcceptingAppointment(int position);

        void onRejectingAppointment(int position);
    }
}
