package com.britannio.moments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * App starts here then moves to the login activity if no account is present
 */

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //setting up the toolbar
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Moments+");
    }

    @Override
    public void onStart() {
        super.onStart();

        // Send user to the Login Activity if they aren't logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { // user isn't logged in

            sendToLogin();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //shows the toolbar menu

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //invoked when a menu item is pressed

        switch (item.getItemId()) {

            case R.id.action_logout_btn:

                logout();

                return true;
            case R.id.action_settings_btn:
                //TODO temporary
                Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);
            default:
                return false;
        }

    }

    private void sendToLogin() {
        //Move to the Main Activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private void logout() {

        mAuth.signOut();
        sendToLogin();

    }
}
