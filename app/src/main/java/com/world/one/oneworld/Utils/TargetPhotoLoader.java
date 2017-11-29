package com.world.one.oneworld.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.world.one.oneworld.base.BaseActivityV2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TargetPhotoLoader implements Target {
    private String name;
    private ImageView imageView;
    private String fullImagePath;

    public TargetPhotoLoader(BaseActivityV2 activity, String name, @Nullable ImageView imageView) {
        this.fullImagePath = activity.fullImagePath;
        this.name = name;
        this.imageView = imageView;
    }

    public TargetPhotoLoader(String fullImagePath, String name) {
        this.fullImagePath = fullImagePath;
        this.name = name;
    }

    public TargetPhotoLoader(String fullImagePath) {
        this.fullImagePath = fullImagePath;
    }

    public void setImageName(String name) {
        this.name = name;
    }

    public void loadAndStore(Context context, String url) {
        try {
            Bitmap bitmap = Picasso.with(context).load(url).get();
            loadAndStore(bitmap, Boolean.TRUE);
        } catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public void loadAndStore(Bitmap bitmap, boolean isRecycleNeeded) {
        if (name != null && !name.isEmpty()) {
            File file = new File(fullImagePath);
            try {
                file.mkdirs();
                FileOutputStream ostream = new FileOutputStream(fullImagePath + name);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                ostream.close();
                if (isRecycleNeeded && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onPrepareLoad(Drawable arg0) {
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        }
        loadAndStore(bitmap, Boolean.FALSE);
    }

    @Override
    public void onBitmapFailed(Drawable arg0) {
    }
}
