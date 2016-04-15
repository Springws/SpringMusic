/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wusen.springmusic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.lidroid.xutils.exception.DbException;
import com.wusen.music_resource.LocalMusicResource;
import com.wusen.utils.MediaUtils;

import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final Handler handler = new Handler();
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor = 0xFF11cd6e;
    private ImageView imageView_album;
    private ImageView imageView_player;
    private ImageView imageView_next;
    private TextView textView_music_name;
    private TextView textView_singer;
    private TextView textView_time;
    private LocalMusicFragment musicFragment;
    private MyFavoriteFragment favoriteFragment;
    private RecentFragment recentFragment;
    private MusicApplication app;
    private List<LocalMusicResource> musicResources;
    private boolean isPause = false;
    private int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        textView_music_name = (TextView) findViewById(R.id.music_name);
        textView_singer = (TextView) findViewById(R.id.singer_name);
        textView_time = (TextView) findViewById(R.id.music_long);
        imageView_album = (ImageView) findViewById(R.id.album);
        imageView_player = (ImageView) findViewById(R.id.music_player);
        imageView_next = (ImageView) findViewById(R.id.music_next);

        imageView_player.setOnClickListener(this);
        imageView_next.setOnClickListener(this);
        imageView_album.setOnClickListener(this);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
        changeColor(currentColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unBindPlayService();
    }

    public void initList() {
        int i = pager.getCurrentItem();
        if (i == 0) {
            musicResources = MediaUtils.musicResourceList(this);
        }
        if (i == 1) {
            app = (MusicApplication) getApplication();
            musicResources = MediaUtils.likeMusicResourceList(this, app.dbLikeUtils);
        }
        if(i ==2){
            app = (MusicApplication) getApplication();
            try {
                musicResources = app.dbRecentUtils.findAll(LocalMusicResource.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

    }

    public void changeUiStatus(int position) {
        //initList();
        musicResources = musicPlayerService.getMusicResourceList();
        this.position = position;
        LocalMusicResource musicResource;
        try {
            Log.i("main","success");
            musicResource = musicResources.get(position);
        } catch (Exception e) {
            Log.i("main","error");
            musicResources = MediaUtils.musicResourceList(this);
            musicResource = musicResources.get(position);
        }

        if (position < musicResources.size() && position >= 0) {
            textView_music_name.setText(musicResource.getMusicName());
            textView_singer.setText(musicResource.getSinger());
            if (musicPlayerService.getIsPlay()) {
                imageView_player.setImageResource(R.drawable.pause);
            } else {
                imageView_player.setImageResource(R.drawable.player);
            }
            try {
                if (pager.getCurrentItem() == 0) {
                    Bitmap bitmap = MediaUtils.getArtwork(this, musicResource.getId(), musicResource.getAlbumId(), true, true);
                    imageView_album.setImageBitmap(bitmap);
                } else if (pager.getCurrentItem() == 1) {
                    Bitmap bitmap = MediaUtils.getArtwork(this, musicResource.getMusic_id(), musicResource.getAlbumId(), true, true);
                    imageView_album.setImageBitmap(bitmap);
                }else if(pager.getCurrentItem() ==2){
                    Bitmap bitmap = MediaUtils.getArtwork(this, musicResource.getId(), musicResource.getAlbumId(), true, true);
                    imageView_album.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                Bitmap bitmap1 = MediaUtils.getDefaultArtwork(this, true);
                imageView_album.setImageBitmap(bitmap1);
            }

        }
    }


    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

        if (pager.getCurrentItem() == 0 && musicFragment != null) {
            changeUiStatus(position);
        }
        if (pager.getCurrentItem() == 1 && favoriteFragment != null) {
            changeUiStatus(position);
        }
        if (pager.getCurrentItem() == 2 && recentFragment != null) {
            changeUiStatus(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    private void changeColor(int newColor) {

        tabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);

        }

        currentColor = newColor;

    }

    public void onColorClicked(View v) {

        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.music_player:
                if (musicPlayerService.getIsPlay()) {
                    imageView_player.setImageResource(R.drawable.player);
                    musicPlayerService.pause();
                    isPause = true;
                } else {
                    if (musicPlayerService.isPause()) {
                        imageView_player.setImageResource(R.drawable.pause);
                        musicPlayerService.startCurrentMusic();
                    } else {
                        musicPlayerService.play(musicPlayerService.getPosition());
                    }
                    isPause = false;
                }
                break;
            case R.id.music_next:
                musicPlayerService.next();
                break;
            case R.id.album:
                Intent intent = new Intent(MainActivity.this, MusicDetailActivity.class);
                intent.putExtra("pager position", pager.getCurrentItem());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"本地音乐", "我的收藏","最近播放"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {return TITLES.length;}


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                musicFragment = LocalMusicFragment.getMusicFragment();
                return musicFragment;
            } else if (position == 1) {
                favoriteFragment = MyFavoriteFragment.getMyFavoriteFragment();
                return favoriteFragment;
           } else if (position == 2) {
                recentFragment = RecentFragment.getRecentFragment();
                return recentFragment;
            }
            return null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicApplication app = (MusicApplication) getApplication();
        SharedPreferences.Editor editor = app.sp.edit();
        editor.putInt("position", musicPlayerService.getPosition());
        editor.putInt("mode", MusicPlayerService.playMode);
        editor.commit();
    }
}