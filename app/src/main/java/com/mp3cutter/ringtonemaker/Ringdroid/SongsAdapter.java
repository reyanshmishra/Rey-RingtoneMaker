package com.mp3cutter.ringtonemaker.Ringdroid;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mp3cutter.ringtonemaker.R;

import java.util.ArrayList;

/**
 * Created by REYANSH on 4/8/2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ItemHolder> {

    public SongsAdapter(RingdroidSelectActivity ringdroidSelectActivity, ArrayList<SongsModel> data) {
        mRingdroidSelectActivity = ringdroidSelectActivity;
        mData = data;
    }

    private RingdroidSelectActivity mRingdroidSelectActivity;
    private ArrayList<SongsModel> mData;


    @Override
    public SongsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs, parent, false));
    }

    @Override
    public void onBindViewHolder(SongsAdapter.ItemHolder holder, int position) {

        holder.mDuration.setText(mData.get(position).mDuration);
        holder.mSongName.setText(mData.get(position).mSongsName);
        holder.mArtistName.setText(mData.get(position).mArtistName);

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mSongsImage;
        private TextView mSongName;
        private TextView mArtistName;
        private TextView mDuration;

        public ItemHolder(View itemView) {
            super(itemView);
            mSongsImage = (ImageView) itemView.findViewById(R.id.album_image_view);
            mSongName = (TextView) itemView.findViewById(R.id.songs_name);
            mArtistName = (TextView) itemView.findViewById(R.id.artist_name);
            mDuration = (TextView) itemView.findViewById(R.id.songs_duration);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mRingdroidSelectActivity.getApplicationContext(), "" + mData.get(getAdapterPosition()).mSongsName, Toast.LENGTH_SHORT).show();
        }
    }
}
