package com.scout.hospitalapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.agrawalsuneet.dotsloader.loaders.TrailingCircularDotsLoader;
import com.scout.hospitalapp.Adapter.AppointmentsSearchAdapter;
import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.SearchViewModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,DatePickerDialog.OnDateSetListener, AppointmentsSearchAdapter.interfaceClickListener {
    @BindView(R.id.layout_filter) CardView layoutFilter;
    @BindView(R.id.textViewFilterDate) TextView textViewFilterDate;
    @BindView(R.id.textViewFilterTime) TextView textViewFilterTime;
    @BindView(R.id.textViewFilter) TextView textViewFilter;
    @BindView(R.id.imageNoData) ImageView imageNoData;
    @BindView(R.id.recyclerViewSearchResults) RecyclerView recyclerView;
    @BindView(R.id.trailingCircularDotsLoader) TrailingCircularDotsLoader trailingCircularDotsLoader;
    @BindView(R.id.progressBarFilter) ProgressBar progressBarFilter;

    private ArrayList<ModelAppointment> list = new ArrayList<ModelAppointment>();
    private Unbinder unbinder;
    AppointmentsSearchAdapter adapter;
    private String hospitalId;
    private String filterQuery = "";
    SearchViewModel searchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        unbinder = ButterKnife.bind(this);

        getSupportActionBar().setTitle("Search Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textViewFilter.setText(getString(R.string.search));
        hospitalId = searchViewModel.getHospitalId(this);

        initRecyclerVIew();
        textViewFilter.setOnClickListener(this);
        textViewFilterDate.setOnClickListener(this);
        textViewFilterTime.setOnClickListener(this);
    }

    private void initRecyclerVIew() {
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.hasFixedSize();
        adapter = new AppointmentsSearchAdapter( SearchActivity.this,list,false);
        recyclerView.setAdapter(adapter);
        adapter.setUpOnClickListener(SearchActivity.this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void openDatePicker(){
        Calendar now = Calendar.getInstance();

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                SearchActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            datePickerDialog.setAccentColor(this.getColor(R.color.colorPrimary));
        }
        datePickerDialog.setOkColor(Color.WHITE);
        datePickerDialog.setCancelColor(Color.WHITE);
        datePickerDialog.show(getSupportFragmentManager(),"DATE_PICKER");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textViewFilter :
                String text = textViewFilter.getText().toString().trim();
                String filterDate = textViewFilterDate.getText().toString().trim();
                final String[] filterTime = {textViewFilterTime.getText().toString().trim()};

                if (filterDate.equals(getString(R.string.example_date))){
                    HelperClass.toast(this,"Select Date");
                    return;
                }

                if (filterTime.equals(getString(R.string.example_time)))
                    filterTime[0] = "";

                if (text.equals(getString(R.string.search))) {
                    textViewFilter.setEnabled(false);
                    textViewFilterDate.setEnabled(false);
                    textViewFilterTime.setEnabled(false);
                    list.clear();
                    adapter.getFilter().filter("");
                    imageNoData.setVisibility(View.GONE);
                    trailingCircularDotsLoader.setVisibility(View.VISIBLE);
                    textViewFilter.setVisibility(View.INVISIBLE);
                    HelperClass.showProgressbar(progressBarFilter);
                    
                    searchViewModel.getFilterAppointments(filterDate,hospitalId).observe(SearchActivity.this, new Observer<ArrayList<ModelAppointment>>() {
                        @Override
                        public void onChanged(ArrayList<ModelAppointment> modelAppointments) {

                            trailingCircularDotsLoader.setVisibility(View.GONE);
                            textViewFilter.setVisibility(View.VISIBLE);
                            HelperClass.hideProgressbar(progressBarFilter);

                            textViewFilter.setEnabled(true);
                            textViewFilterDate.setEnabled(true);
                            textViewFilterTime.setEnabled(true);
                            
                            list.clear();
                            if (modelAppointments!=null)
                                list.addAll(modelAppointments);

                            if (filterTime[0].equals(getString(R.string.example_time)))
                                filterTime[0] = "";
                            filterQuery = filterDate + "#" + filterTime[0];
                            adapter.getFilter().filter(filterQuery);

                            if (list.size()==0)
                                imageNoData.setVisibility(View.VISIBLE);

                            textViewFilter.setText(getString(R.string.search));
                        }
                    });
                }
                break;
            case R.id.textViewFilterDate :
                openDatePicker();
                break;
            case R.id.textViewFilterTime :
                openTimePicker();
                break;
        }
    }

    private void openTimePicker() {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String hr = ""+selectedHour,min = ""+selectedMinute;
                if (selectedHour<10)
                    hr = "0"+selectedHour;
                if (selectedMinute<10)
                    min = "0"+selectedMinute;
                textViewFilterTime.setText(hr + " : " + min);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Filter Time");
        mTimePicker.show();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {
        month++;
        String dayOfMonth = ""+day;
        String monthOfYear = ""+month;
        if (dayOfMonth.length() == 1)
            dayOfMonth = "0" + dayOfMonth;

        if (monthOfYear.length()==1)
            monthOfYear = "0"+ month;
        String date = dayOfMonth+"-"+monthOfYear+"-"+year;
        textViewFilterDate.setText(date);
    }

    @Override
    public void onHolderClick(ModelAppointment modelAppointment) {
        startActivity(new Intent(SearchActivity.this, AppointmentDetailsActivity.class).putExtra("modelAppointment",modelAppointment));
    }

    @Override
    public void onChangeStatusClicked(String appointmentId, ModelAppointment appointment) {
        openAlertDialogueChooseStatus(appointmentId,appointment);
    }

    private void openAlertDialogueChooseStatus(String appointmentId, ModelAppointment appointment) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( this );
        View view = LayoutInflater.from(this).inflate(R.layout.dialogue_set_appointment_status, null, false);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        TrailingCircularDotsLoader trailingCircularDotsLoader = view.findViewById(R.id.trailingCircularDotsLoader);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedButtonId = radioGroup.getCheckedRadioButtonId();

                if (checkedButtonId==-1){
                    HelperClass.toast(SearchActivity.this,"Please Select Status");
                }else {
                    view.setAlpha(0.5f);
                    radioGroup.setEnabled(false);
                    buttonCancel.setEnabled(false);
                    buttonConfirm.setEnabled(false);
                    trailingCircularDotsLoader.setVisibility(View.VISIBLE);
                    String status = "";
                    switch (checkedButtonId){
                        case 1:
                            status = getString(R.string.completed);
                            break;
                        case 2:
                            status = getString(R.string.not_attempted);
                            break;
                        case 3:
                            status = getString(R.string.rejected);
                            break;
                    }
                    searchViewModel.setStatus(hospitalId,appointmentId,status).observe(SearchActivity.this, new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            if (("Status Updated Successfully").equals(s)){
                                list.remove(appointment);
                                adapter.notifyDataSetChanged();
                            }
                            trailingCircularDotsLoader.setVisibility(View.GONE);
                            HelperClass.toast(SearchActivity.this,s);
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAcceptingAppointment(ModelAppointment appointment) {
        searchViewModel.setStatus(hospitalId,appointment.getAppointmentId().getId(),getString(R.string.accepted));
    }

    @Override
    public void onRejectingAppointment(ModelAppointment appointment) {
        searchViewModel.setStatus(hospitalId,appointment.getAppointmentId().getId(),getString(R.string.rejected));
    }
}
