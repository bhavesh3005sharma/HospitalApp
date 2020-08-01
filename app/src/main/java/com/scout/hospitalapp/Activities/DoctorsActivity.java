package com.scout.hospitalapp.Activities;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.scout.hospitalapp.Adapter.ArrayOfStringAdapter;
import com.scout.hospitalapp.Adapter.DoctorAdapter;
import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.Utils.MultiSelectionSpinner;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.DoctorsViewModel;
import com.scout.hospitalapp.R;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DoctorsActivity extends AppCompatActivity implements DoctorAdapter.clickListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.doctorRecyclerView) RecyclerView doctorRecyclerView;
    @BindView(R.id.progressBarDoctorsFrag) ProgressBar progressBar;ProgressBar progressBarDialogue;
    @BindView(R.id.fab_add_doctor) FloatingActionButton addDoctorFab;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private ArrayOfStringAdapter datesAdapter;
    private ArrayOfStringAdapter timeAdapter;
    private AlertDialog alertDialogNumberPicker;
    private ArrayList<String> selectedDates = new ArrayList<>();
    private Unbinder unbinder;
    private ArrayList<String> listDepartments = new ArrayList<>();
    private ArrayList<String> listTimes = new ArrayList<>();
    private ArrayList<ModelDoctorInfo> list = new ArrayList<>();
    private DoctorsViewModel doctorsViewModel;
    private DoctorAdapter doctorAdapter;
    private ModelRequestId hospitalId;
    private AlertDialog dialogueDoctorRegister;
    private ModelDoctorInfo doctorInfo;
    private String hospitalName;
    private Boolean isLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorsViewModel = ViewModelProviders.of(this).get(DoctorsViewModel.class);
        setContentView(R.layout.fragment_doctors);
        unbinder = ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        initRecyclerView();
        hospitalId = doctorsViewModel.getHospitalId(DoctorsActivity.this);
        hospitalName = doctorsViewModel.getHospitalName(DoctorsActivity.this);

        isLoading = true;
        doctorsViewModel.getDoctors(hospitalId.getId()).observe(DoctorsActivity.this, new Observer<ArrayList<ModelDoctorInfo>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ModelDoctorInfo> data) {
                isLoading = false;
                if (data!=null) {
                    list.clear();
                    list.addAll(data);
                }
                doctorAdapter.notifyDataSetChanged();
                HelperClass.hideProgressbar(progressBar);
            }
        });

        doctorsViewModel.getDepartmentsList(hospitalId.getId()).observe(DoctorsActivity.this, new Observer<ArrayList<ModelDepartment>>() {
            @Override
            public void onChanged(ArrayList<ModelDepartment> modelDepartmentArrayList) {
                listDepartments.clear();
                ArrayList<String> departNames = new ArrayList<>();
                for (ModelDepartment department : modelDepartmentArrayList)
                    departNames.add(department.getDepartmentName());
                listDepartments.addAll(departNames);
            }
        });

        addDoctorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlertDialogue();
                listTimes.clear();
                selectedDates.clear();
            }
        });
    }

    private void initRecyclerView() {
        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(DoctorsActivity.this, LinearLayoutManager.VERTICAL, false));
        doctorRecyclerView.hasFixedSize();
        doctorAdapter = new DoctorAdapter(list, DoctorsActivity.this);
        doctorRecyclerView.setAdapter(doctorAdapter);
        doctorAdapter.setOnClickListener(DoctorsActivity.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void removeDoctor(int position) {
        openDelConfirmationDialogue(position);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(DoctorsActivity.this, DoctorProfileActivity.class);
        intent.putExtra("ProfileModel",list.get(position));
        startActivity(intent);
    }

    private void openDelConfirmationDialogue(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder( DoctorsActivity.this );
        builder.setTitle(getString(R.string.title_delete_doctor));
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (list.get(position)!=null && list.get(position).getDoctorId()!=null) {
                    HelperClass.showProgressbar(progressBar);
                    doctorsViewModel.deleteDoctor(list.get(position).getDoctorId().getId(), hospitalId.getId()).observe(DoctorsActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            HelperClass.hideProgressbar(progressBar);
                            if (aBoolean) {
                                list.remove(position);
                                doctorAdapter.notifyDataSetChanged();
                                HelperClass.toast(DoctorsActivity.this, "Doctor Removed Successfully.");
                            } else
                                HelperClass.toast(DoctorsActivity.this, "Oops Some error occurred \n Try Again. ");
                        }
                    });
                }
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

    private void openAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder( DoctorsActivity.this );
        View view = LayoutInflater.from(DoctorsActivity.this).inflate(R.layout.dialogue_register_doctor, null, false);
        builder.setView(view);

        dialogueDoctorRegister = builder.create();
        dialogueDoctorRegister.show();
        dialogueDoctorRegister.setCancelable(false);

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
        progressBarDialogue = view.findViewById(R.id.progressBarDoctorRegisterDialogue);
        final String[] availabilityType = new String[1];
        MultiSelectionSpinner spinnerToSelectWeekDays = view.findViewById(R.id.multiSelectSpinner);
        spinnerToSelectWeekDays.setItems(getResources().getStringArray(R.array.weekDays));

        final Spinner spinner = view.findViewById(R.id.department);
        ArrayList<String> listForSpinner = new ArrayList<>();
        listForSpinner.add(getString(R.string.select_department));
        listForSpinner.addAll(listDepartments);
         ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DoctorsActivity.this,android.R.layout.simple_spinner_item, listForSpinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DoctorsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startTimeButton.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Start Time");
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
                mTimePicker = new TimePickerDialog(DoctorsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTimeButton.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select End Time");
                mTimePicker.show();
            }
        });

        addTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTimeButton.getText().equals(getString(R.string.start_time)) || endTimeButton.getText().equals(getString(R.string.end_time))) {
                    HelperClass.toast(DoctorsActivity.this, "Please Select Time");
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
                    HelperClass.toast(DoctorsActivity.this,"Please Select Weekdays");
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
                dialogueDoctorRegister.dismiss();
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

                 doctorInfo = new ModelDoctorInfo(name.getEditText().getText().toString().trim(),
                        email.getEditText().getText().toString().trim(),phoneNo.getEditText().getText().toString().trim(),
                        address.getEditText().getText().toString().trim(),spinner.getSelectedItem().toString().trim(),
                        careerHistory.getEditText().getText().toString().trim(),learningHistory.getEditText().getText().toString().trim()
                        ,avgCheckupTime.getEditText().getText().toString().trim(),availabilityType[0],doctorsAvailability
                         , listTimes,hospitalId.getId(),hospitalName);

                String msg = doctorsViewModel.validateDataOfDoctor(doctorInfo);
                if (msg.equals(getString(R.string.correct))){
                    HelperClass.showProgressbar(progressBarDialogue);
                    doctorsViewModel.registerDoctor(doctorInfo);
                    checkForResponse();
                }else
                    HelperClass.toast(DoctorsActivity.this,msg);
            }
        });

        timeRecyclerView.setLayoutManager(new GridLayoutManager(DoctorsActivity.this, 1));
        timeRecyclerView.hasFixedSize();
        timeAdapter = new ArrayOfStringAdapter(listTimes, DoctorsActivity.this);
        timeRecyclerView.setAdapter(timeAdapter);
    }

    private void checkForResponse() {
        doctorsViewModel.getIsDoctorRegistered().observe(DoctorsActivity.this, new Observer<ModelRequestId>() {
            @Override
            public void onChanged(ModelRequestId registeredDoctorId) {

                HelperClass.toast(DoctorsActivity.this, "Doctor " + doctorInfo.getName() + " added Successfully.");
                HelperClass.hideProgressbar(progressBarDialogue);
                HelperClass.showProgressbar(progressBar);
                dialogueDoctorRegister.dismiss();
            }
        });
    }

    private void openDateSelectorDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder( DoctorsActivity.this );
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

        recyclerViewSelectedDates.setLayoutManager(new GridLayoutManager(DoctorsActivity.this, 1));
        recyclerViewSelectedDates.hasFixedSize();
        datesAdapter = new ArrayOfStringAdapter(selectedDates, DoctorsActivity.this);
        recyclerViewSelectedDates.setAdapter(datesAdapter);
    }

    @Override
    public void onRefresh() {
        if (!isLoading) {
            list.clear();
            isLoading = true;
            HelperClass.showProgressbar(progressBar);
            doctorsViewModel.getDoctors(hospitalId.getId());
            doctorsViewModel.getDepartmentsList(hospitalId.getId());
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
