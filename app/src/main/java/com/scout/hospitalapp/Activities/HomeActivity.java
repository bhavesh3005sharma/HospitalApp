package com.scout.hospitalapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.scout.hospitalapp.Activities.Auth.LoginActivity;
import com.scout.hospitalapp.Adapter.PageViewAdapter;
import com.scout.hospitalapp.Fragments.AppointmentRequestsFragment;
import com.scout.hospitalapp.Fragments.DepartmentsFragment;
import com.scout.hospitalapp.Fragments.DoctorsFragment;
import com.scout.hospitalapp.Fragments.HistoryFragment;
import com.scout.hospitalapp.Fragments.HomeFragment;
import com.scout.hospitalapp.Fragments.NotificationFragment;
import com.scout.hospitalapp.R;
import com.scout.hospitalapp.Repository.SharedPref.SharedPref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsingToolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;

    Unbinder unbinder;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);

        setUpToolbar();

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_group);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_business);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_menu_gallery);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_notifications);
        tabLayout.getTabAt(5).setIcon(R.drawable.ic_history);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        collapsingToolbar.setTitleEnabled(false);
    }

    public void setUpViewPager(ViewPager viewPager) {
        PageViewAdapter pageViewAdapter = new PageViewAdapter(getSupportFragmentManager(),0);
        HomeFragment homeFragmentTab = new HomeFragment();
        AppointmentRequestsFragment appointmentRequestsFragmentTab = new AppointmentRequestsFragment();
        DoctorsFragment doctorsFragmentTab = new DoctorsFragment();
        DepartmentsFragment departmentsFragmentTab = new DepartmentsFragment();
        NotificationFragment notificationFragmentTab = new NotificationFragment();
        HistoryFragment historyFragment = new HistoryFragment();

        pageViewAdapter.addFragment(homeFragmentTab,"Home");
        pageViewAdapter.addFragment(appointmentRequestsFragmentTab,"Requests");
        pageViewAdapter.addFragment(doctorsFragmentTab,"Doctors");
        pageViewAdapter.addFragment(departmentsFragmentTab,"Departments");
        pageViewAdapter.addFragment(notificationFragmentTab,"Notifications");
        pageViewAdapter.addFragment(historyFragment,"Appointments History");
        viewPager.setAdapter(pageViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search Here!");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               // doctorAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_profile:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                SharedPref.deleteLoginUserData(this);
                //startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
        }
        return true;
    }
}
