package io.github.takusan23.chocodroid.ui.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.BackButtonSmallTopBar
import io.github.takusan23.chocodroid.ui.component.M3Scaffold

/**
 * ライセンス画面
 *
 * ありがとうございます
 *
 * @param onBack 戻ってほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseSettingScreen(
    onBack: () -> Unit,
) {
    val licenseList = listOf(
        coil,
        coroutine,
        exoPlayer,
        jsoup,
        materialComponents,
        okHttp,
        serialization,
    )
    M3Scaffold(
        topBar = {
            BackButtonSmallTopBar(
                title = { Text(text = stringResource(id = R.string.setting_license_title)) },
                onBack = onBack
            )
        },
        content = {
            LazyColumn(content = {
                items(licenseList) {
                    LicenseItem(licenseData = it)
                }
            })
        }
    )
}

/**
 * ライセンス一覧の各項目
 *
 * @param licenseData ライセンス情報
 * */
@Composable
private fun LicenseItem(licenseData: LicenseData) {
    Surface {
        Column {
            Text(
                modifier = Modifier.padding(5.dp),
                text = licenseData.name,
                fontSize = 25.sp
            )
            Text(
                text = licenseData.license,
                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
            )
            Divider()
        }
    }
}

private val coil = LicenseData(
    name = "coil-kt/coil",
    license = """
Copyright 2021 Coil Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
    """.trimIndent()
)

private val exoPlayer = LicenseData(
    name = "google/ExoPlayer",
    license = """
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
    """.trimIndent()
)

private val materialComponents = LicenseData(
    name = "material-components/material-components-android",
    license = """
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
    """.trimIndent()
)

private val serialization = LicenseData(
    name = "Kotlin/kotlinx.serialization",
    license = """
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
    """.trimIndent()
)

private val jsoup = LicenseData(
    name = "jhy/jsoup",
    license = """
The MIT License

Copyright (c) 2009-2021 Jonathan Hedley <https://jsoup.org/>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
    """.trimIndent()
)

private val coroutine = LicenseData(
    name = "Kotlin/kotlinx.coroutines",
    license = """
   Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
    """.trimIndent()
)

private val okHttp = LicenseData(
    name = "square/okhttp",
    license = """
Copyright 2019 Square, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
    """.trimIndent()
)

/**
 * ライセンス情報データクラス
 * @param name 名前
 * @param license ライセンス
 * */
private data class LicenseData(
    val name: String,
    val license: String,
)