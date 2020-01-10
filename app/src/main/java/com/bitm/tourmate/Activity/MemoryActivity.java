package com.bitm.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bitm.tourmate.Activity.MainActivity;
import com.bitm.tourmate.Adapter.MemoryAdapter;
import com.bitm.tourmate.BottomSheet.AddMemoriesBottomSheet;
import com.bitm.tourmate.Model_Class.Memory;
import com.bitm.tourmate.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MemoryActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private String tripId, userId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Uri uri;
    private StorageReference storageReference;
    List<Memory> memoryList;
    RecyclerView recyclerView;
    MemoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        setTitle("Memory");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
        //Toast.makeText(this, "" + userId, Toast.LENGTH_SHORT).show();


        getMemorey();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMemoriesBottomSheet addMemoriesBottomSheet = new AddMemoriesBottomSheet(tripId);
                addMemoriesBottomSheet.show(getSupportFragmentManager(), "Bottom Sheet");


            }
        });


    }


    private void getMemorey() {
        memoryList.clear();
        DatabaseReference dataRef = databaseReference.child("users").child(userId).child("Trips").child(tripId).child("Image");
        //Toast.makeText(this, ""+tripId, Toast.LENGTH_SHORT).show();

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        //   Toast.makeText(getContext(), "Data"+data, Toast.LENGTH_SHORT).show();
                        Memory memory = data.getValue(Memory.class);
                        memoryList.add(memory);
                        adapter.notifyDataSetChanged();

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void init() {
        fab = findViewById(R.id.fab);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        recyclerView = findViewById(R.id.memorylistrecycleView);
        recyclerView.setHasFixedSize(true);
        memoryList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MemoryAdapter(memoryList, this);
        recyclerView.setAdapter(adapter);
        tripId = getIntent().getStringExtra("tripId");
       // Toast.makeText(this, "11 "+tripId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
      Intent intent=new  Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
