package io.github.takusan23.internet.api

import kotlinx.coroutines.runBlocking
import org.junit.Test

class ChannelAPITest {

    @Test
    fun getChannelUploadVideo() {
        runBlocking {
            val channelAPI = ChannelAPI()
            channelAPI.init()
            val uploadVideo = channelAPI.getChannelUploadVideo("UC2uOfatT4Eazb9ljENfhqIQ")
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
            println("追加読み込み")
            val moreUploadVideoList = channelAPI.moreChannelUploadVideo()
            moreUploadVideoList.forEach {
                println(it.videoTitle)
            }
            println("追加追加読み込み")
            val moreUploadVideoList1 = channelAPI.moreChannelUploadVideo()
            moreUploadVideoList1.forEach {
                println(it.videoTitle)
            }
        }
    }

}