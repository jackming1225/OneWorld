package com.world.one.oneworld.activity;

import android.database.SQLException;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.world.one.oneworld.R;
import com.world.one.oneworld.Utils.PreferenceManager;
import com.world.one.oneworld.Utils.RecyclerItemClickSupport;
import com.world.one.oneworld.adapter.CountryAdapter;
import com.world.one.oneworld.base.BaseActivityV2;
import com.world.one.oneworld.logger.Logger;
import com.world.one.oneworld.model.Country;
import com.world.one.oneworld.network.RestServiceV2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivityV2<Void, MainActivityView, MainActivityPresenter> implements MainActivityView {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RestServiceV2 restServiceV2;
    private List<Country> countryList;
    private RecyclerView rvCountryList;
    private CountryAdapter countryAdapter;
    private TextView tvNoData;
    private EditText etSearchCountryName;
    private ImageView ivSearchButton;

    @NonNull
    @Override
    protected MainActivityPresenter createPresenter() {
        return new MainActivityPresenter();
    }

    @NonNull
    @Override
    protected Void createPresentationModel() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(getString(R.string.app_name));
        iniViews();

    }

    private void iniViews() {
        restServiceV2 = new RestServiceV2(this);
        countryList = new ArrayList<>();
        rvCountryList = findViewById(R.id.rvCountryList);
        tvNoData = findViewById(R.id.tvNoData);
        ivSearchButton = findViewById(R.id.ivSearchButton);
        etSearchCountryName = findViewById(R.id.etSearchCountryName);

        Bundle bundle = PreferenceManager.restorePreferenceData(this, PreferenceManager.KEY_SERVER_DATA);
        if (bundle != null && !bundle.isEmpty()) {
            countryList = (List<Country>) bundle.getSerializable(PreferenceManager.KEY_SERVER_DATA);
        } else {
            new DataTask().execute();
        }
    }

    private class DataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            countryList = getDataFromUrl(getString(R.string.data_URL));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initRecycler(countryList);
        }
    }


    private List<Country> getDataFromUrl(String url) {
        Logger.logInfo(TAG, "Start getDataFromUrl");
        List<Country> countryList = new ArrayList<>();
        if (restServiceV2.isOnline()) {
            try {
                countryList = (List<Country>) restServiceV2.get(url, Country.class, Boolean.TRUE);
                if (countryList != null && !countryList.isEmpty()) {
                    Bundle countryBundle = new Bundle();
                    countryBundle.putSerializable(PreferenceManager.KEY_SERVER_DATA, (Serializable) countryList);
                    PreferenceManager.savePreferences(this, PreferenceManager.KEY_SERVER_DATA, countryBundle);
                }
            } catch (SQLException | NullPointerException e) {
                Logger.logException(TAG, e);
            }
        }
        return countryList;
    }

    private void initRecycler(final List<Country> countryList) {
        rvCountryList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rvCountryList.setLayoutManager(layoutManager);

        if (!countryList.isEmpty()) {
            rvCountryList.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            countryAdapter = new CountryAdapter(this, countryList);
            rvCountryList.setAdapter(countryAdapter);

            RecyclerItemClickSupport.addTo(rvCountryList).setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    startCountryDetailsActivty(countryList.get(position));
                }
            });
        } else {
            rvCountryList.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void startCountryDetailsActivty(Country country) {

    }
}
