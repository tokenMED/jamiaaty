package com.example.jamiaaty;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jamiaaty.Model.All_UserMemeber;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class CreateProfile extends AppCompatActivity {

    EditText etname,etBio,etProfession,etEmail,etWeb;
    Button button;
    ImageView imageView;
    ProgressBar progressBar;

    Uri imageUri = null;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String currentUserId;
    private  static final int PICK_IMAGE = 1;
    All_UserMemeber member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        member = new All_UserMemeber();
        etBio = findViewById(R.id.et_bio_cp);
        etEmail = findViewById(R.id.et_email_cp);
        etname = findViewById(R.id.et_name_cp);
        etProfession = findViewById(R.id.et_Profession_cp);
        etWeb = findViewById(R.id.et_website_cp);
        button = findViewById(R.id.btn_cp);
        progressBar = findViewById(R.id.progrssbar_cp);
        imageView = findViewById(R.id.iv_cp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        //For FireStore (Cloud firebase)
        documentReference = db.collection("user").document(currentUserId);
        //For Storage
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");

        //For run time data base
        databaseReference = database.getReference("All Users");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if(requestCode == PICK_IMAGE || requestCode == RESULT_OK || data !=null || data.getData() != null) {
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(imageView);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error" +e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


    private  String getFileExtention(Uri uri){
            if( uri == null) return  "";
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return  mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private  void saveObjectTofireBase(){

    }

    private void uploadData() {

        String name = etname.getText().toString();
        String bio = etBio.getText().toString();
        String email = etEmail.getText().toString();
        String prof = etProfession.getText().toString();
        String web = etWeb.getText().toString();

        if(!TextUtils.isEmpty(name) ||!TextUtils.isEmpty(bio) ||!TextUtils.isEmpty(email) ||!TextUtils.isEmpty(prof) ||!TextUtils.isEmpty(web) ){
            progressBar.setVisibility(View.VISIBLE);
            Map<String, String> profile = new HashMap<>();
            if(imageUri != null) {
                final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(imageUri));
                uploadTask = reference.putFile(imageUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {

                            throw task.getException();
                        }
                        return reference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri dowloadUri = task.getResult();
                            member.setUrl(dowloadUri.toString());
                            profile.put("url", dowloadUri.toString());
                            profile.put("name", name);
                            profile.put("prof", prof);
                            profile.put("email", email);
                            profile.put("uid", currentUserId);
                            profile.put("web", web);
                            profile.put("bio", bio);
                            profile.put("privacy", "Public");


                            member.setName(name);
                            member.setProf(prof);
                            member.setUid(currentUserId);


                            databaseReference.child(currentUserId).setValue(member);
                            documentReference.set(profile)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 2000);
                                        }
                                    });

                        }
                    }
                });
            }else {
                profile.put("url", "");
                member.setUrl("");
                profile.put("name", name);
                profile.put("prof", prof);
                profile.put("email", email);
                profile.put("uid", currentUserId);
                profile.put("web", web);
                profile.put("bio", bio);
                profile.put("privacy", "Public");


                member.setName(name);
                member.setProf(prof);
                member.setUid(currentUserId);


                databaseReference.child(currentUserId).setValue(member);
                documentReference.set(profile)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            }
                        });
            }



        }else{
            Toast.makeText(getApplicationContext(), "Please fill all Fields", Toast.LENGTH_SHORT).show();
        }
    }
}