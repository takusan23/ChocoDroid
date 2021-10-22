package io.github.takusan23.htmlparse.html

import io.github.takusan23.htmlparse.api.ChannelAPI
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ChannelAPITest {

    @Test
    fun getChannelUploadVideo() {
        runBlocking {
            val channelAPI = ChannelAPI()
            channelAPI.init()
            val uploadVideo = channelAPI.getChannelUploadVideo("UCAaGaynFpku5cAx6OOSrW-w")
/*
            println("チャンネル情報")
            println(uploadVideo.header.c4TabbedHeaderRenderer.title)
            println(uploadVideo.header.c4TabbedHeaderRenderer.subscriberCountText.simpleText)
            println("投稿動画")
            uploadVideo.getVideoList()?.forEach {
                println(it.videoId)
                println(it.title.runs[0].text)
                println(it.thumbnailOverlays[0].thumbnailOverlayTimeStatusRenderer!!.text.simpleText)
                println("---")
            }
*/
            // 追加読み込み
            val moreUploadVideoList = channelAPI.moreChannelUploadVideo()
            moreUploadVideoList.forEach {
                println(it.title)
            }
        }
    }

}