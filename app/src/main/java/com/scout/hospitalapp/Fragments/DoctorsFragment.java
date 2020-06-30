package com.scout.hospitalapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scout.hospitalapp.Adapter.DoctorAdapter;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.DoctorsViewModel;
import com.scout.hospitalapp.R;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DoctorsFragment extends Fragment implements DoctorAdapter.clickListener {

    @BindView(R.id.doctorRecyclerView)
    RecyclerView doctorRecyclerView;
    @BindView(R.id.progressBarDoctorsFrag)
    ProgressBar progressBar;

    Unbinder unbinder;
    String hospitalId;
    ArrayList<ModelDoctorInfo> list = new ArrayList<>();
    private DoctorsViewModel doctorsViewModel;
    DoctorAdapter doctorAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        doctorsViewModel = ViewModelProviders.of(this).get(DoctorsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_doctors, container, false);
        unbinder = ButterKnife.bind(this, root);

        initRecyclerView();
        HelperClass.showProgressbar(progressBar);
        hospitalId = doctorsViewModel.getHospitalId(getContext());
        doctorsViewModel.getDoctors(hospitalId).observe(getViewLifecycleOwner(), new Observer<ArrayList<ModelDoctorInfo>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ModelDoctorInfo> data) {
                list.addAll(data);
                doctorAdapter.notifyDataSetChanged();
                HelperClass.hideProgressbar(progressBar);
            }
        });
        return root;
    }

    private void initRecyclerView() {
        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        doctorRecyclerView.hasFixedSize();
        doctorAdapter = new DoctorAdapter(list, getContext());
        doctorRecyclerView.setAdapter(doctorAdapter);
        doctorAdapter.setOnClickListener(DoctorsFragment.this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void removeDoctor(int position) {

    }
}
