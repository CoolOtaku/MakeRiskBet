package com.example.makeriskbet;

import android.content.Context;
import android.media.MediaPlayer;

public class Music {

    private static MediaPlayer mp = null;
    private static MediaPlayer sp = null;

    public static void play(Context context, int resource) {
        stop();
        mp = MediaPlayer.create(context, resource);
        mp.setLooping(true);
        mp.start();
    }

    public static void sound(Context context, int resource) {
        sp = MediaPlayer.create(context, resource);
        sp.start();
    }

    public static void stop() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public static void play(Context context) {
        if (mp != null) {
            mp.start();
        }else{
            play(context, R.raw.main);
        }
    }

    public static void pause() {
        if(mp != null) {
            mp.pause();
        }
    }

    public static boolean isPlaying(){
        if(mp != null){
            return mp.isPlaying();
        }else{
            return false;
        }
    }

}
