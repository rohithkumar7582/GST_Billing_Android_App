package com.example.ubill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class CompanyRegister extends AppCompatActivity {

    EditText businessName;
    EditText businessGST;
    EditText businessAddress;
    EditText businessContact;
    EditText newPass;
    EditText confirmPass;
    EditText businessEmail;
    Button registerButton;
    ProgressBar progressBar;
    FirebaseAuth mauth;

    public static final String SETUP_BUSINESS_NAME_KEY = "setup-business-name-key";
    public static final String SETUP_BUSINESS_ADDRESS_KEY = "setup-business-address-key";
    public static final String SETUP_BUSINESS_CONTACT_KEY = "setup-business-contact-key";
    public static final String SETUP_PASSWORD_KEY = "setup-password-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_setup);
        businessName = findViewById(R.id.companyname);
        businessGST = findViewById(R.id.companygstin);
        businessAddress = findViewById(R.id.companyaddress);
        businessContact = findViewById(R.id.companymobile);
        newPass = findViewById(R.id.setuppassword);
        confirmPass = findViewById(R.id.confirmpassword);
        businessEmail = findViewById(R.id.companyemail);
        progressBar = findViewById(R.id.progressBar);
        registerButton = findViewById(R.id.companyregisterbutton);
        mauth=FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCompany();
            }
        });
    }


    public void registerCompany() {
        final String businessNameValue = businessName.getText().toString();
        final String businessGSTValue = businessGST.getText().toString();
        final String businessAddressValue = businessAddress.getText().toString();
        final String businessContactValue = businessContact.getText().toString();
        final String newPassword = newPass.getText().toString();
        final String confirmPassword = confirmPass.getText().toString();
        final String businessEmailValue = businessEmail.getText().toString();

        if (businessNameValue.isEmpty()) {
            businessName.setError("Company Name Required");
            businessName.requestFocus();
            return;
        }
        if (businessGSTValue.isEmpty()) {
            businessGST.setError("Company GSTIN Required");
            businessGST.requestFocus();
            return;
        }
        if (businessAddressValue.isEmpty()) {
            businessAddress.setError("Company Address Required");
            businessAddress.requestFocus();
            return;
        }
        if (businessContactValue.isEmpty()) {
            businessContact.setError("Company Mobile Required");
            businessContact.requestFocus();
            return;
        }
        if (businessEmailValue.isEmpty()) {
            businessEmail.setError("Company Email Required");
            businessEmail.requestFocus();
            return;
        }
        if (newPassword.isEmpty()) {
            newPass.setError("Setup Password Required");
            newPass.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            confirmPass.setError("Confirm Password Required");
            confirmPass.requestFocus();
            return;
        }
        if (newPassword.equals(confirmPassword)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CompanyRegister.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SETUP_BUSINESS_NAME_KEY, businessNameValue);
            editor.putString(SETUP_BUSINESS_ADDRESS_KEY, businessAddressValue);
            editor.putString(SETUP_BUSINESS_CONTACT_KEY, businessContactValue);
            editor.putString(SETUP_PASSWORD_KEY, newPassword);
            editor.apply();

            progressBar.setVisibility(View.VISIBLE);
            mauth.createUserWithEmailAndPassword(businessEmailValue,newPassword)
                    .addOnCompleteListener(CompanyRegister.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Company business = new Company(businessNameValue,businessGSTValue,businessAddressValue,businessContactValue,businessEmailValue, newPassword);
                                FirebaseDatabase.getInstance().getReference("Companies")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(business).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(CompanyRegister.this,"Company has been registered successfully !",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.VISIBLE);
                                            startActivity(new Intent(CompanyRegister.this, LoginActivity.class));
                                        }
                                        else{
                                            Toast.makeText(CompanyRegister.this,"Company registration failed !",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(CompanyRegister.this,"Company Email already exists !",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
        else{
            Toast.makeText(CompanyRegister.this,"Passwords dont match!",Toast.LENGTH_SHORT).show();
        }
    }
}
