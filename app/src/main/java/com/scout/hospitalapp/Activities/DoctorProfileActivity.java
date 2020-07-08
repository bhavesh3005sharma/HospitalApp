package com.scout.hospitalapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scout.hospitalapp.Adapter.ArrayOfStringAdapter;
import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.Models.ModelRequestId;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.Utils.MultiSelectionSpinner;
import com.scout.hospitalapp.ViewModels.DoctorProfileViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DoctorProfileActivity extends AppCompatActivity {
    @BindView(R.id.profileImage) ImageView profileImg;
    @BindView(R.id.textName) TextView textName;
    @BindView(R.id.textSpecialisation) TextView textSpecialisation;
    @BindView(R.id.textDoctorAvailability) TextView textDoctorAvailability;
    @BindView(R.id.textDoctorAvailabilityTime) TextView textDoctorAvailabilityTime;
    @BindView(R.id.textCareerHistory) TextView textCareerHistory;
    @BindView(R.id.textLearningHistory) TextView textLearningHistory;
    @BindView(R.id.textEmail) TextView textEmail;
    @BindView(R.id.textPhoneNo) TextView textPhoneNo;
    @BindView(R.id.textAddress) TextView textAddress;
    @BindView(R.id.profileLayout) ConstraintLayout profileLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.buttonEditProfile) Button buttonEditProfile;
    ProgressBar progressBarDialogue;

    Unbinder unbinder;
    ModelDoctorInfo doctorInfo;
    DoctorProfileViewModel doctorProfileViewModel;
    private ArrayOfStringAdapter datesAdapter;
    private ArrayOfStringAdapter timeAdapter;
    private AlertDialog alertDialogNumberPicker;
    private ArrayList<String> selectedDates = new ArrayList<>();
    private ArrayList<String> listDepartments = new ArrayList<>();
    private ArrayList<String> listTimes = new ArrayList<>();
    private ModelRequestId hospitalId;
    private AlertDialog dialogueDoctorEdit;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcotor_profile);
        unbinder = ButterKnife.bind(this);
        doctorProfileViewModel = ViewModelProviders.of(this).get(DoctorProfileViewModel.class);

        doctorInfo = (ModelDoctorInfo) getIntent().getSerializableExtra("ProfileModel");
        doctorProfileViewModel.getDepartmentsList(doctorInfo.getHospitalObjectId().getId()).observe(DoctorProfileActivity.this, new Observer<ArrayList<ModelDepartment>>() {
            @Override
            public void onChanged(ArrayList<ModelDepartment> modelDepartmentArrayList) {
                listDepartments.clear();
                ArrayList<String> departNames = new ArrayList<>();
                for (ModelDepartment department : modelDepartmentArrayList)
                    departNames.add(department.getDepartmentName());
                listDepartments.addAll(departNames);
            }
        });
        setToolbar(doctorInfo.getName());
        setProfileData(doctorInfo);
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfileDialogue();
            }
        });
    }

    private void openEditProfileDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder( DoctorProfileActivity.this );
        View view = LayoutInflater.from(DoctorProfileActivity.this).inflate(R.layout.dialogue_register_doctor, null, false);
        builder.setView(view);

        dialogueDoctorEdit = builder.create();
        dialogueDoctorEdit.show();
        dialogueDoctorEdit.setCancelable(false);

        final TextView title = view.findViewById(R.id.textViewTitle);
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

        title.setText(getString(R.string.title_update_doctor));
        Objects.requireNonNull(name.getEditText()).setText(doctorInfo.getName());
        Objects.requireNonNull(email.getEditText()).setText(doctorInfo.getEmail());
        Objects.requireNonNull(phoneNo.getEditText()).setText(doctorInfo.getPhone_no());
        Objects.requireNonNull(address.getEditText()).setText(doctorInfo.getAddress());
        Objects.requireNonNull(careerHistory.getEditText()).setText(doctorInfo.getCareerHistory());
        Objects.requireNonNull(learningHistory.getEditText()).setText(doctorInfo.getLearningHistory());
        Objects.requireNonNull(avgCheckupTime.getEditText()).setText(doctorInfo.getAvgCheckupTime());
        add.setText(getString(R.string.update));
        listTimes.clear();
        listTimes.addAll(doctorInfo.getDoctorAvailabilityTime());
        if (doctorInfo.getAvailabilityType().equals(getString(R.string.monthly))) {
            selectedDates.clear();
            selectedDates.addAll(doctorInfo.getDoctorAvailability());
        }if (doctorInfo.getAvailabilityType().equals(getString(R.string.weekly))) {
            spinnerToSelectWeekDays.setSelection(doctorInfo.getDoctorAvailability());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DoctorProfileActivity.this,android.R.layout.simple_spinner_item, listForSpinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DoctorProfileActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                mTimePicker = new TimePickerDialog(DoctorProfileActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                    HelperClass.toast(DoctorProfileActivity.this, "Please Select Time");
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
                    HelperClass.toast(DoctorProfileActivity.this,"Please Select Weekdays");
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
                dialogueDoctorEdit.dismiss();
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

                ModelDoctorInfo updateRequestModel = new ModelDoctorInfo(name.getEditText().getText().toString().trim(),
                        email.getEditText().getText().toString().trim(),phoneNo.getEditText().getText().toString().trim(),
                        address.getEditText().getText().toString().trim(),spinner.getSelectedItem().toString().trim(),
                        careerHistory.getEditText().getText().toString().trim(),learningHistory.getEditText().getText().toString().trim()
                        ,avgCheckupTime.getEditText().getText().toString().trim(),availabilityType[0],doctorsAvailability,listTimes,doctorInfo.getDoctorId().getId());

                String msg = doctorProfileViewModel.validateDataOfDoctor(updateRequestModel);
                if (msg.equals(getString(R.string.correct))){
                    HelperClass.showProgressbar(progressBarDialogue);
                    doctorProfileViewModel.updateDoctor(updateRequestModel).observe(DoctorProfileActivity.this, new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            dialogueDoctorEdit.dismiss();
                            HelperClass.hideProgressbar(progressBarDialogue);
                            HelperClass.toast(DoctorProfileActivity.this,s);
                            if (s.equals("Doctor Updated Successfully")) {
                                setProfileData(updateRequestModel);
                                setToolbar(updateRequestModel.getName());
                            }
                        }
                    });
                }else
                    HelperClass.toast(DoctorProfileActivity.this,msg);
            }
        });

        timeRecyclerView.setLayoutManager(new GridLayoutManager(DoctorProfileActivity.this, 1));
        timeRecyclerView.hasFixedSize();
        timeAdapter = new ArrayOfStringAdapter(listTimes, DoctorProfileActivity.this);
        timeRecyclerView.setAdapter(timeAdapter);
    }

    private void setToolbar(String title) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setProfileData(ModelDoctorInfo doctorInfo) {
        textName.setText(doctorInfo.getName());
        textSpecialisation.setText(doctorInfo.getDepartment());
        textCareerHistory.setText(doctorInfo.getCareerHistory());
        textLearningHistory.setText(doctorInfo.getLearningHistory());
        textEmail.setText(doctorInfo.getEmail());
        textPhoneNo.setText(doctorInfo.getPhone_no());
        textAddress.setText(doctorInfo.getAddress());

        textDoctorAvailability.setText(doctorProfileViewModel.getAvailabilityType(doctorInfo.getAvailabilityType(),doctorInfo.getDoctorAvailability(),this));
        textDoctorAvailabilityTime.setText(doctorProfileViewModel.getAvailabilityTime(doctorInfo.getAvgCheckupTime(),doctorInfo.getDoctorAvailabilityTime(),this));

        HelperClass.hideProgressbar(progressBar);
        profileLayout.setVisibility(View.VISIBLE);
    }

    private void openDateSelectorDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder( DoctorProfileActivity.this );
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
            }
        });

        recyclerViewSelectedDates.setLayoutManager(new GridLayoutManager(DoctorProfileActivity.this, 1));
        recyclerViewSelectedDates.hasFixedSize();
        datesAdapter = new ArrayOfStringAdapter(selectedDates, DoctorProfileActivity.this);
        recyclerViewSelectedDates.setAdapter(datesAdapter);
    }
}
