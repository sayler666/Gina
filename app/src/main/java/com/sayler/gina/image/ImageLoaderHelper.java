/**
 * Created by sayler on 2016-11-22.
 * <p>
 * Copyright 2016 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.sayler.gina.image;

import android.text.TextUtils;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sayler.gina.R;

public class ImageLoaderHelper {

  private static ImageLoader imageLoader;

  public static ImageLoader getImageLoader() {
    if (imageLoader == null) {
      imageLoader = ImageLoader.getInstance();
    }
    return imageLoader;
  }

  public static void loadImage(ImageView imageView, String photoUri) {
    loadImage(imageView, photoUri, R.color.transparent);
  }

  public static void loadImage(ImageView imageView, String photoUri, int placeholderRes) {
    getImageLoader().cancelDisplayTask(imageView);
    if (TextUtils.isEmpty(photoUri)) {
      imageView.setImageResource(placeholderRes);
    } else {
      getImageLoader().displayImage(photoUri, imageView);
    }
  }
}
