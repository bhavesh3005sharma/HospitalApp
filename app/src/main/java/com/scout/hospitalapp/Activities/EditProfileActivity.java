package com.scout.hospitalapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Utils.HelperClass;
import com.scout.hospitalapp.ViewModels.ProfileActivityViewModel;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.textViewHospitalName)
    TextView textViewHospitalName;
    @BindView(R.id.year_establishment)
    TextView yearEstablishment;
    @BindView(R.id.address)
    TextView textViewAddress;
    @BindView(R.id.contactNo)
    TextView textViewContactNo;
    @BindView(R.id.HospitalImage)
    ImageView HospitalImage;
    @BindView(R.id.saveChanges)
    Button saveChanges;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    Unbinder unbinder;
    TextView gallery,camera,cancel;
    AlertDialog alertDialog;
    Uri imageUri;
    ProfileActivityViewModel viewModel;
    HospitalInfoResponse hospitalInfoResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        unbinder = ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(ProfileActivityViewModel.class);
        hospitalInfoResponse = (HospitalInfoResponse) getIntent().getSerializableExtra("hospitalInfoResponse");
        setUpUi(null,hospitalInfoResponse);
        setUpToolbar();

        HospitalImage.setOnClickListener(this);
        saveChanges.setOnClickListener(this);
    }

    private void setUpToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setUpUi(String message, HospitalInfoResponse uiData) {
        if (uiData!=null) {
            textViewHospitalName.setText(uiData.getName());
            textViewContactNo.setText(uiData.getPhone_no());
            textViewAddress.setText(uiData.getAddress());
            yearEstablishment.setText(uiData.getYear_of_establishment());
            if (uiData.getUrl()!=null)
                Picasso.get().load(Uri.parse(uiData.getUrl())).placeholder(R.color.placeholder_bg).into(HospitalImage);
        }
        if (message!=null)
            HelperClass.toast(this,message);
        HelperClass.hideProgressbar(progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveChanges:
                String name = textViewHospitalName.getText().toString().trim();
                String year = yearEstablishment.getText().toString().trim();
                String contactNo = textViewContactNo.getText().toString().trim();
                String address = textViewAddress.getText().toString().trim();
                if (name.isEmpty()){
                    textViewHospitalName.setError("Name is Required.");
                    textViewHospitalName.requestFocus();
                    return;
                }else
                    textViewHospitalName.setError(null);

                if (year.isEmpty()){
                    yearEstablishment.setError("Specify Year Of Establishment.");
                    yearEstablishment.requestFocus();
                    return;
                }else
                    yearEstablishment.setError(null);

                if (contactNo.isEmpty()){
                    textViewContactNo.setError("Contact No. is Required.");
                    textViewContactNo.requestFocus();
                    return;
                }else
                    textViewContactNo.setError(null);

                if (address.isEmpty()){
                    textViewAddress.setError("Address is Required.");
                    textViewAddress.requestFocus();
                    return;
                }else
                    textViewAddress.setError(null);

                HelperClass.showProgressbar(progressBar);
                viewModel.updateProfile(new HospitalInfoResponse(name,hospitalInfoResponse.getHospitalId().getId(),hospitalInfoResponse.getEmail(),contactNo,address,year))
                        .observe(this, new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                setUpUi(s,new HospitalInfoResponse(name,hospitalInfoResponse.getEmail(),contactNo,address,year));
                            }
                        });
                break;
            case R.id.HospitalImage :
                if(checkForReadPermission() && checkForWritePermission())
                    openAlertDialogue();
                break;
            case R.id.gallery:
                alertDialog.dismiss();
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                cameraIntent.setType("image/*");
                startActivityForResult(cameraIntent,1);
                break;
            case R.id.camera:
                alertDialog.dismiss();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(EditProfileActivity.this.getPackageManager()) != null)
                    startActivityForResult(takePictureIntent, 2);
                break;
            case R.id.cancel:
                alertDialog.dismiss();
                break;
        }
    }

    private boolean checkForWritePermission() {
        if (ActivityCompat.checkSelfPermission(EditProfileActivity.this,
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
        if(ActivityCompat.checkSelfPermission(EditProfileActivity.this,
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
        alertDialog.dismiss();
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1 && data.getData() != null) {
                imageUri = data.getData();
                Picasso.get().load(imageUri).placeholder(R.drawable.ic_profile).into(HospitalImage);
                openAlertDialogueToShowPic();
            } else if (requestCode == 2 && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Log.d("***imageBitmap", imageBitmap + "");
                imageUri = getImageUri(EditProfileActivity.this, imageBitmap);
                openAlertDialogueToShowPic();
                HospitalImage.setImageBitmap(imageBitmap);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog!=null)
            alertDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog!=null)
            alertDialog.dismiss();
        unbinder.unbind();
    }

    private void openAlertDialogueToShowPic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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
                HelperClass.showProgressbar(progressBar);
                viewModel.saveProfilePic(EditProfileActivity.this,imageUri,"ProfilePic." + getFileExtension(imageUri))
                .observe(EditProfileActivity.this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        HelperClass.toast(EditProfileActivity.this,s);
                        HelperClass.hideProgressbar(progressBar);
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

    private void openAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialogue_choose,null));
        alertDialog = builder.create();
        alertDialog.show();

        gallery = alertDialog.findViewById(R.id.gallery);
        camera =  alertDialog.findViewById(R.id.camera);
        cancel = alertDialog.findViewById(R.id.cancel);

        gallery.setOnClickListener(this);
        camera.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = EditProfileActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
