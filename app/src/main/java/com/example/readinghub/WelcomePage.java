package com.example.readinghub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class WelcomePage extends AppCompatActivity {
    EditText ETname;
    EditText ETprogName;
    Button getStartedBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferencesHelper sph = new SharedPreferencesHelper(this);
        getStartedBtn = findViewById(R.id.getStartedBtn);
        ETname = findViewById(R.id.userName);
        ETprogName = findViewById(R.id.programName);
        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ETname.getText().toString().equals("") || ETprogName.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please complete the fields",Toast.LENGTH_SHORT).show();
                }
                else{
                    sph.SaveUserInfoString("userName",ETname.getText().toString());
                    sph.SaveUserInfoString("programName",ETprogName.getText().toString());

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}