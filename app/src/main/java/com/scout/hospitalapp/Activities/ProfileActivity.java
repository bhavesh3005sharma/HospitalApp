package com.scout.hospitalapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Credentials;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.TrailingCircularDotsLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.scout.hospitalapp.Activities.Auth.LoginActivity;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.ProfileActivityViewModel;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener ,SwipeRefreshLayout.OnRefreshListener {
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
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    Unbinder unbinder;
    ProfileActivityViewModel profileActivityViewModel;
    HospitalInfoResponse hospitalInfoResponse;
    Boolean isLoading = false;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        setUpToolbar();

        isLoading = true;
        profileActivityViewModel.loadProfileData(this).observe(this, new Observer<HospitalInfoResponse>() {
            @Override
            public void onChanged(HospitalInfoResponse response) {
                isLoading = false;
                hospitalInfoResponse = response;
                updateUi(hospitalInfoResponse);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        buttonDoctors.setOnClickListener(this);
        buttonDepartments.setOnClickListener(this);
        editProfile.setOnClickListener(this);
    }

    private void setUpToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Profile");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_change_password :
                openAuthDialogue();
                break;
            case R.id.menu_signout :
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                SharedPref.deleteLoginUserData(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAuthDialogue() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( ProfileActivity.this );
        View view = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.layout_password_authenticate, null, false);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        final Boolean[] isAuthenticatedUser = {false};
        FirebaseUser currentUser = mAuth.getCurrentUser();


        TextInputLayout textInputPassword = view.findViewById(R.id.textInputPassword);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        Button buttonAuthenticate = view.findViewById(R.id.buttonAuthenticate);
        TrailingCircularDotsLoader trailingCircularDotsLoader = view.findViewById(R.id.trailingCircularDotsLoader);

        if (isAuthenticatedUser[0]){
            textViewTitle.setText(getString(R.string.text_enter_password_again));
            buttonAuthenticate.setText(getString(R.string.authenticate));
            textInputPassword.setHint(getString(R.string.loginPassword));
        }else {
            textViewTitle.setText(getString(R.string.text_enter_password_again));
            buttonAuthenticate.setText(getString(R.string.authenticate));
            textInputPassword.setHint(getString(R.string.loginPassword));
        }

        buttonAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = textInputPassword.getEditText().getText().toString().trim();
                if (password.isEmpty()){
                    textInputPassword.setError("Enter Your Password");
                    textInputPassword.requestFocus();
                    return;
                }else if(password.length()<6){
                    textInputPassword.setError("Minimum 6 Char Password Required.");
                    textInputPassword.requestFocus();
                    return;
                }else
                    textInputPassword.setError(null);

                trailingCircularDotsLoader.setVisibility(View.VISIBLE);
                view.setAlpha(0.5f);
                buttonAuthenticate.setEnabled(false);

                if (isAuthenticatedUser[0]){
                    currentUser.updatePassword(password).addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            view.setAlpha(1.0f);
                            buttonAuthenticate.setEnabled(true);
                            trailingCircularDotsLoader.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                HelperClass.toast(ProfileActivity.this, "Password Updated Successfully");
                                alertDialog.dismiss();
                            }else {
                                HelperClass.toast(ProfileActivity.this, task.getException().getMessage());
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
                else {
                    AuthCredential credentials = EmailAuthProvider.getCredential(currentUser.getEmail(),password);
                    currentUser.reauthenticate(credentials).addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            view.setAlpha(1.0f);
                            buttonAuthenticate.setEnabled(true);
                            trailingCircularDotsLoader.setVisibility(View.GONE);
                            if (task.isSuccessful()){
                                HelperClass.toast(ProfileActivity.this, "Authentication Successful\nChange Password");
                                isAuthenticatedUser[0] = true;
                                textViewTitle.setText(getString(R.string.enter_your_new_password));
                                buttonAuthenticate.setText(getString(R.string.confirm));
                                textInputPassword.setHint(getString(R.string.new_password));
                            }else {
                                HelperClass.toast(ProfileActivity.this, task.getException().getMessage());
                                textInputPassword.setError("Invalid Password");
                                textInputPassword.getEditText().setText(null);
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateUi(HospitalInfoResponse hospitalInfoResponse) {
        if (hospitalInfoResponse!=null) {
            textViewHospitalName.setText(hospitalInfoResponse.getName());
            textViewEmail.setText(hospitalInfoResponse.getEmail());
            textViewContactNo.setText(hospitalInfoResponse.getPhone_no());
            textViewAddress.setText(hospitalInfoResponse.getAddress());
            yearEstablishment.setText(getString(R.string.year_establishment)+hospitalInfoResponse.getYear_of_establishment());
            if (hospitalInfoResponse.getUrl()!=null)
                Picasso.get().load(Uri.parse(hospitalInfoResponse.getUrl())).placeholder(R.color.placeholder_bg).into(HospitalImage);
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
                Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("hospitalInfoResponse",hospitalInfoResponse);
                startActivity(intent);
                break;
            case R.id.buttonDoctors :
                startActivity(new Intent(ProfileActivity.this, DoctorsActivity.class));
                break;
            case R.id.buttonDepartments :
                startActivity(new Intent(ProfileActivity.this, DepartmentsActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (!isLoading)
            profileActivityViewModel.loadProfileData(this);
        swipeRefreshLayout.setRefreshing(false);
    }
}
