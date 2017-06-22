package com.sayler.gina.util;

import android.animation.ValueAnimator;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by lchromy on 16.07.15.
 */
public class ViewSliderCoordinator {

  public interface Slideable {
    void onSlide(float progress);
  }

  private final ViewGroup container;
  private final Slideable slideable;
  private final int windowHeight;
  private int minimizedHeight;
  private int fullscreenTopOffset;
  private ViewPosition playerPosition = ViewPosition.HIDDEN;

  private ViewSliderCoordinator(ViewSliderCoordinator.Builder builder) {
    minimizedHeight = builder.getMinimizedHeight();
    fullscreenTopOffset = builder.getFullscreenTopOffset();
    windowHeight = builder.getWindowHeight();
    container = builder.getContainer();
    slideable = builder.getSlideable();

    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
    layoutParams.height = windowHeight;
    container.setLayoutParams(layoutParams);
    container.setTranslationY(windowHeight);
    container.setOnTouchListener(touchListener);

  }

  public void animatePlayerToPosition(ViewPosition viewPosition, int duration) {
    switch (viewPosition) {
      case HIDDEN:
        startPlayerAnimation(windowHeight, duration);
        break;
      case MINIMIZED:
        startPlayerAnimation(windowHeight - minimizedHeight, duration);
        break;
      case FULLSCREEN:
        startPlayerAnimation(-fullscreenTopOffset, duration);
        break;
    }
  }

  public ViewPosition getPlayerPosition() {
    return playerPosition;
  }

  private void startPlayerAnimation(int position, float timeOfAnimation) {
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(container.getTranslationY(), position);
    valueAnimator.setDuration((long) timeOfAnimation);
    valueAnimator.addUpdateListener(animation -> slideFragmentY((float) animation.getAnimatedValue()));
    valueAnimator.start();
  }

  private void slideFragmentY(float translationY) {
    float percentageOfMinimalSize = translationY / (windowHeight - minimizedHeight);
    container.setTranslationY(translationY);
    slideable.onSlide(percentageOfMinimalSize);
    if (Float.compare(percentageOfMinimalSize, 0.0f) <= 0) {
      playerPosition = ViewPosition.FULLSCREEN;
    } else {
      playerPosition = ViewPosition.MINIMIZED;
    }
  }

  View.OnTouchListener touchListener = new View.OnTouchListener() {

    public int direction;
    private float velocity;
    private float viewY = 0;
    private long firstClick;
    private MotionEvent lastEvent;
    private long lastEventTime;
    private float lastEventY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      int action = MotionEventCompat.getActionMasked(event);

      switch (action) {
        case (MotionEvent.ACTION_DOWN):
          int[] location = new int[2];
          v.getLocationOnScreen(location);
          viewY = event.getRawY() - location[1];
          firstClick = System.currentTimeMillis();
          return true;
        case (MotionEvent.ACTION_MOVE):
          if (event.getRawY() - viewY >= -fullscreenTopOffset) {
            slideFragmentY(event.getRawY() - viewY);
          } else {
            slideFragmentY(-fullscreenTopOffset);
          }
          if (event.getRawY() - viewY > windowHeight - minimizedHeight) {
            slideFragmentY(windowHeight - minimizedHeight);
          }
          if (lastEvent != null) {
            velocity = Math.abs((event.getRawY() - lastEventY) / (event.getEventTime() - lastEventTime));
          }
          lastEvent = event;
          lastEventTime = event.getEventTime();
          direction = event.getRawY() > lastEventY ? 1 : -1;
          lastEventY = event.getRawY();
          return true;
        case (MotionEvent.ACTION_UP):
          //single click detection
          if (System.currentTimeMillis() - firstClick < 50) {
            //show big player
            startPlayerAnimation(-fullscreenTopOffset, 500);
            return true;
          }

          float timeOfAnimation;
          if (direction == 1) {
            //togo
            timeOfAnimation = (windowHeight - minimizedHeight - container.getTranslationY()) / velocity;
            timeOfAnimation = normalizeTimeOfAnimation(timeOfAnimation);
            //show small player
            animatePlayerToPosition(ViewPosition.MINIMIZED, (int) timeOfAnimation);
          } else {
            //togo
            timeOfAnimation = (container.getTranslationY()) / velocity;
            timeOfAnimation = normalizeTimeOfAnimation(timeOfAnimation);
            //show big player
            animatePlayerToPosition(ViewPosition.FULLSCREEN, (int) timeOfAnimation);
          }
          viewY = 0;
          return true;
        default:
          return false;
      }
    }

    private float normalizeTimeOfAnimation(float timeOfAnimation) {
      if (timeOfAnimation > 400) {
        timeOfAnimation = 400f;
      }
      if (timeOfAnimation < 100) {
        timeOfAnimation = 100f;
      }
      return timeOfAnimation;
    }
  };

  public static class Builder {
    private int minimizedHeight;
    private int fullscreenTopOffset = 0;

    private Slideable slideable;
    private ViewGroup container;
    private int windowHeight;

    public ViewSliderCoordinator build() {
      return new ViewSliderCoordinator(this);
    }

    public Builder setContainer(ViewGroup container) {
      this.container = container;
      return this;
    }

    public Builder setSlideable(Slideable slideable) {
      this.slideable = slideable;
      return this;
    }

    public Builder setWindowHeight(int windowHeight) {
      this.windowHeight = windowHeight;
      return this;
    }

    public Builder setMinimizedHeight(int minimizedHeight) {
      this.minimizedHeight = minimizedHeight;
      return this;
    }

    public Builder setFullscreenTopOffset(int fullscreenTopOffset) {
      this.fullscreenTopOffset = fullscreenTopOffset;
      return this;
    }

    public int getMinimizedHeight() {
      return minimizedHeight;
    }

    public int getFullscreenTopOffset() {
      return fullscreenTopOffset;
    }

    public Slideable getSlideable() {
      return slideable;
    }

    public ViewGroup getContainer() {
      return container;
    }

    public int getWindowHeight() {
      return windowHeight;
    }
  }

  public enum ViewPosition {
    HIDDEN, MINIMIZED, FULLSCREEN
  }

}