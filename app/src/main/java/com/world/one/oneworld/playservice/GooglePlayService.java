package com.world.one.oneworld.playservice;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.world.one.oneworld.base.BaseAppCompatActivity;
import com.world.one.oneworld.logger.Logger;

public class GooglePlayService implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public interface LocationChangeListener {
        void onLocationChange(Location location);
    }

    private LocationChangeListener locationChangeListener;
    private BaseAppCompatActivity mActivity;
    public GoogleApiClient mGoogleApiClient;

    public Location currentLocation;
    public boolean hasCurrentLocation = false;

    //Request code to use when Play service need to update
    public static final int REQUEST_SERVICE_UPDATE = 1002;

    public static final int REQUEST_CHECK_SETTINGS = 1000;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    private int REQUEST_INTERVAL = 100;
    private int REQUEST_FASTEST_INTERVAL = 100;

    private float MIN_ACCURACY = 100;// in Meters
    private long TWO_MINUTES = 1000 * 60 * 2;// in milliseconds

    public GooglePlayService(BaseAppCompatActivity mActivity) {
        this(mActivity, 0, 0);
    }

    public GooglePlayService(BaseAppCompatActivity mActivity, int requestInterval, int requestFastestInterval) {
        this.mActivity = mActivity;
        this.locationChangeListener = mActivity;

        if (requestInterval > 0) {
            REQUEST_INTERVAL = requestInterval;
        }
        if (requestFastestInterval > 0) {
            REQUEST_FASTEST_INTERVAL = requestFastestInterval;
        }

        init();
    }

    private void init() {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Logger.logError("GooglePlayService", "onConnected...");
        startPeriodicUpdates();
        if (isPlayServicesAvailable()) {
            try {
                currentLocation = bestLastKnownLocation(MIN_ACCURACY, TWO_MINUTES);
            } catch (SecurityException e) {
                Logger.logException("Exception", e);
            }

            if (currentLocation != null) {
                Logger.logError("GPS", "current location not null : " + currentLocation.toString());
                if (locationChangeListener != null) {
                    locationChangeListener.onLocationChange(currentLocation);
                }
            } else {
                Logger.logError("GPS", "current location is null");
                //checkLocationSettings();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.logError("GooglePlayService", "onLocationChanged...");

        currentLocation = location;
        hasCurrentLocation = true;

        if (locationChangeListener != null) {
            locationChangeListener.onLocationChange(location);
        }
    }

    private Location bestLastKnownLocation(float minAccuracy, long minTime) throws SecurityException {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        } else {
            return bestResult;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Logger.logError("GooglePlayService", "onConnectionFailed..." + result.getErrorMessage());
        if (result.hasResolution()) {
            if (null != mActivity) {
                try {
                    result.startResolutionForResult(mActivity, REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mGoogleApiClient.connect();
                }
            }
        } else {
            if (null != mActivity) {
                mActivity.hideProgressDialog();
                // Show dialog using GoogleApiAvailability.getErrorDialog()
                GoogleApiAvailability.getInstance().getErrorDialog(mActivity, result.getErrorCode(), REQUEST_RESOLVE_ERROR).show();
            }
        }
    }

    private boolean isPlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        return (ConnectionResult.SUCCESS == resultCode);
    }

    public void startPeriodicUpdates() {
        if (mGoogleApiClient.isConnected())
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        configureLocationRequest(REQUEST_INTERVAL, REQUEST_FASTEST_INTERVAL), this);

            } catch (SecurityException e) {
                e.printStackTrace();
            }
    }

    public void stopPeriodicUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            } catch (Exception e) {
                Logger.logException("Exception", e);
            }
    }

    public void stopClientConnection() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopPeriodicUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    public LocationRequest configureLocationRequest(final int interval, final int fastestInterval) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);

        return mLocationRequest;
    }

    public void checkLocationSettings() {
        if (mGoogleApiClient != null) {
            LocationRequest mLocationRequest = configureLocationRequest(REQUEST_INTERVAL, REQUEST_FASTEST_INTERVAL);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                                         @Override
                                         public void onResult(LocationSettingsResult result) {
                                             final Status status = result.getStatus();
                                             switch (status.getStatusCode()) {
                                                 case LocationSettingsStatusCodes.SUCCESS:
                                                     // All location settings are satisfied. The client can initialize location requests here.
                                                     break;
                                                 case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                                     if (null != mActivity) {
                                                         // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                                                         try {
                                                             // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                                             status.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);
                                                         } catch (IntentSender.SendIntentException e) {
                                                             // Ignore the error.
                                                         }
                                                     }
                                                     break;
                                                 case LocationSettingsStatusCodes.SERVICE_VERSION_UPDATE_REQUIRED:
                                                     if (null != mActivity) {
                                                         // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                                                         try {
                                                             // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                                             status.startResolutionForResult(mActivity, REQUEST_SERVICE_UPDATE);
                                                         } catch (IntentSender.SendIntentException e) {
                                                             // Ignore the error.
                                                         }
                                                     }
                                                     break;
                                                 case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                                     // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                                                     break;
                                             }
                                         }
                                     }
            );
        }
    }
}