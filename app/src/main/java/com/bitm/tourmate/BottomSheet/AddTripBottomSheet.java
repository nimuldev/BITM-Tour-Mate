package com.bitm.tourmate.BottomSheet;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bitm.tourmate.Activity.MainActivity;
import com.bitm.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddTripBottomSheet extends BottomSheetDialogFragment {

    private EditText tripnameET, tripdescriptionET, tripstartdateET, tripenddateET;
    private Button savetripBTN;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Uri uri;
    private StorageReference storageReference;
    private String userId, tripName, description, startDate, endDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.bottom_sheet_add_trip, container, false);


        init(view);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
        //Toast.makeText(getContext(), "" + userId, Toast.LENGTH_SHORT).show();

        savetripBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrip();
            }
        });

        tripstartdateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateStart();
            }
        });
        tripenddateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateEnd();
            }
        });


        return view;


    }

    private void saveTrip() {
        tripName = tripnameET.getText().toString();
        description = tripdescriptionET.getText().toString();
        startDate = tripstartdateET.getText().toString();
        endDate = tripenddateET.getText().toString();

        if (!tripName.equals("") && !description.equals("") && !startDate.equals("") && !endDate.equals("")) {


            DatabaseReference tripData = databaseReference.child("users").child(userId).child("Trips");
            String tripId = tripData.push().getKey();


            HashMap<String, Object> tripInfo = new HashMap<>();
            tripInfo.put("tripName", tripName);
            tripInfo.put("description", description);
            tripInfo.put("startDate", startDate);
            tripInfo.put("endDate", endDate);
            tripInfo.put("tripId", tripId);

         tripData.child(tripId).setValue(tripInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()) {
                     //Toast.makeText(getContext(), "Successfully Add Trip", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getContext(), MainActivity.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     startActivity(intent);

                 } else {
                     Toast.makeText(getContext(), " Something went wrong.", Toast.LENGTH_SHORT).show();
                 }
             }
         });



        } else {
            Toast.makeText(getContext(), "SomeThing is empty", Toast.LENGTH_SHORT).show();
        }

    }

    private void dateEnd() {


        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                String currentDate = year + "/" + month + "/" + day;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

                Date date = null;

                try {
                    date = dateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                tripenddateET.setText(dateFormat.format(date));


            }
        };

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
        datePickerDialog.show();

    }

    private void dateStart() {


        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                String currentDate = year + "/" + month + "/" + day;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

                Date date = null;

                try {
                    date = dateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                tripstartdateET.setText(dateFormat.format(date));


            }
        };

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
        datePickerDialog.show();

    }

    private void init(View view) {

        tripnameET = view.findViewById(R.id.tripnameET);
        tripdescriptionET = view.findViewById(R.id.tripdescriptionET);
        tripstartdateET = view.findViewById(R.id.tripstartdateET);
        tripenddateET = view.findViewById(R.id.tripenddateET);
        tripenddateET.setFocusable(false);
        tripstartdateET.setFocusable(false);
        savetripBTN = view.findViewById(R.id.savetripBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }


}
