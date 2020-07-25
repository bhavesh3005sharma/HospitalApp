package com.scout.hospitalapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.scout.hospitalapp.Adapter.AppointmentsRequestAdapter;
import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.AppointmentsViewModel;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AppointmentRequestsFragment extends Fragment implements AppointmentsRequestAdapter.interfaceClickListener, SwipeRefreshLayout.OnRefreshListener{
    @BindView(R.id.recyclerViewAppointmentRequest) RecyclerView recyclerView;
    @BindView(R.id.shimmerLayout) ShimmerFrameLayout shimmerLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<ModelAppointment> list = new ArrayList<ModelAppointment>();
    private Unbinder unbinder;
    private AppointmentsRequestAdapter adapter;
    private Boolean isScrolling = false , isLoading = false;
    private int currentItems, totalItems, scrollOutItems, startingIndex=-1;
    private AppointmentsViewModel appointmentsViewModel;
    private String hospitalId;

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appointmentsViewModel = ViewModelProviders.of(this).get(AppointmentsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_appointment_request, container, false);
        unbinder = ButterKnife.bind(this,view);
        swipeRefreshLayout.setOnRefreshListener(this);

        isLoading = true;
        hospitalId = appointmentsViewModel.getHospitalId(getContext());
        appointmentsViewModel.loadAppointmentRequestsIdsList(hospitalId);

        appointmentsViewModel.getAppointmentsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<ModelAppointment>>() {
            @Override
            public void onChanged(ArrayList<ModelAppointment> response) {
                isLoading = false;
                if (response!=null){
                    list.addAll(response);
                    adapter.notifyDataSetChanged();
                }
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                HelperClass.hideProgressbar(progressBar);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        appointmentsViewModel.getStartingIndexOfList().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                startingIndex = integer;
            }
        });

        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();
        adapter = new AppointmentsRequestAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        adapter.setUpOnClickListener(AppointmentRequestsFragment.this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems) && startingIndex!=-1 && !isLoading)
                {
                    isLoading = true;
                    isScrolling = false;
                    HelperClass.showProgressbar(progressBar);
                    appointmentsViewModel.loadAppointments(startingIndex);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!isLoading) {
            list.clear();
            isLoading = true;
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();
            appointmentsViewModel.loadAppointmentRequestsIdsList(hospitalId);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void holderClick(int position) {
        openAppointmentDetails(position);
    }

    @Override
    public void onAcceptingAppointment(int position) {
        appointmentsViewModel.setStatus(hospitalId,list.get(position).getAppointmentId().getId(),getString(R.string.accepted));
    }

    @Override
    public void onRejectingAppointment(int position) {
        appointmentsViewModel.setStatus(hospitalId,list.get(position).getAppointmentId().getId(),getString(R.string.rejected));
    }

    private void openAppointmentDetails(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialogue_appointment_details, null, false);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView doctorHospitalName = view.findViewById(R.id.textViewDoctorHospitalName);
        TextView patientName = view.findViewById(R.id.textViewName);
        TextView textViewDate = view.findViewById(R.id.textViewDate);
        TextView textViewTime = view.findViewById(R.id.textViewTime);
        TextView textViewAge = view.findViewById(R.id.textViewAge);
        TextView textViewDisease = view.findViewById(R.id.textViewDisease);

        ModelAppointment appointment = list.get(position);
        doctorHospitalName.setText(appointment.getDoctorName()+"\n("+appointment.getHospitalName()+")");
        patientName.setText(appointment.getPatientName());
        textViewDate.setText(appointment.getAppointmentDate());
        textViewTime.setText(appointment.getAppointmentTime());
        textViewAge.setText(appointment.getAge());
        textViewDisease.setText(appointment.getDisease());
    }
}
