package com.scout.hospitalapp.Adapter;

import android.content.Context;
import android.util.Log;
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

public class AppointmentsSearchAdapter extends RecyclerView.Adapter implements Filterable {
    private Context context;
    private ArrayList<ModelAppointment> list;
    private ArrayList<ModelAppointment> filteredList;
    private interfaceClickListener mListener;
    private Boolean isIncreasingSortingOrder;

    public AppointmentsSearchAdapter(Context context, ArrayList<ModelAppointment> list, Boolean isIncreasingSortingOrder) {
        this.context = context;
        this.list = list;
        this.filteredList = list;
        this.isIncreasingSortingOrder = isIncreasingSortingOrder;
    }

    public void  setUpOnClickListener(interfaceClickListener mListener) {
        this.mListener = mListener;
    }

    public class viewHolderAppointment extends RecyclerView.ViewHolder{
        @BindView(R.id.text_date) TextView date;
        @BindView(R.id.text_time) TextView time;
        @BindView(R.id.text_doctor_name) TextView doctorName;
        @BindView(R.id.textViewSerialNo) TextView textViewSerialNo;
        @BindView(R.id.textChangeStatus) TextView textChangeStatus;

        private viewHolderAppointment(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null)
                        mListener.onHolderClick(filteredList.get(getAdapterPosition()));
                }
            });
        }
    }

    public class viewHolderAppointmentRequest extends RecyclerView.ViewHolder{
        @BindView(R.id.text_date) TextView date;
        @BindView(R.id.text_time) TextView time;
        @BindView(R.id.buttonConfirm) Button buttonConfirm;
        @BindView(R.id.buttonCancel) Button buttonReject;
        @BindView(R.id.text_doctor_name) TextView doctorName;
        @BindView(R.id.textViewSerialNo) TextView textViewStatus;
        private viewHolderAppointmentRequest(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener!=null)
                        mListener.onHolderClick(filteredList.get(getAdapterPosition()));
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==1) {
             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_appointment, parent, false);
            return new viewHolderAppointment(view);
        }
        if (viewType==2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_appointment_request, parent, false);
            return new viewHolderAppointmentRequest(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelAppointment appointment = filteredList.get(position);

        if (appointment.getStatus().equals(context.getString(R.string.pending))){
            ((viewHolderAppointmentRequest) holder).date.setText(context.getString(R.string.mdtp_date)+" : "+appointment.getAppointmentDate());
            ((viewHolderAppointmentRequest) holder).time.setText(context.getString(R.string.mdtp_time)+" - "+appointment.getAppointmentTime());
            ((viewHolderAppointmentRequest) holder).doctorName.setText(appointment.getDoctorName()+" (Doctor Name)");
            ((viewHolderAppointmentRequest) holder).textViewStatus.setVisibility(View.GONE);

            ((viewHolderAppointmentRequest) holder).buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((viewHolderAppointmentRequest) holder).buttonConfirm.setVisibility(View.GONE);
                    ((viewHolderAppointmentRequest) holder).buttonReject.setVisibility(View.GONE);

                    ((viewHolderAppointmentRequest) holder).textViewStatus.setText(R.string.appointment_confirmed);
                    ((viewHolderAppointmentRequest) holder).textViewStatus.setVisibility(View.VISIBLE);
                    if (mListener!=null)
                        mListener.onAcceptingAppointment(appointment);
                }
            });

            ((viewHolderAppointmentRequest) holder).buttonReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((viewHolderAppointmentRequest) holder).buttonConfirm.setVisibility(View.GONE);
                    ((viewHolderAppointmentRequest) holder).buttonReject.setVisibility(View.GONE);

                    ((viewHolderAppointmentRequest) holder).textViewStatus.setText(R.string.appointment_rejected);
                    ((viewHolderAppointmentRequest) holder).textViewStatus.setVisibility(View.VISIBLE);
                    if (mListener!=null)
                        mListener.onRejectingAppointment(appointment);
                }
            });
        }
        else {
            ((viewHolderAppointment) holder).date.setText(context.getString(R.string.mdtp_date)+" - "+appointment.getAppointmentDate());
            ((viewHolderAppointment) holder).time.setText(context.getString(R.string.mdtp_time)+" - "+appointment.getAppointmentTime());
            ((viewHolderAppointment) holder).doctorName.setText(appointment.getDoctorName()+" ("+context.getString(R.string.doctor_name)+")");
            ((viewHolderAppointment) holder).textViewSerialNo.setText(context.getString(R.string.serial_number)+" "+appointment.getSerialNumber());

            if (appointment.getStatus().equals(context.getString(R.string.accepted))) {
                ((viewHolderAppointment) holder).textChangeStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener!=null)
                            mListener.onChangeStatusClicked(appointment.getAppointmentId().getId(),appointment);
                    }
                });
                ((viewHolderAppointment) holder).textChangeStatus.setText(context.getString(R.string.change_status));
            }
            else
                ((viewHolderAppointment) holder).textChangeStatus.setText(appointment.getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return(filteredList!=null? filteredList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (filteredList.get(position).getStatus().equals(context.getString(R.string.pending)))
            return 2;
        else
            return 1;
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

    public interface interfaceClickListener{

        void onHolderClick(ModelAppointment modelAppointment);

        void onChangeStatusClicked(String appointmentId, ModelAppointment appointment);

        void onAcceptingAppointment(ModelAppointment appointment);

        void onRejectingAppointment(ModelAppointment appointment);
    }
}
