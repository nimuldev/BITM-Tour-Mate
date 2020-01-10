package com.bitm.tourmate.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitm.tourmate.Activity.ExpenseActivity;
import com.bitm.tourmate.Model_Class.Expanse;
import com.bitm.tourmate.R;
import com.bitm.tourmate.Activity.UpdateExpanseAcitvity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ExpanseAdapter extends RecyclerView.Adapter<ExpanseAdapter.ViewHolder> {

    List<Expanse> expanseList;
    Context context;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public ExpanseAdapter(List<Expanse> expanseList, Context context) {
        this.expanseList = expanseList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpanseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_expense_recycleview, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ExpanseAdapter.ViewHolder holder, final int position) {

        final Expanse expanse = expanseList.get(position);
        final String tripId = expanse.getTripId();
        final String expanseId = expanse.getExpenseId();
        holder.expanseTypeTv.setText(expanse.getExpanceType());
        holder.dateTv.setText("Date: " + expanse.getDate());
        holder.timeTv.setText("Time: " + expanse.getTime());
        holder.amountTv.setText("৳ " + expanse.getAmount());
        holder.descriptionTv.setText(expanse.getDescription());
        holder.updateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateExpanseAcitvity.class);
                intent.putExtra("tripId", expanse.getTripId());
              //  Toast.makeText(context, ""+tripId, Toast.LENGTH_SHORT).show();

                intent.putExtra("expanseId", expanse.getExpenseId());
                intent.putExtra("amount", expanse.getAmount());
                intent.putExtra("date", expanse.getDate());
                intent.putExtra("description", expanse.getDescription());
                intent.putExtra("time", expanse.getTime());
                intent.putExtra("expanceType", expanse.getExpanceType());
                context.startActivity(intent);
            }
        });

holder.deleteTv.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        final String userId = currentUser.getUid();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Are You sure?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("Trips").child(expanse.getTripId()).child("Expanse").child(expanse.getExpenseId());
                        dataRef.removeValue();
                        Intent intent = new Intent(context, ExpenseActivity.class);
                        intent.putExtra("tripId", expanse.getTripId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        expanseList.remove(position);
                        context.startActivity(intent);

                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();
    }
});
    }


    @Override
    public int getItemCount() {
        return expanseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView expanseTypeTv, descriptionTv, dateTv, timeTv, amountTv, updateTv;
        private TextView deleteTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expanseTypeTv = itemView.findViewById(R.id.expanseTypeTV);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            dateTv = itemView.findViewById(R.id.dateTV);
            deleteTv = itemView.findViewById(R.id.deleteTv);
            updateTv = itemView.findViewById(R.id.updateTv);


        }
    }
}
