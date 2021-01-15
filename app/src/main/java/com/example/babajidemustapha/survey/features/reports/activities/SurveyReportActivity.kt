package com.example.babajidemustapha.survey.features.reports.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.reports.fragments.BarChartFragment
import com.example.babajidemustapha.survey.features.reports.fragments.PieChartFragment
import com.example.babajidemustapha.survey.features.reports.fragments.ResponseList
import com.example.babajidemustapha.survey.features.reports.fragments.TableFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SurveyReportActivity : AppCompatActivity() {
    /**
     * The [PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * The [ViewPager] that will host the section contents.
     */
    private lateinit var mViewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    var survey_id = 0
    var survey_name: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_report)
        val bundle = intent.extras
        survey_id = bundle.getInt("ID")
        survey_name = bundle.getString("name")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = if (survey_name.length > 15) survey_name.substring(0, 12) + "..." else survey_name
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mSectionsPagerAdapter!!.addFragment(ResponseList(), "Responses")
        mSectionsPagerAdapter!!.addFragment(TableFragment(), "Table")
        mSectionsPagerAdapter!!.addFragment(BarChartFragment(), "Bar Chart")
        mSectionsPagerAdapter!!.addFragment(PieChartFragment(), "Pie Chart")
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container)
        tabLayout = findViewById(R.id.tabs)
        mViewPager.adapter = mSectionsPagerAdapter
        tabLayout.setupWithViewPager(mViewPager)
        if (bundle.getBoolean("from_notification")) {
            mViewPager.setCurrentItem(1, true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
        private val fragments: MutableList<Fragment> = ArrayList()
        private val fragmentsTitle: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            fragmentsTitle.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentsTitle[position]
        }
    }
}