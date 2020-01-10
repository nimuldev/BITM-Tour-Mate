package com.bitm.tourmate.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitm.tourmate.Adapter.TripAdapter;
import com.bitm.tourmate.BottomSheet.AddTripBottomSheet;
import com.bitm.tourmate.Model_Class.Trip;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class TripsFragment extends Fragment {

    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userId;
    private StorageReference storageReference;
    List<Trip> tripList;
    RecyclerView recyclerView;
    TripAdapter adapter;
    public TripsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trips, container, false);



        init(view);
        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddTripBottomSheet addTripBottomSheet = new AddTripBottomSheet();
                addTripBottomSheet.show(getFragmentManager(), "Bottom Sheet");
                trips();
            }
        });
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
       /// Toast.makeText(getContext(), "" + userId, Toast.LENGTH_SHORT).show();

        trips();




        return view;
    }

    private void init(View view) {
        tripList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        recyclerView = view.findViewById(R.id.triplistrecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TripAdapter(tripList,getContext());
        recyclerView.setAdapter(adapter);


    }

    private void trips() {
        tripList.clear();
        DatabaseReference dataRef = databaseReference.child("users").child(userId).child("Trips");

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot data: dataSnapshot.getChildren()){
                     //   Toast.makeText(getContext(), "Data"+data, Toast.LENGTH_SHORT).show();
                        Trip trip = data.getValue(Trip.class);
                        tripList.add(trip);
                        adapter.notifyDataSetChanged();

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
