package com.britannio.moments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout reg_email;
    private TextInputLayout reg_pass;
    private TextInputLayout reg_confirm_pass;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar reg_progress;
    private String TAG;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        reg_email = findViewById(R.id.reg_email);
        reg_pass = findViewById(R.id.reg_password);
        reg_confirm_pass = findViewById(R.id.reg_confirm_pass);
        reg_btn = findViewById(R.id.reg_btn);
        reg_login_btn = findViewById(R.id.reg_login);
        reg_progress = findViewById(R.id.reg_progress);
        TAG = "Regiser Activity";

        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish(); //Moves to the login page

            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regEmail = reg_email.getEditText().getText().toString();
                String regPass = reg_pass.getEditText().getText().toString();
                String regConfirmPass = reg_confirm_pass.getEditText().getText().toString();

                if (!TextUtils.isEmpty(regEmail) && !TextUtils.isEmpty(regPass) && !TextUtils.isEmpty(regConfirmPass)) {

                    if (regPass.equals(regConfirmPass)) {

                        reg_progress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(regEmail, regPass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        reg_progress.setVisibility(View.INVISIBLE);

                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");

                                            Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                            startActivity(setupIntent);

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                        // ...
                                    }
                                });

                    } else {

                        Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_LONG).show();

                    }

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            sendToMain();

        }
    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
}
