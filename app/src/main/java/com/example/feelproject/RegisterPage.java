package com.example.feelproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class RegisterPage extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword, mPhone;
    Button mRegisterButton;
    TextView mLoginButton;
    ProgressBar mprogressBar;
    public static final String TAG = "TAG";

    FirebaseFirestore fstore;
    String userID;
    FirebaseAuth fAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        mFullName = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);

        mRegisterButton = findViewById(R.id.registerButton);
        mLoginButton = findViewById(R.id.createtext);
        mprogressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent( getApplicationContext(), MainActivity.class));
            finish();
        }

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              final String email= mEmail.getText().toString().trim();
              String password = mPassword.getText().toString().trim();
              final String fullName = mFullName.getText().toString();
              final String phone = mPhone.getText().toString();
              if(TextUtils.isEmpty(email)){
                  mEmail.setError("Email is Required");
                  return;
              }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;
                }
                if(TextUtils.isEmpty(fullName)){
                    mFullName.setError("Name is Required");
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    mPhone.setError("Phone is Required");
                    return;
                }
                if(password.length() < 6){
                    mPassword.setError("Password must be >= 6 character");
                    return;
                }
                mprogressBar.setVisibility(View.VISIBLE);


                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "OnFailure : Email Not Sent"+e.getMessage());
                                }
                            });

                            Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_LONG);
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("user").document(userID);
                            HashMap<String, Object> user = new HashMap<>();
                            user.put("fName", fullName);
                            user.put("email", email);
                            user.put("phone", phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess : user Profile is Created "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure "+ e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        }
                        else{
                            Toast.makeText(RegisterPage.this, "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            mprogressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });


    }
}