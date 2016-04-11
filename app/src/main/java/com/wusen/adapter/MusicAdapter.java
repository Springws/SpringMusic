package com.wusen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wusen.music_resource.LocalMusicResource;
import com.wusen.springmusic.R;
import com.wusen.utils.MediaUtils;

import java.util.List;

/**
 * Created by 15059 on 2016/3/6.
 */
public class MusicAdapter extends BaseAdapter {

    Context context;
    List<LocalMusicResource> musicResourceList;
    public MusicAdapter(Context context, List<LocalMusicResource> musicResourceList){
        this.context = context;
        this.musicResourceList = musicResourceList;
    }
    public void setMusicResourceList(List<LocalMusicResource> musicResourceList)
    {
        this.musicResourceList = musicResourceList;
    }

    @Override
    public int getCount() {
        if(musicResourceList != null) {
            return musicResourceList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return musicResourceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        View view;
        if(convertView==null)
        {
             view =LayoutInflater.from(context).inflate(R.layout.local_music_item,null);
             viewHolder = new ViewHolder();
             viewHolder.tv_music = (TextView) view.findViewById(R.id.music_name);
             viewHolder.tv_singer = (TextView) view.findViewById(R.id.singer_name);
             viewHolder.tv_time = (TextView) view.findViewById(R.id.music_long);
             view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
            LocalMusicResource musicResouce = musicResourceList.get(i);
            viewHolder.tv_music.setText(musicResouce.getMusicName());
            viewHolder.tv_singer.setText(musicResouce.getSinger());
            viewHolder.tv_time.setText(MediaUtils.timeFormat(musicResouce.getTime()));

        return view;
    }

    class ViewHolder{
        TextView tv_music;
        TextView tv_singer;
        TextView tv_time;
    }


}
