package io.github.takusan23.chocodroid.ui.tool

import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import kotlinx.coroutines.coroutineScope

/**
 * 子要素でタップが消費の有無に関わらず親要素へタッチイベントが行くようにしたもの
 *
 * @param onTap 押したとき
 * */
suspend fun PointerInputScope.detectBackComponentTapGestures(onTap: ((Offset) -> Unit)? = null) = coroutineScope {
    forEachGesture {
        awaitPointerEventScope {
            // awaitPointerEvent を使うことでクリックが消費されてるかどうか関係なくクリックイベントを待機
            awaitPointerEvent()
            var upOrCancel: PointerInputChange? = null
            try {
                upOrCancel = withTimeout(Long.MAX_VALUE / 2) {
                    // ここでタップ判定をしている。長押しとか画面外タッチはnullになる
                    waitForUpIgnoreOrCancellation()
                }
                upOrCancel?.consumeDownChange()
            } catch (_: PointerEventTimeoutCancellationException) {
                consumeUntilUp()
            }
            if (upOrCancel != null) {
                onTap?.invoke(upOrCancel.position)
            }
        }
    }
}

/**
 * クリックするかキャンセルするまで一時停止する。
 * [AwaitPointerEventScope.waitForUpOrCancellation]では他でクリックイベントが消費されたらキャンセルされますが、
 * これはクリックイベントの消費されていてもキャンセル扱いしません。
 *
 * @return クリックしたら[PointerInputChange]。ドラッグ操作やキャンセルならnull
 * */
private suspend fun AwaitPointerEventScope.waitForUpIgnoreOrCancellation(): PointerInputChange? {
    while (true) {
        val event = awaitPointerEvent(PointerEventPass.Main)
        // クリックイベントが消費されてもされてなくてもいいやつ
        if (event.changes.all { it.changedToUpIgnoreConsumed() }) {
            // All pointers are up
            return event.changes[0]
        }

        if (event.changes.any { it.consumed.downChange || it.isOutOfBounds(size, extendedTouchPadding) }) {
            return null // Canceled
        }

        // Check for cancel by position consumption. We can look on the Final pass of the
        // existing pointer event because it comes after the Main pass we checked above.
        val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
        if (consumeCheck.changes.any { it.positionChangeConsumed() }) {
            return null
        }
    }
}

/** クリックイベントをすべて消費する */
private suspend fun AwaitPointerEventScope.consumeUntilUp() {
    do {
        val event = awaitPointerEvent()
        event.changes.forEach { it.consumeAllChanges() }
    } while (event.changes.any { it.pressed })
}