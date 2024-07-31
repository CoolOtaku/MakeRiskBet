package com.example.makeriskbet;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.inflate;

import pl.droidsonroids.gif.GifImageView;

public class CustomToast {

    private static View view;
    private static GifImageView firework;
    private static ImageView imageToast;
    private static TextView textView;
    private static Toast toast;

    public static void showToast(Context context, String text, boolean isWarning) {
        view = inflate(context, R.layout.castom_toast, null);

        imageToast = view.findViewById(R.id.imageToast);
        if (isWarning) {
            imageToast.setImageResource(R.drawable.ic_warning);
        } else {
            imageToast.setImageResource(R.drawable.win);
            firework = view.findViewById(R.id.firework);
            firework.setVisibility(View.VISIBLE);
        }
        imageToast.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shaking));

        textView = view.findViewById(R.id.textToast);
        textView.setText(text);

        toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

}
