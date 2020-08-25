package com.example.voiceassistantapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistantapp.R;
import com.example.voiceassistantapp.activities.MusicPlayer;
import com.example.voiceassistantapp.models.AudioModel;

import java.util.ArrayList;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {

    private List<AudioModel> results;
    private MusicPlayer mainActivity;

    public AudioListAdapter(MusicPlayer context, List<AudioModel> data) {
        this.mainActivity = context;
        this.results = data;
    }

    public void setData(List<AudioModel> data) {
        this.results = new ArrayList<>();
        this.results = data;
        notifyDataSetChanged();
    }

    @Override
    public AudioListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AudioListAdapter.ViewHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final AudioListAdapter.ViewHolder holder, final int position) {
        final AudioModel object = results.get(position);

        String txt = "Name: " + object.getName()
                + "\nPath:" + object.getPath()
                + "\nAlbum: " + object.getAlbum()
                + "\nArtist: " + object.getArtist()
                + "\n\nMore: " + object.getMore();
        holder.text.setText(object.getName());
        if (object.isPlaying()) {
            holder.text.setTextColor(mainActivity.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.text.setTextColor(mainActivity.getResources().getColor(R.color.appBlack));
        }

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.playSelectedIndex(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (results != null) {
            return results.size();
        } else return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mainView;
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = (LinearLayout) itemView.findViewById(R.id.main_view);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}