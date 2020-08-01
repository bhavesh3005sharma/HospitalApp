package com.scout.hospitalapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scout.hospitalapp.R;
import com.scout.hospitalapp.ViewModels.ProfileActivityViewModel;
import com.scout.hospitalapp.response.HospitalInfoResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.textViewHospitalName)
    TextView textViewHospitalName;
    @BindView(R.id.year_establishment)
    TextView yearEstablishment;
    @BindView(R.id.address)
    TextView textViewAddress;
    @BindView(R.id.email)
    TextView textViewEmail;
    @BindView(R.id.contactNo)
    TextView textViewContactNo;
    @BindView(R.id.HospitalImage)
    ImageView HospitalImage;
    @BindView(R.id.editProfile)
    ImageView editProfile;
    @BindView(R.id.buttonDoctors)
    Button buttonDoctors;
    @BindView(R.id.buttonDepartments)
    Button buttonDepartments;
    @BindView(R.id.details)
    LinearLayout contactDetails;
    @BindView(R.id.cardViewImage)
    CardView cardViewImage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    Unbinder unbinder;
    ProfileActivityViewModel profileActivityViewModel;
    HospitalInfoResponse hospitalInfoResponse;

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        unbinder = ButterKnife.bind(this);
        profileActivityViewModel = ViewModelProviders.of(this).get(ProfileActivityViewModel.class);

        profileActivityViewModel.loadProfileData(this).observe(this, new Observer<HospitalInfoResponse>() {
            @Override
            public void onChanged(HospitalInfoResponse response) {
                hospitalInfoResponse = response;
                updateUi(hospitalInfoResponse);
            }
        });

        buttonDoctors.setOnClickListener(this);
        buttonDepartments.setOnClickListener(this);
        editProfile.setOnClickListener(this);
    }

    private void updateUi(HospitalInfoResponse hospitalInfoResponse) {
        if (hospitalInfoResponse!=null) {
            textViewHospitalName.setText(hospitalInfoResponse.getName());
            textViewEmail.setText(hospitalInfoResponse.getEmail());
            textViewContactNo.setText(hospitalInfoResponse.getPhone_no());
            textViewAddress.setText(hospitalInfoResponse.getAddress());
            yearEstablishment.setText(getString(R.string.year_establishment)+hospitalInfoResponse.getYear_of_establishment());
            // Load Image Of Hospital.
            cardViewImage.setVisibility(View.VISIBLE);
            contactDetails.setVisibility(View.VISIBLE);
            buttonDepartments.setVisibility(View.VISIBLE);
            buttonDoctors.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editProfile :
                startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class));
                break;
            case R.id.buttonDoctors :
                startActivity(new Intent(ProfileActivity.this, DoctorsActivity.class));
                break;
            case R.id.buttonDepartments :
                startActivity(new Intent(ProfileActivity.this, DepartmentsActivity.class));
                break;
        }
    }
}
