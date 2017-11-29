package com.world.one.oneworld.mvp;

public interface IMVPView {
    void showProgress(String msg, boolean isCancellable);

    void hideProgress();
}
