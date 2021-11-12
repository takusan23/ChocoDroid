package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.database.entity.FavoriteFolderDBEntity
import kotlinx.coroutines.launch

/**
 * お気に入りフォルダーを追加する画面
 *
 * BottomSheetで使う
 *
 * @param onClose 閉じるときに使う
 * */
@Composable
fun AddFavoriteFolder(
    onClose: () -> Unit,
) {
    val textValue = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    AddFavoriteFolder(
        textValue = textValue.value,
        onValueChange = { textValue.value = it },
        onCreate = { folderName ->
            scope.launch {
                // DBへ追加
                addFavoriteFolder(context, folderName)
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
fun AddFavoriteFolder(
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
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(20.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (textValue.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(20.dp),
                        text = stringResource(id = R.string.folder_name)
                    )
                }
                BasicTextField(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    value = textValue,
                    maxLines = 1,
                    singleLine = true,
                    textStyle = TextStyle(color = LocalContentColor.current),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onCreate(textValue) }),
                    onValueChange = onValueChange,
                )
            }
        }
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
 * DBにフォルダを追加する
 *
 * @param context Context
 * @param folderName フォルダ名
 * */
private suspend fun addFavoriteFolder(context: Context, folderName: String) {
    val folderDBEntity = FavoriteFolderDBEntity(folderName = folderName)
    val dao = FavoriteDB.getInstance(context).favoriteDao()
    dao.insert(folderDBEntity)
}