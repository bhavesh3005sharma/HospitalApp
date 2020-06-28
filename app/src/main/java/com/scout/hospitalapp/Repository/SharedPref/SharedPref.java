package com.scout.hospitalapp.Repository.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.response.HospitalInfoResponse;
import java.lang.reflect.Type;

public class SharedPref {

    public static void saveLoginUserData(Context context, HospitalInfoResponse hospitalInfo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.pref_for_user_data),context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(hospitalInfo);
        editor.putString("hospitalInfo", json);
        editor.apply();
    }

    public static HospitalInfoResponse getLoginUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.pref_for_user_data),context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("hospitalInfo", null);
        Type type = new TypeToken<HospitalInfoResponse>() {}.getType();
        HospitalInfoResponse hospitalInfo = gson.fromJson(json, type);
        return  hospitalInfo;
    }

    public static void deleteLoginUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.pref_for_user_data),context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
