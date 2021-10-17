package io.github.takusan23.htmlparse.tool

import io.github.takusan23.htmlparse.exception.HttpStatusCodeException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/** シングルトンのOkHttpクライアント */
object SingletonOkHttpClientTool {

    /** ユーザーエージェント */
    const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36"

    /** 使い回すOkHttpのクライアント */
    val client = OkHttpClient()

    /**
     * GETリクエストをする関数。URLを指定すれば使える簡単なものです
     *
     * 失敗したら IOException / HttpStatusCodeException をスローします。コルーチンなので例外処理は簡単なはず？
     *
     * @param url URL
     * @return レスポンスボディー
     * */
    suspend fun executeGetRequest(url: String) = withContext(Dispatchers.Default) {
        val request = Request.Builder().apply {
            url(url)
            addHeader("User-Agent", USER_AGENT)
            get()
        }.build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            response.body!!.string()
        } else {
            throw HttpStatusCodeException(response.code, response.message)
        }
    }

    /**
     * POSTリクエストを飛ばす関数。URLとPOSTするJSON（文字列）を入れれば使える簡単なもの
     *
     * 失敗したら IOException / HttpStatusCodeException をスローします。コルーチンなので例外処理は簡単なはず？
     *
     * @param url URL
     * @param postJSON リクエストボディー。JSON
     * @return レスポンスボディー
     * */
    suspend fun executePostRequest(url: String, postJSON: String) = withContext(Dispatchers.Default) {
        val request = Request.Builder().apply {
            url(url)
            addHeader("User-Agent", USER_AGENT)
            post(postJSON.toRequestBody())
        }.build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            response.body!!.string()
        } else {
            throw HttpStatusCodeException(response.code, response.message)
        }
    }

}