package com.poofstudios.android.lolchampselector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_LOCATION = 0;

    private LocationManager mLocationManager;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("LOL", "Location received");

            // Handle the location accordingly
            handleLocation(location);

            // Stop requesting location updates after receiving the first location
            stopLocationUpdates();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.d("LOL", "onCreate");

        // Check that the permissions have been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LOL", "Starting location manager");

            // Register the LocationManager to receive updates
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

            // Use GPS provider when testing on the emulator
            // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        } else {
            // If not, request the permission explicitly
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 1 && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("LOL", "Location permission request granted");

                    // Register the LocationManager to receive updates
                    // Note: Recheck permissions here to prevent Android Studio error from displaying
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

                        // Use GPS provider when testing on the emulator
                        // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                    }

                } else {
                    // TODO Handle permission denied
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void handleLocation(Location location) {
        Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            Log.e("LOL", "Geocoder IOException: " + e.getLocalizedMessage());
        }

        if (addresses == null || addresses.size() == 0) {
            // TODO Handle no addresses found
        } else {
            Address address = addresses.get(0);
            String countryCode = address.getCountryCode();
            String apiRegion = getAPIRegionFromCountryCode(countryCode);
            String apiLocale = getAPILocaleFroMCountryCode(countryCode);
            Log.d("LOL", "Region: " + apiRegion);
            Log.d("LOL", "Locale: " + apiLocale);
        }
    }

    private String getAPIRegionFromCountryCode(String countryCode) {
        switch (countryCode) {
            case "US":              // United States
                return "na";
            case "KR":              // Korea
                return "kr";
            case "JP":              // Japan
                return "jp";
            case "GB":              // Great Britain
            case "FR":              // France
            case "DE":              // Denmark
            case "ES":              // Spain
            case "IT":              // Italy
                return "euw";
            default:
                return null;
        }
    }

    private String getAPILocaleFroMCountryCode(String countryCode) {
        switch (countryCode) {
            case "US":              // United States
                return "en_US";
            case "KR":              // Korea
                return "ko_KR";
            case "JP":               // Japan
                return "ja_JP";
            case "GB":              // Great Britain
                return "en_GB";
            case "FR":              // France
                return "fr_FR";
            case "DE":              // Denmark
                return "de_DE";
            case "ES":              // Spain
                return "es_ES";
            case "IT":              // Italy
                return "it_IT";
            default:
                return null;
        }
    }

    private void stopLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
