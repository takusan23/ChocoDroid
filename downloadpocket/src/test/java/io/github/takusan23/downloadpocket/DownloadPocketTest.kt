package io.github.takusan23.downloadpocket

import kotlinx.coroutines.flow.collect
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
                https://r5---sn-5n5ip-ioqz.googlevideo.com/videoplayback?expire=1634715622&ei=hnNvYbGwK5DpqAHiyrTQCw&ip=118.243.88.134&id=o-ANqLWwTXrrNRdb4QbwXh9LspnLnkD9rIOtXTZSwZRz07&itag=136&aitags=133%2C134%2C135%2C136%2C160%2C242%2C243%2C244%2C247%2C278%2C394%2C395%2C396%2C397%2C398&source=youtube&requiressl=yes&mh=Yr&mm=31%2C29&mn=sn-5n5ip-ioqz%2Csn-ogul7ne6&ms=au%2Crdu&mv=m&mvi=5&pl=18&initcwndbps=1078750&vprv=1&mime=video%2Fmp4&ns=2jcUY-PMIwMAxffdHVwjZxYG&gir=yes&clen=4908377&dur=253.253&lmt=1608464852082004&mt=1634693583&fvip=5&keepalive=yes&fexp=24001373%2C24007246&c=WEB&txp=5432432&n=5Lhrp4YX0vcTBUpnPD9&sparams=expire%2Cei%2Cip%2Cid%2Caitags%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRgIhAJwPz7aAdKo4usQPvngvyUsOu6ca1NWf7z7TP4MKMT48AiEAhV-3xtJcw-Egg35gAjsn1xPzOemHLMS0Hls7-f_GcIE%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRgIhANvwRJg6zIsvAvDMEInqinIkIrSxfrecZ8YO70NnAjvLAiEAgRcc_z4LAfq5YrnELDy4oP28OVZVBVdwyN1MjwIovfk%3D
            """.trimIndent()
            // とりあえずテスト環境のダウンロードフォルダに
            val downloadFolderPath = "${System.getProperty("user.home")}\\Downloads"
            val downloadFile = "$downloadFolderPath\\result.mp4"
            // 一時保存フォルダ
            val tempFolderPath = "$downloadFolderPath\\temp"
            val downloadPocket = DownloadPocket(url, File(tempFolderPath).apply { mkdir() }, File(downloadFile).apply { createNewFile() }, 5)

            downloadPocket.start()
        }
    }

}