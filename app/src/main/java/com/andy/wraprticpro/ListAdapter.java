package com.andy.wraprticpro;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ADIK on 22/08/2016.
 */
public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(String[] item);
    }

    private Context mContext;
    private ArrayList<String[]> mList;
    private OnItemClickListener listener;


    public ListAdapter(Context mContext, ArrayList<String[]> mList,OnItemClickListener listener) {
        this.mList = mList;
        this.mContext = mContext;
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        public TextView titleTV,subtitleTV;
        public ImageView thumbnailIM;
        public ImageView itemAppFM;

        public MyViewHolder(View view) {
            super(view);
            titleTV = (TextView) view.findViewById(R.id.titleData);
            subtitleTV = (TextView) view.findViewById(R.id.subTitle);
            thumbnailIM = (ImageView) view.findViewById(R.id.thumbnail);
            itemAppFM = (ImageView) view.findViewById(R.id.item_app);
        }

    }




    public static class ViewHolderAdMob extends RecyclerView.ViewHolder {
        public NativeExpressAdView mAdView;
        public ViewHolderAdMob(View view) {
            super(view);
            mAdView = (NativeExpressAdView) view.findViewById(R.id.adView);
            mAdView.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType){
            case 1:{
                View v = inflater.inflate(R.layout.list_item, parent, false);
                viewHolder = new MyViewHolder(v);
                break;
            }
            case 2:{
                View v = inflater.inflate(R.layout.list_item_admob, parent, false);
                viewHolder = new ViewHolderAdMob(v);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final String[] model = mList.get(holder.getAdapterPosition());

        switch(holder.getItemViewType()){
            case 1:{
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.titleTV.setText(model[0]);
                viewHolder.itemAppFM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(model);
                    }
                });
                viewHolder.subtitleTV.setText(model[1]);
                Picasso.with(mContext).load(model[2]).error(R.mipmap.ic_launcher).into(viewHolder.thumbnailIM);
                break;
            }
            case 2:{
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.valueOf(mList.get(position)[3]);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}