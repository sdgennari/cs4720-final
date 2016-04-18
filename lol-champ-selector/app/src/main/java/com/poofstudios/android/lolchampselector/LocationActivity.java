package com.poofstudios.android.lolchampselector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_LOCATION = 0;

    private LocationManager mLocationManager;
    private Spinner mRegionSpinner;
    private Spinner mLocaleSpinner;
    private Switch mLocationSwitch;

    private Resources mRes;

    private List<String> mRegionList;
    private List<String> mRegionDataList;
    private List<String> mLocaleList;
    private List<String> mLocaleDataList;
    private ArrayAdapter<String> mLocaleAdapter;
    private String currentLocaleData = null;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("LOL", "Location received");

            // Handle the location accordingly
            handleLocation(location);
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

        // Create new location manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Load arrays from resources
        mRes = getResources();
        mRegionList = Arrays.asList(mRes.getStringArray(R.array.regions));
        mRegionDataList = Arrays.asList(mRes.getStringArray(R.array.regions_data));
        mLocaleList = Arrays.asList(mRes.getStringArray(R.array.locales_na));
        mLocaleDataList = Arrays.asList(mRes.getStringArray(R.array.locales_na_data));

        // Get views from the layout
        mLocationSwitch = (Switch) findViewById(R.id.location_switch);
        mRegionSpinner = (Spinner) findViewById(R.id.spinner_region);
        mLocaleSpinner = (Spinner) findViewById(R.id.spinner_locale);

        // Configure data and listeners for the views
        configureViews();

        // Create the location manager
//        startLocationManager();
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
//                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

                        // Use GPS provider when testing on the emulator
                         mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
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

    private void configureViews() {
        mLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // User has selected to get region and locale from GPS
                    mRegionSpinner.setEnabled(false);
                    mLocaleSpinner.setEnabled(false);
                    startLocationManager();

                } else {
                    // User has selected to manually input region and locale
                    mRegionSpinner.setEnabled(true);
                    mLocaleSpinner.setEnabled(true);
                    stopLocationUpdates();
                }
            }
        });

        // Region spinner
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mRegionList);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRegionSpinner.setAdapter(regionAdapter);
        mRegionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the new locale list and locale data list based on the item selected
                switch (position) {
                    case 0:
                        mLocaleList = Arrays.asList(mRes.getStringArray(R.array.locales_na));
                        mLocaleDataList = Arrays.asList(mRes.getStringArray(R.array.locales_na_data));
                        break;
                    case 1:
                        mLocaleList = Arrays.asList(mRes.getStringArray(R.array.locales_euw));
                        mLocaleDataList = Arrays.asList(mRes.getStringArray(R.array.locales_euw_data));
                        break;
                    case 2:
                        mLocaleList = Arrays.asList(mRes.getStringArray(R.array.locales_jp));
                        mLocaleDataList = Arrays.asList(mRes.getStringArray(R.array.locales_jp_data));
                        break;
                    case 3:
                        mLocaleList = Arrays.asList(mRes.getStringArray(R.array.locales_kr));
                        mLocaleDataList = Arrays.asList(mRes.getStringArray(R.array.locales_kr_data));
                        break;
                    default:
                        mLocaleList = Arrays.asList(mRes.getStringArray(R.array.locales_na));
                        mLocaleDataList = Arrays.asList(mRes.getStringArray(R.array.locales_na_data));
                        break;
                }

                // Update locale spinner
                mLocaleAdapter = new ArrayAdapter<>(LocationActivity.this,
                        android.R.layout.simple_spinner_item, mLocaleList);
                mLocaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mLocaleSpinner.setAdapter(mLocaleAdapter);

                // Update locale spinner selection if applicable
                int localeIdx = mLocaleDataList.indexOf(currentLocaleData);
                if (localeIdx != -1) {
                    mLocaleSpinner.setSelection(localeIdx);
                } else {
                    mLocaleSpinner.setSelection(0);
                }

                // Disable the spinner if only 1 locale is available
                if (mLocaleList.size() == 1) {
                    mLocaleSpinner.setEnabled(false);
                } else {
                    mLocaleSpinner.setEnabled(true);
                }

                // TODO Update preferences
                Log.d("LOL", "Update region preferences");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Locale spinner
        mLocaleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mLocaleList);
        mLocaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocaleSpinner.setAdapter(mLocaleAdapter);
        mLocaleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentLocaleData = mLocaleDataList.get(position);

                // TODO Update preferences
                Log.d("LOL", "Update locale preferences: " + currentLocaleData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void startLocationManager() {
        // Check that the permissions have been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LOL", "Starting location manager");

            // Register the LocationManager to receive updates
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

            // Use GPS provider when testing on the emulator
             mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        } else {
            // If not, request the permission explicitly
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
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
            // No addresses found
        } else {
            Address address = addresses.get(0);
            String countryCode = address.getCountryCode();
            String apiRegion = getAPIRegionFromCountryCode(countryCode);
            currentLocaleData = getAPILocaleFromCountryCode(countryCode);

            // Update the region spinner
            // Note: This will update the new set of locales via the selection listener
            int regionIdx = mRegionDataList.indexOf(apiRegion);
            mRegionSpinner.setSelection(regionIdx);

            // Stop requesting location updates after the region and locale have been found
            stopLocationUpdates();
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

    private String getAPILocaleFromCountryCode(String countryCode) {
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
            Log.d("LOL", "Stopping location updates");
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
