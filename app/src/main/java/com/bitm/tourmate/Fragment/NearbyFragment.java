package com.bitm.tourmate.Fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bitm.tourmate.Map.MapsActivity;
import com.bitm.tourmate.R;
import com.bitm.tourmate.Weather.WeatherActivity;

import static android.content.Context.LOCATION_SERVICE;

public class NearbyFragment extends Fragment {

    private ImageView policeIv, bankIv, atmIv, parkIv, mosqueIv, cafeIv, educationIv, hospitativ;
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};


    public NearbyFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        init(view);

        policeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    nearByLocation("police");

                    //   Toast.makeText(getContext(), "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                } else {

                    showGPSDisabledAlertToUser();
                }





               /* Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("search", "police");
                startActivity(intent);*/
            }
        });
        bankIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    nearByLocation("bank");

                    //  Toast.makeText(getContext(), "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                } else {

                    showGPSDisabledAlertToUser();
                }


            }
        });


        educationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    nearByLocation("university");

                    // Toast.makeText(getContext(), "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                } else {

                    showGPSDisabledAlertToUser();
                }

            }
        });

        cafeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    nearByLocation("cafe");

                    // Toast.makeText(getContext(), "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                } else {

                    showGPSDisabledAlertToUser();
                }


            }
        });


        mosqueIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    nearByLocation("mosque");

                    //  Toast.makeText(getContext(), "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                } else {

                    showGPSDisabledAlertToUser();
                }


            }
        });

        parkIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    nearByLocation("park");

                    // Toast.makeText(getContext(), "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                } else {

                    showGPSDisabledAlertToUser();
                }


            }
        });

        atmIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    nearByLocation("atm");

                    // Toast.makeText(getContext(), "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                } else {

                    showGPSDisabledAlertToUser();
                }


            }
        });
        hospitativ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    nearByLocation("hospital");


                } else {

                    showGPSDisabledAlertToUser();
                    getPermissionFromUser();
                }


            }
        });

        return view;
    }

    private void init(View view) {

        policeIv = view.findViewById(R.id.pliceIv);
        bankIv = view.findViewById(R.id.bankIv);
        atmIv = view.findViewById(R.id.atmIv);
        parkIv = view.findViewById(R.id.parkIv);
        mosqueIv = view.findViewById(R.id.mosqueIv);
        cafeIv = view.findViewById(R.id.cafeIv);
        educationIv = view.findViewById(R.id.educationIv);
        hospitativ = view.findViewById(R.id.hospitalIv);

    }


    public void nearByLocation(String search) {

        Intent intent = new Intent(getContext(), MapsActivity.class);
        intent.putExtra("search", search);
        startActivity(intent);

    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);


                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(), "Not Possible To show near Location", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void getPermissionFromUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0);
            } else {
                Toast.makeText(getContext(), "Something is goint to wrong", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
