package com.world.one.oneworld.mvp;

import android.support.annotation.Nullable;

public abstract class BasePresenter<V extends IMVPView, M> implements IPresenter<V, M> {
    private M presentationModel;
    private V mvpView;

    @Override
    public void attachView(V mvpView, M presentationModel) {
        this.mvpView = mvpView;
        this.presentationModel = presentationModel;
    }


    public void showProgress(String msg, boolean isCancellable) {
        if(getMvpView()!=null) {
            getMvpView().showProgress(msg, isCancellable);
        }
    }

    public void hideProgress() {
        if(getMvpView()!=null) {
            getMvpView().hideProgress();
        }
    }

    @Override
    public void detachView() {
        mvpView = null;
    }

    @Override
    public void onDestroy() {

    }

    public M getPresentationModel() {
        return presentationModel;
    }

    @Nullable
    protected V getMvpView() {
        return mvpView;
    }
}