package io.github.takusan23.chocodroid.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.takusan23.chocodroid.viewmodel.ChannelScreenViewModel

/**
 * SearchScreenViewModelへ引数を渡すためのクラス
 *
 * @param application application
 * @param channelId チャンネルID
 * */
class ChannelScreenViewModelFactory(private val application: Application, private val channelId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChannelScreenViewModel(application, channelId) as T
    }
}
