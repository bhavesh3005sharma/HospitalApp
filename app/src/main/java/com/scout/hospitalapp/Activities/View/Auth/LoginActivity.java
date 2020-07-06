package com.scout.hospitalapp.Activities.View.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.scout.hospitalapp.Activities.View.HomeActivity;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.AuthViewModel;
import com.scout.hospitalapp.response.HospitalInfoResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.textInputDepartmentName) TextInputLayout textInputEmail;
    @BindView(R.id.textForgotPassword) TextView textForgotPassword;
    @BindView(R.id.textInputPassword) TextInputLayout textInputPassword;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnRegistration) Button btnRegistration;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    AuthViewModel authViewModel;
    Unbinder unbinder;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel.class);

        setClickListener();
    }

    private void setClickListener() {
        btnLogin.setOnClickListener(this);
        btnRegistration.setOnClickListener(this);
        textForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin :
                String email = textInputEmail.getEditText().getText().toString();
                String password = textInputPassword.getEditText().getText().toString();

                if (email.isEmpty()){
                    textInputEmail.setError("Email is Required");
                    textInputEmail.requestFocus();
                    return;
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    textInputEmail.setError("Please Enter The Valid Email.");
                    textInputEmail.requestFocus();
                    return;
                }else textInputEmail.setError(null);

                if (password.isEmpty() || password.length() < 6) {
                    textInputPassword.setError("At least 6 Character Password is Required.");
                    textInputPassword.requestFocus();
                    return;
                }else textInputPassword.setError(null);

                authViewModel.loginUser(email,password);
                HelperClass.showProgressbar(progressBar);
                checkForResponse();
                break;
            case R.id.btnRegistration :
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
            case R.id.textForgotPassword :
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void checkForResponse() {
        authViewModel.getRegisterResponse().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                HelperClass.hideProgressbar(progressBar);
                if (s!=null){
                    if (s.equals(getString(R.string.correct))) {
                        saveHospitalInfo();
                        openHomeActivity();
                    }
                    HelperClass.toast(LoginActivity.this,s);
                }
            }
        });
    }

    private void saveHospitalInfo() {
        authViewModel.getHospitalInfoResponse().observe(this, new Observer<HospitalInfoResponse>() {
            @Override
            public void onChanged(HospitalInfoResponse hospitalInfoResponse) {
                SharedPref.saveLoginUserData(LoginActivity.this,hospitalInfoResponse);
            }
        });
    }

    private void openHomeActivity() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        authViewModel.isUserLoggedIn().observe( this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean==true)
                    openHomeActivity();
            }
        });
    }
}
