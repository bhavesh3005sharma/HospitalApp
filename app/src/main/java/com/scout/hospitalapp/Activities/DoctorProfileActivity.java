package com.scout.hospitalapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scout.hospitalapp.Adapter.ArrayOfStringAdapter;
import com.scout.hospitalapp.Models.ModelDepartment;
import com.scout.hospitalapp.Models.ModelDoctorInfo;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.Utils.MultiSelectionSpinner;
import com.scout.hospitalapp.ViewModels.DoctorProfileViewModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DoctorProfileActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.profileImage) CircularImageView profileImg;
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

    private String hospitalName;
    Unbinder unbinder;
    ModelDoctorInfo doctorInfo;
    DoctorProfileViewModel doctorProfileViewModel;
    private ArrayOfStringAdapter datesAdapter;
    private ArrayOfStringAdapter timeAdapter;
    private AlertDialog alertDialogNumberPicker;
    private ArrayList<String> selectedDates = new ArrayList<>();
    private ArrayList<String> listDepartments = new ArrayList<>();
    private ArrayList<String> listTimes = new ArrayList<>();
    private AlertDialog dialogueDoctorEdit;
    private CircularImageView profileImageDialogue;
    TextView gallery,camera,cancel;
    AlertDialog alertDialogChoose;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        unbinder = ButterKnife.bind(this);
        doctorProfileViewModel = ViewModelProviders.of(this).get(DoctorProfileViewModel.class);

        doctorInfo = (ModelDoctorInfo) getIntent().getSerializableExtra("ProfileModel");
        hospitalName = doctorInfo.getHospitalName();
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
        profileImageDialogue = view.findViewById(R.id.profileImage);
        profileImageDialogue.setVisibility(View.VISIBLE);
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
        if (profileImageDialogue!=null && doctorInfo.getUrl()!=null)
            Picasso.get().load(Uri.parse(doctorInfo.getUrl())).placeholder(R.drawable.ic_profile).into(profileImageDialogue);
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
                        String hr = ""+selectedHour,min = ""+selectedMinute;
                        if (selectedHour<10)
                            hr = "0"+selectedHour;
                        if (selectedMinute<10)
                            min = "0"+selectedMinute;
                        startTimeButton.setText(hr + ":" + min);
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
                        String hr = ""+selectedHour,min = ""+selectedMinute;
                        if (selectedHour<10)
                            hr = "0"+selectedHour;
                        if (selectedMinute<10)
                            min = "0"+selectedMinute;
                        endTimeButton.setText(hr + ":" + min);
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
                String time = startTimeButton.getText()+" - "+endTimeButton.getText();
                if ((doctorProfileViewModel.getTimeDifference(time))>0) {
                    listTimes.add(time);
                    timeAdapter.notifyDataSetChanged();
                    startTimeButton.setText(getString(R.string.start_time));
                    endTimeButton.setText(getString(R.string.end_time));
                }else
                    HelperClass.toast(DoctorProfileActivity.this,"Please Provide Valid Time");
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

        profileImageDialogue.setOnClickListener(this);

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
                        ,avgCheckupTime.getEditText().getText().toString().trim(),availabilityType[0],doctorsAvailability,
                        listTimes,doctorInfo.getDoctorId().getId(),hospitalName);

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

    private boolean checkForWritePermission() {
        if (ActivityCompat.checkSelfPermission(DoctorProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {   requestPermissions(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                2000);
            //Permission automatically granted for Api<23 on installation
        }
        else
            return true;

        return false;
    }

    private boolean checkForReadPermission() {
        if(ActivityCompat.checkSelfPermission(DoctorProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        { requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                2000);
            //Permission automatically granted for Api<23 on installation
        }
        else
            return true;

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        alertDialogChoose.dismiss();
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1 && data.getData() != null) {
                imageUri = data.getData();
                if (imageUri!=null && profileImageDialogue!=null) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_profile).into(profileImageDialogue);
                    openAlertDialogueToShowPic();
                }
            } else if (requestCode == 2 && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (profileImageDialogue!=null) {
                    imageUri = getImageUri(DoctorProfileActivity.this, imageBitmap);
                    openAlertDialogueToShowPic();
                    profileImageDialogue.setImageBitmap(imageBitmap);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialogChoose!=null)
            alertDialogChoose.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialogChoose!=null)
            alertDialogChoose.dismiss();
        unbinder.unbind();
    }

    private void openAlertDialogueToShowPic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorProfileActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_show_profile_image,null));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ImageView imageView = alertDialog.findViewById(R.id.profile_imageToShow);
        TextView confirm = alertDialog.findViewById(R.id.confirm);
        TextView cancel = alertDialog.findViewById(R.id.cancel);

        Picasso.get().load(imageUri).placeholder(R.drawable.ic_profile).into(imageView);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperClass.showProgressbar(progressBarDialogue);
                doctorProfileViewModel.saveProfilePic(DoctorProfileActivity.this,imageUri,"ProfilePic." + getFileExtension(imageUri),doctorInfo.getDoctorId().getId())
                        .observe(DoctorProfileActivity.this, new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                HelperClass.toast(DoctorProfileActivity.this,s);
                                HelperClass.hideProgressbar(progressBarDialogue);
                            }
                        });
                alertDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void openAlertDialogueChoose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorProfileActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialogue_choose,null));
        alertDialogChoose = builder.create();
        alertDialogChoose.show();

        gallery = alertDialogChoose.findViewById(R.id.gallery);
        camera =  alertDialogChoose.findViewById(R.id.camera);
        cancel = alertDialogChoose.findViewById(R.id.cancel);

        gallery.setOnClickListener(this);
        camera.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = DoctorProfileActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
        if (profileImageDialogue!=null && doctorInfo.getUrl()!=null && !doctorInfo.getUrl().isEmpty())
            Picasso.get().load(Uri.parse(doctorInfo.getUrl())).placeholder(R.drawable.ic_profile).into(profileImg);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profileImage :
                if(checkForReadPermission() && checkForWritePermission())
                    openAlertDialogueChoose();
                break;
            case R.id.gallery:
                alertDialogChoose.dismiss();
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                cameraIntent.setType("image/*");
                startActivityForResult(cameraIntent,1);
                break;
            case R.id.camera:
                alertDialogChoose.dismiss();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(DoctorProfileActivity.this.getPackageManager()) != null)
                    startActivityForResult(takePictureIntent, 2);
                break;
            case R.id.cancel:
                alertDialogChoose.dismiss();
                break;
        }
    }
}
