package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                try {
                    updateLocationInfo(location);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        else{

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(lastKnownLocation != null){

                try {
                    updateLocationInfo(lastKnownLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            startListening();

        }
    }

    public void startListening(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void updateLocationInfo(Location location) throws IOException {

        TextView latTextView = (TextView) findViewById(R.id.latTextView);
        TextView lonTextView = (TextView) findViewById(R.id.lonTextView);
        TextView accTextView = (TextView) findViewById(R.id.accTextView);
        TextView altTextView = (TextView) findViewById(R.id.altTextView);
        TextView addressTextView = (TextView) findViewById(R.id.addressTextView);


        latTextView.setText("Latitude: " + Double.toString(location.getLatitude()));
        lonTextView.setText("Longitude: " + Double.toString(location.getLongitude()));
        accTextView.setText("Accuracy: " + Double.toString(location.getAccuracy()));
        altTextView.setText("Altitude: " + Double.toString(location.getAltitude()));

        String address = "Could not find address :(";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addresses != null && addresses.size() > 0){
                address = "Address: \n";

                if(addresses.get(0).getThoroughfare() != null){
                    address += addresses.get(0).getThoroughfare() + "\n";
                }

                if(addresses.get(0).getLocality() != null){
                    address += addresses.get(0).getLocality() + " ";
                }

                if(addresses.get(0).getPostalCode() != null){
                    address += addresses.get(0).getPostalCode() + "\n";
                }

                if(addresses.get(0).getAdminArea() != null){
                    address += addresses.get(0).getAdminArea() + " ";
                }

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        addressTextView.setText(address);

    }
}