package com.bitm.tourmate.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitm.tourmate.Activity.ExpenseActivity;
import com.bitm.tourmate.Activity.MainActivity;
import com.bitm.tourmate.Activity.MemoryActivity;
import com.bitm.tourmate.Model_Class.Trip;
import com.bitm.tourmate.R;
import com.bitm.tourmate.Activity.TripDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TripAdapter  extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    List<Trip> tripList;
Context context;
    String tripId,userId ;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public TripAdapter(List<Trip> tripList, Context context) {
        this.tripList = tripList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_tripdetails_recycleview, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

          final Trip trip=tripList.get(position);
        tripId=trip.getTripId();
        holder.tripNameTv.setText(trip.getTripName());
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

       userId = currentUser.getUid();

        holder.descriptionTv.setText(trip.getDescription());
        holder.memoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, MemoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("tripId",trip.getTripId());
                //Toast.makeText(context, ""+tripId, Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });

        holder.expanceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ExpenseActivity.class);
                intent.putExtra("tripId",trip.getTripId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                context.startActivity(intent);
            }
        });
        holder.detailsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(context, ""+tripId, Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(context, TripDetailsActivity.class);

                intent.putExtra("tripId",trip.getTripId());
                intent.putExtra("tripName",trip.getTripName());
                intent.putExtra("Todate",trip.getEndDate());
                intent.putExtra("fromDate",trip.getStartDate());
                intent.putExtra("Description",trip.getDescription());
                //Toast.makeText(context, ""+tripId, Toast.LENGTH_SHORT).show();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                context.startActivity(intent);
            }
        });

        holder.deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage("Are You sure?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("Trips").child(trip.getTripId());
                                dataRef.removeValue();
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("tripId", trip.getTripId());
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                tripList.remove(position);
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
        return tripList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tripNameTv,descriptionTv,detailsTv,memoryTv,expanceTv,deleteTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tripNameTv=itemView.findViewById(R.id.tripnameTV);
            descriptionTv=itemView.findViewById(R.id.tripdetailsTV);
            detailsTv=itemView.findViewById(R.id.detailsTV);
            memoryTv=itemView.findViewById(R.id.memoriesTV);
            expanceTv=itemView.findViewById(R.id.expenseTV);
            deleteTv=itemView.findViewById(R.id.deleteTV);
        }
    }
}
