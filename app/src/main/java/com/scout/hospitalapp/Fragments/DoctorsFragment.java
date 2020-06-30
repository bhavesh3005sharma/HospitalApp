package com.scout.hospitalapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
        setHasOptionsMenu(true);

        initRecyclerView();
        HelperClass.showProgressbar(progressBar);
        hospitalId = doctorsViewModel.getHospitalId(getContext());
        doctorsViewModel.getDoctors(hospitalId).observe(getViewLifecycleOwner(), new Observer<ArrayList<ModelDoctorInfo>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ModelDoctorInfo> data) {
                list.clear();
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_view_menu,menu);
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Here!");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doctorAdapter.getFilter().filter(newText);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void removeDoctor(int position) {

    }
}
