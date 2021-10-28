package io.github.takusan23.chocodroid.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore

private val DarkColorPalette = darkColors(
    primary = PrimaryColor,
    primaryVariant = DarkColor,
    secondary = LightColor,
    surface = Color.Black,
)

private val LightColorPalette = lightColors(
    primary = PrimaryColor,
    primaryVariant = DarkColor,
    secondary = LightColor,
    surface = Color.White,
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

/**
 * テーマ
 *
 * アノテーションで警告を黙らせてるけどちゃんと動くようにしてあるのでおｋ
 *
 * @param darkTheme ダークモード
 * */
@SuppressLint("NewApi")
@Composable
fun ChocoDroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val dataStore = context.dataStore.data.collectAsState(initial = null)
    // ダイナミックカラー使う？
    val isUseDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && dataStore.value?.get(SettingKeyObject.ENABLE_DYNAMIC_THEME) == true

    // Android 12以降で
    val colorScheme = when {
        isUseDynamicColor && darkTheme -> dynamicDarkColorScheme(context)
        isUseDynamicColor && !darkTheme -> dynamicLightColorScheme(context)
        darkTheme -> darkColorScheme(
            primary = PrimaryColor,
            secondary = LightColor,
            tertiary = DarkColor,
            surface = Color.Black,
        )
        else -> lightColorScheme(
            primary = PrimaryColor,
            secondary = LightColor,
            tertiary = DarkColor,
            surface = Color.White,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
