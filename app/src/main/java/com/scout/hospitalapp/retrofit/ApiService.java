package com.scout.hospitalapp.retrofit;

import com.scout.hospitalapp.Utils.HelperClass;

public class ApiService {
    private ApiService() {}

    public static RetrofitNetworkApi getAPIService() {
        return RetrofitClient.getClient(HelperClass.BASE_URL).create(RetrofitNetworkApi.class);
    }
}
