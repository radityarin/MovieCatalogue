package com.radityarin.moviecatalogue.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.radityarin.moviecatalogue.R;

public class FavoriteFragment extends Fragment {

    private String[] PAGE_TITLES;
    private Fragment[] PAGES;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        PAGE_TITLES = getResources().getStringArray(R.array.page_title);
        PAGES = new Fragment[] {new MovieFavoriteFragment(),new TVShowFavoriteFragment()};

        ViewPager mViewPager = view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return PAGES[position];
        }

        @Override
        public int getCount() {
            return PAGES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PAGE_TITLES[position];
        }

    }
}
