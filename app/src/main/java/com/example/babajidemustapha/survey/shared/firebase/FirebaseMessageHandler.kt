package com.example.babajidemustapha.survey.shared.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.babajidemustapha.survey.R

import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FirebaseMessageHandler : FirebaseMessagingService() {
    var requestQueue: RequestQueue? = null
    var data: Map<String, String>? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        requestQueue = Volley.newRequestQueue(this)
        val TAG = "TAG"
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            data = remoteMessage.data
            val `object` = JSONObject()
            try {
                // preferences.getInt("USER_ID",0)
                `object`.put("RESPONSE_ID", data!!["RESPONSE_ID"])
            } catch (e: JSONException) {
                e.printStackTrace()
                return
            }
            val request: JsonObjectRequest = object : JsonObjectRequest(Method.POST, "http://survhey.azurewebsites.net/survey/get_response", `object`, Response.Listener { response ->
                Log.e("ddd", response.toString())
                try {
                    if (response.getString("STATUS").equals("success", ignoreCase = true)) {
//                        DbOperationHelper.execute(object : IDbOperationHelper<Long> {
//                            override fun run(): Long {
//                                return try {
//                                    val response_id = db!!.surveyDao().saveOnlineResponseHeaders(
//                                            Arrays.asList(*Gson().fromJson(response.getJSONArray("RESPONSES").toString(), Array<ResponseHeader>::class.java)))[0]
//                                    db!!.surveyDao().saveOnlineResponseDetails(
//                                            Arrays.asList(*Gson().fromJson(response.getJSONArray("RESPONSE_DETAILS").toString(), Array<ResponseDetail>::class.java)))
//                                    val survey = db!!.surveyDao().getSurveyWithOnlineId(response.getJSONArray("RESPONSES").getJSONObject(0).getInt("SURVEY_ID"))
//                                    response_id
//                                } catch (e: JSONException) {
//                                    throw RuntimeException(e)
//                                }
//                            }
//
//                            override fun onCompleted(response_id: Long) {
//                                buildAndShowNotification(response_id)
//                            }
//                        })
                    } else {
                        //  Toast.makeText(getContext(),"An error occured",Toast.LENGTH_SHORT).show();
                        Log.e("ddd", response.toString())
                    }
                } catch (e: JSONException) {
                    // Toast.makeText(LoginActivity.this,"An error occured",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error -> error.printStackTrace() }) {
                override fun getHeaders(): Map<String, String> {
                    val param: MutableMap<String, String> = HashMap()
                    param["Content-Type"] = "application/json; charset=utf-8"
                    return param
                }
            }
            requestQueue!!.add(request)
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("ddd", "Refreshed token: $s")
        val loginData = getSharedPreferences("user_data", MODE_PRIVATE)
        val edit = loginData.edit()
        edit.putString("DEVICE_TOKEN", s)
        edit.apply()
    }

    private fun buildAndShowNotification(response_id: Long) {
        val intent = Intent(this@FirebaseMessageHandler, ResponseDetail::class.java)
        intent.putExtra("ID", response_id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this@FirebaseMessageHandler, 1410,
                intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder = NotificationCompat.Builder(this@FirebaseMessageHandler)
                .setSmallIcon(R.drawable.ic_library_books_black_18dp)
                .setContentTitle(data!!["TOPIC"])
                .setContentText(data!!["MESSAGE"])
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1410, notificationBuilder.build())
    }
}