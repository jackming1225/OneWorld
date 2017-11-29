package com.world.one.oneworld.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.world.one.oneworld.R;
import com.world.one.oneworld.playservice.GooglePlayService;

public class BaseAppCompatActivity extends AppCompatActivity implements GooglePlayService.LocationChangeListener {

    public final int MAX_CHARACTER_COUNT = 200;
    private ProgressDialog progressDialog;
    public View baseView;
    public Toolbar baseToolbar;

    protected GooglePlayService playService;
    protected Location currentLocation;
    protected boolean isActivityCancelled = false;

    /**
     * change the value to false if do not want to call location api in your activity
     */
    protected boolean isLocationRequired = true;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isLocationRequired) {
            startLocationTracking();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationTracking();
        currentLocation = null;
    }

    public void showProgressDialog(String message, boolean isCancelable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
            progressDialog.setCancelable(isCancelable);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(message);
        }

        if (!isFinishing() && !progressDialog.isShowing())
            progressDialog.show();
    }

    public void hideProgressDialog() {
        if (!isFinishing() && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void setToolbar() {
        setSupportActionBar(baseToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setToolbar(@NonNull String title) {
        setSupportActionBar(baseToolbar);
        setTitle(title);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(Boolean.TRUE);
    }

    public void disableToolbarUpButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void hideToolBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void setIcon(int iconResourceId) {
        getSupportActionBar().setIcon(iconResourceId);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        getSupportActionBar().setTitle(title);
    }

    public void setSubTitle(String subTitle) {
        getSupportActionBar().setSubtitle(subTitle);
    }

    public void showSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void startActivity(Class<?> activityToStart, boolean isFinishCurrentActivity) {
        hideSoftKeyboard();
        Intent intent = new Intent(this, activityToStart);
        startActivity(intent);
        if (isFinishCurrentActivity) {
            supportFinishAfterTransition();
        }
    }

    public void startActivity(Class<?> activityToStart, Bundle extras, boolean isFinishCurrentActivity) {
        hideSoftKeyboard();
        Intent intent = new Intent(this, activityToStart);
        intent.putExtras(extras);
        startActivity(intent);
        if (isFinishCurrentActivity) {
            supportFinishAfterTransition();
        }
    }

    public void startActivityForResult(Class<?> activityToStart, Bundle extras, final int requestCode, boolean isFinishCurrentActivity) {
        hideSoftKeyboard();
        Intent intent = new Intent(this, activityToStart);
        intent.putExtras(extras);
        startActivityForResult(intent, requestCode);
        if (isFinishCurrentActivity) {
            supportFinishAfterTransition();
        }
    }

    protected void startLocationTracking() {
        stopLocationTracking();
        playService = new GooglePlayService(this);
    }

    protected boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            showGPSEnableDialog();
        }
        return isGPSEnabled;
    }

    private void showGPSEnableDialog() {
        isLocationRequired = true;

        stopLocationTracking();
        playService = new GooglePlayService(this);
        playService.checkLocationSettings();
    }

    protected void stopLocationTracking() {
        if (playService != null) {
            playService.stopClientConnection();
            playService = null;
        }
    }

    @Override
    public void onLocationChange(Location location) {
        currentLocation = location;
        stopLocationTracking();
    }

    public int getImageId(String imageName) {
        Resources r = getResources();
        return r.getIdentifier(imageName, "mipmap", getPackageName());
    }

    public void showDialog(String title, String button1, String button2) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(
                getSupportActionBar().getThemedContext(), android.R.style.Theme_Holo_Light);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(wrapper);
        alertDialogBuilder.setMessage(title);
        alertDialogBuilder.setPositiveButton(button1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        onDialogOkButtonClick(dialog);
                    }
                });
        alertDialogBuilder.setNegativeButton(button2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                onDialogCancelButtonClick(dialog);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        if (!isFinishing()) {
            alertDialog.show();
        }
    }

    public void onDialogOkButtonClick(DialogInterface dialog) {
        finish();
    }

    public void onDialogCancelButtonClick(@NonNull DialogInterface dialog) {
        if (!isFinishing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}