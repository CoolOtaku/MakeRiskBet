package com.example.makeriskbet;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SlotActivity extends AppCompatActivity {

    @BindView(R.id.downarrow)
    ImageView downArrow;
    @BindView(R.id.plus)
    ImageView btnPlus;
    @BindView(R.id.minus)
    ImageView btnMinus;
    @BindView(R.id.info)
    ImageView btnInfo;
    @BindView(R.id.handup)
    ImageView btnHandUp;
    @BindView(R.id.handdown)
    ImageView btnHandDown;

    @BindView(R.id.bettxt)
    TextView betTxt;
    @BindView(R.id.textCoins)
    TextView moneyTxt;

    @BindView(R.id.firstpart)
    ConstraintLayout fp;

    private ViewFlipper[][] flipper;

    private List<Integer> images;
    private Context context;
    private Thread slotTr1, slotTr2, slotTr3;
    private Runnable slot1, slot2, slot3, winnerCheck;
    boolean startSl1 = false, startSl2 = false, startSl3 = false, finishGame = false;
    private int bet = 1;

    private int uiOpt;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);
        hideNavBar();

        ButterKnife.bind(this);

        if (App.sp.getBoolean("MUSIC_ON_OF", true)) Music.play(this);

        initFlippers();
        context = getApplicationContext();
        downarrowAnimation();
        touchEnable();
        initThreads();

        slotTr1.start();
        slotTr2.start();
        slotTr3.start();

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_info_slot);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window windowAlDl = dialog.getWindow();

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.systemUiVisibility = uiOpt;
        windowAlDl.setAttributes(layoutParams);

        Button yes = (Button) dialog.findViewById(R.id.alertbox_yes);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavBar();
    }

    private void hideNavBar(){
        View decor = getWindow().getDecorView();
        uiOpt = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decor.setSystemUiVisibility(uiOpt);
    }

    private void initButtonMinusTouch(){
        btnMinus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnMinus.setImageResource(R.drawable.minuspush);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnMinus.setImageResource(R.drawable.minus);
                    minusBet();
                }
                return true;
            }
        });
    }

    private void initButtonPlusTouch(){
        btnPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnPlus.setImageResource(R.drawable.pluspush);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnPlus.setImageResource(R.drawable.plus);
                    plusBet();
                }
                return true;
            }
        });
    }

    private void initButtonInfoTouch(){
        btnInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnInfo.setImageResource(R.drawable.infopush);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnInfo.setImageResource(R.drawable.info);
                    dialog.show();
                }
                return true;
            }
        });
    }

    private void initButtonHandUpClick(){
        btnHandUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinSlots();
            }
        });
    }

    private void downarrowAnimation() {
        downArrow.setBackgroundResource(R.drawable.arrow_animation);
        AnimationDrawable animation = (AnimationDrawable) downArrow.getBackground();
        animation.start();
    }

    private void setBetTxtMoneyTxt() {
        moneyTxt.setText(String.valueOf(App.COINS));
        betTxt.setText(String.valueOf(bet));
    }

    private void minusBet() {
        if (bet >= 2){
            bet -= 1;
            setBetTxtMoneyTxt();
        }
    }

    private void plusBet() {
        if (bet <= App.COINS - 1){
            bet += 1;
            setBetTxtMoneyTxt();
        }
    }

    private void initThreads() {
        slot1 = new Runnable() {
            @Override
            public void run() {
                flipper[0][0].showNext();
                flipper[1][0].showNext();
                flipper[2][0].showNext();
            }
        };
        slot2 = new Runnable() {
            @Override
            public void run() {
                flipper[0][1].showPrevious();
                flipper[1][1].showPrevious();
                flipper[2][1].showPrevious();
            }
        };
        slot3 = new Runnable() {
            @Override
            public void run() {
                flipper[0][2].showNext();
                flipper[1][2].showNext();
                flipper[2][2].showNext();
            }
        };

        slotTr1 = new Thread() {
            Random r = new Random();
            int speed1, speed2, speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSl1) {
                        bet = Integer.parseInt(betTxt.getText().toString());
                        if (App.COINS > 0 && App.COINS >= bet) {

                            speed1 = r.nextInt(20) + 5;
                            speed2 = r.nextInt(20) + 5;
                            speed3 = r.nextInt(20) + 5;

                            try {
                                for (i = 0; i < speed1; ++i) {
                                    runOnUiThread(slot1);
                                    sleep(100);
                                }
                                for (i = 0; i < speed2; ++i) {
                                    runOnUiThread(slot1);
                                    sleep(200);
                                }
                                for (i = 0; i < speed3; ++i) {
                                    runOnUiThread(slot1);
                                    sleep(300);
                                }
                            } catch (Exception ex) {
                            }

                            startSl1 = false;
                            runOnUiThread(winnerCheck);
                        }
                    }
                    try {sleep(35);} catch (InterruptedException e) {e.printStackTrace();}
                }
            }
        };

        slotTr2 = new Thread() {
            Random r = new Random();
            int speed1, speed2, speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSl2) {

                        bet = Integer.parseInt(betTxt.getText().toString());
                        if (App.COINS > 0 && App.COINS >= bet) {

                            speed1 = r.nextInt(20) + 5;
                            speed2 = r.nextInt(20) + 5;
                            speed3 = r.nextInt(20) + 5;

                            try {
                                for (i = 0; i < speed1; ++i) {
                                    runOnUiThread(slot2);
                                    sleep(100);
                                }
                                for (i = 0; i < speed2; ++i) {
                                    runOnUiThread(slot2);
                                    sleep(200);
                                }
                                for (i = 0; i < speed3; ++i) {
                                    runOnUiThread(slot2);
                                    sleep(300);
                                }
                            } catch (Exception ex) {
                            }

                            startSl2 = false;
                            runOnUiThread(winnerCheck);
                        }
                    }
                    try {sleep(35);} catch (InterruptedException e) {e.printStackTrace();}
                }
            }
        };

        slotTr3 = new Thread() {
            Random r = new Random();
            int speed1, speed2, speed3;
            int i;

            @Override
            public void run() {
                while (!finishGame) {
                    while (startSl3) {

                        bet = Integer.parseInt(betTxt.getText().toString());
                        if (App.COINS > 0 && App.COINS >= bet) {

                            speed1 = r.nextInt(20) + 5;
                            speed2 = r.nextInt(20) + 5;
                            speed3 = r.nextInt(20) + 5;

                            try {
                                for (i = 0; i < speed1; ++i) {
                                    runOnUiThread(slot3);
                                    sleep(100);
                                }
                                for (i = 0; i < speed2; ++i) {
                                    runOnUiThread(slot3);
                                    sleep(200);
                                }
                                for (i = 0; i < speed3; ++i) {
                                    runOnUiThread(slot3);
                                    sleep(300);
                                }
                            } catch (Exception ex) {
                            }

                            startSl3 = false;
                            runOnUiThread(winnerCheck);
                        }
                    }
                    try {sleep(35);} catch (InterruptedException e) {e.printStackTrace();}
                }
            }
        };

        winnerCheck = new Runnable() {
            @Override
            public void run() {
                if (!startSl1 && !startSl2 && !startSl3) {
                    int num1 = (flipper[1][0].getDisplayedChild() + 4) % 7;
                    int num2 = (flipper[1][1].getDisplayedChild() + 6) % 7;
                    int num3 = (flipper[1][2].getDisplayedChild() + 1) % 7;

                    int multiply = -1;

                    if (num1 == 0 && num2 == 0 && num3 ==0){
                        multiply = 10;
                    }
                    else if (num1 == num2 && num2 == num3){
                        multiply = 5;
                    }
                    else if(num1 == num2 || num1 == num3|| num2 == num3){
                        multiply = 3;
                    }

                    App.COINS += bet * multiply;
                    App.SaveCOINS();
                    if (multiply != -1) {
                        Music.sound(SlotActivity.this, R.raw.kassa);
                        CustomToast.showToast(SlotActivity.this, getString(R.string.you_win, bet * multiply), false);
                    }

                    if(App.COINS <= 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        App.COINS = 0;
                        CustomToast.showToast(SlotActivity.this, getString(R.string.no_coins), true);
                    }
                    if(bet > App.COINS){
                        bet = App.COINS;
                    }

                    setBetTxtMoneyTxt();
                    btnHandUp.setVisibility(View.VISIBLE);
                    btnHandDown.setVisibility(View.GONE);
                    touchEnable();
                }
                try {sleep(35);} catch (InterruptedException e) {e.printStackTrace();}
            }
        };
    }

    private void spinSlots() {
        Music.sound(SlotActivity.this, R.raw.spin);
        startSl1 = true;
        startSl2 = true;
        startSl3 = true;
        btnHandUp.setVisibility(View.GONE);
        btnHandDown.setVisibility(View.VISIBLE);
        touchDisable();
    }

    private void touchEnable() {
        initButtonMinusTouch();
        initButtonPlusTouch();
        initButtonInfoTouch();
        initButtonHandUpClick();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void touchDisable() {
        btnPlus.setOnTouchListener(null);
        btnMinus.setOnTouchListener(null);
        btnHandUp.setOnClickListener(null);
    }

    private void initFlippers() {
        images = new ArrayList<>(Arrays.asList(R.drawable.bar, R.drawable.bell, R.drawable.cherry, R.drawable.diamond, R.drawable.lemon, R.drawable.plum, R.drawable.seven));

        flipper = new ViewFlipper[][]{{findViewById(R.id.viewflipper00), findViewById(R.id.viewflipper01), findViewById(R.id.viewflipper02)},
                {findViewById(R.id.viewflipper10), findViewById(R.id.viewflipper11), findViewById(R.id.viewflipper12)},
                {findViewById(R.id.viewflipper20), findViewById(R.id.viewflipper21), findViewById(R.id.viewflipper22)} };
    }

    public void Exit(View view) {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        moneyTxt.setText(String.valueOf(App.COINS));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (App.sp.getBoolean("MUSIC_ON_OF", true)) Music.play(this, R.raw.main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.SaveCOINS();
        Music.pause();
    }

}