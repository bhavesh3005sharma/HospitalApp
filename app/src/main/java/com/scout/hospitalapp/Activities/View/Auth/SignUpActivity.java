package com.scout.hospitalapp.Activities.View.Auth;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.scout.hospitalapp.Adapter.ArrayOfStringAdapter;
import com.scout.hospitalapp.Adapter.DoctorAdapter;
import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelHospitalRegisterRequest;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.Utils.MultiSelectionSpinner;
import com.scout.hospitalapp.ViewModels.AuthViewModel;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, ArrayOfStringAdapter.clickListener,DoctorAdapter.clickListener {
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
    @BindView(R.id.addSingleDepartmentButton) Button addSingleDepartmentButton;
    @BindView(R.id.departmentRecyclerView) RecyclerView departmentRecyclerView;
    @BindView(R.id.doctorRecyclerView) RecyclerView doctorRecyclerView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.btnLogin) Button btnLogin;

    AuthViewModel signUpActivityViewModel;
    private ArrayList<String> listDepartmentName = new ArrayList<>();
    private ArrayList<ModelDepartment> listDepartment = new ArrayList<>();
    private ArrayList<String> listTimes = new ArrayList<>();
    ArrayList<ModelDoctorInfo> listDoctors= new ArrayList<>();
    ArrayOfStringAdapter arrayOfStringAdapter;
    DoctorAdapter doctorAdapter;
    Unbinder unbinder;
    private ArrayOfStringAdapter datesAdapter;
    private ArrayOfStringAdapter timeAdapter;
    private AlertDialog alertDialogNumberPicker;
    private ArrayList<String> selectedDates = new ArrayList<>();

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
        arrayOfStringAdapter = new ArrayOfStringAdapter(listDepartmentName,this);
        departmentRecyclerView.setAdapter(arrayOfStringAdapter);
        arrayOfStringAdapter.setOnClickListener(this);

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
                    ModelHospitalRegisterRequest registerRequest = new ModelHospitalRegisterRequest(name,email,password,phoneNo,address,yearOfEstablishment, listDepartment,listDoctors);
                    signUpActivityViewModel.registerHospital(registerRequest);
                    HelperClass.showProgressbar(progressBar);
                    checkForResponse();
                }else
                    HelperClass.toast(this,msg);
                break;
            case R.id.addSingleDepartmentButton :
                openAddDepartmentAlertDialogue();
                 break;
            case R.id.addDoctorButton :
                listTimes.clear();
                selectedDates.clear();
                openAddDoctorAlertDialogue();
                break;
            case R.id.btnLogin :
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                break;
        }
    }

    private void openAddDepartmentAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder( SignUpActivity.this );
        View view = LayoutInflater.from(this).inflate(R.layout.layout_input_department_dialogue, null, false);
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
                listDepartmentName.add(departmentName);
                listDepartment.add(department);
                arrayOfStringAdapter.notifyDataSetChanged();

                textNoOfDepartment.setVisibility(View.VISIBLE);
                textNoOfDepartment.setText(String.valueOf(listDepartmentName.size()));
                alertDialog.dismiss();
            }
        });
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

    private void openAddDoctorAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder( SignUpActivity.this );
        View view = LayoutInflater.from(this).inflate(R.layout.dialogue_register_doctor, null, false);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        final TextInputLayout name = view.findViewById(R.id.textInputName);
        final TextInputLayout email = view.findViewById(R.id.textInputEmailRegister);
        final TextInputLayout phoneNo = view.findViewById(R.id.textInputPhoneNo);
        final TextInputLayout address = view.findViewById(R.id.textInputAddress);
        final TextInputLayout careerHistory = view.findViewById(R.id.careerHistory);
        final TextInputLayout learningHistory = view.findViewById(R.id.learningHistory);
        final TextInputLayout avgCheckupTime = view.findViewById(R.id.timeToCheck);
        final ChipGroup chipGroup = view.findViewById(R.id.choice_chip_group);
        RecyclerView timeRecyclerView = view.findViewById(R.id.availabilityTimeRecyclerView);
        Button startTimeButton = view.findViewById(R.id.startTimeButton);
        Button endTimeButton = view.findViewById(R.id.endTimeButton);
        Button addTimeButton = view.findViewById(R.id.addTimeButton);
        Button cancel = view.findViewById(R.id.cancel);
        Button add = view.findViewById(R.id.add);
        final String[] availabilityType = new String[1];
        MultiSelectionSpinner spinnerToSelectWeekDays = view.findViewById(R.id.multiSelectSpinner);
        spinnerToSelectWeekDays.setItems(getResources().getStringArray(R.array.weekDays));

        final Spinner spinner = view.findViewById(R.id.department);
        ArrayList<String> listForSpinner = new ArrayList<>();
        listForSpinner.add(getString(R.string.select_department));
        listForSpinner.addAll(listDepartmentName);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listForSpinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SignUpActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTimeButton.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SignUpActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTimeButton.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        addTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTimeButton.getText().equals(getString(R.string.start_time)) || endTimeButton.getText().equals(getString(R.string.end_time))) {
                    HelperClass.toast(SignUpActivity.this, "Please Select Time");
                    return;
                }
                listTimes.add(startTimeButton.getText()+" - "+endTimeButton.getText());
                timeAdapter.notifyDataSetChanged();
                startTimeButton.setText(getString(R.string.start_time));
                endTimeButton.setText(getString(R.string.end_time));
            }
        });

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId==-1) {
                    availabilityType[0] = null;
                    spinnerToSelectWeekDays.setVisibility(View.GONE);
                }
                if (checkedId==R.id.choice_chip_daily){
                    availabilityType[0] = getString(R.string.daily);
                    spinnerToSelectWeekDays.setVisibility(View.GONE);
                }
                if (checkedId==R.id.choice_chip_weekly){
                    availabilityType[0] = getString(R.string.weekly);
                    spinnerToSelectWeekDays.setVisibility(View.VISIBLE);
                    HelperClass.toast(SignUpActivity.this,"Please Select Weekdays");
                }
                if (checkedId==R.id.choice_chip_monthly){
                    availabilityType[0] = getString(R.string.monthly);
                    openDateSelectorDialogue();
                    spinnerToSelectWeekDays.setVisibility(View.GONE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> doctorsAvailability = new ArrayList<>();
                if (availabilityType[0]!=null && availabilityType[0].equals(getString(R.string.weekly)))
                    doctorsAvailability.addAll(spinnerToSelectWeekDays.getSelectedStrings());
                if (availabilityType[0]!=null && availabilityType[0].equals(getString(R.string.monthly)))
                    doctorsAvailability.addAll(selectedDates);

                ModelDoctorInfo doctorInfo = new ModelDoctorInfo(name.getEditText().getText().toString().trim(),
                        email.getEditText().getText().toString().trim(),phoneNo.getEditText().getText().toString().trim(),
                        address.getEditText().getText().toString().trim(),spinner.getSelectedItem().toString().trim(),
                        careerHistory.getEditText().getText().toString().trim(),learningHistory.getEditText().getText().toString().trim()
                        ,avgCheckupTime.getEditText().getText().toString().trim(),availabilityType[0],doctorsAvailability,listTimes);

                String msg = signUpActivityViewModel.validateDataOfDoctor(doctorInfo);
                if (msg.equals(getString(R.string.correct))){
                    listDoctors.add(doctorInfo);
                    doctorAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }else
                    HelperClass.toast(SignUpActivity.this,msg);
            }
        });

        timeRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        timeRecyclerView.hasFixedSize();
        timeAdapter = new ArrayOfStringAdapter(listTimes, this);
        timeRecyclerView.setAdapter(timeAdapter);
    }

    private void openDateSelectorDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        View view = LayoutInflater.from(this).inflate(R.layout.layout_number_picker_dialogue, null, false);
        builder.setView(view);

        alertDialogNumberPicker = builder.create();
        alertDialogNumberPicker.show();
        alertDialogNumberPicker.setCancelable(false);

        NumberPicker numberPicker = view.findViewById(R.id.dateNumberPicker);
        RecyclerView recyclerViewSelectedDates = view.findViewById(R.id.recyclerViewSelectedDates);
        Button cancelNumberPickerDialogue = view.findViewById(R.id.cancel);
        Button selectNumber = view.findViewById(R.id.select);
        ImageButton dateSelectionComplete = view.findViewById(R.id.confimSelection);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(31);

        cancelNumberPickerDialogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogNumberPicker.dismiss();
            }
        });

        dateSelectionComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogNumberPicker.dismiss();
            }
        });

        selectNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDates.add(String.valueOf(numberPicker.getValue()));
                datesAdapter.notifyDataSetChanged();
                Log.d("ListS",selectedDates.toString());
            }
        });

        recyclerViewSelectedDates.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerViewSelectedDates.hasFixedSize();
        datesAdapter = new ArrayOfStringAdapter(selectedDates, this);
        recyclerViewSelectedDates.setAdapter(datesAdapter);
    }

    @Override
    public void removeItem(int position) {
        if (listDepartmentName.size()==0)
            textNoOfDepartment.setVisibility(View.GONE);
        else {
            textNoOfDepartment.setVisibility(View.VISIBLE);
            textNoOfDepartment.setText(""+ listDepartmentName.size());
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
