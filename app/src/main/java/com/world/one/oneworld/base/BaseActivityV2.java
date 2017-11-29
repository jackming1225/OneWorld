package com.world.one.oneworld.base;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.world.one.oneworld.R;
import com.world.one.oneworld.logger.Logger;
import com.world.one.oneworld.mvp.IMVPView;
import com.world.one.oneworld.mvp.IPresenter;
import com.world.one.oneworld.mvp.MVPViewDelegate;
import com.world.one.oneworld.mvp.ParcelableAndSerializablePresentationModelSerializer;
import com.world.one.oneworld.mvp.PresentationModelSerializer;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivityV2<M, V extends IMVPView, P extends IPresenter<V, M>>
        extends BaseAppCompatActivity implements IMVPView {

    private static final String TAG = BaseActivityV2.class.getSimpleName();
    public final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;

    //MVP WORK IN PROGRESS BELOW THIS LINE
    private MVPViewDelegate<M, V, P> mvpDelegate =
            new MVPViewDelegate<M, V, P>(createPresentationModelSerializer()) {
                @NonNull
                @Override
                protected P createPresenter() {
                    return BaseActivityV2.this.createPresenter();
                }

                @NonNull
                @Override
                protected M createPresentationModel() {
                    return BaseActivityV2.this.createPresentationModel();
                }
            };
    public String fullImagePath;

    @NonNull
    protected P getPresenter() {
        return mvpDelegate.getPresenter();
    }

    /**
     * Feel free to override this method that returns your own implementation of
     * PresentationModelSerializer.
     * Useful if you use a Parceler library for example
     *
     * @return an instance of PresentationModelSerializer that will serialize and deserialize your
     * PresentationModel from Bundle.
     */
    protected PresentationModelSerializer<M> createPresentationModelSerializer() {
        return new ParcelableAndSerializablePresentationModelSerializer<>();
    }

    /**
     * Used for creating the presenter instance, called in #onCreate(Bundle) method.
     *
     * @return an instance of your Presenter.
     */
    @NonNull
    protected abstract P createPresenter();

    /**
     * Used to create the Presentation Model that will be attached to your presenter in #onAttach()
     * method of your presenter.
     * <p>
     * NOTE: this will be called only if there is no Presentation Model persisted in your
     * savedInstanceState!
     * <p>
     * You can retrieve the arguments from your Intent's extra and pass it
     * to your Presentation's model constructor.
     *
     * @return Presentation Model instance used by your Presenter.
     */
    @NonNull
    protected abstract M createPresentationModel();

    @Override
    public void setContentView(int layoutResID) {
        baseView = getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) baseView.findViewById(R.id.activity_base_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        baseToolbar = (Toolbar) baseView.findViewById(R.id.base_toolbar);
        super.setContentView(baseView);
//        setToolbar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            mvpDelegate.onSaveInstanceState(outState);
        } catch (IllegalStateException e) {
            Logger.logException("MvpDelegate", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mvpDelegate.onCreate(this, savedInstanceState);
        fullImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.path_images);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mvpDelegate.onDestroy();
    }

    @Override
    public void showProgress(String msg, boolean isCancellable) {

    }

    @Override
    public void hideProgress() {

    }
}