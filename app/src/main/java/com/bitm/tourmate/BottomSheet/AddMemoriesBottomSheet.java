package com.bitm.tourmate.BottomSheet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bitm.tourmate.Activity.MainActivity;
import com.bitm.tourmate.Activity.MemoryActivity;
import com.bitm.tourmate.Activity.SignUpActivity;
import com.bitm.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class AddMemoriesBottomSheet extends BottomSheetDialogFragment {

    private EditText captionET;
    private Button savememoryBTN, cameraBTN, galleryBTN;
    private ImageView memoryIV;

    private String tripId, userId;

    private String downloadLink;

    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Uri uri;
    private StorageReference storageReference;

    private int checkImage = 0;
    int check=0;

    public AddMemoriesBottomSheet(String tripId) {
        this.tripId = tripId;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.bottom_sheet_add_memories, container, false);

        init(view);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
        //Toast.makeText(getContext(), "" + userId, Toast.LENGTH_SHORT).show();

        dataSet();

        return view;


    }

    private void dataSet() {
        cameraBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getContext(), "Developing", Toast.LENGTH_SHORT).show();

               getImageFromCamera();
                check=1;

            }
        });

        galleryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getImageFromGallery();
                check=2;

            }
        });

        savememoryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String caption = captionET.getText().toString();
                if (checkImage == 1 && !caption.equals("")) {

                    progress = new ProgressDialog(getContext());
                    progress.setMessage("Please Wait");
                    progress.setTitle("Add Memory");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.show();
                    progress.setCancelable(true);

                    if (check == 1) {
                        memoryIV.setDrawingCacheEnabled(true);
                        memoryIV.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) memoryIV.getDrawable()).getBitmap();

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

                                        final String downloadLink1 = String.valueOf(uri);


                                        DatabaseReference tripData = databaseReference.child("users").child(userId).child("Trips").child(tripId);
                                        String memoreyId = tripData.push().getKey();
                                        //  Toast.makeText(getContext(), "111" + downloadLink, Toast.LENGTH_SHORT).show();

                                        HashMap<String, Object> memorey = new HashMap<>();
                                        memorey.put("caption", caption);

                                        memorey.put("picture", downloadLink1);
                                        memorey.put("memoreyId", memoreyId);
                                        memorey.put("tripId", tripId);

                                        tripData.child("Image").child(memoreyId).setValue(memorey).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    progress.setMessage("Successfully Add Trip");
                                                    //Toast.makeText(getContext(), "Successfully Add Trip", Toast.LENGTH_SHORT).show();


                                                    Intent intent = new Intent(getContext(), MemoryActivity.class);

                                                    intent.putExtra("tripId", tripId);

                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    progress.dismiss();
                                                    startActivity(intent);

                                                    //    startActivity(new Intent(getContext(),MemoryActivity.class));

                                                } else {
                                                    progress.dismiss();
                                                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

//
                                    }
                                });


                            }
                        });

//
                    } else {



                    final StorageReference imageRef = storageReference.child("image" + uri.getLastPathSegment());

                    imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadLink = String.valueOf(uri);

                                    //Toast.makeText(getContext(), "Sto" + downloadLink, Toast.LENGTH_SHORT).show();


                                    DatabaseReference tripData = databaseReference.child("users").child(userId).child("Trips").child(tripId);
                                    String memoreyId = tripData.push().getKey();
                                    //  Toast.makeText(getContext(), "111" + downloadLink, Toast.LENGTH_SHORT).show();

                                    HashMap<String, Object> memorey = new HashMap<>();
                                    memorey.put("caption", caption);

                                    memorey.put("picture", downloadLink);
                                    memorey.put("memoreyId", memoreyId);
                                    memorey.put("tripId", tripId);

                                    tripData.child("Image").child(memoreyId).setValue(memorey).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                progress.setMessage("Successfully Add Trip");
                                                //Toast.makeText(getContext(), "Successfully Add Trip", Toast.LENGTH_SHORT).show();


                                                Intent intent = new Intent(getContext(), MemoryActivity.class);

                                                intent.putExtra("tripId", tripId);

                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                progress.dismiss();
                                                startActivity(intent);

                                                //    startActivity(new Intent(getContext(),MemoryActivity.class));

                                            } else {
                                                progress.dismiss();
                                                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });


                        }
                    });
                }

                } else {
                    Toast.makeText(getContext(), "Something is Empty", Toast.LENGTH_SHORT).show();
                }

///


            }
        });

    }


    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 0);


    }

    private void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 0) {
                uri = data.getData();
                memoryIV.setImageURI(uri);
               // Toast.makeText(getContext(), "" + uri, Toast.LENGTH_SHORT).show();
                checkImage = 1;

            } else if (requestCode == 1) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");

                memoryIV.setImageBitmap(bitmap);
                checkImage = 1;
            }
        }

    }


    private void init(View view) {
        downloadLink = null;
        captionET = view.findViewById(R.id.captionET);
        savememoryBTN = view.findViewById(R.id.savetripBtn);
        cameraBTN = view.findViewById(R.id.cameraBTN);
        galleryBTN = view.findViewById(R.id.galleryBTN);
        memoryIV = view.findViewById(R.id.memoryIV);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

    }


}
