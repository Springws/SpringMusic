package com.wusen.springmusic;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.wusen.music_resource.LocalMusicResource;
import com.wusen.utils.MediaUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import douzi.android.view.DefaultLrcBuilder;
import douzi.android.view.ILrcBuilder;
import douzi.android.view.LrcRow;
import douzi.android.view.LrcView;

public class MusicDetailActivity extends BaseActivity implements OnClickListener, SeekBar.OnSeekBarChangeListener {
    private TextView titleOfTv;
    private TextView currentTimeOfTv;
    private TextView endTimeOfTv;
    private ImageView albumOfIv;
    private ImageView previousOfIv;
    private ImageView nextOfIv;
    private ImageView playerOfIv;
    private ImageView menuOfIv;
    private SeekBar seekBar;
    private ViewPager pager;
    private boolean isPause;
    private TextView LrcOfTV;
    private ImageView ivOfLike;
    private MyAdapter myAdapter;
    private MusicApplication app;
    private int pagerPosition;
    private LrcView lrcView;
    List<View> viewList;
    private final static int UPDATE_TIME = 1;
    private List<LocalMusicResource> resourceList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TIME:
                    currentTimeOfTv.setText(MediaUtils.timeFormat((Long) msg.obj));
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_detail_layout);

        initView();
        pagerPosition = getIntent().getIntExtra("pager position",0);
        if(pagerPosition==0) {
            resourceList = MediaUtils.musicResourceList(this);
        }else if(pagerPosition==1)
        {
            app = (MusicApplication) getApplication();
            resourceList = MediaUtils.likeMusicResourceList(this,app.dbLikeUtils);
        }
        myAdapter = new MyAdapter();
        initPager();
        register();
        bindPlayService();


    }

    @Override
    protected void onPause() {
        super.onPause();
        unBindPlayService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unBindPlayService();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindPlayService();

    }

    //更新进度条
    @Override
    public void publish(int progress) {
        seekBar.setProgress(progress);
        Message message = new Message();
        message.what = UPDATE_TIME;
        message.obj = (long) progress;
        handler.sendMessage(message);
    }

    @Override
    public void change(int position) {
        LocalMusicResource musicResource;
        try {
            musicResource = resourceList.get(position);
        } catch (Exception e) {
            resourceList = MediaUtils.musicResourceList(this);
            musicResource = resourceList.get(position);
        }
        if (this.musicPlayerService.getIsPlay()) {
            playerOfIv.setImageResource(R.drawable.pause);
        } else {
            playerOfIv.setImageResource(R.drawable.player);
        }
        titleOfTv.setText(musicResource.getMusicName());
        endTimeOfTv.setText(MediaUtils.timeFormat(musicResource.getTime()));
        seekBar.setProgress(0);
        seekBar.setMax((int) musicResource.getTime());

        try {
            Bitmap bitmap = MediaUtils.getArtwork(this, musicResource.getId(), musicResource.getAlbumId(), true, false);
            albumOfIv.setImageBitmap(bitmap);
        } catch (Exception e) {
            Bitmap bitmap1 = MediaUtils.getDefaultArtwork(this, false);
            albumOfIv.setImageBitmap(bitmap1);
        }
        switch (MusicPlayerService.playMode) {
            case MusicPlayerService.ORDER_PLAY:
                menuOfIv.setImageResource(R.drawable.playorder);
                menuOfIv.setTag(MusicPlayerService.ORDER_PLAY);
                break;
            case MusicPlayerService.RANDOM_PLAY:
                menuOfIv.setImageResource(R.drawable.playrandom);
                menuOfIv.setTag(MusicPlayerService.RANDOM_PLAY);
                break;
            case MusicPlayerService.SINGLE_PLAY:
                menuOfIv.setImageResource(R.drawable.playsingle);
                menuOfIv.setTag(MusicPlayerService.SINGLE_PLAY);
                break;
            default:
                break;
        }
        LocalMusicResource music = resourceList.get(musicPlayerService.getPosition());
        LocalMusicResource likeMusic = null;
        try {
            likeMusic = app.dbLikeUtils.findFirst(
                    Selector.from(LocalMusicResource.class).where(" music_id", "=", music.getMusic_id()));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(likeMusic != null)
        {
            ivOfLike.setImageResource(R.drawable.like1);
        }else {
            ivOfLike.setImageResource(R.drawable.like);
        }

//        String songName = musicResource.getMusicName();
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+songName+".lrc";
//        File file = new File(path);
//        try{
//
//            loadLrc(file);
//            Log.i("file",path);
//        }
//        catch (Exception e){
//            Toast.makeText(MusicDetailActivity.this,"没有找到歌词",Toast.LENGTH_SHORT).show();
//        }
    }


    public void initView() {
        currentTimeOfTv = (TextView) findViewById(R.id.tv_current_time);
        endTimeOfTv = (TextView) findViewById(R.id.end_time);
        playerOfIv = (ImageView) findViewById(R.id.iv_player);
        previousOfIv = (ImageView) findViewById(R.id.iv_previous);
        nextOfIv = (ImageView) findViewById(R.id.iv_next);
        menuOfIv = (ImageView) findViewById(R.id.iv_menu);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        pager = (ViewPager) findViewById(R.id.pager);
        ivOfLike = (ImageView) findViewById(R.id.iv_like);

    }

    public void register() {
        titleOfTv.setOnClickListener(this);
        playerOfIv.setOnClickListener(this);
        previousOfIv.setOnClickListener(this);
        nextOfIv.setOnClickListener(this);
        menuOfIv.setOnClickListener(this);
        albumOfIv.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        ivOfLike.setOnClickListener(this);


    }

    public void initPager() {
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.music_layout, null);
        titleOfTv = (TextView) view1.findViewById(R.id.music_name);
        albumOfIv = (ImageView) view1.findViewById(R.id.iv_album);
        View view2 = inflater.inflate(R.layout.lrc_layout, null);
        lrcView = (LrcView) view2.findViewById(R.id.lrc_view);
//        lrcView.setListener(new ILrcView.LrcViewListener() {
//            @Override
//            public void onLrcSeeked(int newPosition, LrcRow row) {
//                if(musicPlayerService.getIsPlay())
//                {
//                    musicPlayerService.seekTo((int) row.time);
//                }
//            }
//        });

//        lrcView.setBackgroundResource(R.drawable.lrcbackground);
//        lrcView.getBackground().setAlpha(150);
        lrcView.setLoadingTipText("正在加载歌词");
        viewList = new ArrayList<>();
        viewList.add(view1);
        viewList.add(view2);
        pager.setAdapter(myAdapter);
    }

    public void loadLrc(File file)
    {
        FileInputStream in;
        StringBuffer buffer = new StringBuffer();
        try {
            in = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line=reader.readLine()) != null)
            {
                buffer.append(line);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("file",buffer.toString());
        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(buffer.toString());
        lrcView.setLrc(rows);
    }

    class MyAdapter extends android.support.v4.view.PagerAdapter {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_player:
                if (musicPlayerService.getIsPlay()) {
                    playerOfIv.setImageResource(R.drawable.player);
                    musicPlayerService.pause();
                } else {
                    if (musicPlayerService.isPause()) {
                        playerOfIv.setImageResource(R.drawable.pause);
                        musicPlayerService.startCurrentMusic();
                    } else {
                        musicPlayerService.play(musicPlayerService.getPosition());
                    }
                    isPause = false;
                }
                break;

            case R.id.iv_next:
                musicPlayerService.next();
                break;

            case R.id.iv_previous:
                musicPlayerService.previous();
                break;

            case R.id.iv_menu:
                int mode = (int) menuOfIv.getTag();
                switch (mode) {
                    case MusicPlayerService.ORDER_PLAY:
                        menuOfIv.setImageResource(R.drawable.playsingle);
                        menuOfIv.setTag(MusicPlayerService.SINGLE_PLAY);
                        musicPlayerService.setPlayMode(musicPlayerService.SINGLE_PLAY);
                        Toast.makeText(MusicDetailActivity.this, R.string.SINGLE_PLAY, Toast.LENGTH_SHORT).show();
                        break;
                    case MusicPlayerService.SINGLE_PLAY:
                        menuOfIv.setImageResource(R.drawable.playrandom);
                        menuOfIv.setTag(MusicPlayerService.RANDOM_PLAY);
                        musicPlayerService.setPlayMode(musicPlayerService.RANDOM_PLAY);
                        Toast.makeText(MusicDetailActivity.this, R.string.RANDOM_PLAY, Toast.LENGTH_SHORT).show();
                        break;
                    case MusicPlayerService.RANDOM_PLAY:
                        menuOfIv.setImageResource(R.drawable.playorder);
                        menuOfIv.setTag(MusicPlayerService.ORDER_PLAY);
                        musicPlayerService.setPlayMode(musicPlayerService.ORDER_PLAY);
                        Toast.makeText(MusicDetailActivity.this, R.string.ORDER_PLAY, Toast.LENGTH_SHORT).show();
                        break;
                }

            case R.id.iv_like:
                LocalMusicResource music = resourceList.get(musicPlayerService.getPosition());
                try {
                    LocalMusicResource likeMusic = app.dbLikeUtils.findFirst(Selector.from(LocalMusicResource.class).where(" music_id", "=", music.getMusic_id()));
                    if (likeMusic != null) {
                        app.dbLikeUtils.deleteById(LocalMusicResource.class, likeMusic.getMusic_id());
                        ivOfLike.setImageResource(R.drawable.like);
                        Toast.makeText(MusicDetailActivity.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                    } else {
                        music.setMusic_id(music.getId());
                        app.dbLikeUtils.save(music);
                        ivOfLike.setImageResource(R.drawable.like1);
                        Toast.makeText(MusicDetailActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if (fromUser) {
            musicPlayerService.seekTo(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
