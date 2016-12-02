package com.sayler.gina.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import rx.functions.Action0;
import rx.functions.Action2;

public class BroadcastReceiverHelper {

  private final Action0 action;
  private Intent intent;
  private BroadcastReceiver broadcastReceiver;
  private boolean needToRunAction = false;

  public Intent getIntent() {
    return intent;
  }

  public static BroadcastReceiver onReceive(Action2<Context, Intent> action2) {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        action2.call(context, intent);
      }
    };
  }

  /**
   * @param action action to perform after broadcast gets received
   */
  public BroadcastReceiverHelper(Action0 action) {
    broadcastReceiver = BroadcastReceiverHelper.onReceive((context, intent) -> {
      //SCHEDULE FOR LATER (call in callScheduledAction)
      needToRunAction = true;
      this.intent = intent;
    });

    this.action = action;
  }

  /**
   * @return true if action was called
   * best to call this method in onResume
   */
  public boolean callScheduledAction() {
    if (needToRunAction) {
      needToRunAction = false;
      action.call();
      return true;
    }
    return false;
  }

  public void register(Context context, IntentFilter intentFilter) {
    context.registerReceiver(broadcastReceiver, intentFilter);
  }

  public void unregister(Context context) {
    context.unregisterReceiver(broadcastReceiver);
  }

}