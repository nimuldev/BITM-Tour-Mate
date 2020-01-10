package com.bitm.tourmate.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitm.tourmate.Activity.MemoryActivity;
import com.bitm.tourmate.Model_Class.Memory;
import com.bitm.tourmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {

    private ProgressDialog progress;
    List<Memory> memoryList;
    Context context;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String tripId;
    String memoryId;
    FirebaseStorage storage ;//= storage.getReference();

    String userId;

    public MemoryAdapter(List<Memory> memoryList, Context context) {
        this.memoryList = memoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_memory_recycleview, parent, false);

        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Memory memory=memoryList.get(position);
       // Toast.makeText(context, "z"+memory.getTripId(), Toast.LENGTH_SHORT).show();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final FirebaseStorage storage = FirebaseStorage.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
       userId = currentUser.getUid();
        tripId= memory.getTripId();
        memoryId=memory.getMemoreyId();
              Picasso.get().load(memory.getPicture()).into(holder.memoreyIv);
              holder.captionIv.setText(memory.getCaption());

             holder.removeIv.setOnClickListener(new View.OnClickListener() {
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
                                     progress=new ProgressDialog(context);
                                     progress.setMessage("Please Wait");
                                     progress.setTitle("LogIn ");
                                     progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                     progress.setIndeterminate(true);
                                     progress.show();
                                     progress.setCancelable(true);


                                     final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("Trips").child(memory.getTripId()).child("Image").child(memory.getMemoreyId());
                                     dataRef.removeValue();

                                     StorageReference deleteFile = storage.getReferenceFromUrl(memory.getPicture());
                                     deleteFile.delete();
                                     memoryList.remove(position);
                                     Intent intent = new Intent(context, MemoryActivity.class);
                                     intent.putExtra("tripId", memory.getTripId());

                                     intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

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
        return memoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView memoreyIv,removeIv;
        private TextView captionIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            memoreyIv=itemView.findViewById(R.id.memoryimageIV);
            captionIv=itemView.findViewById(R.id.memorydetailsTV);
            removeIv=itemView.findViewById(R.id.removeIv);

        }
    }
}
