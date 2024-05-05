package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.activity.MainActivityViewDestinations
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.viewModel.HomeViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun HomeView(
    onSettingButtonPressed: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val TAG = "HomeView"

    val firebaseUser = homeViewModel.firebaseUser.value
    val user = homeViewModel.user.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        /**
         * 상단 바
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = firebaseUser?.displayName ?: "닉네임",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

//            Image(
//                modifier = Modifier
//                    .size(36.dp),
//                painter = painterResource(id = R.mipmap.ic_main_foreground), // ic_main은 안되네?
//                contentDescription = "앱 아이콘"
//            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Icon(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSettingButtonPressed()
                    }
                    .size(24.dp),
                painter = painterResource(id = R.drawable.baseline_settings_24),
                contentDescription = "환경 설정"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = user?.statusMessage ?: "상태 메세지를\n설정해 주세요.",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            // 익명 가입 시 uid를 제외하고는 null이 할당됨.

//            Text(
//                text = "유저 이름 : ${user?.displayName}"
//            )
//
//            Text(
//                text = "익명 계정 : ${user?.isAnonymous}"
//            )
//
//            Text(
//                text = "이메일 : ${user?.email}"
//            )
//
//            Text(
//                text = "uid : ${user?.uid}"
//            )
//
//            Text(
//                text = "이메일 인증 여부 : ${user?.isEmailVerified}"
//            )
//
//            Text(
//                text = "전화 번호 : ${user?.phoneNumber}"
//            )
//
//            Text(
//                text = "사진 : ${user?.photoUrl}"
//            )
        }
    }
}