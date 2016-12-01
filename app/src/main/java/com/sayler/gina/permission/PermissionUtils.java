/**
 * Created by MiQUiDO on 09.05.16.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.permission;

/**
 * Krzysztof on 19.01.2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.Random;

/**
 * HOW TO USE:
 * - Call PermissionUtils.onRequestPermissionsResult(...) in your ActivityCompat onRequestPermissionsResult
 * - Call askForPermissions or askForPermission
 * - Handle result in permission callback
 */

public final class PermissionUtils {

  private static HashMap<Integer, PermissionRequest> singleRequestMap = new HashMap<>();
  private static HashMap<Integer, PermissionsRequest> multipleRequestsMap = new HashMap<>();

  public interface PermissionCallback {
    void onPermissionGranted(String permission);

    void onPermissionRejected(String permission);
  }

  interface PermissionsCallback {
    void onPermissionsResult(HashMap<String, PermissionResult> permissionsResult);
  }

  private enum PermissionResult {
    PERMISSION_GRANTED,
    PERMISSION_DENIED
  }

  private PermissionUtils() {
  }

  public static boolean hasPermission(Context context, String permission) {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
  }

  public static void askForPermissions(Activity activity, PermissionsCallback permissionsCallback, String... permissions) {
    if (permissionsCallback == null) {
      return;
    }

    //Can only use lower 8 bits for requestCode
    int requestCode = new Random().nextInt((255) + 1);
    PermissionsRequest request = new PermissionsRequest(new HashMap<>(), permissionsCallback);
    multipleRequestsMap.put(requestCode, request);
    for (String permission : permissions) {
      request.addPermission(permission);
    }

    ActivityCompat.requestPermissions(activity, permissions, requestCode);
  }

  public static void askForPermission(Activity activity, PermissionCallback permissionCallback, String permission) {
    if (permissionCallback == null) {
      return;
    }

    //Can only use lower 8 bits for requestCode
    int requestCode = new Random().nextInt((255) + 1);
    if (hasPermission(activity, permission)) {
      permissionCallback.onPermissionGranted(permission);
      return;
    } else {
      PermissionRequest request = new PermissionRequest(permission, permissionCallback);
      singleRequestMap.put(requestCode, request);
    }

    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
  }

  public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (multipleRequestsMap != null && multipleRequestsMap.get(requestCode) != null) {
      returnMultiplePermissionsResult(multipleRequestsMap.get(requestCode), permissions, grantResults);
      multipleRequestsMap.remove(requestCode);
    }

    if (singleRequestMap != null && singleRequestMap.get(requestCode) != null) {
      returnSinglePermissionResult(singleRequestMap.get(requestCode), permissions, grantResults);
      singleRequestMap.remove(requestCode);
    }
  }

  private static void returnMultiplePermissionsResult(PermissionsRequest request, String[] permissions, int[] grantResults) {
    for (int i = 0; i < permissions.length; i++) {
      String permission = permissions[i];
      int grantResult = grantResults[i];
      if (request.getPermissions().containsKey(permission)) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
          request.getPermissions().put(permission, PermissionResult.PERMISSION_GRANTED);
        } else {
          request.getPermissions().put(permission, PermissionResult.PERMISSION_DENIED);
        }
      }
    }
    request.returnResult();
  }

  private static void returnSinglePermissionResult(PermissionRequest request, String[] permissions, int[] grantResults) {
    for (int i = 0; i < permissions.length; i++) {
      String permission = permissions[i];
      int grantResult = grantResults[i];
      if (request != null && request.getPermission().equals(permission)) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
          request.getPermissionCallback().onPermissionGranted(permission);
        } else {
          request.getPermissionCallback().onPermissionRejected(permission);
        }
      }
    }
  }

  private static class PermissionRequest {
    private String permission;
    private PermissionCallback permissionCallback;

    PermissionRequest(String permission, PermissionCallback permissionCallback) {
      this.permission = permission;
      this.permissionCallback = permissionCallback;
    }

    public String getPermission() {
      return permission;
    }

    PermissionCallback getPermissionCallback() {
      return permissionCallback;
    }
  }

  private static class PermissionsRequest {
    private HashMap<String, PermissionResult> permissions;
    private PermissionsCallback callback;

    PermissionsRequest(HashMap<String, PermissionResult> permissions, PermissionsCallback callback) {
      this.permissions = permissions;
      this.callback = callback;
    }

    void addPermission(String permission) {
      if (permissions != null) {
        permissions.put(permission, null);
      }
    }

    public HashMap<String, PermissionResult> getPermissions() {
      return permissions;
    }

    void returnResult() {
      if (callback != null && permissions != null) {
        callback.onPermissionsResult(permissions);
      }
    }
  }
}

