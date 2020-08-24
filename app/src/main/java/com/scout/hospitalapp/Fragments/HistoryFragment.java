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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.agrawalsuneet.dotsloader.loaders.TrailingCircularDotsLoader;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.scout.hospitalapp.Activities.AppointmentDetailsActivity;
import com.scout.hospitalapp.Adapter.AppointmentsAdapter;
import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.HistoryViewModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener , AppointmentsAdapter.interfaceClickListener , View.OnClickListener , DatePickerDialog.OnDateSetListener{
    @BindView(R.id.recyclerViewAppointment) RecyclerView recyclerView;
    @BindView(R.id.shimmerLayout) ShimmerFrameLayout shimmerLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_filter) CardView layoutFilter;
    @BindView(R.id.textViewFilterDate) TextView textViewFilterDate;
    @BindView(R.id.textViewFilterTime) TextView textViewFilterTime;
    @BindView(R.id.textViewFilter) TextView textViewFilter;
    @BindView(R.id.trailingCircularDotsLoader) TrailingCircularDotsLoader trailingCircularDotsLoader;
    @BindView(R.id.textViewNoAppointments) TextView textViewNoAppointments;
    @BindView(R.id.progressBarFilter) ProgressBar progressBarFilter;
    @BindView(R.id.imageNoData) ImageView imageNoData;

    private ArrayList<ModelAppointment> list = new ArrayList<ModelAppointment>();
    private Unbinder unbinder;
    private Boolean isScrolling = false , isLoading = false;
    private int currentItems, totalItems, scrollOutItems, startingIndex=-1, check=0;
    private String hospitalId;
    private HistoryViewModel historyViewModel;
    private AppointmentsAdapter adapter;
    private String filterQuery = "";

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this,root);
        swipeRefreshLayout.setOnRefreshListener(this);

        isLoading = true;
        hospitalId = historyViewModel.getHospitalId(getContext());
        historyViewModel.loadAppointmentIdsList(hospitalId);

        historyViewModel.getAppointmentsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<ModelAppointment>>() {
            @Override
            public void onChanged(ArrayList<ModelAppointment> response) {
                Log.d("List",""+list.size());
                Log.d("Response",""+response.size());
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

                trailingCircularDotsLoader.setVisibility(View.GONE);
                if (list.size()==0)
                    textViewNoAppointments.setVisibility(View.VISIBLE);
                else
                    textViewNoAppointments.setVisibility(View.GONE);

                textViewFilter.setText(getString(R.string.filter));
                textViewFilter.setEnabled(true);
                textViewFilterDate.setText(getString(R.string.example_date));
                textViewFilterTime.setText(getString(R.string.example_time));
                textViewFilterDate.setEnabled(true);
                textViewFilterTime.setEnabled(true);
                HelperClass.hideProgressbar(progressBarFilter);
                textViewFilter.setVisibility(View.VISIBLE);
                imageNoData.setVisibility(View.GONE);
            }
        });

        historyViewModel.getStartingIndexOfList().observe(getViewLifecycleOwner(), new Observer<Integer>() {
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
        adapter = new AppointmentsAdapter(getContext(), list, false);
        recyclerView.setAdapter(adapter);
        adapter.setUpOnClickListener(HistoryFragment.this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                    currentItems = manager.getChildCount();
                    totalItems = manager.getItemCount();
                    scrollOutItems = manager.findFirstVisibleItemPosition();

                    if(isScrolling && (currentItems + scrollOutItems == totalItems) && startingIndex!=-1 && !isLoading && filterQuery.isEmpty()) {
                        isLoading = true;
                        isScrolling = false;
                        HelperClass.showProgressbar(progressBar);
                        historyViewModel.loadAppointments(startingIndex);
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!isLoading) {
            list.clear();
            adapter.notifyDataSetChanged();
            isLoading = true;
            filterQuery = "";
            layoutFilter.setVisibility(View.GONE);
            textViewNoAppointments.setVisibility(View.GONE);
            HelperClass.hideProgressbar(progressBar);
            trailingCircularDotsLoader.setVisibility(View.GONE);
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();
            imageNoData.setVisibility(View.GONE);
            historyViewModel.loadAppointmentIdsList(hospitalId);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void holderClick(int position) {
        startActivity(new Intent(getContext(), AppointmentDetailsActivity.class).putExtra("modelAppointment",list.get(position)));
    }

    @Override
    public void onItemSelected(String appointmentId, int position) {

    }

    @Override
    public void loadMoreData() {
        // We don't have more data because we get all filtered data from server.
    }

    private void openDatePicker(){
        Calendar now = Calendar.getInstance();
        // Initial year selection
        // Initial month selection
        // Inital day selection
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                HistoryFragment.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );

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
                final String[] filterTime = {textViewFilterTime.getText().toString().trim()};

                if (filterDate.equals(getString(R.string.example_date))){
                    HelperClass.toast(getContext(),"Select Date");
                    return;
                }

                if (text.equals(getString(R.string.filter))) {
                    textViewFilter.setEnabled(false);
                    textViewFilterDate.setEnabled(false);
                    textViewFilterTime.setEnabled(false);
                    isLoading = true;
                    list.clear();
                    adapter.notifyDataSetChanged();
                    trailingCircularDotsLoader.setVisibility(View.VISIBLE);
                    textViewFilter.setVisibility(View.INVISIBLE);
                    HelperClass.showProgressbar(progressBarFilter);

                    historyViewModel.getFilterAppointments(filterDate,hospitalId).observe(getViewLifecycleOwner(), new Observer<ArrayList<ModelAppointment>>() {
                        @Override
                        public void onChanged(ArrayList<ModelAppointment> modelAppointments) {
                            isLoading = false;

                            trailingCircularDotsLoader.setVisibility(View.GONE);
                            textViewFilter.setEnabled(true);
                            textViewFilter.setVisibility(View.VISIBLE);
                            HelperClass.hideProgressbar(progressBarFilter);
                            list.clear();
                            if (modelAppointments!=null)
                                list.addAll(modelAppointments);

                            if (filterTime[0].equals(getString(R.string.example_time)))
                                filterTime[0] = "";
                            filterQuery = filterDate + "#" + filterTime[0];
                            adapter.getFilter().filter(filterQuery);

                            if (list.size()==0)
                                imageNoData.setVisibility(View.VISIBLE);

                            textViewFilter.setText(getString(R.string.clear_filter));
                        }
                    });
                }
                else {
                    isLoading = true;
                    textViewFilter.setEnabled(false);
                    textViewFilterDate.setEnabled(false);
                    textViewFilterTime.setEnabled(false);

                    list.clear();
                    filterQuery = "";
                    adapter.getFilter().filter(filterQuery);
                    trailingCircularDotsLoader.setVisibility(View.VISIBLE);
                    textViewFilter.setVisibility(View.INVISIBLE);
                    HelperClass.showProgressbar(progressBarFilter);
                    imageNoData.setVisibility(View.GONE);
                    historyViewModel.loadAppointmentIdsList(hospitalId);
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
