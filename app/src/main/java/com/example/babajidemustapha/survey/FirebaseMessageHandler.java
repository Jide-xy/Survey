package com.example.babajidemustapha.survey;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMessageHandler extends FirebaseMessagingService {

    RequestQueue requestQueue;
    SurveyDatabase db;
    Map<String,String> data;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        requestQueue = Volley.newRequestQueue(this);
        db = new SurveyDatabase(this);
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
                            long response_id = db.saveResponse(response.getJSONArray("RESPONSES"));
                            db.saveResponseDetail(response.getJSONArray("RESPONSE_DETAILS"));
                            Intent intent = new Intent(FirebaseMessageHandler.this, ResponseDetail.class);
                            Log.e(TAG,"online id: "+response.getJSONArray("RESPONSES").getJSONObject(0).getInt("SURVEY_ID"));
                            Survey survey = db.getSurvey(response.getJSONArray("RESPONSES").getJSONObject(0).getInt("SURVEY_ID"));
                           // Log.e(TAG,"offline id: "+db.getSurvey(response.getJSONArray("RESPONSES").getJSONObject(0).getInt("SURVEY_ID")));
                            intent.putExtra("ID", response_id);
//                            intent.putExtra("name",survey.getName());
//                            intent.putExtra("Description",survey.getDesc());
//                            intent.putExtra("quesNo",survey.getNoOfQues());
//                            intent.putExtra("online", false);
//                            intent.putExtra("from_notification", true);
//                            intent.putExtra("response_id", response_id);
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
                            notificationManager.notify(1410,notificationBuilder.build());
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
}
