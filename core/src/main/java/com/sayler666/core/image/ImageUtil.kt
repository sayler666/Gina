package com.sayler666.core.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import timber.log.Timber

data class ScaledBitmapInfo(
    val bitmap: Bitmap,
    val width: Int,
    val height: Int
)

fun ByteArray.scaleToMinSize(minWidth: Int = 1080, minHeight: Int = 1920): ScaledBitmapInfo {
    var scaledBitmap: Bitmap?

    BitmapFactory.decodeByteArray(
        this,
        0,
        this.size
    ).let {
        var (w, h) = it.width to it.height

        Timber.d("Image: Source size: w: $w, h: $h")
        if (it.width < it.height && it.width < 1080) {
            val m = android.graphics.Matrix()
            val ratio = it.height / it.width.toFloat()
            val dstHeight = minWidth * ratio
            Timber.d("Image: Ratio: $ratio")
            Timber.d("Image: dst: w: $minWidth, h: $dstHeight")
            if (w != minWidth || h.toFloat() != dstHeight) {
                val sx = minWidth / w.toFloat()
                val sy = dstHeight / h.toFloat()
                m.setScale(sx, sy)
            }
            scaledBitmap = Bitmap.createBitmap(it, 0, 0, w, h, m, true)
            w = minWidth
            h = dstHeight.toInt()
        } else if (it.height < it.width && it.height < 1920) {
            val m = android.graphics.Matrix()
            val ratio = it.width.toFloat() / it.height
            val destWidth = minHeight * ratio
            Timber.d("Image: Ratio: $ratio")
            Timber.d("Image: dst: w: $destWidth, h: $minHeight")
            if (w.toFloat() != destWidth || h != minHeight) {
                val sx = destWidth / w.toFloat()
                val sy = minHeight / h.toFloat()
                m.setScale(sx, sy)
            }
            scaledBitmap = Bitmap.createBitmap(it, 0, 0, w, h, m, true)
            w = destWidth.toInt()
            h = minHeight
        } else if (it.height == it.width && it.height < 1080) {
            val m = android.graphics.Matrix()
            val ratio = it.width.toFloat() / it.height
            val destWidth = minHeight * ratio
            Timber.d("Image: Ratio: $ratio")
            Timber.d("Image: dst: w: $destWidth, h: $minHeight")
            if (w.toFloat() != destWidth || h != minHeight) {
                val sx = destWidth / w.toFloat()
                val sy = minHeight / h.toFloat()
                m.setScale(sx, sy)
            }
            scaledBitmap = Bitmap.createBitmap(it, 0, 0, w, h, m, true)
            w = destWidth.toInt()
            h = minHeight
        } else {
            scaledBitmap = it.copy(it.config!!, true)
        }

        it.recycle()
        return ScaledBitmapInfo(scaledBitmap!!, w, h)
    }
}
