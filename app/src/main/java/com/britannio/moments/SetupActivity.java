package com.britannio.moments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class SetupActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar setupToolbar;
    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private TextInputLayout setupName;
    private Button setupBtn;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth; //needed to get the user id

    private ProgressBar setupProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setupToolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = setupName.getEditText().getText().toString();

                if (!(TextUtils.isEmpty(username) && mainImageURI != null)) {
                    //check if text is in the username field and an image was added

                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    setupProgress.setVisibility(View.VISIBLE);

                    StorageReference image_path = storageReference
                            .child("profile_images")
                            .child(user_id + ".jpg");

                    image_path.putFile(mainImageURI)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                            {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                Uri download_url = task.getResult().getDownloadUrl();
                                Log.i("SetupActivity", "Image Uploaded");

                            } else {

                                String error = task.getException().getMessage();
                                Log.e("SetupActivity", error);

                            }

                            setupProgress.setVisibility(View.INVISIBLE);

                        }
                    });

                } else {

                    Log.i("SetupActivity", "Image or name missing");

                }

            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() { //changing profile pictore
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toasty.info(getApplicationContext(),"Permission Denied",
                                Toast.LENGTH_LONG, true).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1) // forces a 1:1 ratio
                                .start(SetupActivity.this);

                    }
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }


}
