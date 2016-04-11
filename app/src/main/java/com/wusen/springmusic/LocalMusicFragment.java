package com.wusen.springmusic;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wusen.adapter.MusicAdapter;
import com.wusen.music_resource.LocalMusicResource;
import com.wusen.utils.MediaUtils;

import java.util.List;

/**
 * Created by 15059 on 2016/3/6.
 */
public class LocalMusicFragment extends Fragment {

    private ListView listView;
    private MainActivity mainActivity;
    private List<LocalMusicResource> musicResources;
    private MusicAdapter adapter;
    private boolean isPause = false;
    private int position = 0;

    public static LocalMusicFragment getMusicFragment() {
        LocalMusicFragment musicFragment = new LocalMusicFragment();
        return musicFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.local_music_list, null, false);
        listView = (ListView) view.findViewById(R.id.local_list);
        loadDate();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mainActivity.musicPlayerService.setMusicResourceList(musicResources);
                mainActivity.musicPlayerService.play(i);

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.unBindPlayService();
    }


    public void loadDate() {
        musicResources = MediaUtils.musicResourceList(mainActivity);
        adapter = new MusicAdapter(mainActivity, musicResources);
        listView.setAdapter(adapter);
    }

  }



