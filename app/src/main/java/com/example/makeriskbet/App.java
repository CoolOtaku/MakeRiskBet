package com.example.makeriskbet;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application {

    public static int COINS = 300;

    public static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();
        COINS = sp.getInt("COINS", 300);
    }

    public static void SaveCOINS() {
        editor.putInt("COINS", COINS);
        editor.apply();
    }

    public static void SaveMusicSetting() {
        editor.putBoolean("MUSIC_ON_OF", Music.isPlaying());
        editor.apply();
    }

}
