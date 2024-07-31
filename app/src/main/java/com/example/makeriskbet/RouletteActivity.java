package com.example.makeriskbet;

import android.app.Dialog;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RouletteActivity extends AppCompatActivity {

    @BindView(R.id.RelativeViewChips)
    RelativeLayout RelativeViewChips;
    @BindView(R.id.PlayingField)
    ConstraintLayout PlayingField;

    @BindView(R.id.f1)
    ImageView f1;
    @BindView(R.id.f2)
    ImageView f2;
    @BindView(R.id.f3)
    ImageView f3;

    @BindView(R.id.textCoins)
    TextView textCoins;
    @BindView(R.id.textViewBet)
    TextView textViewBet;
    @BindView(R.id.resultTv)
    TextView resultTv;
    @BindView(R.id.wheel)
    ImageView wheel;
    @BindView(R.id.btnPlayingField)
    ImageView btnPlayingField;

    private static final String[] sectors = {"32 red", "15 black",
            "19 red", "4 black", "21 red", "2 black", "25 red", "17 black", "34 red",
            "6 black", "27 red", "13 black", "36 red", "11 black", "30 red", "8 black",
            "23 red", "10 black", "5 red", "24 black", "16 red", "33 black",
            "1 red", "20 black", "14 red", "31 black", "9 red", "22 black",
            "18 red", "29 black", "7 red", "28 black", "12 red", "35 black",
            "3 red", "26 black", "zero"
    };

    private static final Random RANDOM = new Random();
    private int degree = 0, degreeOld = 0;
    private static final float HALF_SECTOR = 360f / 37f / 2f;

    private int ChipsId = 0;
    private ArrayList<Bet> arrSelectedSectors = new ArrayList<>();
    private ArrayList<Bet> arrSelectedColors = new ArrayList<>();
    private ArrayList<Bet> arrSelectedInterval = new ArrayList<>();
    private ArrayList<Bet> arrSelectedEvenOrOdd = new ArrayList<>();
    private ArrayList<Bet> arrSelected2Or1 = new ArrayList<>();
    private ArrayList<Bet> arrSelectedDozen = new ArrayList<>();

    private int bet = 0;
    private int win = 0;

    private boolean isSpin = false;
    private int uiOpt;
    private boolean toScale = false;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);
        hideNavBar();

        ButterKnife.bind(this);

        if (App.sp.getBoolean("MUSIC_ON_OF", true)) Music.play(this);

        textViewBet.setText(getString(R.string.bet_coins, bet));

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_info_roulette);
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

    public void setChips(View view) {
        f1.setBackground(null);
        f2.setBackground(null);
        f3.setBackground(null);
        switch (view.getId()) {
            case R.id.f1:
                ChipsId = R.drawable.f1;
                break;
            case R.id.f2:
                ChipsId = R.drawable.f2;
                break;
            case R.id.f3:
                ChipsId = R.drawable.f3;
                break;
        }
        view.setBackgroundResource(R.drawable.green_radius);
    }

    public void SetBatInSector(View view) {
        Button vb = (Button) view;
        AddToArrayBet(arrSelectedSectors, String.valueOf(vb.getText()), view);
    }

    public void SetBatInColor(View view) {
        String strColorSector = "";
        switch (view.getId()) {
            case R.id.sectorColorRed:
                strColorSector = "red";
                break;
            case R.id.sectorColorBlack:
                strColorSector = "black";
                break;
        }
        AddToArrayBet(arrSelectedColors, strColorSector, view);
    }

    public void SetBatInInterval(View view) {
        Button vb = (Button) view;
        AddToArrayBet(arrSelectedInterval, String.valueOf(vb.getText()), view);
    }

    public void SetBatInEvenOrOdd(View view) {
        Button vb = (Button) view;
        AddToArrayBet(arrSelectedEvenOrOdd, String.valueOf(vb.getText()), view);
    }

    public void SetBatInDozen(View view) {
        String strDozen = "";
        switch (view.getId()) {
            case R.id._1st_12:
                strDozen = "1 2 3 4 5 6 7 8 9 10 11 12";
                break;
            case R.id._2nd_12:
                strDozen = "13 14 15 16 17 18 19 20 21 22 23 24";
                break;
            case R.id._3rd_12:
                strDozen = "25 26 27 28 29 30 31 32 33 34 35 36";
                break;
        }
        AddToArrayBet(arrSelectedDozen, strDozen, view);
    }

    public void SetBatIn2or1(View view) {
        String str2or1 = "";
        switch (view.getId()) {
            case R.id.btn1_2or1:
                str2or1 = "3 6 9 12 15 18 21 24 27 30 33 36";
                break;
            case R.id.btn2_2or1:
                str2or1 = "2 5 8 11 14 17 20 23 26 29 32 35";
                break;
            case R.id.btn3_2or1:
                str2or1 = "1 4 7 10 13 16 19 22 25 28 31 34";
                break;
        }
        AddToArrayBet(arrSelected2Or1, str2or1, view);
    }

    private void AddToArrayBet(ArrayList<Bet> bets, String betItem, View view) {
        if (ChipsId == 0) {
            CustomToast.showToast(RouletteActivity.this, getString(R.string.not_selected_chip), true);
            return;
        }
        Bet currentBet = getBetIsContains(bets, betItem);
        if (currentBet != null) currentBet.plusBet(getBetOfChips());
        else bets.add(new Bet(String.valueOf(betItem), getBetOfChips()));
        checkBet();
        if (bet > App.COINS){
            Clear(null);
            CustomToast.showToast(RouletteActivity.this, getString(R.string.bet_is_greater_balance), true);
            return;
        }
        PlaceChip(view);
    }

    private void PlaceChip(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];

        for (int i = 0; i < RelativeViewChips.getChildCount(); i++) {
            View ChildView = RelativeViewChips.getChildAt(i);
            int[] locationOld = new int[2];
            ChildView.getLocationInWindow(locationOld);
            if (locationOld[0] == location[0] && locationOld[1] == location[1]) {
                y -= 10;
                if (toScale) {
                    x -= 13;
                    toScale = false;
                } else {
                    x += 13;
                    toScale = true;
                }
                break;
            }
        }

        ImageView imgView = new ImageView(RouletteActivity.this);
        imgView.setImageResource(ChipsId);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(view.getWidth(), view.getHeight());
        lp.leftMargin = x;
        lp.topMargin = y;
        imgView.setLayoutParams(lp);
        RelativeViewChips.addView(imgView);
    }

    public void spin(View v) {
        if (arrSelectedSectors.isEmpty() && arrSelectedColors.isEmpty() && arrSelectedInterval.isEmpty()
                && arrSelectedEvenOrOdd.isEmpty() && arrSelected2Or1.isEmpty() && arrSelectedDozen.isEmpty()) {
            CustomToast.showToast(RouletteActivity.this, getString(R.string.please_make_bet), true);
            return;
        }
        if (!isSpin) {
            isSpin = true;
            if (App.COINS <= 0) {
                App.COINS = 0;
                textCoins.setText(String.valueOf(App.COINS));
                CustomToast.showToast(RouletteActivity.this, getString(R.string.no_coins), true);
                return;
            }
            App.COINS -= bet;
            textCoins.setText(String.valueOf(App.COINS));
        }

        degreeOld = degree % 360;
        degree = RANDOM.nextInt(360) + 720;

        RotateAnimation rotateAnim = new RotateAnimation(degreeOld, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(4600);
        rotateAnim.setFillAfter(true);
        rotateAnim.setInterpolator(new DecelerateInterpolator());
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ViewPlayingField(btnPlayingField);
                resultTv.setText("");
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                String sector = getSector(360 - (degree % 360));
                if (sector == null || sector.isEmpty()) sector = "zero";
                resultTv.setText(sector);
                Rules(sector);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        wheel.startAnimation(rotateAnim);
    }

    private void Rules(String sector) {
        int number = 0;
        String color = "green";
        if (!sector.equals("zero")) {
            String[] s = sector.split(" ");
            number = Integer.parseInt(s[0]);
            color = s[1];
        }

        for (Bet currentBet : arrSelectedSectors) {
            if (number == Integer.parseInt(currentBet.getBetItem())) {
                win += (currentBet.getBet() * 36);
                break;
            }
        }
        for (Bet currentBet : arrSelectedColors) {
            if (number == 0) break;
            if (color.equals(currentBet.getBetItem())) {
                win += (currentBet.getBet() * 2);
                break;
            }
        }
        for (Bet currentBet : arrSelectedInterval) {
            if (number == 0) break;
            String[] betItems = currentBet.getBetItem().split("-");
            if (number >= Integer.parseInt(betItems[0]) && number <= Integer.parseInt(betItems[1]))
                win += (currentBet.getBet() * 2);
        }
        for (Bet currentBet : arrSelectedEvenOrOdd) {
            if (number == 0) break;
            if (number % 2 == 0 && currentBet.getBetItem().equals(getString(R.string.even))
                    || number % 2 != 0 && currentBet.getBetItem().equals(getString(R.string.odd)))
                win += (currentBet.getBet() * 2);
        }
        for (Bet currentBet : arrSelected2Or1) {
            if (number == 0) break;
            List<String> items = Arrays.asList(currentBet.getBetItem().split(" "));
            if (items.contains(String.valueOf(number)))
                win += (currentBet.getBet() * 3);
        }
        for (Bet currentBet : arrSelectedDozen) {
            if (number == 0) break;
            List<String> items = Arrays.asList(currentBet.getBetItem().split(" "));
            if (items.contains(String.valueOf(number)))
                win += (currentBet.getBet() * 3);
        }

        if (win != 0) {
            Music.sound(this, R.raw.kassa);
            App.COINS += win;
            CustomToast.showToast(RouletteActivity.this, getString(R.string.you_win, win), false);
        }

        isSpin = false;
        win = 0;
        textCoins.setText(String.valueOf(App.COINS));
        App.SaveCOINS();

        Handler ShowPlayingField = new Handler();
        ShowPlayingField.postDelayed(() -> {
            ViewPlayingField(btnPlayingField);
        }, 1500);
    }

    public void Clear(View view) {
        RelativeViewChips.removeAllViews();

        arrSelectedSectors.clear();
        arrSelectedColors.clear();
        arrSelectedInterval.clear();
        arrSelectedEvenOrOdd.clear();
        arrSelected2Or1.clear();
        arrSelectedDozen.clear();

        ChipsId = 0;
        f1.setBackground(null);
        f2.setBackground(null);
        f3.setBackground(null);
        bet = 0;
        textViewBet.setText(getString(R.string.bet_coins, bet));
    }

    private String getSector(int degrees) {
        int i = 0;
        String text = null;

        do {
            float start = HALF_SECTOR * (i * 2 + 1);
            float end = HALF_SECTOR * (i * 2 + 3);

            if (degrees >= start && degrees < end) text = sectors[i];

            i++;
        } while (text == null && i < sectors.length);

        return text;
    }

    private int getBetOfChips() {
        int resBet = 0;
        switch (ChipsId) {
            case R.drawable.f1:
                resBet = 10;
                break;
            case R.drawable.f2:
                resBet = 20;
                break;
            case R.drawable.f3:
                resBet = 50;
                break;
        }
        return resBet;
    }

    private Bet getBetIsContains(ArrayList<Bet> bets, String betItem) {
        Bet resBet = null;
        for (Bet currentBet : bets) {
            if (currentBet.getBetItem().equals(betItem)) resBet = currentBet;
            break;
        }
        return resBet;
    }

    private void checkBet() {
        bet = 0;
        getIntBet(arrSelectedSectors);
        getIntBet(arrSelectedColors);
        getIntBet(arrSelectedInterval);
        getIntBet(arrSelectedEvenOrOdd);
        getIntBet(arrSelected2Or1);
        getIntBet(arrSelectedDozen);
        textViewBet.setText(getString(R.string.bet_coins, bet));
    }

    private void getIntBet(ArrayList<Bet> bets) {
        for (Bet currentBet : bets) {
            bet += currentBet.getBet();
        }
    }

    private void setVisibilityViewChips(int visibility) {
        for (int i = 0; i < RelativeViewChips.getChildCount(); i++) {
            View ChildView = RelativeViewChips.getChildAt(i);
            ChildView.setVisibility(visibility);
        }
    }

    public void ViewPlayingField(View view) {
        if (bet > App.COINS){
            Clear(null);
        }
        Animation anim;
        if (PlayingField.getVisibility() == View.GONE) {
            anim = AnimationUtils.loadAnimation(this, R.anim.a_info_in);
            PlayingField.setVisibility(View.VISIBLE);
            setVisibilityViewChips(View.VISIBLE);
        } else {
            anim = AnimationUtils.loadAnimation(this, R.anim.a_info_out);
            PlayingField.setVisibility(View.GONE);
            setVisibilityViewChips(View.GONE);
        }
        PlayingField.setAnimation(anim);
        RelativeViewChips.setAnimation(anim);
        view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_anim));
    }

    public void ShowInfo(View view) {
        dialog.show();
    }

    public void Exit(View view) {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textCoins.setText(String.valueOf(App.COINS));
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