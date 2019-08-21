package com.example.babajidemustapha.survey.features.introduction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.LoginActivity
import com.example.babajidemustapha.survey.shared.utils.SharedPreferenceHelper
import kotlinx.android.synthetic.main.activity_introduction.*

class IntroductionActivity : AppCompatActivity() {

    lateinit var featureHeaders: Array<String>
    lateinit var featureDetails: Array<String>
    lateinit var featureImages: IntArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!SharedPreferenceHelper.isfirstLogin(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
        setContentView(R.layout.activity_introduction)
        featureHeaders = resources.getStringArray(R.array.feature_headers)
        featureDetails = resources.getStringArray(R.array.feature_details)
        featureImages = intArrayOf(R.drawable.ic_access_to_polls, R.drawable.ic_variety_question_types, R.drawable.ic_verified_surveys, R.drawable.ic_make_money)
        viewPager.adapter = FeaturesAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        proceedEmail.setOnClickListener {
            SharedPreferenceHelper.setFirstLogin(this)
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    inner class FeaturesAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {

        override fun getItem(position: Int): Fragment {
            return IntroductionFragment.newInstance(featureHeaders[position], featureDetails[position], featureImages[position])
        }

        override fun getCount(): Int {
            return featureHeaders.size
        }

    }
}
