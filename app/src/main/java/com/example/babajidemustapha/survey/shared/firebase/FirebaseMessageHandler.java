package com.example.babajidemustapha.survey.shared.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader;
import com.example.babajidemustapha.survey.shared.room.entities.Survey;
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseMessageHandler extends FirebaseMessagingService {

    RequestQueue requestQueue;
    SurveyDatabase db;
    Map<String,String> data;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        requestQueue = Volley.newRequestQueue(this);
        db = SurveyDatabase.getInstance(this);
        final String TAG = "TAG";
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
             data = remoteMessage.getData();

            JSONObject object = new JSONObject();
            try {
                // preferences.getInt("USER_ID",0)
                object.put("RESPONSE_ID", data.get("RESPONSE_ID"));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://survhey.azurewebsites.net/survey/get_response",object,new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("ddd",response.toString());
                    try {
                        if(response.getString("STATUS").equalsIgnoreCase("success")){
                            DbOperationHelper.execute(new DbOperationHelper.IDbOperationHelper<Long>() {
                                @Override
                                public Long run() {
                                    try {
                                        long response_id = db.surveyDao().saveOnlineResponseHeaders(
                                                Arrays.asList(new Gson().fromJson(response.getJSONArray("RESPONSES").toString(), ResponseHeader[].class)))[0];
                                        db.surveyDao().saveOnlineResponseDetails(
                                                Arrays.asList(new Gson().fromJson(response.getJSONArray("RESPONSE_DETAILS").toString(), ResponseDetail[].class)));
                                        Survey survey = db.surveyDao().getSurveyWithOnlineId(response.getJSONArray("RESPONSES").getJSONObject(0).getInt("SURVEY_ID"));
                                        return response_id;
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                @Override
                                public void onCompleted(Long response_id) {
                                    buildAndShowNotification(response_id);
                                }
                            });


                        }
                        else {
                          //  Toast.makeText(getContext(),"An error occured",Toast.LENGTH_SHORT).show();
                            Log.e("ddd",response.toString());
                        }
                    } catch (JSONException e) {
                        // Toast.makeText(LoginActivity.this,"An error occured",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            })
            {
                @Override
                public Map<String,String> getHeaders(){
                    Map<String,String> param = new HashMap<>();
                    param.put("Content-Type", "application/json; charset=utf-8");
                    return param;
                }
            };
            requestQueue.add(request);


            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("ddd", "Refreshed token: " + s);
        SharedPreferences loginData = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor edit = loginData.edit();
        edit.putString("DEVICE_TOKEN", s);
        edit.apply();
    }

    private void buildAndShowNotification(long response_id) {
        Intent intent = new Intent(FirebaseMessageHandler.this, ResponseDetail.class);
        intent.putExtra("ID", response_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseMessageHandler.this, 1410,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(FirebaseMessageHandler.this)
                .setSmallIcon(R.drawable.ic_library_books_black_18dp)
                .setContentTitle(data.get("TOPIC"))
                .setContentText(data.get("MESSAGE"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1410, notificationBuilder.build());
    }
}
