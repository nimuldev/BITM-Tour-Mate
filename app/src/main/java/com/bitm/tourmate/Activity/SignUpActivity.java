package com.bitm.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bitm.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout firstnameET, lastnameET, emailET, passwordET, confirmpasswordET;
    private ImageView userIV;
    private Button signupBTN;
    private String firstName, lastName, password, confirmPassword, email;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Uri uri;
    private Bitmap bitmap;
    private StorageReference storageReference;
    //private String downloadLink = null;
    private int checkImage = 0;
    private ProgressDialog progress;
    String     downloadLinks;
    int check=0;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        init();


        signupBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = firstnameET.getEditText().getText().toString();
                lastName = lastnameET.getEditText().getText().toString();
                email = emailET.getEditText().getText().toString().trim();
                password = passwordET.getEditText().getText().toString();
                confirmPassword = confirmpasswordET.getEditText().getText().toString();


                if (!firstName.equals("") && !lastName.equals("") && !email.equals("") && !password.equals("") && password.equals(confirmPassword)) {


                    if (checkImage == 1) {

                        if(email.matches(emailPattern) && email.length() > 0){
                            progress = new ProgressDialog(SignUpActivity.this);
                            progress.setMessage("Please Wait");
                            progress.setTitle("Sign Up");
                            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progress.setIndeterminate(true);
                            progress.show();
                            progress.setCancelable(true);


                            signUpFromGallary(firstName, lastName, email, password);
                        }
                        else {
                            Toast.makeText(SignUpActivity.this,"Invalid email address",Toast.LENGTH_SHORT).show();

                        }
                    }

                    else {
                        if(email.matches(emailPattern) && email.length() > 0){
                            progress = new ProgressDialog(SignUpActivity.this);
                            progress.setMessage("Please Wait");
                            progress.setTitle("Sign Up");
                            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progress.setIndeterminate(true);
                            progress.show();
                            progress.setCancelable(true);


                            signUp(firstName, lastName, email, password);
                        }
                        else {
                            Toast.makeText(SignUpActivity.this,"Invalid email address",Toast.LENGTH_SHORT).show();

                        }
                    }

                } else {
                    Toast.makeText(SignUpActivity.this, "Something is going Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();

            }
        });
    }

    private void getImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        View view = getLayoutInflater().inflate(R.layout.picture, null);
        Button cameraBtn = view.findViewById(R.id.cameraBTN);
        Button gallaryBtn = view.findViewById(R.id.galleryBTN);


        builder.setView(view);

        final Dialog dialog = builder.create();
        dialog.show();

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromCamera();
                check=1;
                //Toast.makeText(SignUpActivity.this, "Developing", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        gallaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
                check=2;
                dialog.dismiss();
            }
        });


    }

    private void getImageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }




    public void clear() {
        firstnameET.getEditText().setText("");
        lastnameET.getEditText().setText("");
        passwordET.getEditText().setText("");
        confirmpasswordET.getEditText().setText("");
        emailET.getEditText().setText("");
        userIV.setImageBitmap(null);
        checkImage = 0;

    }


    private void getImageFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 0) {
                uri = data.getData();
                userIV.setImageURI(uri);
                checkImage = 1;
            } else if (requestCode == 1) {

                Bundle bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                userIV.setImageBitmap(bitmap);
                checkImage = 1;

            }
        }


    }
public void signUp(final String firstName, final String lastName, final String email, final String password){

    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                String id = firebaseAuth.getCurrentUser().getUid();


                DatabaseReference dataRef = databaseReference.child("users").child(id);

                HashMap<String, Object> userInfo = new HashMap<>();
                userInfo.put("firstName", firstName);
                userInfo.put("lastName", lastName);
                userInfo.put("email", email);
               // userInfo.put("picture", downloadLink);
                userInfo.put("uid", id);

                dataRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            clear();
                            progress.setMessage("Sign Up Successfully Complete");
                            progress.dismiss();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(SignUpActivity.this, " Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else {
                Toast.makeText(SignUpActivity.this, "SignUp went to wrong", Toast.LENGTH_SHORT).show();
            }
        }
    });



}


    public void signUpFromCamera(final String firstName, final String lastName, final String email, final String password) {

        // Get the data from an ImageView as bytes
        userIV.setDrawingCacheEnabled(true);
        userIV.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) userIV.getDrawable()).getBitmap();

        //   Uri fileUri = Uri.fromFile(new File(currentPhotoPath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference imgRef =
                storageReference.child("moment" + UUID.randomUUID());

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                    final String    downloadLink = String.valueOf(uri);
                        Picasso.get().load(downloadLink).into(userIV);


                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String id = firebaseAuth.getCurrentUser().getUid();


                                    DatabaseReference dataRef = databaseReference.child("users").child(id);

                                    HashMap<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("firstName", firstName);
                                    userInfo.put("lastName", lastName);
                                    userInfo.put("email", email);
                                    userInfo.put("picture", downloadLink);
                                    userInfo.put("uid", id);

                                    dataRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                clear();
                                                progress.setMessage("Sign Up Successfully Complete");
                                                progress.dismiss();
                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                            } else {
                                                Toast.makeText(SignUpActivity.this, " Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                } else {
                                    Toast.makeText(SignUpActivity.this, "SignUp went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                });


            }
        });

    }



    private void signUpFromGallary(final String firstName, final String lastName, final String email, final String password) {


        if (check == 1) {

            userIV.setDrawingCacheEnabled(true);
            userIV.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) userIV.getDrawable()).getBitmap();

            //   Uri fileUri = Uri.fromFile(new File(currentPhotoPath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference imgRef =
                    storageReference.child("moment" + UUID.randomUUID());

            UploadTask uploadTask = imgRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            final String downloadLink = String.valueOf(uri);
                            Picasso.get().load(downloadLink).into(userIV);


                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String id = firebaseAuth.getCurrentUser().getUid();


                                        DatabaseReference dataRef = databaseReference.child("users").child(id);

                                        HashMap<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("firstName", firstName);
                                        userInfo.put("lastName", lastName);
                                        userInfo.put("email", email);
                                        userInfo.put("picture", downloadLink);
                                        userInfo.put("uid", id);

                                        dataRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    clear();
                                                    progress.setMessage("Sign Up Successfully Complete");
                                                    progress.dismiss();
                                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, " Something went wrong.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    } else {
                                        Toast.makeText(SignUpActivity.this, "SignUp went wrong.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });


                }
            });


        } else if (check == 2) {



        final StorageReference imageRefa = storageReference.child("image" + uri.getLastPathSegment());

        imageRefa.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRefa.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadLinks = String.valueOf(uri);
                      //  Toast.makeText(SignUpActivity.this, "" + downloadLinks, Toast.LENGTH_SHORT).show();


///
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String id = firebaseAuth.getCurrentUser().getUid();


                                    DatabaseReference dataRef = databaseReference.child("users").child(id);

                                    HashMap<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("firstName", firstName);
                                    userInfo.put("lastName", lastName);
                                    userInfo.put("email", email);
                                    userInfo.put("picture", downloadLinks);
                                    userInfo.put("uid", id);

                                    dataRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                clear();
                                                progress.setMessage("Sign Up Successfully Complete");
                                                progress.dismiss();
                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                            } else {
                                                Toast.makeText(SignUpActivity.this, " Something went wrong ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                } else {
                                    Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                });


            }
        });
    }
    }



    private void init() {

        firstnameET = findViewById(R.id.firstnameET);
        lastnameET = findViewById(R.id.lastnameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        confirmpasswordET = findViewById(R.id.confirmpasswordET);
        userIV = findViewById(R.id.userIV);
        signupBTN = findViewById(R.id.signupBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void SignUp(View view) {

        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));

    }
}
