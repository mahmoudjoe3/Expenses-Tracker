package com.mahmoudjoe3.tracker.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mahmoudjoe3.tracker.R;
import com.mahmoudjoe3.tracker.ui.fragments.expenses.ExpensesFragment;
import com.mahmoudjoe3.tracker.ui.fragments.toDo.ToDoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("Follow");

        ViewpagerAdapter pagerAdapter = new ViewpagerAdapter(getSupportFragmentManager()
                , FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pagerAdapter.addFragment(ToDoFragment.newInstance(), "ToDo");
        pagerAdapter.addFragment(ExpensesFragment.newInstance(), "Expenses");
        viewpager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewpager);

    }

    static class ViewpagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments;
        List<String> titles;

        public ViewpagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}