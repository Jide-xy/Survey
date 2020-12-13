package com.example.babajidemustapha.survey.features

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.LoginActivity
import com.example.babajidemustapha.survey.features.dashboard.activities.DashboardActivity
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase
import com.example.babajidemustapha.survey.shared.room.entities.Question
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader
import com.example.babajidemustapha.survey.shared.room.entities.Survey
import com.example.babajidemustapha.survey.shared.utils.Constants
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper
import com.example.babajidemustapha.survey.shared.utils.Sha1
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //  private UserLoginTask mAuthTask = null;
    // UI references.
    private var mUsernameView: EditText? = null
    private var mPasswordView: EditText? = null
    private var mProgressView: ProgressBar? = null
    var mSignInButton: Button? = null
    var loginData: SharedPreferences? = null
    var request: JsonObjectRequest? = null
    var db: SurveyDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginData = getSharedPreferences("user_data", MODE_PRIVATE)
        if (loginData!!.getBoolean("isLoggedIn", false)) {
            goToDashboard()
        }
        setContentView(R.layout.activity_login)
        db = SurveyDatabase.Companion.getInstance(this)
        // Set up the login form.
        mUsernameView = findViewById(R.id.username)
        mPasswordView = findViewById(R.id.password)
        mProgressView = findViewById(R.id.login_progress)
        mSignInButton = findViewById(R.id.sign_in_button)
        mSignInButton!!.setOnClickListener(View.OnClickListener { view: View? ->

            //  mProgressView.setVisibility(View.VISIBLE);
            attemptLogin()
        })
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Reset errors.
        mUsernameView!!.error = null
        mPasswordView!!.error = null

        // Store values at the time of the login attempt.
        val username = mUsernameView!!.text.toString()
        val password = mPasswordView!!.text.toString()
        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView!!.error = getString(R.string.error_invalid_password)
            focusView = mPasswordView
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView!!.error = getString(R.string.error_field_required)
            focusView = mUsernameView
            cancel = true
        } else if (!isEmailValid(username)) {
            mUsernameView!!.error = getString(R.string.error_invalid_email)
            focusView = mUsernameView
            cancel = true
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
            return
        } else {
            mSignInButton!!.isEnabled = false
            mProgressView!!.visibility = View.VISIBLE
            var obj: JSONObject? = null
            try {
                obj = JSONObject()
                obj.put("username", mUsernameView!!.text.toString())
                obj.put("password", Sha1.getHash(mPasswordView!!.text.toString()))
                Log.e("token", loginData!!.getString("DEVICE_TOKEN", null))
                obj.put("DEVICE_TOKEN", loginData!!.getString("DEVICE_TOKEN", null))
            } catch (e: JSONException) {
                e.printStackTrace()
                mProgressView!!.visibility = View.GONE
                Toast.makeText(this, "An error occured", Toast.LENGTH_SHORT).show()
                return
            }
            request = object : JsonObjectRequest(Method.POST, "http://survhey.azurewebsites.net/survey/user/login", obj, Response.Listener { response_ ->
                mSignInButton!!.isEnabled = true
                mProgressView!!.visibility = View.GONE
                Log.e("ddd", response_.toString())
                val response: JSONObject
                try {
                    response = response_.getJSONArray("RESPONSE").getJSONObject(0)
                    if (response.getString("STATUS").equals("success", ignoreCase = true)) {
                        DbOperationHelper.Companion.execute(object : IDbOperationHelper<Void?> {
                            override fun run(): Void? {
                                db!!.clearAllTables()
                                try {
                                    db!!.surveyDao().saveOnlineSurvey(
                                            Arrays.asList(*Gson().fromJson(response_.getJSONArray("SURVEYS").toString(), Array<Survey>::class.java)))
                                    db!!.questionDao().saveOnlineQuestions(
                                            Arrays.asList(*Gson().fromJson(response_.getJSONArray("QUESTIONS").toString(), Array<Question>::class.java)))
                                    db!!.responseHeaderDao().saveOnlineResponseHeaders(
                                            Arrays.asList(*Gson().fromJson(response_.getJSONArray("RESPONSES").toString(), Array<ResponseHeader>::class.java)))
                                    db!!.responseDetailDao().saveOnlineResponseDetails(
                                            Arrays.asList(*Gson().fromJson(response_.getJSONArray("RESPONSE_DETAILS").toString(), Array<ResponseDetail>::class.java)))
                                } catch (e: JSONException) {
                                    throw RuntimeException(e)
                                }
                                return null
                            }

                            override fun onCompleted(aVoid: Void?) {
                                try {
                                    val user_dt = response_.getJSONArray("USER_DETAILS").getJSONObject(0)
                                    saveUserData(user_dt)
                                    goToDashboard()
                                } catch (e: JSONException) {
                                    throw RuntimeException(e)
                                }
                            }
                        })
                        //                            download();
                    } else if (response.getString("STATUS").equals("fail", ignoreCase = true)) {
                        Toast.makeText(this@LoginActivity, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                        request = null
                    } else {
                        Toast.makeText(this@LoginActivity, "An error occured", Toast.LENGTH_SHORT).show()
                        Log.e("ddd", response.toString())
                        request = null
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this@LoginActivity, "An error occured", Toast.LENGTH_SHORT).show()
                    request = null
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                mSignInButton!!.isEnabled = true
                mProgressView!!.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Failed to connect", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }) {
                override fun getHeaders(): Map<String, String> {
                    val param: MutableMap<String, String> = HashMap()
                    param["Content-Type"] = "application/json; charset=utf-8"
                    return param
                }
            }
            Volley.newRequestQueue(this@LoginActivity).add(request)
        }
    }

    private fun isEmailValid(username: String): Boolean {
        //TODO: Replace this with your own logic
        return username.length >= 1
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length >= 1
    }

    @Throws(JSONException::class)
    private fun saveUserData(user_dt: JSONObject?) {
        if (user_dt != null) {
            val editor = loginData!!.edit()
            editor.putBoolean(Constants.IS_LOGGED_IN, true)
            editor.putString("USERNAME", user_dt.getString("USERNAME"))
            editor.putInt("USER_ID", user_dt.getInt("ID"))
            editor.putString("FIRST_NAME", user_dt.getString("FIRSTNAME"))
            editor.putString("PASSWORD", user_dt.getString("PASSWORD"))
            editor.putString("SURNAME", user_dt.getString("SURNAME"))
            editor.putString("EMAIL", user_dt.getString("EMAIL"))
            editor.putString("PHONE_NUMBER", user_dt.getString("PHONE_NUMBER"))
            editor.apply()
        }
    }

    private fun goToDashboard() {
        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    fun signUp(view: View?) {
        val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun skipLogin(view: View?) {
        val editor = loginData!!.edit()
        editor.putBoolean(Constants.IS_LOGGED_IN, true)
        editor.putBoolean(Constants.IS_GUEST, true)
        editor.apply()
        goToDashboard()
    }
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