package com.wusen.springmusic;

import android.app.Application;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.wusen.utils.Constant;

/**
 * Created by 15059 on 2016/3/17.
 */
public class MusicApplication extends Application
{
    public static SharedPreferences sp;
    public static DbUtils dbLikeUtils;
    public static DbUtils dbRecentUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Constant.SP_NAME,MODE_PRIVATE);
        dbLikeUtils = DbUtils.create(getApplicationContext(),Constant.DB_FAVORITE_NAME);
        dbRecentUtils = DbUtils.create(getApplicationContext(),Constant.DB_RECENT_NAME);
    }
}
