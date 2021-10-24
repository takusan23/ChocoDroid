package io.github.takusan23.chocodroid.tool

import android.annotation.SuppressLint
import android.media.MediaCodec.BufferInfo
import android.media.MediaExtractor
import android.media.MediaMuxer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer


/**
 * 映像と音声を合体させる。MediaMuxerとMediaExtractorでできている。何もわからん。
 *
 * 多分MediaCodec系ってインスタンス生成に上限があったはずなので、一気にダウンロードすると多分落ちる
 *
 * ダウンロード機能の最後、音声のみのダウンロードじゃない場合はここに書いてある関数を利用して一つの動画ファイルにする
 *
 * ちなみに変換とかはせず、そのまま追加してる。
 *
 * AOSPの画面録画機能の内部音声と合成する部分のコードそのままです。
 *
 * Apache License 2.0
 *
 * https://cs.android.com/android/platform/superproject/+/master:frameworks/base/packages/SystemUI/src/com/android/systemui/screenrecord/ScreenRecordingMuxer.java
 *
 * */
object DownloadVideoMuxer {

    /**
     * 新しく動画ファイルを作成して、映像トラックと音声トラックを付けて一つの動画にする
     *
     * @param mixMediaPathList 映像パスと音声パスを入れてください。
     * @param resultFilePath 最終的にできるファイル
     * */
    @SuppressLint("WrongConstant")
    suspend fun startMixer(mixMediaPathList: List<String>, resultFilePath: String) = withContext(Dispatchers.Default) {

        // 映像トラックと音声トラックを追加して一つの動画にする。そのために使うのがMediaMuxer
        val mediaMuxer = MediaMuxer(resultFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        // 映像、音声ファイルからフォーマットとデータを取り出す準備をする
        val trackIndexToExtractorPairList = mixMediaPathList
            .map { path ->
                // トラックを取り出して、フォーマットを取得
                val mediaExtractor = MediaExtractor()
                mediaExtractor.setDataSource(path)
                val trackFormat = mediaExtractor.getTrackFormat(0)
                mediaExtractor.selectTrack(0)
                // FormatとExtractorを返す
                trackFormat to mediaExtractor
            }
            .map { (format, extractor) ->
                // フォーマットをMediaMuxerに渡して、トラックを追加してもらう
                val videoTrackIndex = mediaMuxer.addTrack(format)
                videoTrackIndex to extractor
            }

        // スタート！
        mediaMuxer.start()

        // 映像と音声を一つの動画ファイルに書き込んでいく
        trackIndexToExtractorPairList.forEach { (index, extractor) ->
            val byteBuffer = ByteBuffer.allocate(1024 * 4096)
            val bufferInfo = BufferInfo()
            // データが無くなるまで回す
            while (true) {
                // データを読み出す
                val offset = byteBuffer.arrayOffset()
                bufferInfo.size = extractor.readSampleData(byteBuffer, offset)
                // もう無い場合
                if (bufferInfo.size < 0) break
                // 書き込む
                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = extractor.sampleFlags
                mediaMuxer.writeSampleData(index, byteBuffer, bufferInfo)
                // 次のデータに進める
                extractor.advance()
            }
            // あとしまつ
            extractor.release()
        }

        // あとしまつ
        mediaMuxer.stop()
        mediaMuxer.release()
    }

}