package com.world.one.oneworld.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.world.one.oneworld.R;
import com.world.one.oneworld.base.BaseActivityV2;
import com.world.one.oneworld.model.Country;

public class CountryDetailsActivity extends BaseActivityV2<Void, CountryDetailsActivityView, CountryDetailsActivityPresenter> implements CountryDetailsActivityView, OnMapReadyCallback {

    private Country country;

    @NonNull
    @Override
    protected CountryDetailsActivityPresenter createPresenter() {
        return new CountryDetailsActivityPresenter();
    }

    @NonNull
    @Override
    protected Void createPresentationModel() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getIntentExtras();
        initViews();
        setMap();
    }

    private void initViews() {

    }

    private void setMap() {
        SupportMapFragment map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        map.getMapAsync(this);
    }

    private void getIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            country = (Country) bundle.getSerializable("country");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(country.getLatlng().get(0), country.getLatlng().get(1));
        googleMap.addMarker(new MarkerOptions().position(latLng)
                .title(country.getName() + " (" + country.getNativeName() + ")"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
