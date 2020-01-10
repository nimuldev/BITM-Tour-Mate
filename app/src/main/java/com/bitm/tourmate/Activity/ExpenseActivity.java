package com.bitm.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitm.tourmate.Adapter.ExpanseAdapter;
import com.bitm.tourmate.Adapter.MemoryAdapter;
import com.bitm.tourmate.BottomSheet.AddTripExpenseBottomSheet;
import com.bitm.tourmate.Model_Class.Expanse;
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

import java.util.ArrayList;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    String userId,tripId;
    List<Expanse> expanseList;
    RecyclerView recyclerView;
    ExpanseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        setTitle("Expense");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        tripId = getIntent().getStringExtra("tripId");
       // Toast.makeText(this, ""+tripId, Toast.LENGTH_SHORT).show();
        fab=findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTripExpenseBottomSheet addTripExpenseBottomSheet = new AddTripExpenseBottomSheet(tripId);
                addTripExpenseBottomSheet.show(getSupportFragmentManager(), "Bottom Sheet");

            }
        });

        getExpanseData();

    }

    private void getExpanseData() {

        expanseList.clear();
        DatabaseReference dataRef = databaseReference.child("users").child(userId).child("Trips").child(tripId).child("Expanse");

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        //   Toast.makeText(getContext(), "Data"+data, Toast.LENGTH_SHORT).show();
                        Expanse expanse = data.getValue(Expanse.class);
                       expanseList.add(expanse);
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

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
       // Toast.makeText(this, "" + userId, Toast.LENGTH_SHORT).show();

        tripId = getIntent().getStringExtra("tripId");


        recyclerView = findViewById(R.id.expenselistrecycleView);
        recyclerView.setHasFixedSize(true);
        expanseList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpanseAdapter(expanseList,this);
        recyclerView.setAdapter(adapter);

    }
}
