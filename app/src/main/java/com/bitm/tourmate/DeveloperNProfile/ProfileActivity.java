package com.bitm.tourmate.DeveloperNProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitm.tourmate.Model_Class.User;
import com.bitm.tourmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userId;
    private StorageReference storageReference;
    private ImageView userIv;
    private TextView firstNameTv, lastname, emailTv;
    private ProgressDialog progress;

    String lastnameP,firstnamPe,emailP,pictureP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


       /* progress=new ProgressDialog(this);
        progress.setMessage("Please Wait");
        progress.setTitle("LogIn ");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.show();
        progress.setCancelable(true);*/
        init();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        firstnamPe = getIntent().getStringExtra("first");
        lastnameP = getIntent().getStringExtra("last");
        emailP= getIntent().getStringExtra("email");
        pictureP = getIntent().getStringExtra("picture");
        getDataFromfirebase();

    }

    private void getDataFromfirebase() {
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    //for(DataSnapshot data: dataSnapshot.getChildren()) {

                        User user = dataSnapshot.getValue(User.class);
                        Picasso.get().load(user.getPicture()).into(userIv);
                        firstNameTv.setText(user.getFirstName());
                        lastname.setText(user.getLastName());
                        emailTv.setText(user.getEmail());
                   // }



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }

    private void init() {

        userIv = findViewById(R.id.userIV);
        firstNameTv = findViewById(R.id.fristNameTv);
        lastname = findViewById(R.id.lastNameTv);
        emailTv = findViewById(R.id.emailTv);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }
}
