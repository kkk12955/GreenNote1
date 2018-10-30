package com.im.tku.greennote;

/**
 * Created by B310 on 2017/7/26.
 */

public class Time{
    int h;
    int m;
    public Time(int h, int m) { this.h = h ; this.m = m; }
    public String toString(){return h+"："+String.format("%02d",m);}
    public boolean time_compare(int _h , int _m){
        if(_h*60+_m >= this.h*60+this.m){ return true; }
        return false;
    }
}