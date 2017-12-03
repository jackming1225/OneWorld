package com.world.one.oneworld.adapter;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.world.one.oneworld.R;
import com.world.one.oneworld.Utils.SvgDecoder;
import com.world.one.oneworld.Utils.SvgDrawableTranscoder;
import com.world.one.oneworld.Utils.SvgSoftwareLayerSetter;
import com.world.one.oneworld.base.BaseActivityV2;
import com.world.one.oneworld.model.Country;

import java.io.InputStream;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryDataHolder> {

    private List<Country> countryList;
    private BaseActivityV2 context;

    public CountryAdapter(Context context, List<Country> countryList) {
        this.context = (BaseActivityV2) context;
        this.countryList = countryList;
    }

    @Override
    public CountryDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.cardview_country_item, parent, false);
        return new CountryDataHolder(rootView);
    }

    @Override
    public void onBindViewHolder(CountryDataHolder holder, int position) {
        Country country = countryList.get(holder.getAdapterPosition());
        String countryName = country.getName() + " ( " + country.getCapital() + " )";
        holder.tvCountryName.setText(countryName);
        holder.tvCountryNativeName.setText(country.getNativeName());
        if (country.getFlag() != null && !country.getFlag().trim().isEmpty()) {
            GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;
            requestBuilder = Glide.with(context)
                    .using(Glide.buildStreamModelLoader(Uri.class, context), InputStream.class)
                    .from(Uri.class)
                    .as(SVG.class)
                    .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                    .sourceEncoder(new StreamEncoder())
                    .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                    .decoder(new SvgDecoder())
                    .placeholder(R.drawable.iv_default_image)
                    .error(R.drawable.iv_default_image)
                    .animate(android.R.anim.fade_in)
                    .listener(new SvgSoftwareLayerSetter<Uri>());

            Uri uri = Uri.parse(country.getFlag());
            requestBuilder
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .load(uri)
                    .into(holder.ivCountryFlag);


        }

    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public class CountryDataHolder extends RecyclerView.ViewHolder {
        ImageView ivCountryFlag;
        TextView tvCountryName, tvCountryNativeName;

        public CountryDataHolder(View itemView) {
            super(itemView);
            ivCountryFlag = itemView.findViewById(R.id.ivCountryFlag);
            tvCountryName = itemView.findViewById(R.id.tvCountryName);
            tvCountryNativeName = itemView.findViewById(R.id.tvCountryNativeName);
        }
    }
}
