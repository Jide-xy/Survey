package com.example.babajidemustapha.survey.features.dashboard.activities

import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.LoginActivity
import com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList
import com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList.OnNavigationMenuSelected
import com.example.babajidemustapha.survey.features.searchsurvey.fragments.SearchSurvey

import com.example.babajidemustapha.survey.shared.utils.SharedPreferenceHelper
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnNavigationMenuSelected {
    lateinit var drawer: DrawerLayout
    lateinit var user_data: SharedPreferences
    lateinit var username: TextView
    lateinit var email: TextView
    lateinit var progressDialog: ProgressDialog
    lateinit var framelayout: FrameLayout
    lateinit var jsonObject: JSONObject
    var syncRequest: JsonRequest<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        framelayout = findViewById(R.id.container)
        setSupportActionBar(toolbar)
        user_data = getSharedPreferences("user_data", MODE_PRIVATE)
        //toolbar.setTitle("My Surveys");
        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, SurveyList())
                .commit()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        username = navigationView.getHeaderView(0).findViewById(R.id.username)
        email = navigationView.getHeaderView(0).findViewById(R.id.email)
        username.text = user_data.getString("USERNAME", "GUEST")
        email.text = user_data.getString("EMAIL", "")
        navigationView.setNavigationItemSelectedListener(this)
        if (SharedPreferenceHelper.isGuestUser(this)) {
            navigationView.menu.findItem(R.id.sync).isVisible = false
            navigationView.menu.findItem(R.id.search).isVisible = false
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item)
    }

    fun trySync() {
        progressDialog = ProgressDialog(this)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Syncing survey. Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        //            final JSONObject jsonObject;
//        DbOperationHelper.Companion.execute(object : IDbOperationHelper<SurveySyncRequest?> {
//            override fun run(): SurveySyncRequest? {
//                return db!!.surveyDao().dataToSync
//            }
//
//            override fun onCompleted(surveySyncRequest: SurveySyncRequest?) {
//                try {
//                    jsonObject = JSONObject(Gson().toJson(surveySyncRequest))
//                    syncRequest = sync()
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//                if (syncRequest != null) {
//                    //Volley.newRequestQueue(this@MainActivity).add<SurveySyncRequest>(syncRequest)
//                }
//            }
//        })
    }

    @Throws(JSONException::class)
    private fun sync(): JsonRequest<*>? {
        jsonObject.put("USER_ID", user_data.getInt("USER_ID", 0))
        printlog(jsonObject)
        if (jsonObject.optJSONArray("SURVEYS").length() == 0 && jsonObject.optJSONArray("SYNCED_SURVEY_RESPONSE").length() == 0) {
            progressDialog.dismiss()
            Snackbar.make(framelayout, "Backup up to date", Snackbar.LENGTH_SHORT).show()
        } else {
            //            syncRequest.setRetryPolicy(new RetryPolicy() {
//                @Override
//                public int getCurrentTimeout() {
//                    return 50000;
//                }
//
//                @Override
//                public int getCurrentRetryCount() {
//                    return 50000;
//                }
//
//                @Override
//                public void retry(VolleyError error) throws VolleyError {
//
//                }
//            });
            return object : JsonObjectRequest(Method.POST, "http://survhey.azurewebsites.net/survey/sync", jsonObject, Response.Listener { response_ ->
                try {
                    if (response_.getString("STATUS").equals("success", ignoreCase = true)) {
                        progressDialog.setMessage("Sync successful. Updating...")
//                        DbOperationHelper.Companion.execute(object : IDbOperationHelper<Void?> {
//                            override fun run(): Void? {
//                                db!!.surveyDao().updateSyncedDataWithOnlineIds(
//                                        Gson().fromJson(response_.toString(), SurveySyncRequest::class.java))
//                                return null
//                            }
//
//                            override fun onCompleted(aVoid: Void?) {
//                                progressDialog!!.dismiss()
//                                Toast.makeText(this@MainActivity, "Sync successful", Toast.LENGTH_LONG).show()
//                            }
//                        })
                    } else if (response_.getString("STATUS").equals("fail", ignoreCase = true)) {
                        progressDialog.dismiss()
                        Toast.makeText(this@MainActivity, "Sync unsuccessful", Toast.LENGTH_SHORT).show()
                        Log.e("ddd", response_.toString())
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this@MainActivity, "An error occured", Toast.LENGTH_SHORT).show()
                        Log.e("ddd", response_.toString())
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this@MainActivity, "An error occured", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
                Log.e("ddd", response_.toString())
            }, Response.ErrorListener { error ->
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity, "Failed to connect", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }) {
                override fun getHeaders(): Map<String, String> {
                    val param: MutableMap<String, String> = HashMap()
                    param["Content-Type"] = "application/json; charset=utf-8"
                    return param
                }
            }
        }
        return null
    }

    fun printlog(jsonObject: JSONObject?) {
        val veryLongString = jsonObject.toString()
        val maxLogSize = 200
        for (i in 0..veryLongString.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > veryLongString.length) veryLongString.length else end
            Log.v("json", veryLongString.substring(start, end))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.survey) {
            fragment = SurveyList()
        } else if (id == R.id.sync) {
            trySync()
        } else if (id == R.id.logout) {
            if (SharedPreferenceHelper.isGuestUser(this)) {
                val builderSingle = AlertDialog.Builder(this)
                builderSingle.setTitle("Logout")
                builderSingle.setMessage("Logging out as a guest will clear all created surveys. Are you sure you want to continue?")
                builderSingle.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                builderSingle.setPositiveButton("Continue") { dialog, which ->
                    logout()
                    dialog.dismiss()
                }
                builderSingle.create().show()
            } else {
                logout()
            }
        } else if (id == R.id.search) {
            fragment = SearchSurvey()
        }
        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        val user_token = user_data.getString("DEVICE_TOKEN", null)
        val editor = user_data.edit()
        editor.clear().apply()
        editor.putString("DEVICE_TOKEN", user_token).commit()
        val intent = Intent(this, LoginActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
        finishAffinity()
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
        }
    }

    override fun setTitle(title: String?) {

    }

}