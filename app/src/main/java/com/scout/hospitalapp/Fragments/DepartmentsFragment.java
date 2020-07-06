package com.scout.hospitalapp.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.scout.hospitalapp.Activities.View.Auth.SignUpActivity;
import com.scout.hospitalapp.Adapter.DepartmentsAdapter;
import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDepartmentRequest;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.DepartmentsViewModel;
import com.scout.hospitalapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DepartmentsFragment extends Fragment implements DepartmentsAdapter.clickListener{
    @BindView(R.id.departmentRecyclerView) RecyclerView departmentRecyclerView;
    @BindView(R.id.progressBarDepartmentFrag) ProgressBar progressBar;
    @BindView(R.id.fab_add_department) FloatingActionButton addDepartmentFab;

    private ArrayList<ModelDepartment> list = new ArrayList<>();
    private DepartmentsViewModel departmentsViewModel;
    private DepartmentsAdapter departmentsAdapter;
    private Unbinder unbinder;
    private ModelRequestId hospitalId;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        departmentsViewModel = ViewModelProviders.of(this).get(DepartmentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_departments, container, false);
        unbinder = ButterKnife.bind(this,root);

        hospitalId = departmentsViewModel.getHospitalId(getContext());
        HelperClass.showProgressbar(progressBar);
        initRecyclerView();
        departmentsViewModel.getDepartmentsList(hospitalId.getId()).observe(getViewLifecycleOwner(), new Observer<ArrayList<ModelDepartment>>() {
            @Override
            public void onChanged(ArrayList<ModelDepartment> data) {
                if (data!=null){
                    list.clear();
                    list.addAll(data);
                }
                HelperClass.hideProgressbar(progressBar);
                departmentsAdapter.notifyDataSetChanged();
            }
        });

        addDepartmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDepartmentDialogue();
            }
        });
        return root;
    }

    private void openAddDepartmentDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_input_department_dialogue, null, false);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final TextInputLayout textInputDepartmentName = view.findViewById(R.id.textInputDepartmentName);
        final EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        final Button buttonAddDepartment = view.findViewById(R.id.buttonAddDepartment);

        buttonAddDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String departmentName = textInputDepartmentName.getEditText().getText().toString().trim();
                String departmentDescription = editTextDescription.getText().toString().trim();

                if (departmentName.isEmpty()){
                    textInputDepartmentName.setError("Please Enter Name.");
                    textInputDepartmentName.requestFocus();
                    return;
                }else textInputDepartmentName.setError(null);

                if (departmentDescription.isEmpty()){
                    editTextDescription.setError("Please Provide Some Description.");
                    editTextDescription.requestFocus();
                    return;
                }else editTextDescription.setError(null);


                ModelDepartment department = new ModelDepartment(departmentName,departmentDescription);
                HelperClass.showProgressbar(progressBar);
                ModelDepartmentRequest request = new ModelDepartmentRequest(hospitalId.getId(),department);
                departmentsViewModel.addDepartment(request).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        HelperClass.hideProgressbar(progressBar);
                        if (aBoolean){
                            list.add(department);
                            departmentsAdapter.notifyDataSetChanged();
                            HelperClass.toast(getContext(),"Department Added Successfully");
                        }else
                            HelperClass.toast(getContext(),"Oops Some Error Occurred\n Try Again");
                    }
                });
                alertDialog.dismiss();
            }
        });
    }

    private void initRecyclerView() {
        departmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        departmentRecyclerView.hasFixedSize();
        departmentsAdapter = new DepartmentsAdapter(list, getContext());
        departmentRecyclerView.setAdapter(departmentsAdapter);
        departmentsAdapter.setOnClickListener(DepartmentsFragment.this);
    }

    @Override
    public void removeItem(int position) {
        openDelConfirmationDialogue(position);
    }

    private void openDelConfirmationDialogue(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        builder.setTitle(getString(R.string.title_delete_doctor));
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                HelperClass.showProgressbar(progressBar);
                departmentsViewModel.removeDepartment(new ModelDepartmentRequest(hospitalId.getId(),list.get(position))).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        HelperClass.hideProgressbar(progressBar);
                        if (aBoolean) {
                            list.remove(position);
                            departmentsAdapter.notifyDataSetChanged();
                            HelperClass.toast(getContext(), "Department Removed Successfully.");
                        } else
                            HelperClass.toast(getContext(), "Oops Some error occurred \n Try Again. ");
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
