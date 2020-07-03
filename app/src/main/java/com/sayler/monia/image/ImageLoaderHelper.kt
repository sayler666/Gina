/**
 * Created by sayler on 2016-11-22.
 *
 *

 */
package com.sayler.monia.image

import android.widget.ImageView
import com.nostra13.universalimageloader.core.ImageLoader
import com.sayler.monia.R

object ImageLoaderHelper {

    val imageLoader: ImageLoader = ImageLoader.getInstance()

    @JvmOverloads fun loadImage(imageView: ImageView, photoUri: String, placeholderRes: Int = R.color.transparent) {
        imageLoader.cancelDisplayTask(imageView)
        if (photoUri.isEmpty()) {
            imageView.setImageResource(placeholderRes)
        } else {
            imageLoader.displayImage(photoUri, imageView)
        }
    }
}
