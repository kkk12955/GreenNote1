package com.im.tku.greennote;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by B310 on 2017/8/7.
 */

public class MyAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    private List<Curse> curses;
    public MyAdapter(Context context, List<Curse> curses){
        myInflater = LayoutInflater.from(context);
        this.curses = curses;

    }

    private class ViewHolder {
        TextView txtName;
        TextView txtLocation;
        public ViewHolder(TextView txtName, TextView txtLocation){
            this.txtName = txtName;
            this.txtLocation = txtLocation;
        }
    }
    @Override
    public int getCount() {
        return curses.size();
    }

    @Override
    public Object getItem(int arg0) {
        return curses.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return curses.indexOf(getItem(position));
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            convertView = myInflater.inflate(R.layout.activity_curse_page, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.title),
                    (TextView) convertView.findViewById(R.id.time)
            );
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Curse movie = (Curse) getItem(position);
        //0 = movie, 1 = title, 2 = nine
        int color_title[] = {Color.WHITE,Color.WHITE,Color.YELLOW};
        int color_time[] = {Color.WHITE,Color.WHITE,Color.YELLOW};
        int color_back[] = {Color.BLACK,Color.BLUE,Color.BLACK};
        int time_vis[] = {View.VISIBLE,View.GONE,View.VISIBLE};

        holder.txtName.setText("123");

        return convertView;
    }
}
