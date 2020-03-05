package com.carldroid.taskmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private Button btn_login;

    private TextView sign_up;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        sign_up = findViewById(R.id.signup_txv);
        email = findViewById(R.id.email_login);
        pass = findViewById(R.id.password_login);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPass = pass.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Required Field..");
                    return;
                }
                if (TextUtils.isEmpty(mPass)) {
                    pass.setError("Required Field..");
                    return;
                }

                mDialog.setTitle("Processing..");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "A problem occurred..", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }

                });
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
    }
}
