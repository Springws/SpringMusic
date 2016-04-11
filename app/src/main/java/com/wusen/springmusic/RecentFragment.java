package com.wusen.springmusic;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.wusen.adapter.MusicAdapter;
import com.wusen.music_resource.LocalMusicResource;

import java.util.List;

/**
 * Created by 15059 on 2016/3/23.
 */
public class RecentFragment extends Fragment {
    private MusicApplication app;
    private MainActivity mainActivity;
    private List<LocalMusicResource> recentMusicResources = null;
    private ListView listView;
    private MusicAdapter adapter;

    public static RecentFragment getRecentFragment() {
        RecentFragment recentFragment = new RecentFragment();
        return recentFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_recent_fragment, null);
        recentMusicResources = loadDate();
        MusicAdapter adapter = new MusicAdapter(getActivity(), recentMusicResources);
        listView = (ListView) view.findViewById(R.id.recent_lv);
        listView.setAdapter(adapter);
        return view;
    }

    public List<LocalMusicResource> loadDate() {
        try {
            app = (MusicApplication) getActivity().getApplication();
            //recentMusicResources = app.dbRecentUtils.findAll(LocalMusicResource.class);
            recentMusicResources = app.dbRecentUtils.findAll(Selector.from(LocalMusicResource.class).orderBy(LocalMusicResource.PLAY_TIME,true));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return recentMusicResources;
    }
}
