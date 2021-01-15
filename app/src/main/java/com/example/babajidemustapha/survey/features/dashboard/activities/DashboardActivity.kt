package com.example.babajidemustapha.survey.features.dashboard.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.LoginActivity
import com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList
import com.example.babajidemustapha.survey.features.searchsurvey.fragments.SearchSurvey
import com.example.babajidemustapha.survey.shared.utils.SharedPreferenceHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_dashboard.*

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, SurveyList.OnNavigationMenuSelected {
    override fun setTitle(title: String?) {

    }

    lateinit var user_data: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)
        user_data = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        //toolbar.setTitle("My Surveys");
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, SurveyList(), SurveyList.TAG)
                .commit()
        nav_view.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        // Handle navigation view item clicks here.
        val id = item.itemId
        when (id) {
            R.id.survey -> fragment = SurveyList()
            R.id.sync -> {
            } //trySync()
            R.id.logout -> {
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
            }
            R.id.search -> fragment = SearchSurvey()
        }
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            if (fragment !is SurveyList && supportFragmentManager.findFragmentByTag(SurveyList.TAG)?.isVisible == true) transaction.addToBackStack(null)
            transaction.commit()
        }
        return true
    }

    private fun logout() {
//        val user_token = user_data.getString("DEVICE_TOKEN", null)
//        val editor = user_data.edit()
//        editor.clear().apply()
//        editor.putString("DEVICE_TOKEN", user_token).commit()
        val intent = Intent(this, LoginActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
        finishAffinity()
    }

}
