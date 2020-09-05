package com.example.hsquare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.denzcoskun.imageslider.ImageSlider;
import com.example.hsquare.Fragments.CartFragment;
import com.example.hsquare.Fragments.HomeFragment;
import com.example.hsquare.Fragments.LogoutFragment;
import com.example.hsquare.Fragments.ProfileFragment;
import com.example.hsquare.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;

public class HomeActivity extends AppCompatActivity implements LifecycleOwner {
    BottomNavigationView navigationView;
    ImageSlider imageSlider;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navigationView = findViewById(R.id.bottom_nav_view);
        imageSlider = findViewById(R.id.imageslider_home);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        navigationView.setSelectedItemId(R.id.nav_home);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_logout:
                        fragment = new LogoutFragment();
                        break;

                    case R.id.nav_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.nav_cart:
                        fragment = new CartFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

                return true;
            }
        });


    }
}