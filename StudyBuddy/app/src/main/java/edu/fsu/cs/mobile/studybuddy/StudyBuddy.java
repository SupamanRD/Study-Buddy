package edu.fsu.cs.mobile.studybuddy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class StudyBuddy extends AppCompatActivity {

    private BottomNavigationView nav;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_messages:
                    MessagesFragment mess = new MessagesFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, mess)
                            .commit();

                    return true;

                case R.id.navigation_map:
                    MapFragment map = new MapFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, map)
                            .commit();

                    return true;

                case R.id.navigation_study:
                    MainFragment main = new MainFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, main)
                            .commit();

                    return true;

                case R.id.navigation_classes:
                    ClassesFragment classes = new ClassesFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, classes)
                            .commit();
                    return true;

                case R.id.navigation_menu:
                    MenuFragment menu = new MenuFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, menu)
                            .commit();
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_buddy);

        nav = findViewById(R.id.navigation);
        nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //set up main fragment first

        MainFragment frag = new MainFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, frag)
                .addToBackStack(MainFragment.TAG)
                .commit();



    }

}
