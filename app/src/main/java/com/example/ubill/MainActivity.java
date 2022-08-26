package com.example.ubill;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button loginregister;
    CheckBox confirm;
    Button rateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginregister=findViewById(R.id.startbutton);
        confirm=findViewById(R.id.checkBox);
        rateButton=findViewById(R.id.rateButton);
        loginregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirm.isChecked()){
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
            else{
                    Toast.makeText(MainActivity.this,"Please agree to continue!", Toast.LENGTH_SHORT).show();
            }
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Thank you for valuable rating!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}