package io.github.takusan23.htmlparse.magic

import org.junit.Test

class DecryptMagicTest {

    @Test
    fun decryptTest() {
        val text = ""
        val decryptText = DecryptMagic.decrypt(text)
        println(decryptText)
    }

}