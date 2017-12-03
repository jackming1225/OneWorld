package com.world.one.oneworld.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

public class PreferenceManager {

    private static final String preferenceStore = "one_World";
    public static final String KEY_SERVER_DATA = "CountryList";
    public static final String KEY_LAST_SYNCED_TIMESTAMP = "lastSyncedTime";


    public static void savePreferences(Context context, String preferenceName, @Nullable Bundle bundle) {
        if (context != null && bundle != null) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            //editor.putString(preferenceName, Serializer.serializeBundle(bundle));

            Object obj;
            for (String key : bundle.keySet()) {
                obj = bundle.get(key);
                if (obj instanceof String) {
                    editor.putString(key, obj.toString());
                } else if (obj instanceof Integer) {
                    editor.putInt(key, Integer.parseInt(obj.toString()));
                } else if (obj instanceof Boolean) {
                    editor.putBoolean(key, Boolean.parseBoolean(obj.toString()));
                } else if (obj instanceof Long) {
                    editor.putLong(key, Long.parseLong(obj.toString()));
                }
            }
            editor.apply();
        }
    }

    @NonNull
    public static Bundle restorePreferenceData(Context context, String preferenceName) {
        Bundle bundle = new Bundle();
        if (context != null) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            Object obj;
            Map<String, ?> keys = sharedpreferences.getAll();
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                obj = keys.get(entry.getKey());
                if (obj instanceof String) {
                    bundle.putString(entry.getKey(), entry.getValue().toString());
                } else if (obj instanceof Integer) {
                    bundle.putInt(entry.getKey(), Integer.parseInt(entry.getValue().toString()));
                } else if (obj instanceof Boolean) {
                    bundle.putBoolean(entry.getKey(), Boolean.parseBoolean(entry.getValue().toString()));
                } else if (obj instanceof Long) {
                    bundle.putLong(entry.getKey(), Long.parseLong(entry.getValue().toString()));
                }
            }
        }
        return bundle;
    }
}
