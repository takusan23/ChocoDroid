package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.viewmodel.AddFavoriteFolderViewModel
import kotlinx.coroutines.launch

/**
 * お気に入りフォルダーを追加する画面
 *
 * BottomSheetで使う
 *
 * @param onClose 閉じるときに使う
 * */
@Composable
fun AddFavoriteFolderScreen(
    videoToFavoriteFolderViewModel: AddFavoriteFolderViewModel = viewModel(),
    onClose: () -> Unit,
) {
    val textValue = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AddFavoriteFolderScreen(
        textValue = textValue.value,
        onValueChange = { textValue.value = it },
        onCreate = { folderName ->
            scope.launch {
                // DBへ追加
                videoToFavoriteFolderViewModel.addFolder(folderName)
                // 閉じる
                onClose()
            }
        }
    )
}

/**
 * お気に入りフォルダ作成画面
 * @param textValue 入力中テキスト
 * @param onValueChange テキスト変更時に呼ばれる
 * @param onCreate 作成ボタンを押したら呼ばれる
 * */
@Composable
fun AddFavoriteFolderScreen(
    textValue: String,
    onValueChange: (String) -> Unit,
    onCreate: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = stringResource(id = R.string.add_favorite_folder),
            fontSize = 25.sp
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = textValue,
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = LocalContentColor.current),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onCreate(textValue) }),
            onValueChange = onValueChange,
            label = {
                Text(
                    modifier = Modifier.padding(20.dp),
                    text = stringResource(id = R.string.folder_name)
                )
            },
        )
        Button(
            onClick = { if (textValue.isNotEmpty()) onCreate(textValue) },
            modifier = Modifier.padding(10.dp),
            content = {
                Icon(painter = painterResource(id = R.drawable.ic_outline_folder_special_24), contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = R.string.add_favorite_folder))
            }
        )
    }
}

/**
 * [AddFavoriteFolderScreen]を表示する際に使うデータをまとめたデータクラス
 *
 * 特に無いけど
 * */
class AddFavoriteFolderScreenInitData : BottomSheetInitData(BottomSheetScreenList.AddFavoriteFolder)