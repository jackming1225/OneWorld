package com.world.one.oneworld.mvp;

public interface IPresenter<V extends IMVPView, M> {
    void attachView(V mvpView, M presentationModel);

    void detachView();

    void onDestroy();
}
