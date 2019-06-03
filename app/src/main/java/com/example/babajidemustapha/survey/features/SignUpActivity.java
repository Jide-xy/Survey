package com.example.babajidemustapha.survey.features;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.utils.Sha1;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    JsonObjectRequest request;
    EditText username;
    EditText password;
    EditText fname;
    EditText lname;
    EditText email;
    EditText phone;
    EditText password2;
    Button button;
    ProgressDialog progressDialog;
    SharedPreferences loginData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        username = findViewById(R.id.cUsername);
        password = findViewById(R.id.cPassword);
        password2 = findViewById(R.id.cPassword2);
        email = findViewById(R.id.cEmail);
        phone = findViewById(R.id.cPhone);
        fname = findViewById(R.id.cFirstName);
        lname = findViewById(R.id.cSurname);
        button = findViewById(R.id.create);
        progressDialog = new ProgressDialog(this);
        loginData = getSharedPreferences("user_data",MODE_PRIVATE);
    }

    public void createUser(View view) {
        button.setEnabled(false);
        attemptSignUp();
    }
    private void attemptSignUp() {

        if (!validate()) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            button.setEnabled(true);
            return;
        }

        try {
            JSONObject obj = buildJson();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Signing Up. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            request = new JsonObjectRequest(Request.Method.POST, "http://survhey.azurewebsites.net/survey/user/signup", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    button.setEnabled(true);
                    progressDialog.dismiss();
                    try {
                        response = response.getJSONArray("response").getJSONObject(0);
                        if (response.getString("STATUS").equalsIgnoreCase("success")) {
                            Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (response.getString("STATUS").equalsIgnoreCase("fail")) {
                            Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            username.requestFocus();
                            username.setError("Username exists");
                        } else {
                            Toast.makeText(SignUpActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                            Log.e("ddd", response.toString());
                        }
                    } catch (JSONException e) {
                        Toast.makeText(SignUpActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Log.e("ddd", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    button.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                    request = null;
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> param = new HashMap<>();
                    param.put("Content-Type", "application/json; charset=utf-8");
                    return param;
                }
            };
            Volley.newRequestQueue(SignUpActivity.this).add(request);
        }
        catch(JSONException e){
            e.printStackTrace();
            button.setEnabled(true);
            Toast.makeText(SignUpActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validate(){
        // Reset errors.
        boolean isValid = true;
            if(username.getText().toString().isEmpty()){
                isValid = false;
                username.setError("Please input valid username");
            }
        if(lname.getText().toString().isEmpty()){
            isValid = false;
            lname.setError("Please enter your last name");
        }
        if(fname.getText().toString().isEmpty()){
            isValid = false;
            fname.setError("Please enter your first name");
        }
        if(email.getText().toString().isEmpty()){
            isValid = false;
            email.setError("Please input valid email address");
        }
        if(phone.getText().toString().isEmpty()){
            isValid = false;
            phone.setError("Please enter your mobile number");
        }
        if(password.getText().toString().isEmpty()){
            isValid = false;
            password.setError("Please input valid password");
        }
        if(!password2.getText().toString().equals(password.getText().toString())){
            isValid = false;
            password2.setError("Password mismatch");
        }
        return isValid;
    }
    private JSONObject buildJson() throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put("username", username.getText().toString());
        obj.put("password", Sha1.getHash(password.getText().toString()));
            obj.put("surname",lname.getText().toString());
            obj.put("firstname",fname.getText().toString());
            obj.put("email",email.getText().toString());
            obj.put("phone",phone.getText().toString());
            obj.put("DEVICE_TOKEN",loginData.getString("DEVICE_TOKEN",null));
        return obj;
    }
}
