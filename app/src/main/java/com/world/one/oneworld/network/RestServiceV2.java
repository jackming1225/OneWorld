package com.world.one.oneworld.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.ParameterizedType;
import com.world.one.oneworld.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RestServiceV2 {
    public final static String HTTP_RESPONSE = "http-response";
    public final static String HTTP_RESPONSE_CODE = "http-status";
    public final static String USER_AGENT = "Mobile";
    public final static String ACCEPT_ENCODING = "gzip";

    protected final static int HTTP_RESPONSE_OK = 200;
    protected final static int HTTP_RESPONSE_FORBIDDEN = 403;
    protected final static int HTTP_RESPONSE_BAD_REQUEST = 400;

    protected static final String KEY_HEADER_TOKEN = "X-Auth-TOKEN";
    protected static final String KEY_HEADER_MOBILE_APK_VERSION = "mobileApkVersion";
    protected final static String KEY_SOURCE = "X-Source";
    protected final static String KEY_USER_AGENT = "user-agent";
    protected final static String KEY_USER_PREFERRED_LANGUAGE_CODE = "preferredLanguageCode";
    protected final static String KEY_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String KEY_CONTENT_ENCODING = "Content-Encoding";

    protected static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    protected static Headers.Builder headersBuilder = new Headers.Builder();
    protected static final MediaType MEDIA_JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final MediaType MEDIA_IMAGE = MediaType.parse("image/png");
    protected static final int TIMEOUT_IN_SECONDS = 180;
    protected Context context;

    public Context getContext() {
        return context;
    }

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

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        // if running on emulator return true always.
        return android.os.Build.MODEL.equals("google_sdk");
    }

    protected static OkHttpClient configureClient(boolean isShortLiveServiceCall) {
        final int TIME = isShortLiveServiceCall ? 30 : TIMEOUT_IN_SECONDS;
        httpClientBuilder.readTimeout(TIME, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(TIME, TimeUnit.SECONDS);
        httpClientBuilder.connectTimeout(TIME, TimeUnit.SECONDS);
        httpClientBuilder.retryOnConnectionFailure(!isShortLiveServiceCall);
        return httpClientBuilder.build();
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
                    if (response.header(KEY_CONTENT_ENCODING) != null && response.header(KEY_CONTENT_ENCODING).contains(ACCEPT_ENCODING)) {
                        return isList ? LoganSquare.parseList(new GZIPInputStream(response.body().byteStream()), type) : LoganSquare.parse(new GZIPInputStream(response.body().byteStream()), type);
                    } else {
                        return isList ? LoganSquare.parseList(response.body().byteStream(), type) : LoganSquare.parse(response.body().byteStream(), type);
                    }
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

    private static float roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Float.valueOf(twoDForm.format(d));
    }
}