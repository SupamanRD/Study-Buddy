package edu.fsu.cs.mobile.studybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartScreen extends AppCompatActivity {

    private Button login;
    private Button signup;
    private Button main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        // this will eventually be taken out
        main = findViewById(R.id.main);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to login activity
                //startActivity(new Intent(StartScreen.this, ______.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to signup activity
                //startActivity(new Intent(StartScreen.this, ______.class));
            }
        });

        //will be removed eventually
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartScreen.this, StudyBuddy.class));
            }
        });

    }
}
