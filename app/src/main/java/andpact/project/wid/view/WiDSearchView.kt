package andpact.project.wid.view

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier

@Composable
fun WiDSearchView() {
    val TAG = "WiDSearchView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            /** 기간, 카테고리 */
            Row(
                content = {
                    Text(
                        text = "기간"
                    )

                    Text(
                        text = "전체 카테고리"
                    )
                }
            )
        },
        content = { contentPadding: PaddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding),
                content = {
                    item{
                        Text(
                            text = TAG
                        )
                    }
                }
            )
        }
    )
}