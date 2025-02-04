package andpact.project.wid.view

import andpact.project.wid.model.City
import andpact.project.wid.viewModel.MyAccountViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAccountView(
    onCityPickerClicked: (currentCity: City) -> Unit,
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
//    val email = myAccountViewModel.firebaseUser.value?.email ?: ""
    val email = myAccountViewModel.user.value?.email ?: ""
    val emailForDialog = myAccountViewModel.emailForDialog.value
    val level = myAccountViewModel.user.value?.level ?: 0
    val city = myAccountViewModel.user.value?.city ?: City.SEOUL
    val signedUpOn = myAccountViewModel.user.value?.signedUpOn ?: LocalDate.now()
//    val displayName = myAccountViewModel.firebaseUser.value?.displayName ?: "tmp nickname222"
//    val displayNameForDialog = myAccountViewModel.displayNameForDialog.value
//    val showDisplayNameDialog = myAccountViewModel.showDisplayNameDialog.value
    val currentExp = myAccountViewModel.user.value?.currentExp ?: 0
    val wiDTotalExp = myAccountViewModel.user.value?.wiDTotalExp ?: 0
    val showSignOutDialog = myAccountViewModel.showSignOutDialog.value
    val showDeleteUserDialog = myAccountViewModel.showDeleteUserDialog.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item(
            key = "email",
            contentType = "list-item"
        ) {
            ListItem(
                headlineContent = {
                    Text(text = "이메일")
                },
                supportingContent = {
                    Text(text = email)
                }
            )
        }

//        item(
//            key = "display-name",
//            contentType = "list-item"
//        ) {
//            ListItem(
//                modifier = Modifier
//                    .clickable {
//                        myAccountViewModel.setShowDisplayNameDialog(show = true)
//                    },
//                headlineContent = {
//                    Text(text = "닉네임")
//                },
//                supportingContent = {
//                    Text(text = displayName)
//                },
//                trailingContent = {
//                    Icon(
//                        imageVector = Icons.Default.ArrowDropDown,
//                        contentDescription = "닉네임 수정 대화상자 열기"
//                    )
//                }
//            )
//        }

        item(
            key = "location",
            contentType = "list-item"
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        onCityPickerClicked(city)
                    },
                headlineContent = {
                    Text(text = "위치")
                },
                supportingContent = {
                    Text(text = "${city.kr}, ${city.country.kr}")
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "위치 수정 대화상자 열기"
                    )
                }
            )
        }

        item(
            key = "signed-up-on",
            contentType = "list-item"
        ) {
            ListItem(
                headlineContent = {
                    Text(text = "가입 날짜")
                },
                supportingContent = {
                    Text(text = myAccountViewModel.getDateString(date = signedUpOn))
                }
            )
        }

        item(
            key = "level",
            contentType = "list-item"
        ) {
            ListItem(
                headlineContent = {
                    Text(text = "레벨")
                },
                supportingContent = {
                    Text(text = "$level")
                }
            )
        }

        item(
            key = "current-exp",
            contentType = "list-item"
        ) {
            ListItem(
                headlineContent = {
                    Text(text = "현재 경험치")
                },
                supportingContent = {
                    Text(text = "$currentExp")
                }
            )
        }

        item(
            key = "wid-total-exp",
            contentType = "list-item"
        ) {
            ListItem(
                headlineContent = {
                    Text(text = "총 경험치")
                },
                supportingContent = {
                    Text(text = "$wiDTotalExp")
                }
            )
        }

        item(
            key = "sign-out",
            contentType = "list-item"
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        myAccountViewModel.setShowSignOutDialog(show = true)
                    },
                headlineContent = {
                    Text(text = "로그아웃")
                },
                supportingContent = {
                    Text(text = "계정이 로그아웃 됩니다.")
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "로그아웃 대화상자 열기"
                    )
                }
            )
        }

        item(
            key = "delete-user",
            contentType = "list-item"
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        myAccountViewModel.setShowDeleteUserDialog(show = true)
                    },
                headlineContent = {
                    Text(text = "회원 탈퇴")
                },
                supportingContent = {
                    Text(text = "계정을 삭제합니다.")
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "회원 탈퇴 대화상자 열기"
                    )
                }
            )
        }
    }

//    if (showDisplayNameDialog) {
//        DatePickerDialog(
//            onDismissRequest = {
//                myAccountViewModel.setShowDisplayNameDialog(show = false)
//            },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        myAccountViewModel.setShowDisplayNameDialog(show = false)
//
//                        myAccountViewModel.updateDisplayName(newDisplayName = displayName)
//                    },
//                ) {
//                    Text(text = "확인")
//                }
//            },
//            dismissButton = {
//                TextButton(
//                    onClick = {
//                        myAccountViewModel.setShowDisplayNameDialog(show = false)
//                    }
//                ) {
//                    Text(text = "취소")
//                }
//            }
//        ) {
//            Text(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                text = "닉네임 수정",
//                style = MaterialTheme.typography.bodyLarge,
//                textAlign = TextAlign.Center
//            )
//
//            OutlinedTextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                value = displayNameForDialog,
//                onValueChange = {
//                    myAccountViewModel.setDisplayNameForDialog(newDisplayNameForDialog = it)
//                },
//                placeholder = {
//                    Text(text = "닉네임")
//                }
//            )
//        }
//    }

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
                    Text(text = "로그아웃")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        myAccountViewModel.setShowSignOutDialog(show = false)
                    }
                ) {
                    Text(text = "취소")
                }
            }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "로그아웃 하시겠습니까?",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

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
                    Text(text = "삭제")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        myAccountViewModel.setShowDeleteUserDialog(show = false)
                    },
                ) {
                    Text(text = "취소")
                }
            }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "계정을 삭제 하시려면 이메일을 입력해 주세요.",
                style = MaterialTheme.typography.bodyLarge
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
                    Text(text = "이메일")
                }
            )
        }
    }
}