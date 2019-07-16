package com.sayler.gina.domain.store

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import com.sayler.gina.domain.Constants
import java.lang.reflect.Type

/**
 * Created by sayler on 23.05.2017.
 */
class SharedPreferenceHelper<T> constructor(context: Context, val key: String, val tClazz: Class<T>, type: Type? = null) {
    val gson: Gson = Gson()
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
    val listType: Type? = null
    var storesList: Boolean = type != null

    fun store(obj: T) {
        val editor = sharedPreferences.edit()
        editor.putString(key, gson.toJson(obj)).apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun storeSync(obj: T) {
        val editor = sharedPreferences.edit()
        editor.putString(key, gson.toJson(obj)).commit()
    }

    fun retrieve(): T? {
        val storeJson = sharedPreferences.getString(key, "")
        if (TextUtils.isEmpty(storeJson))
            return null
        return if (storesList) gson.fromJson<T>(storeJson, listType) else gson.fromJson<T>(storeJson, tClazz)
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.remove(key).apply()
    }
}