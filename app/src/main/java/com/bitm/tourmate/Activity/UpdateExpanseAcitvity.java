package com.bitm.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bitm.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class UpdateExpanseAcitvity extends AppCompatActivity {
    private EditText amountEt, dateEt, timeEt, descriptionEt, expaseTyeEt;
    private Spinner expanceTypeSP;
    private Button saveBtn, cancelBtn;
    private String[] expaceTypesArrey;
    ArrayAdapter expanseAdapter;

    private String amount, date, time, description, expanceType;

    String tripId, userId, expanseId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_expanse_acitvity);
        setTitle("Update Expense");

        init();
        getDataFromAdapter();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
    //    Toast.makeText(this, "" + userId, Toast.LENGTH_SHORT).show();

        tripId = getIntent().getStringExtra("tripId");
        expanseId=getIntent().getStringExtra("expanseId");
     //   Toast.makeText(this, ""+expanseId, Toast.LENGTH_SHORT).show();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateExpanseAcitvity.this, ExpenseActivity.class);
                intent.putExtra("tripId", tripId);


                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();

            }
        });

        timeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker();
            }
        });
    }

    private void updateData() {

        expanceType = expaseTyeEt.getText().toString();

        if(!expanceTypeSP.getSelectedItem().toString().equals("Select Expanse")){
            expanceType = expanceTypeSP.getSelectedItem().toString();
            expaseTyeEt.setText(expanceType);
        }

        amount = amountEt.getText().toString();
        date = dateEt.getText().toString();
        time = timeEt.getText().toString();
        description = descriptionEt.getText().toString();


        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("Trips").child(tripId).child("Expanse").child(expanseId);

        HashMap<String, Object> UpdateExpanse = new HashMap<>();
        UpdateExpanse.put("amount", amount);

        UpdateExpanse.put("date", date);
        UpdateExpanse.put("time", time);
        UpdateExpanse.put("description", description);
        UpdateExpanse.put("expanceType", expanceType);
        UpdateExpanse.put("expenseId", expanseId);
        UpdateExpanse.put("tripId", tripId);


        dataRef.updateChildren(UpdateExpanse).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(UpdateExpanseAcitvity.this, "Successfully Add Trip", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateExpanseAcitvity.this, ExpenseActivity.class);
                    intent.putExtra("tripId", tripId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    Toast.makeText(UpdateExpanseAcitvity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getDataFromAdapter() {


        tripId = getIntent().getStringExtra("tripId");
        expanseId=getIntent().getStringExtra("expanseId");

        expanceType = getIntent().getStringExtra("expanceType");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        amount = getIntent().getStringExtra("amount");
        description = getIntent().getStringExtra("description");

        expaseTyeEt.setText(expanceType);
        dateEt.setText(date);
        timeEt.setText(time);
        amountEt.setText(amount);
        descriptionEt.setText(description);


    }

    private void openTimePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_time_picker, null);

        Button doneBtn = view.findViewById(R.id.doneBtn);
        final TimePicker timePicker = view.findViewById(R.id.timePicker);

        builder.setView(view);

        final Dialog dialog = builder.create();
        dialog.show();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");

                @SuppressLint({"NewApi", "LocalSuppress"}) int hour = timePicker.getHour();
                @SuppressLint({"NewApi", "LocalSuppress"}) int min = timePicker.getMinute();

                Time time = new Time(hour, min, 0);

                timeEt.setText(timeFormat.format(time));
                dialog.dismiss();


            }
        });

    }

    private void openDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
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
                        dateEt.setText(dateFormat.format(date));


                    }
                };

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();

    }


    private void init() {

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        expaseTyeEt = findViewById(R.id.expanseET);
        amountEt = findViewById(R.id.amountET);
        dateEt = findViewById(R.id.dateET);
        dateEt.setFocusable(false);
        timeEt = findViewById(R.id.timeET);
        timeEt.setFocusable(false);
        descriptionEt = findViewById(R.id.descriptionET);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        expanceTypeSP = findViewById(R.id.expansetypeSP);
        expaceTypesArrey = getResources().getStringArray(R.array.cost_arrays);
        expanseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expaceTypesArrey);
        expanseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expanceTypeSP.setAdapter(expanseAdapter);
    }
}
