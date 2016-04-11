package com.wusen.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;
import com.wusen.music_resource.LocalMusicResource;
import com.wusen.springmusic.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 15059 on 2016/3/6.
 */
public class MediaUtils {

    private static final Uri albumUri = Uri.parse("content://media/external/audio/albumart");

    //得到本地音乐
    public static LocalMusicResource getResource(Context context, long _id) {
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                        MediaStore.Audio.Media._ID + "=" + _id, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        LocalMusicResource musicResource = null;

        if (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String URL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));

            if (isMusic != 0) {
                musicResource.setAlbum(album);
                musicResource.setAlbumId(albumId);
                musicResource.setId(id);
                musicResource.setMusicName(name);
                musicResource.setSinger(singer);
                musicResource.setSize(size);
                musicResource.setTime(time);
                musicResource.setUrl(URL);
                musicResource.setIsMusic(isMusic);
            }
        }
        cursor.close();
        return musicResource;
    }

   //得到本地音乐的id
    public static long[] getIDs(Context context) {

        Cursor cursor = context.getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Media._ID}, MediaStore.Audio.Media.DURATION + ">=1500",
                        null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        long[] IDs = null;
        if (cursor != null) {
            IDs = new long[cursor.getCount()];
            for (int i = 0; i < IDs.length; i++) {
                cursor.moveToNext();
                IDs[i] = cursor.getLong(0);
            }

        }
        cursor.close();
        return IDs;
    }

    //得到收藏的音乐列表
    public static List<LocalMusicResource> likeMusicResourceList(Context context, DbUtils dbUtils) {
        List<LocalMusicResource> musicResources = new ArrayList<>();
        List<LocalMusicResource> likeList = null;
        try {
            likeList = dbUtils.findAll(LocalMusicResource.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (likeList != null) {
            try {
                ArrayList<DbModel> nameList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource.MUSIC_NAME));
                ArrayList<DbModel> _idList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource._ID));
                ArrayList<DbModel> albumList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource.ALBUM));
                ArrayList<DbModel> albumIdList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource.ALBUM_ID));
                ArrayList<DbModel> isMusicList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource.IS_MUSIC));
                ArrayList<DbModel> musicIdList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource.MUSIC_ID));
                ArrayList<DbModel> singerList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource.SINGER));
                ArrayList<DbModel> sizeList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource.SIZE));
                ArrayList<DbModel> timeList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource.TIME));
                ArrayList<DbModel> urlList = (ArrayList<DbModel>) dbUtils.
                        findDbModelAll(Selector.from(LocalMusicResource.class).select(LocalMusicResource.URL));
                for (int i = 0; i < likeList.size(); i++) {
                    LocalMusicResource musicResource = new LocalMusicResource();

                    LocalMusicResource likeResource = likeList.get(i);
                    String name = nameList.get(i).getString(LocalMusicResource.MUSIC_NAME);
                    long _id = _idList.get(i).getLong(LocalMusicResource._ID);
                    String album = albumList.get(i).getString(LocalMusicResource.ALBUM);
                    long albumId = albumIdList.get(i).getLong(LocalMusicResource.ALBUM_ID);
                    int isMusic = isMusicList.get(i).getInt(LocalMusicResource.IS_MUSIC);
                    long musicId = musicIdList.get(i).getLong(LocalMusicResource.MUSIC_ID);
                    String singer = singerList.get(i).getString(LocalMusicResource.SINGER);
                    long size = sizeList.get(i).getLong(LocalMusicResource.SIZE);
                    long time = timeList.get(i).getLong(LocalMusicResource.TIME);
                    String url = urlList.get(i).getString(LocalMusicResource.URL);


                    musicResource.setAlbum(album);
                    musicResource.setAlbumId(albumId);
                    musicResource.setId(_id);
                    musicResource.setMusicName(name);
                    musicResource.setSinger(singer);
                    musicResource.setSize(size);
                    musicResource.setTime(time);
                    musicResource.setUrl(url);
                    musicResource.setIsMusic(isMusic);
                    musicResource.setMusic_id(musicId);

                    musicResources.add(musicResource);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return musicResources;
        }
        return null;

    }

    //得到本地音乐列表
    public static List<LocalMusicResource> musicResourceList(Context context) {
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                        MediaStore.Audio.Media.DURATION + ">=1500", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<LocalMusicResource> musicResources = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            LocalMusicResource musicResource = null;
            cursor.moveToNext();

            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String URL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));

            musicResource = new LocalMusicResource();
            musicResource.setAlbum(album);
            musicResource.setAlbumId(albumId);
            musicResource.setId(id);
            musicResource.setMusicName(name);
            musicResource.setSinger(singer);
            musicResource.setSize(size);
            musicResource.setTime(time);
            musicResource.setUrl(URL);
            musicResource.setIsMusic(isMusic);

            musicResources.add(musicResource);
        }
        cursor.close();
        return musicResources;
    }

    //格式化时间
    public static String timeFormat(long time) {
        String minute = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (minute.length() < 2) {
            minute = "0" + minute;
        }
        if (sec.length() == 4) {
            sec = "0" + sec;
        }
        if (sec.length() == 3) {
            sec = "00" + sec;
        }
        if (sec.length() == 2) {
            sec = "000" + sec;
        }
        if (sec.length() == 1) {
            sec = "0000" + sec;
        }
        String timeFormat = minute + ":" + sec.trim().substring(0, 2);
        return timeFormat;
    }

    //得到专辑封面
    public static Bitmap getDefaultArtwork(Context context, boolean small) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
//        if (small) {
//            return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.defaultalum), null, opt);
//        }
        return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.defaultalum), null, opt);
    }

    public static Bitmap getArtwork(Context context, long song_id, long album_id, boolean allowDefault, boolean small) {
        if (album_id < 0) {
            if (song_id < 0) {
                Bitmap bitmap = getArtworkFromFile(context, song_id, -1);
                if (bitmap != null)
                    return bitmap;
            }

            if (allowDefault) {
                return getDefaultArtwork(context, small);
            }
            return null;
        }
        ContentResolver resolver = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumUri, album_id);
        if (uri != null) {
            InputStream inputStream = null;
            try {
                inputStream = resolver.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                if (small) {
                    options.inSampleSize = computeSampleSize(options, 40);
                } else {
                    options.inSampleSize = computeSampleSize(options, 600);
                }
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                inputStream = resolver.openInputStream(uri);
                return BitmapFactory.decodeStream(inputStream, null, options);
            } catch (FileNotFoundException e) {
                Bitmap bitmap = getArtworkFromFile(context, song_id, album_id);
                if (bitmap != null) {
                    if (bitmap.getConfig() == null) {
                        bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
                        if (bitmap == null && allowDefault) {
                            return getDefaultArtwork(context, small);
                        }
                    }
                } else if (allowDefault) {
                    bitmap = getDefaultArtwork(context, small);
                }
                return bitmap;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        return null;
    }


    //计算图片大小
    public static int computeSampleSize(BitmapFactory.Options options, int target) {
        int w = options.outWidth;
        int h = options.outHeight;
        int candidateW = w / target;
        int candidateH = h / target;
        int candidate = Math.max(candidateW, candidateH);
        if (candidate == 0) {
            return 1;
        }
        if (candidate > 1) {
            if ((w > target) && (w / candidate) < target) {
                candidate -= 1;
            }
        }
        if (candidate > 1) {
            if ((h > target) && (h / candidate) < target) {
                candidate -= 1;
            }
        }
        return candidate;
    }

    public static Bitmap getArtworkFromFile(Context context, long song_id, long album_id) {
        Bitmap bitmap = null;
        if (song_id < 0 && album_id < 0) {
            throw new IllegalArgumentException(
                    "must specify an ablum or a song id");
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        FileDescriptor fd = null;
        if (album_id < 0) {
            Uri uri = Uri.parse("content://media/external/audio/media/" + song_id + "/albumart");
            ParcelFileDescriptor pfd = null;
            try {
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (pfd != null) {
                fd = pfd.getFileDescriptor();
            }

        } else {
            Uri uri = ContentUris.withAppendedId(albumUri, album_id);
            ParcelFileDescriptor pfd = null;
            try {
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (pfd != null) {
                fd = pfd.getFileDescriptor();
            }
        }
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = 100;
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
        return bitmap;

    }

    public static List<HashMap<String, String>> getMusicMaps(List<LocalMusicResource> musicResouceList) {
        List<HashMap<String, String>> maps = new ArrayList<>();
        for (Iterator iterator = musicResouceList.iterator(); iterator.hasNext(); ) {
            LocalMusicResource resouce = (LocalMusicResource) iterator.next();
            HashMap<String, String> map = new HashMap<>();
            map.put("title", resouce.getMusicName());
            map.put("singer", resouce.getSinger());
            map.put("album", resouce.getAlbum());
            map.put("duration", String.valueOf(resouce.getTime()));
            map.put("uri", resouce.getUrl());
            map.put("album_id", String.valueOf(resouce.getAlbumId()));
            map.put("song_id", String.valueOf(resouce.getId()));
            map.put("size", String.valueOf(resouce.getSize()));
            maps.add(map);
        }
        return maps;

    }


}


