package com.example.babajidemustapha.survey.features.dashboard.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.features.LoginActivity;
import com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList;
import com.example.babajidemustapha.survey.features.searchsurvey.fragments.SearchSurvey;
import com.example.babajidemustapha.survey.shared.models.SurveySyncRequest;
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase;
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper;
import com.example.babajidemustapha.survey.shared.utils.SharedPreferenceHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SurveyList.OnNavigationMenuSelected {

    SurveyDatabase db;
    DrawerLayout drawer;
    SharedPreferences user_data;
    TextView username;
    TextView email;
    ProgressDialog progressDialog;
    FrameLayout framelayout;
    JSONObject jsonObject;
    JsonRequest syncRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        framelayout = findViewById(R.id.container);
        setSupportActionBar(toolbar);
        user_data = getSharedPreferences("user_data", MODE_PRIVATE);
        //toolbar.setTitle("My Surveys");
        db = SurveyDatabase.getInstance(this);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SurveyList())
                .commit();

        NavigationView navigationView = findViewById(R.id.nav_view);
        username = navigationView.getHeaderView(0).findViewById(R.id.username);
        email = navigationView.getHeaderView(0).findViewById(R.id.email);
        username.setText(user_data.getString("USERNAME", "GUEST"));
        email.setText(user_data.getString("EMAIL", ""));
        navigationView.setNavigationItemSelectedListener(this);
        if (SharedPreferenceHelper.isGuestUser(this)) {
            navigationView.getMenu().findItem(R.id.sync).setVisible(false);
            navigationView.getMenu().findItem(R.id.search).setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void trySync() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Syncing survey. Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
//            final JSONObject jsonObject;
        DbOperationHelper.execute(new IDbOperationHelper<SurveySyncRequest>() {
            @Override
            public SurveySyncRequest run() {
                return db.surveyDao().getDataToSync();
            }

            @Override
            public void onCompleted(SurveySyncRequest surveySyncRequest) {
                try {
                    jsonObject = new JSONObject(new Gson().toJson(surveySyncRequest));
                    syncRequest = sync();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (syncRequest != null) {
                    Volley.newRequestQueue(MainActivity.this).add(syncRequest);
                }
            }
        });

    }

    private JsonRequest sync() throws JSONException {
        jsonObject.put("USER_ID", user_data.getInt("USER_ID", 0));
        printlog(jsonObject);
        if (jsonObject.optJSONArray("SURVEYS").length() == 0 && jsonObject.optJSONArray("SYNCED_SURVEY_RESPONSE").length() == 0) {
            progressDialog.dismiss();
            Snackbar.make(framelayout, "Backup up to date", Snackbar.LENGTH_SHORT).show();
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
            return new JsonObjectRequest(Request.Method.POST, "http://survhey.azurewebsites.net/survey/sync", jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response_) {

                    try {
                        if (response_.getString("STATUS").equalsIgnoreCase("success")) {
                            progressDialog.setMessage("Sync successful. Updating...");
                            DbOperationHelper.execute(new IDbOperationHelper<Void>() {
                                @Override
                                public Void run() {
                                    db.surveyDao().updateSyncedDataWithOnlineIds(
                                            new Gson().fromJson(response_.toString(), SurveySyncRequest.class));
                                    return null;
                                }

                                @Override
                                public void onCompleted(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Sync successful", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (response_.getString("STATUS").equalsIgnoreCase("fail")) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Sync unsuccessful", Toast.LENGTH_SHORT).show();
                            Log.e("ddd", response_.toString());
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                            Log.e("ddd", response_.toString());
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Log.e("ddd", response_.toString());
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> param = new HashMap<>();
                    param.put("Content-Type", "application/json; charset=utf-8");
                    return param;
                }
            };

        }
        return null;
    }

    public void printlog(JSONObject jsonObject) {
        String veryLongString = jsonObject.toString();
        int maxLogSize = 200;
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            Log.v("json", veryLongString.substring(start, end));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.survey) {
            fragment = new SurveyList();
        }
        else if (id == R.id.sync) {
            trySync();
        } else if (id == R.id.logout) {
            if (SharedPreferenceHelper.isGuestUser(this)) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
                builderSingle.setTitle("Logout");
                builderSingle.setMessage("Logging out as a guest will clear all created surveys. Are you sure you want to continue?");
                builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderSingle.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                        dialog.dismiss();
                    }
                });
                builderSingle.create().show();
            } else {
                logout();
            }
        } else if (id == R.id.search) {
            fragment = new SearchSurvey();
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        String user_token = user_data.getString("DEVICE_TOKEN", null);
        SharedPreferences.Editor editor = user_data.edit();
        db.clearAllTables();
        editor.clear().apply();
        editor.putString("DEVICE_TOKEN", user_token).commit();
        Intent intent = new Intent(this, LoginActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }
}
