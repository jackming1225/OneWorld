package com.world.one.oneworld.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.world.one.oneworld.R;
import com.world.one.oneworld.Utils.TargetPhotoLoader;
import com.world.one.oneworld.base.BaseActivityV2;
import com.world.one.oneworld.model.Country;

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

        holder.tvCountryName.setText(country.getName());
        holder.tvCountryNativeName.setText(country.getNativeName());
        if (country.getFlag() != null && !country.getFlag().trim().isEmpty()) {
            String name = country.getFlag().substring(country.getFlag().lastIndexOf("/") + 1, country.getFlag().length());
            TargetPhotoLoader targetPhotoLoader = new TargetPhotoLoader(context, name, holder.ivCountryFlag);

            holder.ivCountryFlag.setTag(targetPhotoLoader);
            Picasso.with(context)
                    .load(country.getFlag())
                    .resize(100, 100)
                    .into(targetPhotoLoader);
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
