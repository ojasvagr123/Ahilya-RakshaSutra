package com.example.ahilya_rakshasutra.utils;

import android.animation.*;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class AnimationHelper {

    public static void staggeredEntrance(View[] views, int delayBetween) {
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.setAlpha(0f);
            view.setTranslationY(100f);

            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(i * delayBetween)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    public static void pulseAnimation(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(1000);
        set.setDuration(ValueAnimator.INFINITE);
        set.setDuration(ValueAnimator.RESTART);
        set.start();
    }

    public static void buttonPressAnimation(View button, Runnable onComplete) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    button.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction(onComplete)
                            .start();
                })
                .start();
    }
}