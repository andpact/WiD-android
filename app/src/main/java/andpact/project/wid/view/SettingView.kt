package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.SettingViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun SettingView(
    onBackButtonPressed: () -> Unit,
    onUserSignedOut: (Boolean) -> Unit,
    onUserDeleted: (Boolean) -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel()
) {
    val TAG = "SettingView"

    val firebaseUser = settingViewModel.firebaseUser.value
    val user = settingViewModel.user.value

    val showSignOutDialog = settingViewModel.showSignOutDialog.value
    val showDeleteUserDialog = settingViewModel.showDeleteUserDialog.value

//    val firebaseUser = settingViewModel.firebaseUser

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    // 휴대폰 뒤로 가기 버튼 클릭 시
    BackHandler(enabled = showSignOutDialog || showDeleteUserDialog) {
        settingViewModel.setShowSignOutDialog(false)
        settingViewModel.setShowDeleteUserDialog(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (showSignOutDialog || showDeleteUserDialog) {
                    settingViewModel.setShowSignOutDialog(false)
                    settingViewModel.setShowDeleteUserDialog(false)
                }
            }
    ) {
        if (showSignOutDialog) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.secondary)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "로그아웃 하시겠습니까?",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                settingViewModel.setShowSignOutDialog(false)
                            },
                        text = "취소",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                settingViewModel.signOut()
                                onUserSignedOut(true)
                            },
                        text = "로그아웃",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (showDeleteUserDialog) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.secondary)
                    .clip(RoundedCornerShape(8.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .padding(16.dp),
                    text = "회원 탈퇴 하시겠습니까?",
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                settingViewModel.setShowDeleteUserDialog(false)
                            },
                        text = "취소",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                              settingViewModel.deleteUser(
                                  onUserDeleted = { userDeleted: Boolean ->
                                      onUserDeleted(userDeleted)
                                  }
                              )
                            },
                        text = "회원 탈퇴",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            /**
             * 상단 바
             */
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterStart)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
//                            mainActivityNavController.popBackStack()
                            onBackButtonPressed()
                        },
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "뒤로 가기",
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = "환경 설정",
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    Text(
                        text = "계정",
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                            .clip(RoundedCornerShape(8.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
//                        if (user?.isAnonymous == true) {
//                            Text(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp),
//                                text = "비회원 입니다.",
//                                style = Typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//
//                            Text(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp)
//                                    .background(OrangeRed)
//                                    .clickable {
//                                        settingViewModel.setShowDeleteUserDialog(true)
//                                    },
//                                text = "회원 탈퇴",
//                                style = Typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                           ) {
                                Text(
                                    text = "이메일",
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = "${firebaseUser?.email}",
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "닉네임",
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = firebaseUser?.displayName ?: "닉네임",
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(DeepSkyBlue)
                                        .clickable {
                                            settingViewModel.setShowSignOutDialog(true)
                                        },
                                    text = "로그아웃",
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(OrangeRed)
                                        .clickable {
                                            settingViewModel.setShowDeleteUserDialog(true)
                                        },
                                    text = "회원 탈퇴",
                                    style = Typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
//                        }
                    }
                }

    //            item {
    //                Text(
    //                    text = "일반",
    //                    style = Typography.bodyMedium,
    //                    color = MaterialTheme.colorScheme.primary
    //                )
    //            }
    //
    //            item {
    //                Text(
    //                    text = "스톱 워치",
    //                    style = Typography.bodyMedium,
    //                    color = MaterialTheme.colorScheme.primary
    //                )
    //            }
    //
    //            item {
    //                Text(
    //                    text = "타이머",
    //                    style = Typography.bodyMedium,
    //                    color = MaterialTheme.colorScheme.primary
    //                )
    //            }
            }
        }
    }
}