package io.github.takusan23.downloadpocket

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File

class DownloadPocketTest {

    /**
     * 分割ダウンロードをテストする。ちゃんと結合できてるか確認する
     * 保存先は開発環境PCのダウンロードフォルダに入ります。Windowsしか知らん
     * */
    @Test
    fun splitDownloadTest() {
        runBlocking {
            val url = """
                

""".trimIndent()
            // とりあえずテスト環境のダウンロードフォルダに
            val downloadFolderPath = """${System.getProperty("user.home")}\Downloads"""
            val downloadFile = """$downloadFolderPath\result.mp4"""
            // 一時保存フォルダ
            val tempFolderPath = """$downloadFolderPath\temp"""
            val downloadPocket = DownloadPocket(url, File(tempFolderPath).apply { mkdir() }, File(downloadFile).apply { createNewFile() }, 10)

            launch {
                downloadPocket.progressFlow
                    .onEach { println("ダウンロード進捗 = $it") }
                    .onEach { if (it == 100) cancel() }
                    .collect()
            }

            downloadPocket.start()
        }
    }

}