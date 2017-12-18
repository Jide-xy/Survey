package com.example.babajidemustapha.survey;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Babajide Mustapha on 10/27/2017.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    SharedPreferences loginData;
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("ddd", "Refreshed token: " + refreshedToken);
        loginData = getSharedPreferences("user_data",MODE_PRIVATE);
        SharedPreferences.Editor edit = loginData.edit();
        edit.putString("DEVICE_TOKEN",refreshedToken);
        edit.commit();
        super.onTokenRefresh();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
}
