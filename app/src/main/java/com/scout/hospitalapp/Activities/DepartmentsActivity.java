package com.scout.hospitalapp.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
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

public class DepartmentsActivity extends AppCompatActivity implements DepartmentsAdapter.clickListener , SwipeRefreshLayout.OnRefreshListener{
    @BindView(R.id.departmentRecyclerView) RecyclerView departmentRecyclerView;
    @BindView(R.id.progressBarDepartmentFrag) ProgressBar progressBar;
    @BindView(R.id.fab_add_department) FloatingActionButton addDepartmentFab;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<ModelDepartment> list = new ArrayList<>();
    private DepartmentsViewModel departmentsViewModel;
    private DepartmentsAdapter departmentsAdapter;
    private Unbinder unbinder;
    private ModelRequestId hospitalId;
    private Boolean isLoading = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        departmentsViewModel = ViewModelProviders.of(this).get(DepartmentsViewModel.class);
        setContentView(R.layout.activity_departments);
        unbinder = ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        getSupportActionBar().setTitle("Departments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        hospitalId = departmentsViewModel.getHospitalId(this);
        initRecyclerView();

        isLoading = true;
        departmentsViewModel.getDepartmentsList(hospitalId.getId()).observe(this, new Observer<ArrayList<ModelDepartment>>() {
            @Override
            public void onChanged(ArrayList<ModelDepartment> data) {
                isLoading= false;
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
                openDepartmentDialogue(false,0);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void openDepartmentDialogue(Boolean isEditDialogue,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        View view = LayoutInflater.from(this).inflate(R.layout.layout_input_department_dialogue, null, false);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final TextInputLayout textInputDepartmentName = view.findViewById(R.id.textInputDepartmentName);
        final EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        final Button buttonAddDepartment = view.findViewById(R.id.buttonAddDepartment);

        if (isEditDialogue){
            textInputDepartmentName.getEditText().setText(list.get(position).getDepartmentName());
            editTextDescription.setText(list.get(position).getDescription());
            buttonAddDepartment.setText(getString(R.string.update));
        }

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

                if (isEditDialogue) {
                    departmentsViewModel.updateDepartment(request).observe(DepartmentsActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            HelperClass.hideProgressbar(progressBar);
                            if (aBoolean) {
                                list.remove(position);
                                list.add(department);
                                departmentsAdapter.notifyDataSetChanged();
                                HelperClass.toast(DepartmentsActivity.this,"Update Successful");
                            } else
                                HelperClass.toast(DepartmentsActivity.this, "Oops Some Error Occurred\n Try Again");
                        }
                    });
                }
                else {
                    departmentsViewModel.addDepartment(request).observe(DepartmentsActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            HelperClass.hideProgressbar(progressBar);
                            if (aBoolean) {
                                list.add(department);
                                departmentsAdapter.notifyDataSetChanged();
                                HelperClass.toast(DepartmentsActivity.this, "Department Added Successfully");
                            } else
                                HelperClass.toast(DepartmentsActivity.this, "Oops Some Error Occurred\n Try Again");
                        }
                    });
                }
                alertDialog.dismiss();
            }
        });
    }

    private void initRecyclerView() {
        departmentRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        departmentRecyclerView.hasFixedSize();
        departmentsAdapter = new DepartmentsAdapter(list, this);
        departmentRecyclerView.setAdapter(departmentsAdapter);
        departmentsAdapter.setOnClickListener(DepartmentsActivity.this);
    }

    @Override
    public void removeItem(int position) {
        openDelConfirmationDialogue(position);
    }

    @Override
    public void onItemClick(int position) {
        openDepartmentDialogue(true,position);
    }

    private void openDelConfirmationDialogue(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle(getString(R.string.title_delete_doctor));
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                HelperClass.showProgressbar(progressBar);
                departmentsViewModel.removeDepartment(new ModelDepartmentRequest(hospitalId.getId(),list.get(position))).observe(DepartmentsActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        HelperClass.hideProgressbar(progressBar);
                        if (aBoolean) {
                            list.remove(position);
                            departmentsAdapter.notifyDataSetChanged();
                            HelperClass.toast(DepartmentsActivity.this, "Department Removed Successfully.");
                        } else
                            HelperClass.toast(DepartmentsActivity.this, "Oops Some error occurred \n Try Again. ");
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

    @Override
    public void onRefresh() {
        if (!isLoading) {
            list.clear();
            isLoading = true;
            HelperClass.showProgressbar(progressBar);
            departmentsViewModel.getDepartmentsList(hospitalId.getId()).observe(this, new Observer<ArrayList<ModelDepartment>>() {
                @Override
                public void onChanged(ArrayList<ModelDepartment> data) {
                    isLoading= false;
                    if (data!=null){
                        list.clear();
                        list.addAll(data);
                    }
                    HelperClass.hideProgressbar(progressBar);
                    departmentsAdapter.notifyDataSetChanged();
                }
            });
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
