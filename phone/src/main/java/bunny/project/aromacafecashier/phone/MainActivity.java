package bunny.project.aromacafecashier.phone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import bunny.project.aromacafecashier.common.MLog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mTextMessage;
    private ViewPager mViewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navi_sales:
                    mViewPager.setCurrentItem(0);
                    return true;
//                case R.id.navi_manager:
//                    mViewPager.setCurrentItem(1);
//                    return true;
                case R.id.navi_setting:
                    mViewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPagerChangeListener();
    private BottomNavigationView mNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MLog.i(TAG,"[getItem] pos:"+position);
            if (position == 0) {
                return SalesFragment.newInstance("", "");
//            } else if (position == 1) {
//                return ManagerFragment.newInstance("","");
            } else if (position == 1) {
                return SettingFragment.newInstance("","");
            }else {
                return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
//            return 3;
            return 2;
        }
    }


    private class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {
        private MenuItem menuItem;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mNavigation.getMenu().getItem(position).setChecked(true);
            if (menuItem != null) {
                menuItem.setChecked(false);
            } else {
                mNavigation.getMenu().getItem(0).setChecked(false);
            }
            menuItem = mNavigation.getMenu().getItem(position);
            menuItem.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
