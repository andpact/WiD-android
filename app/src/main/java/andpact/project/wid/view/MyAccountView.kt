package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.util.getDateString
import andpact.project.wid.util.getRandomNickname
import andpact.project.wid.viewModel.MyAccountViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAccountView(
    onUserSignedOut: () -> Unit,
    onUserDeleted: (Boolean) -> Unit,
    myAccountViewModel: MyAccountViewModel = hiltViewModel()
) {
    val TAG = "MyAccountView"

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    // 계정
    val email = myAccountViewModel.firebaseUser.value?.email ?: ""
    val emailForDialog = myAccountViewModel.emailForDialog.value
    val level = myAccountViewModel.user.value?.level
    val levelDateMap = myAccountViewModel.user.value?.levelUpHistoryMap
    val showLevelDateMapDialog = myAccountViewModel.showLevelDateMapDialog.value
    val signedUpOn = myAccountViewModel.user.value?.signedUpOn ?: LocalDate.now()
    val displayName = myAccountViewModel.firebaseUser.value?.displayName ?: getRandomNickname()
    val displayNameForDialog = myAccountViewModel.displayNameForDialog.value
    val showDisplayNameDialog = myAccountViewModel.showDisplayNameDialog.value
    val currentExp = myAccountViewModel.user.value?.currentExp
    val wiDTotalExp = myAccountViewModel.user.value?.wiDTotalExp
    val showSignOutDialog = myAccountViewModel.showSignOutDialog.value
    val showDeleteUserDialog = myAccountViewModel.showDeleteUserDialog.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Column {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                    text = "이메일",
                    style = Typography.titleMedium,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
                    text = email,
                    style = Typography.bodyMedium,
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        myAccountViewModel.setShowDisplayNameDialog(show = true)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                        text = "닉네임",
                        style = Typography.titleMedium,
                    )

                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
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
                        .padding(horizontal = 16.dp),
                    onClick = {
                        myAccountViewModel.setShowDisplayNameDialog(show = true)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_edit_24),
                        contentDescription = "닉네임 수정"
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Column {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                    text = "가입 날짜",
                    style = Typography.titleMedium,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
                    text = getDateString(date = signedUpOn),
                    style = Typography.bodyMedium,
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Column {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                    text = "레벨",
                    style = Typography.titleMedium,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
                    text = "$level",
                    style = Typography.bodyMedium,
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        myAccountViewModel.setShowLevelDateMapDialog(show = true)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                        text = "레벨 업 히스토리",
                        style = Typography.titleMedium,
                    )

                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
                        text = "레벨 업 기록을 표시합니다.",
                        style = Typography.bodyMedium,
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                IconButton(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    onClick = {
                        myAccountViewModel.setShowLevelDateMapDialog(show = true)
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.outline_delete_16),
                        contentDescription = "레벨 업 날짜 표시"
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Column {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                    text = "현재 경험치",
                    style = Typography.titleMedium,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
                    text = "$currentExp",
                    style = Typography.bodyMedium,
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Column {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                    text = "총 경험치",
                    style = Typography.titleMedium,
                )

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
                    text = "$wiDTotalExp",
                    style = Typography.bodyMedium,
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        myAccountViewModel.setShowSignOutDialog(show = true)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                        text = "로그아웃",
                        style = Typography.titleMedium,
                    )

                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
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
                        .padding(horizontal = 16.dp),
                    onClick = {
                        myAccountViewModel.setShowSignOutDialog(show = true)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_edit_24),
                        contentDescription = "로그아웃"
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        myAccountViewModel.setShowDeleteUserDialog(show = true)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                        text = "회원 탈퇴",
                        style = Typography.titleMedium,
                    )

                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
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
                        .padding(horizontal = 16.dp),
                    onClick = {
                        myAccountViewModel.setShowDeleteUserDialog(show = true)
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

    /** 닉네임 수정 대화상자 */
    if (showDisplayNameDialog) {
        DatePickerDialog(
            onDismissRequest = {
                myAccountViewModel.setShowDisplayNameDialog(show = false)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        myAccountViewModel.setShowDisplayNameDialog(show = false)

                        myAccountViewModel.updateDisplayName(newDisplayName = displayName)
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
                        myAccountViewModel.setShowDisplayNameDialog(show = false)
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
                    myAccountViewModel.setDisplayNameForDialog(newDisplayNameForDialog = it)
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

    /** 레벨 업 히스토리 맵 대화상자 */
    if (showLevelDateMapDialog) {
        AlertDialog(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.extraLarge
                ),
            onDismissRequest = {
                myAccountViewModel.setShowLevelDateMapDialog(show = false)
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp),
                    text = "레벨 업 히스토리",
                    style = Typography.titleLarge
                )

                levelDateMap?.forEach { (level, date) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = level,
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )

                        Text(
                            text = getDateString(date),
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    /** 로그아웃 대화상자 */
    if (showSignOutDialog) {
        DatePickerDialog(
            onDismissRequest = {
                myAccountViewModel.setShowSignOutDialog(show = false)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        myAccountViewModel.setShowSignOutDialog(show = false)
                        onUserSignedOut()

                        myAccountViewModel.signOut()
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
                        myAccountViewModel.setShowSignOutDialog(show = false)
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
                text = "로그아웃 하시겠습니까?",
                style = Typography.bodyMedium
            )
        }
    }

    /** 회원탈퇴 대화상자 */
    if (showDeleteUserDialog) {
        DatePickerDialog(
            onDismissRequest = {
                myAccountViewModel.setShowDeleteUserDialog(show = false)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        myAccountViewModel.setShowDeleteUserDialog(show = false)

                        myAccountViewModel.deleteUser(
                            onUserDeleted = { userDeleted: Boolean ->
                                onUserDeleted(userDeleted)
                            }
                        )
                    },
                    enabled = email == emailForDialog
                ) {
                    Text(
                        text = "삭제",
                        style = Typography.bodyMedium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        myAccountViewModel.setShowDeleteUserDialog(show = false)
                    },
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
                text = "계정을 삭제 하시려면 이메일을 입력해 주세요.",
                style = Typography.bodyMedium
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = emailForDialog,
                onValueChange = {
                    myAccountViewModel.setEmailForDialog(newEmailForDialog = it)
                },
                placeholder = {
                    Text(
                        text = "이메일",
                        style = Typography.bodyMedium
                    )
                },
            )
        }
    }
}