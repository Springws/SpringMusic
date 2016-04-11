package com.wusen.music_resource;

/**
 * Created by 15059 on 2016/3/6.
 */
public class LocalMusicResource {

    public static final String TIME= "time";
    public static final String URL = "url";
    public static final String _ID = "id";
    public static final String SIZE = "size";
   ;public static final String ALBUM_ID = "albumId";
    public static final String MUSIC_ID = "music_id";
    public static final String SINGER = "singer";
    public static final String ALBUM = "album";
    public static final String IS_MUSIC = "isMusic";
    public static final String MUSIC_NAME = "musicName";
    public static final String PLAY_TIME = "play_time";
    public static final String RECENT_ID = "recent_id";

    private long id; //自增长的id
    private long music_id; //存入收藏数据库一个id和contentProvider中的id一致
    private long time;
    private long size;
    private long albumId;
    private String url;
    private String musicName;
    private String singer;
    private String album;
    private int isMusic;
    private long recent_id;//存入最近播放的数据库的id

    public long getRecent_id() {
        return recent_id;
    }

    public void setRecent_id(long recent_id) {
        this.recent_id = recent_id;
    }

    public long getPlay_time() {
        return play_time;
    }

    public void setPlay_time(long play_time) {
        this.play_time = play_time;
    }

    private long play_time;

    public long getMusic_id() {
        return music_id;
    }

    public void setMusic_id(long music_id) {
        this.music_id = music_id;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    public long getAlbumId() {

        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public void setMusic(int music) {
        isMusic = music;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public long getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getSinger() {
        return singer;
    }

    public String getAlbum() {
        return album;
    }


}
