package com.example.babajidemustapha.survey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
  //  private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private ProgressBar mProgressView;
    Button mSignInButton;
    SharedPreferences loginData;
    JsonObjectRequest request;
    SurveyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginData = getSharedPreferences("user_data",MODE_PRIVATE);
        if(loginData.getBoolean("isLoggedIn",false)){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        db = new SurveyDatabase(LoginActivity.this);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

              //  mProgressView.setVisibility(View.VISIBLE);
                attemptLogin();

              //  mProgressView.setVisibility(View.GONE);
            }
        });


    }

    /**
     * Callback received when a permissions request has been completed.
     */

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isEmailValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        } else {
            mSignInButton.setEnabled(false);
            mProgressView.setVisibility(View.VISIBLE);
            JSONObject obj = null;
            try {
              obj = new JSONObject();
                obj.put("username",mUsernameView.getText().toString());
                obj.put("password",Sha1.getHash(mPasswordView.getText().toString()));
                Log.e("token",loginData.getString("DEVICE_TOKEN",null));
                obj.put("DEVICE_TOKEN",loginData.getString("DEVICE_TOKEN",null));
            } catch (JSONException e) {
                e.printStackTrace();
                mProgressView.setVisibility(View.GONE);
                Toast.makeText(this,"An error occured",Toast.LENGTH_SHORT).show();
                return;
            }
            request = new JsonObjectRequest(Request.Method.POST, "http://survhey.azurewebsites.net/survey/user/login",obj,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response_) {
                    mSignInButton.setEnabled(true);
                    mProgressView.setVisibility(View.GONE);
                    Log.e("ddd",response_.toString());
                    JSONObject response;
                    try {
                        response = response_.getJSONArray("RESPONSE").getJSONObject(0);
                        if(response.getString("STATUS").equalsIgnoreCase("success")){
                            JSONObject user_dt = response_.getJSONArray("USER_DETAILS").getJSONObject(0);
                            db.saveSurvey(response_.getJSONArray("SURVEYS"));
                            db.saveQuestion(response_.getJSONArray("QUESTIONS"));
                            db.saveResponse(response_.getJSONArray("RESPONSES"));
                            db.saveResponseDetail(response_.getJSONArray("RESPONSE_DETAILS"));
                            SharedPreferences.Editor editor =  loginData.edit();
                            editor.putBoolean("isLoggedIn",true);
                            editor.putString("USERNAME",user_dt.getString("USERNAME"));
                            editor.putInt("USER_ID",user_dt.getInt("ID"));
                            editor.putString("FIRST_NAME",user_dt.getString("FIRSTNAME"));
                            editor.putString("PASSWORD",user_dt.getString("PASSWORD"));
                            editor.putString("SURNAME",user_dt.getString("SURNAME"));
                            editor.putString("EMAIL",user_dt.getString("EMAIL"));
                            editor.putString("PHONE_NUMBER",user_dt.getString("PHONE_NUMBER"));
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
//                            download();

                        }
                        else if(response.getString("STATUS").equalsIgnoreCase("fail")){
                            Toast.makeText(LoginActivity.this,"Incorrect username or password",Toast.LENGTH_SHORT).show();
                            request = null;
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"An error occured",Toast.LENGTH_SHORT).show();
                            Log.e("ddd",response.toString());
                            request = null;
                        }
                    } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this,"An error occured",Toast.LENGTH_SHORT).show();
                        request = null;
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSignInButton.setEnabled(true);
                    mProgressView.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,"Failed to connect",Toast.LENGTH_SHORT).show();
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
            Volley.newRequestQueue(LoginActivity.this).add(request);
        }
    }

    private boolean isEmailValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() >= 1;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    public void signUp(View view) {
        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

//    private void download() {
//        JSONObject obj = new JSONObject();
//        JsonObjectRequest downloadRequest = null;
//        try {
//            obj.put("USER_ID", loginData.getInt("USER_ID", 0));
//            downloadRequest = new JsonObjectRequest(Request.Method.POST, "http://192.168.1.9:64719/survey/download/all",obj,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                Log.e("response", response.toString());
//                                if (response.getString("STATUS").equalsIgnoreCase("SUCCESS")) {
//                                    Log.e("response", "reching here");
//
//                                    db.saveSurvey(response.getJSONArray("SURVEYS"));
//                                    db.saveQuestion(response.getJSONArray("QUESTIONS"));
//                                    db.saveResponse(response.getJSONArray("RESPONSES"));
//                                    db.saveResponseDetail(response.getJSONArray("RESPONSE_DETAILS"));
//
//                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                    //   intent.putExtra("username", finalObj.getString("username"));
//                                    startActivity(intent);
//                                    finish();
//                                }
//                                else if (response.getString("STATUS").equalsIgnoreCase("FAIL")){
//                                    Log.e("error", response.getString("MESSAGE"));
//                                }
//                                else {
//                                    Log.e("error", "unknown error");
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                        }
//                    });
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (downloadRequest != null){
//            Volley.newRequestQueue(LoginActivity.this).add(downloadRequest);
//        }
//    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
//    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mEmail;
//        private final String mPassword;
//        String response="";
//        String line  = "";
//
//
//        UserLoginTask(String email, String password) {
//            mEmail = email;
//            mPassword = password;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            // TODO: attempt authentication against a network service.
//            try {
//                HttpURLConnection conn = (HttpURLConnection) new URL("http://192.168.81.98:64719/survey/user/login").openConnection();
//                conn.setRequestMethod("POST");
//                conn.setReadTimeout(10000);
//                conn.setConnectTimeout(10000);
//                conn.setRequestProperty("accept-type","application/json");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
//               out.write("{username:jidexy,password:jidexy}".getBytes());
//                BufferedReader br = new BufferedReader(new InputStreamReader(
//                        conn.getInputStream()));
//                while ((line = br.readLine()) != null) {
//                    response += line;
//                }
//            }
//            catch (Exception ex){
//                ex.printStackTrace();
//                return false;
//            }
//            // TODO: register the new account here.
//            return false;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            mAuthTask = null;
//         //   showProgress(false);
//            Log.e("fff",response);
//            if (success) {
//                finish();
//            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAuthTask = null;
//       //     showProgress(false);
//        }
//    }
}

