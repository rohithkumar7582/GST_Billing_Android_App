package com.example.ubill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.*;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    Button login, register;
    EditText email, password;
    FirebaseAuth mFirebaseAuth;
    public static final String BUSINESS_EMAIL = "business-email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CompanyRegister.class));
            }
        });


        mFirebaseAuth=FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailID=email.getText().toString();
                String pwd=password.getText().toString();
                if(emailID.isEmpty()){
                    email.setError("Please enter email");
                    email.requestFocus();
                    return;
                }
                if (pwd.isEmpty()){
                    password.setError("Please enter password");
                    password.requestFocus();
                    return;
                }

                mFirebaseAuth.signInWithEmailAndPassword(emailID,pwd)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this,"Logged In successfully !",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, BillsActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
}
