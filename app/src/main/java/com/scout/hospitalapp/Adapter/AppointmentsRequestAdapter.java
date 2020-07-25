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
import java.util.ArrayList;
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
        holder.date.setText(appointment.getAppointmentDate());
        holder.time.setText(appointment.getAppointmentTime());
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
        @BindView(R.id.text_date) TextView date;
        @BindView(R.id.text_time) TextView time;
        @BindView(R.id.buttonConfirm) Button buttonConfirm;
        @BindView(R.id.buttonReject) Button buttonReject;
        @BindView(R.id.text_doctor_name) TextView doctorName;
        @BindView(R.id.textViewStatus) TextView textViewStatus;
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
