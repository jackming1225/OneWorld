package com.world.one.oneworld.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.world.one.oneworld.R;
import com.world.one.oneworld.Utils.DateUtils;
import com.world.one.oneworld.Utils.PreferenceManager;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestServiceV2 {
    public final static String HTTP_RESPONSE = "http-response";
    public final static String HTTP_RESPONSE_CODE = "http-status";
    protected final static int HTTP_RESPONSE_OK = 200;
    protected final static int HTTP_RESPONSE_FORBIDDEN = 403;
    protected final static int HTTP_RESPONSE_BAD_REQUEST = 400;
    protected static final int TIMEOUT_IN_SECONDS = 180;
    protected static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    protected static Headers.Builder headersBuilder = new Headers.Builder();
    protected Context context;
    protected Handler uiMessageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if (context != null) {
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public RestServiceV2(Context context) {
        this.context = context;
    }

    protected static OkHttpClient configureClient(boolean isShortLiveServiceCall) {
        final int TIME = isShortLiveServiceCall ? 30 : TIMEOUT_IN_SECONDS;
        httpClientBuilder.readTimeout(TIME, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(TIME, TimeUnit.SECONDS);
        httpClientBuilder.connectTimeout(TIME, TimeUnit.SECONDS);
        httpClientBuilder.retryOnConnectionFailure(!isShortLiveServiceCall);
        return httpClientBuilder.build();
    }

    private static float roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Float.valueOf(twoDForm.format(d));
    }

    public Context getContext() {
        return context;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        // if running on emulator return true always.
        return android.os.Build.MODEL.equals("google_sdk");
    }

    public Object get(@NonNull final String endPoint, Class type, Boolean isList) {
        Request request = new Request.Builder().url(endPoint).get().build();
        return execute(request, type, isList, Boolean.FALSE);
    }

    protected Object execute(@NonNull final Request request, Class type, boolean isList, boolean isShortLiveServiceCall) {
        try {
            if (isOnline()) {
                OkHttpClient client = configureClient(isShortLiveServiceCall);
                // Execute the request and retrieve the response.
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Bundle countryBundle = new Bundle();
                    countryBundle.putString("countryList",  jsonResponse);
                    countryBundle.putLong(PreferenceManager.KEY_LAST_SYNCED_TIMESTAMP, DateUtils.getCurrentUnixTimeStampWithTime());
                    PreferenceManager.savePreferences(context, PreferenceManager.KEY_SERVER_DATA, countryBundle);
                    return isList ? LoganSquare.parseList(jsonResponse, type) : LoganSquare.parse(jsonResponse, type);
                }
            }
        } catch (@NonNull UnknownHostException | SocketTimeoutException e) {
            uiMessageHandler.sendEmptyMessageDelayed(0, 2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getData(@NonNull final HashMap<String, Object> response) {
        return response.get(RestServiceV2.HTTP_RESPONSE);
    }

    public boolean isSuccessful(final HashMap<String, Object> response) {
        int httpResponseCode = -1;
        if (!response.isEmpty()) {
            httpResponseCode = Integer.parseInt(response.get(RestServiceV2.HTTP_RESPONSE_CODE).toString());
        }
        return (!response.isEmpty() && httpResponseCode == RestServiceV2.HTTP_RESPONSE_OK);
    }

    public boolean isBadRequest(final HashMap<String, Object> response) {
        int httpResponseCode = -1;
        if (!response.isEmpty()) {
            httpResponseCode = Integer.parseInt(response.get(RestServiceV2.HTTP_RESPONSE_CODE).toString());
        }
        return (!response.isEmpty() && httpResponseCode == RestServiceV2.HTTP_RESPONSE_BAD_REQUEST);
    }

    public boolean isForbidden(final HashMap<String, Object> response) {
        int httpResponseCode = -1;
        if (!response.isEmpty()) {
            httpResponseCode = Integer.parseInt(response.get(RestServiceV2.HTTP_RESPONSE_CODE).toString());
        }
        return (!response.isEmpty() && httpResponseCode == RestServiceV2.HTTP_RESPONSE_FORBIDDEN);
    }
}