package com.calvin.baserecyclerviewadapter;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.calvin.baserecyclerviewadapter.fragment.SectionDemoFragment;

public class MainActivity extends AppCompatActivity {

    private Class[] mFragmentClasses = new Class[]{
//            RecyclerViewDemoFragment.class, HeaderFooterDemoFragment.class, MultiTypeDemoFragment.class
//            ,MultiTypeWithHeaderDemoFragment.class,
            SectionDemoFragment.class};
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        ContentPagerAdapter contentPagerAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(contentPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
    }


    private class ContentPagerAdapter extends FragmentStatePagerAdapter {

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment.instantiate(MainActivity.this, mFragmentClasses[position].getName());
        }

        @Override
        public int getCount() {
            return mFragmentClasses.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentClasses[position].getSimpleName().replace("Fragment", "");
        }
    }
}
