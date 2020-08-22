package com.scout.hospitalapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.scout.hospitalapp.Activities.AppointmentDetailsActivity;
import com.scout.hospitalapp.Adapter.AppointmentsAdapter;
import com.scout.hospitalapp.Models.ModelAppointment;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.HistoryViewModel;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener , AppointmentsAdapter.interfaceClickListener{
    @BindView(R.id.recyclerViewAppointment) RecyclerView recyclerView;
    @BindView(R.id.shimmerLayout) ShimmerFrameLayout shimmerLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<ModelAppointment> list = new ArrayList<ModelAppointment>();
    private Unbinder unbinder;
    private Boolean isScrolling = false , isLoading = false;
    private int currentItems, totalItems, scrollOutItems, startingIndex=-1, check=0;
    private String hospitalId;
    private HistoryViewModel historyViewModel;
    AppointmentsAdapter adapter;

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
                isLoading = false;
                if (response!=null){
                    list.addAll(response);
                    adapter.getFilter().filter("");
                }
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
                HelperClass.hideProgressbar(progressBar);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        historyViewModel.getStartingIndexOfList().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                startingIndex = integer;
            }
        });

        initRecyclerView();
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

                    if(isScrolling && (currentItems + scrollOutItems == totalItems) && startingIndex!=-1 && !isLoading) {
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
            isLoading = true;
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();
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

    }
}
