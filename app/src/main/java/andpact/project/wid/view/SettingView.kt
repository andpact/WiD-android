package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.util.getDateString
import andpact.project.wid.viewModel.SettingViewModel
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingView(
    onBackButtonPressed: () -> Unit,
    onUserSignedOut: () -> Unit,
    onUserDeleted: (Boolean) -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel()
) {
    val TAG = "SettingView"

    val email = settingViewModel.firebaseUser.value?.email ?: ""
    val displayName = settingViewModel.firebaseUser.value?.displayName ?: ""
    val displayNameForDialog = settingViewModel.displayName.value
//    val statusMessage = settingViewModel.user.value?.statusMessage ?: ""
//    val statusMessageForDialog = settingViewModel.statusMessage.value
    val signedUpOn = settingViewModel.user.value?.signedUpOn ?: LocalDate.now()
    val level = settingViewModel.user.value?.level
    val currentExp = settingViewModel.user.value?.currentExp
    val totalExp = settingViewModel.user.value?.totalExp

    val setShowDisplayNameDialog = settingViewModel.showDisplayNameDialog.value
    val showStatusMessageDialog = settingViewModel.showStatusMessageDialog.value
    val showSignOutDialog = settingViewModel.showSignOutDialog.value
    val showDeleteUserDialog = settingViewModel.showDeleteUserDialog.value

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

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackButtonPressed()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "뒤로 가기",
                        )
                    }
                },
                title = {
                    Text(
                        text = "환경 설정",
                        style = Typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /** 이메일 */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "이메일",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = email,
                        style = Typography.bodyMedium,
                    )
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            /** 닉네임 */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingViewModel.setShowDisplayNameDialog(show = true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "닉네임",
                            style = Typography.titleMedium,
                        )

                        Text(
                            text = displayName,
                            style = Typography.bodyMedium,
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    IconButton(
                        modifier = Modifier
                            .padding(16.dp),
                        onClick = {
                            settingViewModel.setShowDisplayNameDialog(show = true)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_edit_24),
                            contentDescription = "닉네임 수정"
                        )
                    }
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            /** 상태 메시지 */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingViewModel.setShowStatusMessageDialog(show = true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Column(
//                        modifier = Modifier
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Text(
//                            text = "상태 메시지",
//                            style = Typography.titleMedium,
//                        )
//
//                        Text(
//                            text = statusMessage,
//                            style = Typography.bodyMedium,
//                        )
//                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    IconButton(
                        modifier = Modifier
                            .padding(16.dp),
                        onClick = {
                            settingViewModel.setShowStatusMessageDialog(show = true)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_edit_24),
                            contentDescription = "상태 메시지 수정"
                        )
                    }
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            /** 가입 날짜 */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "가입 날짜",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = getDateString(date = signedUpOn),
                        style = Typography.bodyMedium,
                    )
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            /** 레벨 */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "레벨",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = "$level",
                        style = Typography.bodyMedium,
                    )
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            /** 현재 경험치 */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "현재 경험치",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = "$currentExp",
                        style = Typography.bodyMedium,
                    )
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            /** 총 경험치 */
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "총 경험치",
                        style = Typography.titleMedium,
                    )

                    Text(
                        text = "$totalExp",
                        style = Typography.bodyMedium,
                    )
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            /** 로그아웃 */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingViewModel.setShowSignOutDialog(show = true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "로그아웃",
                            style = Typography.titleMedium,
                        )

                        Text(
                            text = "계정이 로그아웃 됩니다.",
                            style = Typography.bodyMedium,
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    IconButton(
                        modifier = Modifier
                            .padding(16.dp),
                        onClick = {
                            settingViewModel.setShowSignOutDialog(show = true)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_edit_24),
                            contentDescription = "로그아웃"
                        )
                    }
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }

            /** 회원 탈퇴 */
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingViewModel.setShowDeleteUserDialog(show = true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "회원 탈퇴",
                            style = Typography.titleMedium,
                        )

                        Text(
                            text = "계정을 삭제합니다.",
                            style = Typography.bodyMedium,
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    IconButton(
                        modifier = Modifier
                            .padding(16.dp),
                        onClick = {
                            settingViewModel.setShowDeleteUserDialog(show = true)
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.outline_delete_16),
                            contentDescription = "회원 탈퇴"
                        )
                    }
                }
            }
        }

        if (setShowDisplayNameDialog) {
            DatePickerDialog(
                onDismissRequest = {
                    settingViewModel.setShowDisplayNameDialog(show = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            settingViewModel.setShowDisplayNameDialog(show = false)

                            settingViewModel.updateDisplayName(newDisplayName = displayName)
                        },
                    ) {
                        Text(
                            text = "확인",
                            style = Typography.bodyMedium
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            settingViewModel.setShowDisplayNameDialog(show = false)
                        }
                    ) {
                        Text(
                            text = "취소",
                            style = Typography.bodyMedium
                        )
                    }
                }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = "닉네임 수정",
                    style = Typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = displayNameForDialog,
                    onValueChange = {
                        settingViewModel.setDisplayName(newDisplayName = it)
                    },
                    placeholder = {
                        Text(
                            text = "닉네임",
                            style = Typography.bodyMedium
                        )
                    },
                )
            }
        }

//        if (showStatusMessageDialog) {
//            DatePickerDialog(
//                onDismissRequest = {
//                    settingViewModel.setShowStatusMessageDialog(show = false)
//                },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            settingViewModel.setShowStatusMessageDialog(show = false)
//
//                            settingViewModel.updateStatusMessage(newStatusMessage = statusMessage)
//                        },
//                    ) {
//                        Text(
//                            text = "확인",
//                            style = Typography.bodyMedium
//                        )
//                    }
//                },
//                dismissButton = {
//                    TextButton(
//                        onClick = {
//                            settingViewModel.setShowStatusMessageDialog(show = false)
//                        }
//                    ) {
//                        Text(
//                            text = "취소",
//                            style = Typography.bodyMedium
//                        )
//                    }
//                }
//            ) {
//                Text(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    text = "상태 메시지 수정",
//                    style = Typography.titleLarge,
//                    textAlign = TextAlign.Center
//                )
//
//                OutlinedTextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    value = statusMessageForDialog,
//                    onValueChange = {
//                        settingViewModel.setStatusMessage(newStatusMessage = it)
//                    },
//                    placeholder = {
//                        Text(
//                            text = "상태 메시지",
//                            style = Typography.bodyMedium
//                        )
//                    },
//                )
//            }
//        }

        if (showSignOutDialog) {
            DatePickerDialog(
                onDismissRequest = {
                    settingViewModel.setShowSignOutDialog(show = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            settingViewModel.setShowSignOutDialog(show = false)
                            onUserSignedOut()

                            settingViewModel.signOut()
                        },
                    ) {
                        Text(
                            text = "로그아웃",
                            style = Typography.bodyMedium
                        )
                    }
                },
                dismissButton = {
                   TextButton(
                        onClick = {
                            settingViewModel.setShowSignOutDialog(show = false)
                        }
                    ) {
                        Text(
                            text = "취소",
                            style = Typography.bodyMedium
                        )
                    }
                }
            ) {
                Text(
                    text = "로그아웃 하시겠습니까?",
                    style = Typography.bodyMedium
                )
            }
        }

        if (showDeleteUserDialog) {
            DatePickerDialog(
                onDismissRequest = {
                    settingViewModel.setShowDeleteUserDialog(show = false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            settingViewModel.setShowDeleteUserDialog(show = false)

                            settingViewModel.deleteUser() { userDeleted: Boolean ->
                                onUserDeleted(userDeleted)
                            }
                        },
                    ) {
                        Text(
                            text = "계정 삭제",
                            style = Typography.bodyMedium
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            settingViewModel.setShowDeleteUserDialog(show = false)
                        }
                    ) {
                        Text(
                            text = "취소",
                            style = Typography.bodyMedium
                        )
                    }
                }
            ) {
                Text(
                    text = "계정을 삭제 하시겠습니까?",
                    style = Typography.bodyMedium
                )
            }
        }
    }

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.tertiary)
//            .clickable(
//                interactionSource = remember { MutableInteractionSource() },
//                indication = null
//            ) {
//                if (showSignOutDialog || showDeleteUserDialog) {
//                    settingViewModel.setShowSignOutDialog(false)
//                    settingViewModel.setShowDeleteUserDialog(false)
//                }
//            }
//    ) {
//        if (showSignOutDialog) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .align(Alignment.Center)
//                    .background(MaterialTheme.colorScheme.secondary)
//                    .clip(RoundedCornerShape(8.dp))
//            ) {
//                Text(
//                    text = "로그아웃 하시겠습니까?",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Text(
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                settingViewModel.setShowSignOutDialog(false)
//                            },
//                        text = "취소",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//
//                    Text(
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                settingViewModel.signOut()
//                                onUserSignedOut(true)
//                            },
//                        text = "로그아웃",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//        }
//
//        if (showDeleteUserDialog) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .align(Alignment.Center)
//                    .background(MaterialTheme.colorScheme.secondary)
//                    .clip(RoundedCornerShape(8.dp)),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    modifier = Modifier
//                        .padding(16.dp),
//                    text = "회원 탈퇴 하시겠습니까?",
//                    style = Typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Text(
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                settingViewModel.setShowDeleteUserDialog(false)
//                            },
//                        text = "취소",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//
//                    Text(
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                settingViewModel.deleteUser(
//                                    onUserDeleted = { userDeleted: Boolean ->
//                                        onUserDeleted(userDeleted)
//                                    }
//                                )
//                            },
//                        text = "회원 탈퇴",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            /**
//             * 상단 바
//             */
////            Box(
////                modifier = Modifier
////                    .padding(horizontal = 16.dp)
////                    .fillMaxWidth()
////                    .height(56.dp)
////            ) {
////                Icon(
////                    modifier = Modifier
////                        .size(24.dp)
////                        .align(Alignment.CenterStart)
////                        .clickable(
////                            interactionSource = remember { MutableInteractionSource() },
////                            indication = null
////                        ) {
//////                            mainActivityNavController.popBackStack()
////                            onBackButtonPressed()
////                        },
////                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
////                    contentDescription = "뒤로 가기",
////                    tint = MaterialTheme.colorScheme.primary
////                )
////
////                Text(
////                    modifier = Modifier
////                        .align(Alignment.Center),
////                    text = "환경 설정",
////                    style = Typography.titleLarge,
////                    color = MaterialTheme.colorScheme.primary
////                )
////            }
//
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//            ) {
//                item {
//                    Text(
//                        text = "계정",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(MaterialTheme.colorScheme.secondary)
//                            .clip(RoundedCornerShape(8.dp)),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
////                        if (user?.isAnonymous == true) {
////                            Text(
////                                modifier = Modifier
////                                    .fillMaxWidth()
////                                    .padding(16.dp),
////                                text = "비회원 입니다.",
////                                style = Typography.bodyMedium,
////                                color = MaterialTheme.colorScheme.primary
////                            )
////
////                            Text(
////                                modifier = Modifier
////                                    .fillMaxWidth()
////                                    .padding(16.dp)
////                                    .background(OrangeRed)
////                                    .clickable {
////                                        settingViewModel.setShowDeleteUserDialog(true)
////                                    },
////                                text = "회원 탈퇴",
////                                style = Typography.bodyMedium,
////                                color = MaterialTheme.colorScheme.primary
////                            )
////                        } else {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                           ) {
//                                Text(
//                                    text = "이메일",
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//
//                                Text(
//                                    text = "${firebaseUser?.email}",
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Text(
//                                    text = "닉네임",
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//
//                                Text(
//                                    text = firebaseUser?.displayName ?: "닉네임",
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp)
//                            ) {
//                                Text(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                        .background(DeepSkyBlue)
//                                        .clickable {
//                                            settingViewModel.setShowSignOutDialog(true)
//                                        },
//                                    text = "로그아웃",
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//
//                                Text(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                        .background(OrangeRed)
//                                        .clickable {
//                                            settingViewModel.setShowDeleteUserDialog(true)
//                                        },
//                                    text = "회원 탈퇴",
//                                    style = Typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
////                        }
//                    }
//                }
//
//    //            item {
//    //                Text(
//    //                    text = "일반",
//    //                    style = Typography.bodyMedium,
//    //                    color = MaterialTheme.colorScheme.primary
//    //                )
//    //            }
//    //
//    //            item {
//    //                Text(
//    //                    text = "스톱 워치",
//    //                    style = Typography.bodyMedium,
//    //                    color = MaterialTheme.colorScheme.primary
//    //                )
//    //            }
//    //
//    //            item {
//    //                Text(
//    //                    text = "타이머",
//    //                    style = Typography.bodyMedium,
//    //                    color = MaterialTheme.colorScheme.primary
//    //                )
//    //            }
//            }
//        }
//    }
}