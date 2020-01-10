package com.bitm.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.bitm.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TripDetailsActivity extends AppCompatActivity {

    private Button edittourBTN;
    private EditText tourtitleTV, tourlocationTV, startdateTV, enddateTV, tourdescriptionTV;
    String tripId, userId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
        private int check=0;
    private ProgressDialog progress;

    private String  tripName, description, startDate, endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        setTitle("Trip Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        getTourDeteilsFromList();
        if(check==1){

           edittourBTN.setVisibility(View.VISIBLE);
            tourtitleTV.setEnabled(true);
            tourdescriptionTV.setEnabled(true);

            startdateTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateStart();
                }
            });
            enddateTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateEnd();
                }
            });
            edittourBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progress=new ProgressDialog(TripDetailsActivity.this);
                    progress.setMessage("Please Wait");
                    progress.setTitle("Updating");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.show();
                    progress.setCancelable(true);
                    getData();
                    tourDeteilsUpdate();
                }
            });

        }




    }

    private void getData() {
        tripName=tourtitleTV.getText().toString();
        description=tourdescriptionTV.getText().toString();
        startDate=startdateTV.getText().toString();
        endDate=enddateTV.getText().toString();


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


                enddateTV.setText(dateFormat.format(date));


            }
        };

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
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


                startdateTV.setText(dateFormat.format(date));


            }
        };

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();

    }

    private void getTourDeteilsFromList() {
        tripName=getIntent().getStringExtra("tripName");
      //  Toast.makeText(this, "name "+tripName, Toast.LENGTH_SHORT).show();
        startDate=getIntent().getStringExtra("fromDate");
        endDate=getIntent().getStringExtra("Todate");
        description=getIntent().getStringExtra("Description");
         tripId = getIntent().getStringExtra("tripId");
      //  Toast.makeText(this, "name "+tripId, Toast.LENGTH_SHORT).show();
       // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        tourtitleTV.setText(tripName);
        startdateTV.setText(startDate);
        enddateTV.setText(endDate);
        tourdescriptionTV.setText(description);
    }

    private void tourDeteilsUpdate() {
        //Toast.makeText(this, ""+tripId, Toast.LENGTH_SHORT).show();
       // Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();

        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("Trips").child(tripId);


        HashMap<String, Object> tripInfo = new HashMap<>();
        tripInfo.put("tripName", tripName);
        tripInfo.put("description", description);
        tripInfo.put("startDate", startDate);
        tripInfo.put("endDate", endDate);
        tripInfo.put("tripId", tripId);


        dataRef.updateChildren(tripInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(TripDetailsActivity.this, "Successfully Add Trip", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TripDetailsActivity.this, MainActivity.class);
                    intent.putExtra("tripId",tripId);
                   startActivity(intent);
                    progress.dismiss();

                } else {
                    progress.dismiss();
                    Toast.makeText(TripDetailsActivity.this,"Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void init() {


        edittourBTN = findViewById(R.id.editTourBtn);
        edittourBTN.setVisibility(View.GONE);
        tourtitleTV = findViewById(R.id.tourTitleTV);
        tourtitleTV.setEnabled(false);
        startdateTV = findViewById(R.id.startDateTV);
        startdateTV.setFocusable(false);

        enddateTV = findViewById(R.id.endDateTV);
        enddateTV.setFocusable(false);
        tourdescriptionTV = findViewById(R.id.tourDescriptionTV);
        tourdescriptionTV.setEnabled(false);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

     //   Toast.makeText(this, "11 "+tripId, Toast.LENGTH_SHORT).show();
        check = getIntent().getIntExtra("check",0);
     //   Toast.makeText(this, ""+check, Toast.LENGTH_SHORT).show();
        if (check==1){
            tourtitleTV.setEnabled(true);
            tourdescriptionTV.setEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {


        Intent intent = new Intent(this,TripDetailsActivity.class);
        intent.putExtra("tripId",tripId);
        intent.putExtra("check",1);

        intent.putExtra("tripName",tripName);
        intent.putExtra("fromDate",startDate);
        intent.putExtra("Todate",endDate);
        intent.putExtra("Description",description);


        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }


    }

