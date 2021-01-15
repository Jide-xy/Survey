package com.example.babajidemustapha.survey.features.takesurvey.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.takesurvey.fragments.AnswerQuestionFragment
import com.example.babajidemustapha.survey.features.takesurvey.viewmodel.AnswerSurveyViewModel
import com.jide.surveyapp.model.QuestionType
import com.jide.surveyapp.model.ResponseDetail
import com.jide.surveyapp.model.ResponseHeader
import com.jide.surveyapp.model.relations.QuestionWithOptionsAndResponse
import com.jide.surveyapp.model.relations.ResponseHeaderWithDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_survey_action.*
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class SurveyAction : AppCompatActivity(), AnswerQuestionFragment.OnQuestionSelectedInteractionListener {

    /**
     * The [PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private val viewModel by viewModels<AnswerSurveyViewModel>()

    //    TabLayout tabLayout;
    var survey_id: String = ""
    var online: Boolean = false
    var questions: List<QuestionWithOptionsAndResponse> = emptyList()
    var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_action)
        val bundle = intent.extras
        online = bundle?.getBoolean("online") ?: false
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!online) {
            survey_id = bundle!!.getString("ID", "")
            viewModel.getQuestions(survey_id)
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    currentPosition = position
                    when (position) {
                        0 -> {
                            next.visibility = View.VISIBLE
                            previous.visibility = View.GONE
                        }
                        questions.size - 1 -> {
                            next.visibility = View.GONE
                            previous.visibility = View.VISIBLE
                        }
                        else -> {
                            next.visibility = View.VISIBLE
                            previous.visibility = View.VISIBLE
                        }
                    }
                }

            })
            viewPager.offscreenPageLimit = questions.size

            lifecycleScope.launchWhenStarted {
                viewModel.questionsFlow.collect {
                    this@SurveyAction.questions = it
                    mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, this@SurveyAction.questions)
                    viewPager.adapter = mSectionsPagerAdapter
                }
            }

        } else {
            title = "External Survey"
            viewPager.adapter = mSectionsPagerAdapter
            //            tabLayout.setupWithViewPager(mViewPager);
        }
        next.setOnClickListener {
            viewPager.setCurrentItem(currentPosition + 1, true)
            // viewPager.currentItem
        }
        previous.setOnClickListener {
            viewPager.setCurrentItem(currentPosition - 1, true)
        }

    }

    private fun validate(): Boolean {
        for ((index, question) in questions.withIndex()) {
            when (question.question.questionType) {
                QuestionType.SHORT_TEXT, QuestionType.LONG_TEXT, QuestionType.SINGLE_OPTION, QuestionType.MULTIPLE_OPTION, QuestionType.REACTIONS -> {
                    if (question.responses.isEmpty() && question.question.mandatory) {
                        viewPager.setCurrentItem(index, false)
                        Toast.makeText(this, "One or more mandatory questions have not been answered.", Toast.LENGTH_LONG).show()
                        return false
                    }
                }
                QuestionType.IMAGES -> TODO()
                else -> return false
            }
        }
        return true
    }

    private fun storeResponse() {
        val responseDetails: List<ResponseDetail> = questions.flatMap { it.responses }
        viewModel.saveResponse(ResponseHeaderWithDetails(ResponseHeader(null, survey_id), responseDetails))
        Toast.makeText(this@SurveyAction, "Your response has been recorded", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun calculateProgress(): Float {
        var count = 0
        forLoop@ for (question in questions) {
            when (question.question.questionType) {
                QuestionType.SHORT_TEXT, QuestionType.LONG_TEXT, QuestionType.SINGLE_OPTION, QuestionType.MULTIPLE_OPTION, QuestionType.REACTIONS -> {
                    if (question.responses.isNullOrEmpty()) {
                        continue@forLoop
                    }
                }
                QuestionType.IMAGES -> TODO()
                else -> {
                }
            }
            count++
        }
        return count.toFloat() * 100 / questions.size
    }

    override fun updateProgress() {
        progressBar.progress = calculateProgress().toInt()
    }

    override fun getTotal(): Int {
        return questions.size
    }

    override fun onQuestionSelected(index: Int): QuestionWithOptionsAndResponse {
        return questions[index]
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.take_survey_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId


        return if (id == R.id.save_answers) {
            if (validate()) {
                storeResponse()
            }
            true
        } else super.onOptionsItemSelected(item)

    }

    /**
     * A placeholder fragment containing a simple view.
     */

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager, val questions: List<QuestionWithOptionsAndResponse>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return AnswerQuestionFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return questions.size
        }
    }
}
