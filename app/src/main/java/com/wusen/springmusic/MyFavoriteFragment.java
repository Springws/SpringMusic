package com.wusen.springmusic;


import android.content.Context;
import android.os.Bundle;
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
 * Created by 15059 on 2016/3/21.
 */
public class MyFavoriteFragment extends Fragment {
    private MusicApplication app;
    private MainActivity mainActivity;
    private List<LocalMusicResource> likeMusicResources = null;
    private ListView listView;
    private MusicAdapter adapter;
    public static MyFavoriteFragment getMyFavoriteFragment()
    {
        MyFavoriteFragment myFavoriteFragment = new MyFavoriteFragment();
        return myFavoriteFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        //mainActivity.bindPlayService();
        //likeMusicResources = MediaUtils.likeMusicResourceList(getActivity(),app.dbLikeUtils);
        loadDate();
       // listView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
      //  mainActivity.unBindPlayService();
        adapter = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_my_favorite,null);
            listView = (ListView) view.findViewById(R.id.favorite_lv);
            //loadDate();
           // adapter = new MusicAdapter(getActivity(),likeMusicResources);

            //listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mainActivity.musicPlayerService.setMusicResourceList(likeMusicResources);
                    mainActivity.musicPlayerService.play(i);
                }
            });

            return view;
    }

    public void loadDate()
    {
        app = (MusicApplication) getActivity().getApplication();
        likeMusicResources = MediaUtils.likeMusicResourceList(getActivity(),app.dbLikeUtils);
        adapter = new MusicAdapter(mainActivity,likeMusicResources);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

}
