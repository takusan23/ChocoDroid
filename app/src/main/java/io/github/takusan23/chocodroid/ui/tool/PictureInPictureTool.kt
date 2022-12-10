package io.github.takusan23.chocodroid.ui.tool

import android.app.PictureInPictureParams
import android.graphics.Rect
import android.os.Build
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

/**
 * ピクチャーインピクチャーの処理
 *
 * @param activity [Activity]
 * @param onPictureInPictureModeChange 変化したら呼ばれる
 */
class PictureInPictureTool(
    private val activity: ComponentActivity,
    private val onPictureInPictureModeChange: (Boolean) -> Unit,
) {

    /** Android Oreo 以上 */
    private val isAndroidOAndLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    /** Android Tiramisu 以上 */
    private val isAndroidSAndLater: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    /** パラメーター */
    private val params by lazy {
        if (isAndroidOAndLater) {
            PictureInPictureParams.Builder().apply {
                setAspectRatio(Rational(16, 9))
                if (isAndroidSAndLater) {
                    setAutoEnterEnabled(true)
                    setSeamlessResizeEnabled(true)
                }
            }
        } else null
    }

    /** ピクチャーインピクチャーかどうか */
    val isPictureInPicture: Boolean
        get() = if (isAndroidOAndLater) {
            activity.isInPictureInPictureMode
        } else false

    init {
        if (isAndroidOAndLater) {
            activity.setPictureInPictureParams(params!!.build())
            activity.addOnPictureInPictureModeChangedListener {
                onPictureInPictureModeChange(it.isInPictureInPictureMode)
            }
        }
    }

    /** ピクチャーインピクチャーに切り替える */
    fun enterPictureInPicture() {
        if (isAndroidOAndLater) {
            activity.enterPictureInPictureMode(params!!.build())
        }
    }

    /** プレイヤーの座標をセットする */
    fun setPictureInPictureRect(rect: Rect) {
        if (isAndroidOAndLater) {
            params?.setSourceRectHint(rect)
            activity.setPictureInPictureParams(params!!.build())
        }
    }

    /** [AppCompatActivity.onUserLeaveHint]の際に呼び出す */
    fun onUserLeaveHint() {
        if (isAndroidOAndLater && !isAndroidSAndLater) {
            enterPictureInPicture()
        }
    }

}