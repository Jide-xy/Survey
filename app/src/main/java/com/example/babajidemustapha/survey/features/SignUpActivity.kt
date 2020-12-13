package com.example.babajidemustapha.survey.features

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.SignUpActivity
import com.example.babajidemustapha.survey.shared.utils.Sha1
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SignUpActivity : AppCompatActivity() {
    var request: JsonObjectRequest? = null
    var username: EditText? = null
    var password: EditText? = null
    var fname: EditText? = null
    var lname: EditText? = null
    var email: EditText? = null
    var phone: EditText? = null
    var password2: EditText? = null
    var button: Button? = null
    var progressDialog: ProgressDialog? = null
    var loginData: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        username = findViewById(R.id.cUsername)
        password = findViewById(R.id.cPassword)
        password2 = findViewById(R.id.cPassword2)
        email = findViewById(R.id.cEmail)
        phone = findViewById(R.id.cPhone)
        fname = findViewById(R.id.cFirstName)
        lname = findViewById(R.id.cSurname)
        button = findViewById(R.id.create)
        progressDialog = ProgressDialog(this)
        loginData = getSharedPreferences("user_data", MODE_PRIVATE)
    }

    fun createUser(view: View?) {
        button!!.isEnabled = false
        attemptSignUp()
    }

    private fun attemptSignUp() {
        if (!validate()) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            button!!.isEnabled = true
            return
        }
        try {
            val obj = buildJson()
            progressDialog!!.isIndeterminate = true
            progressDialog!!.setMessage("Signing Up. Please wait...")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
            request = object : JsonObjectRequest(Method.POST, "http://survhey.azurewebsites.net/survey/user/signup", obj, Response.Listener { response ->
                var response = response
                button!!.isEnabled = true
                progressDialog!!.dismiss()
                try {
                    response = response.getJSONArray("response").getJSONObject(0)
                    if (response.getString("STATUS").equals("success", ignoreCase = true)) {
                        Toast.makeText(applicationContext, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                        finish()
                    } else if (response.getString("STATUS").equals("fail", ignoreCase = true)) {
                        Toast.makeText(this@SignUpActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                        username!!.requestFocus()
                        username!!.error = "Username exists"
                    } else {
                        Toast.makeText(this@SignUpActivity, "An error occured", Toast.LENGTH_SHORT).show()
                        Log.e("ddd", response.toString())
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this@SignUpActivity, "An error occured", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
                Log.e("ddd", response.toString())
            }, Response.ErrorListener { error ->
                progressDialog!!.dismiss()
                button!!.isEnabled = true
                Toast.makeText(this@SignUpActivity, "Failed to connect", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
                request = null
            }) {
                override fun getHeaders(): Map<String, String> {
                    val param: MutableMap<String, String> = HashMap()
                    param["Content-Type"] = "application/json; charset=utf-8"
                    return param
                }
            }
            Volley.newRequestQueue(this@SignUpActivity).add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
            button!!.isEnabled = true
            Toast.makeText(this@SignUpActivity, "An error occured", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(): Boolean {
        // Reset errors.
        var isValid = true
        if (username!!.text.toString().isEmpty()) {
            isValid = false
            username!!.error = "Please input valid username"
        }
        if (lname!!.text.toString().isEmpty()) {
            isValid = false
            lname!!.error = "Please enter your last name"
        }
        if (fname!!.text.toString().isEmpty()) {
            isValid = false
            fname!!.error = "Please enter your first name"
        }
        if (email!!.text.toString().isEmpty()) {
            isValid = false
            email!!.error = "Please input valid email address"
        }
        if (phone!!.text.toString().isEmpty()) {
            isValid = false
            phone!!.error = "Please enter your mobile number"
        }
        if (password!!.text.toString().isEmpty()) {
            isValid = false
            password!!.error = "Please input valid password"
        }
        if (password2!!.text.toString() != password!!.text.toString()) {
            isValid = false
            password2!!.error = "Password mismatch"
        }
        return isValid
    }

    @Throws(JSONException::class)
    private fun buildJson(): JSONObject {
        val obj = JSONObject()
        obj.put("username", username!!.text.toString())
        obj.put("password", Sha1.getHash(password!!.text.toString()))
        obj.put("surname", lname!!.text.toString())
        obj.put("firstname", fname!!.text.toString())
        obj.put("email", email!!.text.toString())
        obj.put("phone", phone!!.text.toString())
        obj.put("DEVICE_TOKEN", loginData!!.getString("DEVICE_TOKEN", null))
        return obj
    }
}