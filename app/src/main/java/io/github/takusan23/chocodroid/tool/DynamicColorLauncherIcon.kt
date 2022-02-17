package io.github.takusan23.chocodroid.tool

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * Android 13 (Tiramisu) で、サードパーティーアプリでもテーマアイコンが利用できるようになったが、
 *
 * どうやらAndroid 12でも変えられるらしいので、そのための関数がある。
 * */
object DynamicColorLauncherIcon {

    /**
     * Android 12 以降で利用できるMaterial Youのテーマアイコン機能を利用したアイコンに切り替えるための関数
     *
     * <activity-alias>を利用することで静的に用意しておいたアイコンに切り替えることができる模様。
     *
     * @param context [Context]
     * @param isEnable テーマアイコンにする場合はtrue
     * */
    fun setDynamicColorLauncherIcon(context: Context, isEnable: Boolean) {
        // Android 12 以降
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val packageManager = context.packageManager
            // 既存のアイコン
            val defaultIconComponentName = ComponentName(context.packageName, "${context.packageName}.MainActivity")
            // テーマアイコン
            val dynamicColorIconComponentName = ComponentName(context.packageName, "${context.packageName}.MainActivity_dynamic_icon")
            packageManager.setComponentEnabledSetting(
                if (isEnable) dynamicColorIconComponentName else defaultIconComponentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(
                if (!isEnable) dynamicColorIconComponentName else defaultIconComponentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
        }
    }

}