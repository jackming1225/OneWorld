package com.world.one.oneworld.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class MVPViewDelegate<M, V extends IMVPView, P extends IPresenter<V, M>> {
    private P presenter;
    private M presentationModel;
    private final PresentationModelSerializer<M> serializer;

    private String presentationModelKey;

    public MVPViewDelegate(@NonNull PresentationModelSerializer<M> serializer) {
        this.serializer = serializer;
    }

    /**
     * Commonly called from fragment's/activity's onCreate. Presentation Model is either created or
     * restored
     * here.
     *
     * @param mvpView            the MVP view that is being created.
     * @param savedInstanceState instanceState provided by android framework in which we store the
     *                           Presentation Model
     */
    public void onCreate(IMVPView mvpView, @Nullable Bundle savedInstanceState) {
        presentationModelKey = mvpView.getClass().getCanonicalName() + "$PresentationModel";
       /* presentationModel =
                serializer.restorePresentationModel(savedInstanceState, presentationModelKey)*/
        if (presentationModel == null) {
            presentationModel = createPresentationModel();
        }
        this.presenter = createPresenter();
        initialize((V)mvpView);
    }

    /**
     * Commonly called from fragment's/activity's onStart. Used for attaching the view to
     * presenter,
     * so the presenter can start to update the view's state
     */
    public void initialize(V mvpView) {
        checkPresenter();
        checkPresentationModel();
        presenter.attachView(mvpView, presentationModel);
    }

    /**
     * Commonly called from fragment's/activity's onDestroy. Used to notify the presenter that the
     * view
     * will no longer be attached to the presenter, so all the long running tasks have to be
     * terminated
     * and the context should be cleared.
     */
    public void onDestroy() {
        checkPresenter();
        presenter.detachView();
        presenter.onDestroy();
    }

    /**
     * Used by the delegate to persist current Presentation Model due to configuration change.
     */
    public void onSaveInstanceState(Bundle outState) {
        //serializer.savePresentationModel(outState, presentationModelKey, presentationModel);
    }

    public P getPresenter() {
        return presenter;
    }

    @NonNull
    protected abstract P createPresenter();

    @NonNull
    protected abstract M createPresentationModel();

    private void checkPresenter() {
        if (presenter == null) {
            throw new IllegalStateException(
                    "call onCreate in DroidMVPViewDelegate, because presenter is missing");
        }
    }

    private void checkPresentationModel() {
        /*if (presentationModel == null) {
            throw new IllegalStateException(
                    "seems like you forgot to create presentationModel in #createPresentationModel() method, or call the #onCreate() of this delegate");
        }*/
    }
}
