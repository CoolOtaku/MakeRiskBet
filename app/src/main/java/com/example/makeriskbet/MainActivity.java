package com.example.makeriskbet;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.imgSlot)
    ImageButton imgSlot;
    @BindView(R.id.imgRoulette)
    ImageButton imgRoulette;
    @BindView(R.id.musicOnOfImage)
    ImageButton musicOnOfImage;
    @BindView(R.id.imgWheel)
    ImageView imgWheel;

    @BindView(R.id.textCoins)
    TextView textCoins;

    private int uiOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideNavBar();

        ButterKnife.bind(this);

        if (App.sp.getBoolean("MUSIC_ON_OF", true)) {
            Music.play(this, R.raw.main);
            musicOnOfImage.setImageResource(R.drawable.ic_music_note);
        }

        ObjectAnimator scaleDown1 = ObjectAnimator.ofPropertyValuesHolder(imgSlot,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown1.setDuration(310);
        scaleDown1.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown1.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown1.start();

        ObjectAnimator scaleDown2 = ObjectAnimator.ofPropertyValuesHolder(imgRoulette,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown2.setDuration(310);
        scaleDown2.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown2.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown2.start();

        imgWheel.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_indefinitely));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavBar();
    }

    private void hideNavBar() {
        View decor = getWindow().getDecorView();
        uiOpt = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decor.setSystemUiVisibility(uiOpt);
    }

    public void PlaySlot(View view) {
        startActivity(new Intent(MainActivity.this, SlotActivity.class));
    }

    public void PlayRoulette(View view) {
        startActivity(new Intent(MainActivity.this, RouletteActivity.class));
    }

    public void musicOnOf(View view) {
        if (Music.isPlaying()) {
            Music.stop();
            musicOnOfImage.setImageResource(R.drawable.ic_music_off);
        } else {
            Music.play(this, R.raw.main);
            musicOnOfImage.setImageResource(R.drawable.ic_music_note);
        }
        App.SaveMusicSetting();
    }

    public void GiveCoins(View view) {
        if(App.COINS <= 0){
            App.COINS = 300;
            App.SaveCOINS();
            textCoins.setText(String.valueOf(App.COINS));
        }else{
            CustomToast.showToast(MainActivity.this, getString(R.string.no_take_coins), true);
        }
    }

    public void ExitGame(View view) {
        finish();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textCoins.setText(String.valueOf(App.COINS));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (App.sp.getBoolean("MUSIC_ON_OF", true)) Music.play(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.pause();
    }

}