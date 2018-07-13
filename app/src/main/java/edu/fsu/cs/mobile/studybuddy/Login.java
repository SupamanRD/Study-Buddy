package edu.fsu.cs.mobile.studybuddy;

import android.app.Activity;
        import android.os.Bundle;
        import android.view.View;

        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;


public class Login extends Activity  {
    Button b1;
    EditText ed1,ed2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        b1 = findViewById(R.id.logButton);
        ed1 = findViewById(R.id.logEmail);
        ed2 = findViewById(R.id.logPass);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed1.getText().toString().equals("temp") &&
                        ed2.getText().toString().equals("temp")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
