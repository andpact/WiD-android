package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Typography
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

//@Composable
//fun SignInAnonymouslyView(
//    authenticationActivityNavController: NavController,
//    authViewModel: AuthViewModel
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.secondary),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        /** 상단 바 */
//        Box(
//            modifier = Modifier
//                .padding(horizontal = 16.dp)
//                .fillMaxWidth()
//                .height(56.dp)
//        ) {
//            Icon(
//                modifier = Modifier
//                    .size(24.dp)
//                    .align(Alignment.CenterStart)
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        authenticationActivityNavController.popBackStack()
//                    },
//                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
//                contentDescription = "뒤로 가기",
//                tint = MaterialTheme.colorScheme.primary
//            )
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
//        ) {
//            Text(
//                text = "비회원으로 시작 시 본 기기에서만 서비스 이용이 가능하며,\n다른 휴대폰 및 태블릿에서 서비스가 연동되지 않습니다.",
//                style = Typography.bodyMedium,
//                color = MaterialTheme.colorScheme.primary
//            )
//
//            Text(
//                modifier = Modifier
//                    .clickable {
//                        authViewModel.signInAnonymously()
//                    },
//                text = "비회원으로 시작하기",
//                style = Typography.bodyMedium,
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//    }
//}