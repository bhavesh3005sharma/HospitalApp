package com.scout.hospitalapp.Activities.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputLayout;
import com.scout.hospitalapp.Adapter.DepartmentsAdapter;
import com.scout.hospitalapp.Adapter.DoctorAdapter;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelHospitalRegisterRequest;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.AuthViewModel;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener,DepartmentsAdapter.clickListener,DoctorAdapter.clickListener {
    @BindView(R.id.textInputName) TextInputLayout textInputName;
    @BindView(R.id.textInputEmailRegister) TextInputLayout textInputEmailRegister;
    @BindView(R.id.textInputPhoneNo) TextInputLayout textInputPhoneNo;
    @BindView(R.id.textInputPassword) TextInputLayout textInputPassword;
    @BindView(R.id.textInputAddress) TextInputLayout textInputAddress;
    @BindView(R.id.textInputEstablishmentYear) TextInputLayout textInputEstablishmentYear;
    @BindView(R.id.textNoOfDepartment) TextView textNoOfDepartment;
    @BindView(R.id.textNoOfDoctor) TextView textNoOfDoctor;
    @BindView(R.id.addDoctorButton) Button addDoctorButton;
    @BindView(R.id.signUp) Button signUp;
    @BindView(R.id.inputDepartmentName) EditText inputDepartmentName;
    @BindView(R.id.addSingleDepartmentButton) Button addSingleDepartmentButton;
    @BindView(R.id.departmentRecyclerView) RecyclerView departmentRecyclerView;
    @BindView(R.id.doctorRecyclerView) RecyclerView doctorRecyclerView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.btnLogin) Button btnLogin;

    AuthViewModel signUpActivityViewModel;
    ArrayList<String> listDepartments = new ArrayList<>();
    ArrayList<ModelDoctorInfo> listDoctors= new ArrayList<>();
    DepartmentsAdapter departmentsAdapter;
    DoctorAdapter doctorAdapter;
    AlertDialog alertDialog;
    Unbinder unbinder;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        unbinder = ButterKnife.bind(this);

        signUpActivityViewModel = ViewModelProviders.of(this).get(AuthViewModel.class);
        setClickListener();
        initRecyclerViews();
    }

    private void initRecyclerViews() {
        departmentRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        departmentRecyclerView.hasFixedSize();
        departmentsAdapter = new DepartmentsAdapter(listDepartments,this);
        departmentRecyclerView.setAdapter(departmentsAdapter);
        departmentsAdapter.setOnClickListener(this);

        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        doctorRecyclerView.hasFixedSize();
        doctorAdapter = new DoctorAdapter(listDoctors,this);
        doctorRecyclerView.setAdapter(doctorAdapter);
        doctorAdapter.setOnClickListener(this);
    }

    private void setClickListener() {
        btnLogin.setOnClickListener(this);
        signUp.setOnClickListener(this);
        addSingleDepartmentButton.setOnClickListener(this);
        addDoctorButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signUp :
                String name = textInputName.getEditText().getText().toString().trim();
                String email = textInputEmailRegister.getEditText().getText().toString().trim();
                String password = textInputPassword.getEditText().getText().toString().trim();
                String phoneNo = textInputPhoneNo.getEditText().getText().toString().trim();
                String address = textInputAddress.getEditText().getText().toString().trim();
                String yearOfEstablishment = textInputEstablishmentYear.getEditText().getText().toString().trim();

                String msg = signUpActivityViewModel.isDetailsValid(name,email,password,phoneNo,address,yearOfEstablishment);
                if (msg.equals(getString(R.string.correct))){
                    ModelHospitalRegisterRequest registerRequest = new ModelHospitalRegisterRequest(name,email,password,phoneNo,address,yearOfEstablishment,listDepartments,listDoctors);
                    signUpActivityViewModel.registerHospital(registerRequest);
                    HelperClass.showProgressbar(progressBar);
                    checkForResponse();
                }else
                    HelperClass.toast(this,msg);
                break;

            case R.id.addSingleDepartmentButton :
                String departmentName = inputDepartmentName.getText().toString().trim();

                if (departmentName.isEmpty()){
                    inputDepartmentName.setError("Please Enter Name.");
                    inputDepartmentName.requestFocus();
                    return;
                }

                listDepartments.add(departmentName);
                departmentsAdapter.notifyDataSetChanged();
                inputDepartmentName.setText(null);

                textNoOfDepartment.setVisibility(View.VISIBLE);
                textNoOfDepartment.setText(String.valueOf(listDepartments.size()));
                 break;
            case R.id.addDoctorButton :
                openAlertDialogue();
                break;
            case R.id.btnLogin :
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                break;
        }
    }

    private void checkForResponse() {
        signUpActivityViewModel.getRegisterResponse().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                HelperClass.hideProgressbar(progressBar);
                if (s!=null){
                    HelperClass.toast(SignUpActivity.this,s);
                }
            }
        });
    }

    private void openAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View view = LayoutInflater.from(this).inflate(R.layout.dialogue_register_doctor, viewGroup, false);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        final TextInputLayout name = view.findViewById(R.id.textInputName);
        final TextInputLayout email = view.findViewById(R.id.textInputEmailRegister);
        final TextInputLayout phoneNo = view.findViewById(R.id.textInputPhoneNo);
        final TextInputLayout address = view.findViewById(R.id.textInputAddress);
        final TextInputLayout careerHistory = view.findViewById(R.id.careerHistory);
        final TextInputLayout learningHistory = view.findViewById(R.id.learningHistory);
        Button cancel = view.findViewById(R.id.cancel);
        Button add = view.findViewById(R.id.add);

        final Spinner spinner = view.findViewById(R.id.department);
        ArrayList<String> listForSpinner = new ArrayList<>();
        listForSpinner.add(getString(R.string.select_department));
        listForSpinner.addAll(listDepartments);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listForSpinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelDoctorInfo doctorInfo = new ModelDoctorInfo(name.getEditText().getText().toString().trim(),
                        email.getEditText().getText().toString().trim(),phoneNo.getEditText().getText().toString().trim(),
                        address.getEditText().getText().toString().trim(),spinner.getSelectedItem().toString().trim(),
                        careerHistory.getEditText().getText().toString().trim(),learningHistory.getEditText().getText().toString().trim());

                String msg = signUpActivityViewModel.validateDataOfDoctor(doctorInfo);
                if (msg.equals(getString(R.string.correct))){
                    listDoctors.add(doctorInfo);
                    doctorAdapter.notifyDataSetChanged();
                    textNoOfDoctor.setText(""+listDoctors.size());
                    textNoOfDoctor.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();
                }else
                    HelperClass.toast(SignUpActivity.this,msg);
            }
        });
    }

    @Override
    public void removeDepartment(int position) {
        listDepartments.remove(position);
        departmentsAdapter.notifyDataSetChanged();
        if (listDepartments.size()==0)
            textNoOfDepartment.setVisibility(View.GONE);
        else {
            textNoOfDepartment.setVisibility(View.VISIBLE);
            textNoOfDepartment.setText(""+listDepartments.size());
        }
    }

    @Override
    public void removeDoctor(int position) {
        listDoctors.remove(position);
        doctorAdapter.notifyDataSetChanged();
        if (listDoctors.size()==0)
            textNoOfDoctor.setVisibility(View.GONE);
        else {
            textNoOfDoctor.setText(""+listDoctors.size());
            textNoOfDoctor.setVisibility(View.VISIBLE);
        }
    }
}
