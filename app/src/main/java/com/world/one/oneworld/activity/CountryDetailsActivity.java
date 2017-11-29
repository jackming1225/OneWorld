package com.world.one.oneworld.activity;

import android.support.annotation.NonNull;

import com.world.one.oneworld.base.BaseActivityV2;

public class CountryDetailsActivity extends BaseActivityV2<Void,CountryDetailsActivityView,CountryDetailsActivityPresenter> implements CountryDetailsActivityView {
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
}
