package com.example.babajidemustapha.survey.features.takesurvey.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.style.ReplacementSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.features.takesurvey.fragments.AnswerSurvey;
import com.example.babajidemustapha.survey.features.takesurvey.fragments.SurveyDescription;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SurveyAction extends AppCompatActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    TabLayout tabLayout;
    int survey_id;
    boolean online;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_action);
        Bundle bundle = getIntent().getExtras();
        online = bundle.getBoolean("online");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mSectionsPagerAdapter.addFragment(new SurveyDescription(), "Description");
        mSectionsPagerAdapter.addFragment(new AnswerSurvey(), "Take Survey");
        mViewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tabs);
        if(!online) {
            survey_id = bundle.getInt("ID");

            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.

            // mSectionsPagerAdapter.addFragment(new ResponseList(), "Responses");
            // Set up the ViewPager with the sections adapter.

            mViewPager.setAdapter(mSectionsPagerAdapter);

            tabLayout.setupWithViewPager(mViewPager);
//            setupTabs();
//            if(bundle.getBoolean("from_notification")){
//                mViewPager.setCurrentItem(2,true);
//            }
        }
        else{
            setTitle("External Survey");
            mViewPager.setAdapter(mSectionsPagerAdapter);
            tabLayout.setupWithViewPager(mViewPager);
        }

    }

//    public void setupTabs(){
//        for(int i = 0; i<tabLayout.getTabCount();i++){
//           // tabLayout.getTabAt(i).setCustomView(R.layout.survey_item_layout);
//            Log.e("tab text",tabLayout.getTabAt(i).getText().toString());
//            //Log.e("count",db.getResponsesCount(survey_id)+"");
//            switch (tabLayout.getTabAt(i).getText().toString().toLowerCase()){
//                case "responses":
//                    SpannableString customText = new SpannableString("RESPONSES "+db.getResponsesCount(survey_id));
////                    customText.setSpan(new BackgroundColorSpan(0xFFFFFFFF) , 10, customText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                    customText.setSpan(new ForegroundColorSpan(0xFF000000) , 10, customText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    customText.setSpan(new RoundedBackgroundSpan(this) , 10, customText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    View view = getLayoutInflater().inflate(R.layout.tab_layout_with_badge,null);
//                    TextView textView = view.findViewById(R.id.tabText);
//                    TextView textView2 = view.findViewById(R.id.tabBadge);
//                    textView.setText("RESPONSES");
//                    textView2.setText(db.getResponsesCount(survey_id)+"");
////                    textView.setTextColor(new ColorStateList(new int[][]{
////                            {},{}
////                    },new int[]{}));
//                    tabLayout.getTabAt(i).setCustomView(view);
//                    break;
//                default:
//
//                    break;
//
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_survey_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentsTitle = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentsTitle.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
          return fragmentsTitle.get(position);
        }
    }
    public class RoundedBackgroundSpan extends ReplacementSpan {


        private int backgroundColor = 0;
        private int textColor = 0;
        private int PADDING = 5;

        public RoundedBackgroundSpan(Context context) {
            super();
            backgroundColor = 0xFFFFFFFF;
            textColor = 0xFF000000;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            float CORNER_RADIUS = (bottom - top);
            float textWidth = x + measureText(paint, text, start, end);
          //  float CORNER_RADIUS = ( measureText(paint, text, start, end) + ( 2 * PADDING)) /2 ;
            RectF rect = new RectF(x - PADDING, top, textWidth + PADDING, bottom);
            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
            paint.setColor(textColor);
            canvas.drawText(text, start, end, x, y, paint);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            return Math.round(paint.measureText(text, start, end)) + PADDING;
        }

        private float measureText(Paint paint, CharSequence text, int start, int end) {
            return paint.measureText(text, start, end);
        }
    }
}
