package com.wusen.springmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.wusen.music_resource.LocalMusicResource;
import com.wusen.utils.MediaUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private MusicUpdateListener musicUpdateListener;

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }

    private MusicApplication app;
    private boolean isPause = false;
    private MediaPlayer mediaPlayer;
    private int currentPosition;
    private List<LocalMusicResource> musicResourceList;
    private ExecutorService es = Executors.newSingleThreadExecutor();


    public static final int ORDER_PLAY = 1;
    public static final int SINGLE_PLAY = 2;
    public static final int RANDOM_PLAY = 3;
    public static int playMode = ORDER_PLAY;

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public MusicPlayerService() {}

    private Random random = new Random();

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch (playMode) {
            case ORDER_PLAY:
                next();
                break;
            case SINGLE_PLAY:
                play(currentPosition);
                break;
            case RANDOM_PLAY:
                currentPosition = random.nextInt(musicResourceList.size());
                play(currentPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }


    class PlayBinder extends Binder {
        public MusicPlayerService getMusicPlayerService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();

    }

    public List<LocalMusicResource> getMusicResourceList() {
        return musicResourceList;
    }

    public void setMusicResourceList(List<LocalMusicResource> musicResouceList) {
        this.musicResourceList = musicResouceList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MusicApplication app = (MusicApplication) getApplication();
        currentPosition = app.sp.getInt("position", 0);
        playMode = app.sp.getInt("mode", ORDER_PLAY);

        mediaPlayer = new MediaPlayer();
        musicResourceList = MediaUtils.musicResourceList(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        es.execute(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (es != null && !es.isShutdown()) {
            es.shutdown();
            es = null;
        }

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (musicUpdateListener != null && mediaPlayer != null && mediaPlayer.isPlaying()) {
                    musicUpdateListener.onPublish(getCurrentProcess());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public int getCurrentProcess() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getPosition() {
        return currentPosition;
    }

    public boolean getIsPlay() {
        return mediaPlayer.isPlaying();
    }

    public void play(int position) {
        if (position < 0 && position > musicResourceList.size()) {
            currentPosition = 0;
            play(currentPosition);
        } else {
            Log.i("service","play");
            LocalMusicResource musicResource = musicResourceList.get(position);
            String name = musicResource.getMusicName();
            try {
                LocalMusicResource recentResource = app.dbRecentUtils.findFirst(Selector.from(LocalMusicResource.class).where(LocalMusicResource.MUSIC_NAME,"=",name));
                long time = System.currentTimeMillis();
                musicResource.setPlay_time(time);
                musicResource.setRecent_id(musicResource.getId());
                Log.i("service","try");
                if(recentResource!=null)
                {
                    Log.i("service","不为空");

                    app.dbRecentUtils.delete(LocalMusicResource.class, WhereBuilder.b(LocalMusicResource.MUSIC_NAME,"=",name));
                    Log.i("service",name);
                    app.dbRecentUtils.save(musicResource);
                }
                if(recentResource == null)
                {
                    Log.i("service","为空");
                    app.dbRecentUtils.save(musicResource);
                }
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, Uri.parse(musicResource.getUrl()));
                mediaPlayer.prepare();
                mediaPlayer.start();
            }catch (Exception e)
            {
                Toast.makeText(this,"这首歌曲无法播放",Toast.LENGTH_SHORT).show();
            }
        }
               currentPosition = position;
        if (musicUpdateListener != null) {
            Log.i("service","onchange");
            musicUpdateListener.onChange(currentPosition);

        }

    }


    public boolean isPause() {
        return isPause;
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    public void next() {
        if (currentPosition >= 0 && currentPosition < musicResourceList.size() - 1) {
            currentPosition++;
        } else {
            currentPosition = 0;
        }
        play(currentPosition);

    }

    public void previous() {
        if (currentPosition > 0 && currentPosition < musicResourceList.size()) {
            currentPosition--;
        } else {
            currentPosition = musicResourceList.size() - 1;
        }
        play(currentPosition);


    }

    public void startCurrentMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start();
            }catch (Exception e){
                Toast.makeText(this,"这首歌曲无法播放",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public interface MusicUpdateListener {
        public void onPublish(int progress);
        public void onChange(int position);
    }


}
