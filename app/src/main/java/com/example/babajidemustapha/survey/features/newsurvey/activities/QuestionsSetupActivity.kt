package com.example.babajidemustapha.survey.features.newsurvey.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.newsurvey.fragments.CreateSurveyFragment
import com.example.babajidemustapha.survey.features.newsurvey.fragments.QuestionsSetupFragment
import com.example.babajidemustapha.survey.shared.room.entities.Survey
import kotlinx.android.synthetic.main.activity_questions_setup.*

class QuestionsSetupActivity : AppCompatActivity(), QuestionsSetupFragment.OnSurveySetupInteractionListener {

    lateinit var mSurvey: Survey

    override fun getSurvey(): Survey {
        return mSurvey
    }

    override fun createSurvey(survey: Survey) {
        mSurvey = survey
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, QuestionsSetupFragment.newInstance())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions_setup)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, CreateSurveyFragment.newInstance())
        transaction.commit()
    }

}
