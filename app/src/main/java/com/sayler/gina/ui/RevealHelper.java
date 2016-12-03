package com.sayler.gina.ui;

import android.animation.Animator;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

public class RevealHelper {
  public void reveal(View viewToReveal, View frameView) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      int finalRadius = Math.max(frameView.getWidth(), frameView.getHeight()) / 2;
      int cx = frameView.getMeasuredWidth() / 2;
      int cy = frameView.getMeasuredHeight() / 2;
      Animator anim = ViewAnimationUtils.createCircularReveal(viewToReveal, cx, cy, 0, finalRadius);
      anim.start();
    }
  }
}