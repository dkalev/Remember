package com.example.dkalev.remember

import android.content.SharedPreferences
import android.content.Context



class PrefManager constructor(context: Context){
    private var pref: SharedPreferences = context.getSharedPreferences(context.getString(R.string.preferencesName), 0)
    private var edit: SharedPreferences.Editor = pref.edit()

    fun setFirstTime(isFirstTime: Boolean) {
        edit.putBoolean("isFirstTimeLaunch", isFirstTime)
        edit.commit()
    }

    fun isFirstTime(): Boolean {
        return pref.getBoolean("isFirstTimeLaunch", true)
    }
}