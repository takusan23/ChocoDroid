package io.github.takusan23.internet.api

import kotlinx.coroutines.runBlocking
import org.junit.Test

class ChannelAPITest {

    @Test
    fun getChannelUploadVideo() {
        runBlocking {
            val channelAPI = ChannelAPI()
            channelAPI.init()
            val uploadVideo = channelAPI.getChannelUploadVideo("")
            println("チャンネル情報")
            println(uploadVideo.header.c4TabbedHeaderRenderer.title)
            println(uploadVideo.header.c4TabbedHeaderRenderer.subscriberCountText.simpleText)
            println("投稿動画")
            uploadVideo.getVideoList()?.forEach {
                println(it.videoId)
                println(it.videoTitle)
                println(it.thumbnailUrl)
                println("---")
            }
            // 追加読み込み
            val moreUploadVideoList = channelAPI.moreChannelUploadVideo()
            println("追加読み込み")
            moreUploadVideoList.forEach {
                println(it.videoTitle)
            }
        }
    }

}