package com.world.one.oneworld.activity;

import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.world.one.oneworld.R;
import com.world.one.oneworld.Utils.DateUtils;
import com.world.one.oneworld.Utils.PreferenceManager;
import com.world.one.oneworld.Utils.RecyclerItemClickSupport;
import com.world.one.oneworld.adapter.CountryAdapter;
import com.world.one.oneworld.base.BaseActivityV2;
import com.world.one.oneworld.logger.Logger;
import com.world.one.oneworld.model.Country;
import com.world.one.oneworld.network.RestServiceV2;

import java.io.IOException;
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
        offlineCheck();
        searchByString();
    }

    private void searchByString() {
        etSearchCountryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Country> countryList1 = new ArrayList<>();
                for (Country country : countryList) {
                    if (country.getName().toLowerCase().contains(charSequence)) {
                        countryList1.add(country);
                    }
                }
                initRecycler(countryList1);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void offlineCheck() {
        int timeWhenToSync = 60 * 2;

        Bundle bundle = PreferenceManager.restorePreferenceData(this, PreferenceManager.KEY_SERVER_DATA);
        if (!bundle.isEmpty()) {
            long savedTimeStamp = bundle.getLong(PreferenceManager.KEY_LAST_SYNCED_TIMESTAMP);
            long currentTimeStamp = DateUtils.getCurrentUnixTimeStampWithTime();
            long cachedTime = DateUtils.getDifferenceInMinutes(savedTimeStamp, currentTimeStamp);
            if (cachedTime > timeWhenToSync) {
                new DataTask().execute();
            } else {
                recyclerViewCreateFromBundle(bundle);
            }
        } else {
            new DataTask().execute();
        }
    }

    private void recyclerViewCreateFromBundle(Bundle bundle) {
        String countryJson = bundle.getString("countryList");
        try {
            countryList = LoganSquare.parseList(countryJson, Country.class);
            initRecycler(countryList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Country> getDataFromUrl(String url) {
        Logger.logInfo(TAG, "Start getDataFromUrl");
        List<Country> countryList = new ArrayList<>();
        if (restServiceV2.isOnline()) {
            try {
                countryList = (List<Country>) restServiceV2.get(url, Country.class, Boolean.TRUE);
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("country", country);
        startActivity(CountryDetailsActivity.class, bundle, false);
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
}
