package com.wusen.springmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

/**
 * Created by 15059 on 2016/3/7.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected MusicPlayerService musicPlayerService;
    private Boolean isBlind = false;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    MusicPlayerService.MusicUpdateListener updateListener = new MusicPlayerService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MusicPlayerService.PlayBinder playBinder = (MusicPlayerService.PlayBinder) iBinder;
                musicPlayerService = playBinder.getMusicPlayerService();
                musicPlayerService.setMusicUpdateListener(updateListener);
                updateListener.onChange(musicPlayerService.getPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
               musicPlayerService = null;
        }
    };



         public abstract void publish(int progress);
         public abstract void change(int position);
    //绑定服务
    public void bindPlayService(){
        if(isBlind==false) {

            Intent intent = new Intent(BaseActivity.this, MusicPlayerService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
            isBlind = true;
    }

    public void unBindPlayService() {
        if (isBlind == true) {
            unbindService(connection);
        }
        isBlind = false;
    }



}
