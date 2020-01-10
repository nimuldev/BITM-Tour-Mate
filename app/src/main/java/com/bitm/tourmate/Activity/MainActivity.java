package com.bitm.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bitm.tourmate.DeveloperNProfile.DeveloperActivity;
import com.bitm.tourmate.DeveloperNProfile.ProfileActivity;
import com.bitm.tourmate.Model_Class.User;
import com.bitm.tourmate.Fragment.NearbyFragment;
import com.bitm.tourmate.R;
import com.bitm.tourmate.Fragment.TripsFragment;
import com.bitm.tourmate.Fragment.weatherFragment;
import com.bitm.tourmate.Weather.WeatherActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    private String email,password;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int check=0;
    String userId;
    String lastnameP,firstnamPe,emailP,pictureP;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String [] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private ProgressDialog progress;
    int checkLocation=0;
    String weather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        check = getIntent().getIntExtra("check", 0);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
        locationOn();

        getPermissionFromUser();
        replaceFragment(new TripsFragment());
        setTitle("Trips");


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnav);



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.nav_trips:
                        replaceFragment(new TripsFragment());
                        setTitle("Trips");
                        return true;

                    case R.id.nav_nearby:

                        replaceFragment(new NearbyFragment());
                        setTitle("NearBy");
                        return true;


                    case R.id.nav_weather:

                        int a=locationOn();
                        if(a==1){
                            replaceFragment(new weatherFragment());
                            setTitle("Weather");
                            return true;
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Not Possible to show weather", Toast.LENGTH_SHORT).show();
                        }




                }


                return false;
            }


        });


    }

    private void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Save Account?");
        builder.setPositiveButton("Always", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



            }
        });
        builder.setNegativeButton("Never", null);

        AlertDialog alert = builder.create();
        alert.show();
    }



    public void replaceFragment(Fragment fragment) {


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,fragment);
        ft.commit();


    }
    public void shareFrance(String email,String password){
        sharedPreferences=getSharedPreferences("userInfo",MODE_PRIVATE);
        editor =sharedPreferences.edit();

        editor.putString("password",password);
        editor.putString("email",email);
        editor.apply();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menus, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case R.id.profile:

                passData();
              Intent intent= new Intent(MainActivity.this, ProfileActivity.class);
              intent.putExtra("first",firstnamPe);
              intent.putExtra("last",lastnameP);
              intent.putExtra("email",emailP);
              intent.putExtra("picture",pictureP);
              startActivity(intent);

                return true;

            case R.id.developer:
                startActivity(new Intent(this, DeveloperActivity.class));

                return true;

            case R.id.logOut:
                sharedPreferences=getSharedPreferences("userInfo",MODE_PRIVATE);
                editor =sharedPreferences.edit();

                editor.clear();
                editor.apply();
                Toast.makeText(this, "Remove account from this device", Toast.LENGTH_SHORT).show();
                 intent=new Intent(MainActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();




                return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private void passData() {


        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    //for(DataSnapshot data: dataSnapshot.getChildren()) {

                    User user = dataSnapshot.getValue(User.class);



                   firstnamPe=user.getFirstName();
                    lastnameP=user.getLastName();
                    emailP=user.getEmail();
                    pictureP=user.getPicture();




                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }





    private void getPermissionFromUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(permissions,0);
            }
        }
    }
    public int locationOn() {


        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
            return  1;
        } else {
           showGPSDisabledAlertToUser();
            return  0;
        }
    }

        private void showGPSDisabledAlertToUser(){
            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Goto Settings Page To Enable GPS",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);


                                }

                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            //Toast.makeText(MainActivity.this, "Not Possible To show near Location", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });
            androidx.appcompat.app.AlertDialog alert = alertDialogBuilder.create();
            alert.show();

    }

    @Override
    public void onBackPressed() {
       Intent intent=new Intent(MainActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
