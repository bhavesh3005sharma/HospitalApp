package com.scout.hospitalapp.Fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.agrawalsuneet.dotsloader.loaders.TrailingCircularDotsLoader;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.scout.hospitalapp.Activities.AppointmentDetailsActivity;
import com.scout.hospitalapp.Activities.BookAppointmentActivity;
import com.scout.hospitalapp.Activities.DoctorsActivity;
import com.scout.hospitalapp.Adapter.AppointmentsAdapter;
import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.HomeViewModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener , AppointmentsAdapter.interfaceClickListener , View.OnClickListener , DatePickerDialog.OnDateSetListener{
    @BindView(R.id.recyclerViewAppointment) RecyclerView recyclerView;
    @BindView(R.id.shimmerLayout) ShimmerFrameLayout shimmerLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fab_book_appointment) ExtendedFloatingActionButton fabBookAppointment;
    @BindView(R.id.layout_filter) CardView layoutFilter;
    @BindView(R.id.textViewFilterDate) TextView textViewFilterDate;
    @BindView(R.id.textViewFilterTime) TextView textViewFilterTime;
    @BindView(R.id.textViewFilter) TextView textViewFilter;

    private ArrayList<ModelAppointment> list = new ArrayList<ModelAppointment>();
    private Unbinder unbinder;
    private Boolean isScrolling = false , isLoading = false;
    private int currentItems, totalItems, scrollOutItems, startingIndex=-1, check=0;
    private String hospitalId;
    private HomeViewModel homeViewModel;
    private AppointmentsAdapter adapter;
    private String filterQuery = "";
    private com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog;

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this,root);
        swipeRefreshLayout.setOnRefreshListener(this);

        isLoading = true;
        hospitalId = homeViewModel.getHospitalId(getContext());
        homeViewModel.loadAppointmentIdsList(hospitalId);

        fabBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BookAppointmentActivity.class));
            }
        });

        homeViewModel.getAppointmentsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<ModelAppointment>>() {
            @Override
            public void onChanged(ArrayList<ModelAppointment> response) {
                isLoading = false;
                if (response!=null){
                    list.addAll(response);
                    adapter.getFilter().filter(filterQuery);
                }
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                HelperClass.hideProgressbar(progressBar);
                recyclerView.setVisibility(View.VISIBLE);
                layoutFilter.setVisibility(View.VISIBLE);
            }
        });

        homeViewModel.getStartingIndexOfList().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                startingIndex = integer;
            }
        });

        initRecyclerView();
        textViewFilterDate.setOnClickListener(this);
        textViewFilterTime.setOnClickListener(this);
        textViewFilter.setOnClickListener(this);

        return root;
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();
        adapter = new AppointmentsAdapter(getContext(), list, true);
        recyclerView.setAdapter(adapter);
        adapter.setUpOnClickListener(HomeFragment.this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                    currentItems = manager.getChildCount();
                    totalItems = manager.getItemCount();
                    scrollOutItems = manager.findFirstVisibleItemPosition();

                    if(isScrolling && (currentItems + scrollOutItems == totalItems) && startingIndex!=-1 && !isLoading) {
                        isLoading = true;
                        isScrolling = false;
                        HelperClass.showProgressbar(progressBar);
                        homeViewModel.loadAppointments(startingIndex);
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!isLoading) {
            list.clear();
            isLoading = true;
            layoutFilter.setVisibility(View.GONE);
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();
            homeViewModel.loadAppointmentIdsList(hospitalId);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void holderClick(int position) {
        startActivity(new Intent(getContext(), AppointmentDetailsActivity.class).putExtra("modelAppointment",list.get(position)));
    }

    @Override
    public void onItemSelected(String appointmentId, int position) {
        openAlertDialogueChooseStatus(appointmentId,position);
    }

    @Override
    public void loadMoreData() {
        if ( startingIndex != -1 && !isLoading) {
            Log.d("getAppointmentsList","loading Data..");
            isLoading = true;
            isScrolling = false;
            HelperClass.showProgressbar(progressBar);
            homeViewModel.loadAppointments(startingIndex);
        }
    }

    private void openAlertDialogueChooseStatus(String appointmentId, int position) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( getContext() );
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialogue_set_appointment_status, null, false);
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
                    HelperClass.toast(getContext(),"Please Select Status");
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
                    homeViewModel.setStatus(hospitalId,appointmentId,status).observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            if (("Status Updated Successfully").equals(s)){
                                list.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                            trailingCircularDotsLoader.setVisibility(View.GONE);
                            HelperClass.toast(getContext(),s);
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void openDatePicker(){
        Calendar now = Calendar.getInstance();
        datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                HomeFragment.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );

        datePickerDialog.setMinDate(now);
        now.add(Calendar.MONTH,2);
        datePickerDialog.setMaxDate(now);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            datePickerDialog.setAccentColor(getContext().getColor(R.color.colorPrimary));
        }
        datePickerDialog.setOkColor(Color.WHITE);
        datePickerDialog.setCancelColor(Color.WHITE);
        datePickerDialog.show(getChildFragmentManager(),"DATE_PICKER");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textViewFilter :
                String text = textViewFilter.getText().toString().trim();
                String filterDate = textViewFilterDate.getText().toString().trim();
                String filterTime = textViewFilterTime.getText().toString().trim();

                if (filterDate.equals(getString(R.string.example_date))){
                    HelperClass.toast(getContext(),"Select Date");
                    return;
                }

                if (filterTime.equals(getString(R.string.example_time)))
                    filterTime = "";

                if (text.equals(getString(R.string.filter))) {
                    filterQuery = filterDate + "#" + filterTime;
                    adapter.getFilter().filter(filterQuery);
                    textViewFilter.setText(getString(R.string.clear_filter));
                    textViewFilterDate.setEnabled(false);
                    textViewFilterTime.setEnabled(false);
                }
                else {
                    filterQuery = "";
                    adapter.getFilter().filter(filterQuery);
                    textViewFilter.setText(getString(R.string.filter));
                    textViewFilterDate.setText(getString(R.string.example_date));
                    textViewFilterTime.setText(getString(R.string.example_time));
                    textViewFilterDate.setEnabled(true);
                    textViewFilterTime.setEnabled(true);
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
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
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
}
