package com.scout.hospitalapp.Activities.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.scout.hospitalapp.Activities.HomeActivity;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.AuthViewModel;
import com.scout.hospitalapp.response.HospitalInfoResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginTask";
    @BindView(R.id.textInputDepartmentName) TextInputLayout textInputEmail;
    @BindView(R.id.textForgotPassword) TextView textForgotPassword;
    @BindView(R.id.textInputPassword) TextInputLayout textInputPassword;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnRegistration) Button btnRegistration;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.google_signIn) SignInButton googleSignIn;
    @BindView(R.id.tashieLoader) TashieLoader tashieLoader;

    AuthViewModel authViewModel;
    Unbinder unbinder;
    String loggedInEmail;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    private static final int GOOGLE_SIGN_IN = 9001;

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
        googleSignIn.setOnClickListener(this);
    }

    private void initGoogleSignIn() {
        // [START config_signIn]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signIn]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tashieLoader.setVisibility(View.GONE);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                HelperClass.toast(this,"Google sign in Failed");
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        HelperClass.showProgressbar(progressBar);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(mAuth.getCurrentUser().isEmailVerified()){
                                // Person is Signing with google hence its email is obviously verified no need to check.
                                String email = mAuth.getCurrentUser().getEmail();
                                saveHospitalInfo(email);
                            }
                        }else{
                            HelperClass.hideProgressbar(progressBar);
                            HelperClass.toast(LoginActivity.this,task.getException().getMessage());
                            mAuth.signOut();
                        }
                    }
                });
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

                loggedInEmail = email;
                authViewModel.loginUser(email,password);
                HelperClass.showProgressbar(progressBar);
                checkForResponse();
                break;
            case R.id.google_signIn :
                textInputPassword.setError(null);
                textInputEmail.setError(null);
                tashieLoader.setVisibility(View.VISIBLE);
                initGoogleSignIn();
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
                if (s!=null && s.equals(getString(R.string.correct))){
                    saveHospitalInfo(loggedInEmail);
                }
            }
        });
    }

    private void saveHospitalInfo(String email) {
        HelperClass.showProgressbar(progressBar);
        authViewModel.getHospitalInfoResponse(email,null).observe(this, new Observer<HospitalInfoResponse>() {
            @Override
            public void onChanged(HospitalInfoResponse hospitalInfoResponse) {
                HelperClass.hideProgressbar(progressBar);

                if (hospitalInfoResponse!=null) {
                    SharedPref.saveLoginUserData(LoginActivity.this, hospitalInfoResponse);
                    openHomeActivity();
                    generateFcmToken(hospitalInfoResponse.getEmail());
                    Log.d("Saved",hospitalInfoResponse.getEmail()+"\n"+hospitalInfoResponse.getName());
                }else {
                    HelperClass.toast(LoginActivity.this,"User Data Not Found");
                    signOut();
                    Log.d("Saved","null"+"");
                }
            }
        });
    }

    public void signOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out
        mGoogleSignInClient.signOut();
    }

    private void generateFcmToken(String email) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Task : ", "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d("InstanceId(Token) : ",token);
                        authViewModel.saveFcmToken(email,token);
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
