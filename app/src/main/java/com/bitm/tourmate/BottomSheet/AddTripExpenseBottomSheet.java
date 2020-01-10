package com.bitm.tourmate.BottomSheet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bitm.tourmate.Activity.ExpenseActivity;
import com.bitm.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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

public class AddTripExpenseBottomSheet extends BottomSheetDialogFragment {

    private EditText amountEt, dateEt, timeEt, descriptionEt;
    private Spinner expanceTypeSP;
    private Button saveBtn, cancelBtn;
    private String[] expaceTypesArrey;
    ArrayAdapter expanseAdapter;
    private ProgressDialog progress;
    private String amount, date, time, description, expanceType;

    String tripId, userId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public AddTripExpenseBottomSheet(String tripId) {
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_expense_custom_dialog, container, false);

        init(view);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
               /// Toast.makeText(getContext(), "" + userId, Toast.LENGTH_SHORT).show();
               // Toast.makeText(getContext(), "" + tripId , Toast.LENGTH_SHORT).show();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ExpenseActivity.class);

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
        return view;


    }

    private void openTimePicker() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.custom_time_picker,null);

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

                Time time = new Time(hour,min,0);

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
                        String currentDate = year+"/"+month+"/"+ day;
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),dateSetListener,year,month,day);
        datePickerDialog.show();
    }


    private void getData() {


        amount = amountEt.getText().toString();
        date = dateEt.getText().toString();
        time = timeEt.getText().toString();
        description = descriptionEt.getText().toString();
        expanceType = expanceTypeSP.getSelectedItem().toString();


        if (!amount.equals("") && !date.equals("") && !time.equals("") && !description.equals("") && !expanceType.equals("Select Expanse")) {

            progress=new ProgressDialog(getContext());
            progress.setMessage("Please Wait");
            progress.setTitle("Add Expanse");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            progress.setCancelable(true);
            DatabaseReference tripData = databaseReference.child("users").child(userId).child("Trips").child(tripId);
            String expenseId = tripData.push().getKey();

            HashMap<String, Object> expanse = new HashMap<>();
            expanse.put("amount", amount);

            expanse.put("date", date);
            expanse.put("time", time);
            expanse.put("description", description);
            expanse.put("expanceType", expanceType);
            expanse.put("expenseId", expenseId);
            expanse.put("tripId", tripId);

            tripData.child("Expanse").child(expenseId).setValue(expanse).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                       // Toast.makeText(getContext(), "Successfully Add Expanse", Toast.LENGTH_SHORT).show();
                        progress.setMessage("Successfully Add Expanse");
                        Intent intent = new Intent(getContext(), ExpenseActivity.class);


                        intent.putExtra("tripId", tripId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        progress.dismiss();
                        startActivity(intent);

                        //    startActivity(new Intent(getContext(),MemoryActivity.class));

                    } else {
                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Something is empty", Toast.LENGTH_SHORT).show();
        }

///
    }

    private void init(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        amountEt = view.findViewById(R.id.amountET);
        dateEt = view.findViewById(R.id.dateET);
        dateEt.setFocusable(false);
        timeEt = view.findViewById(R.id.timeET);
        timeEt.setFocusable(false);
        descriptionEt = view.findViewById(R.id.descriptionET);
        saveBtn = view.findViewById(R.id.saveBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        expanceTypeSP = view.findViewById(R.id.expansetypeSP);
        expaceTypesArrey = getResources().getStringArray(R.array.cost_arrays);
        expanseAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, expaceTypesArrey);
        expanseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expanceTypeSP.setAdapter(expanseAdapter);
    }


}
