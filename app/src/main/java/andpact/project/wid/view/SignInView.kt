package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

/**
 * 이메일 링크를 사용한 로그인을 기본 제시하고,
 * 이메일 + 비밀 번호를 사용한 로그인 방법을 추가로 제시함.
 */
//@Composable
//fun SignInView(
//    onBackButtonPressed: () -> Unit,
//    signInViewModel: SignInViewModel = hiltViewModel()
//) {
//    val TAG = "SignInView"
//
//    // 화면
//    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp.dp
//
//    val signInEmail = signInViewModel.signInEmail.value
//    val isSignInEmailEdited = signInViewModel.isSignInEmailEdited.value
//    val isSignInEmailValid = signInViewModel.isSignInEmailValid.value
//    val isSignInEmailLinkSent = signInViewModel.isSignInEmailLinkSent.value
//    val showGoToSignUpDialog = signInViewModel.showGoToSignUpDialog.value
//
////    val password = signInViewModel.password.value
////    val isPasswordValid = signInViewModel.isPasswordValid.value
//
//    // 대화상자 나와있을 떄, 대화 상자 없애도록.
////    BackHandler(enabled = true) {
////
////    }
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//
//        onDispose {
//            Log.d(TAG, "disposed")
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.secondary)
//            .clickable(
//                interactionSource = remember { MutableInteractionSource() },
//                indication = null
//            ) {
//                if (showGoToSignUpDialog) {
//                    signInViewModel.setShowGoToSignUpDialog(show = false)
//                }
//            }
//    ) {
//        if (showGoToSignUpDialog) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .align(Alignment.Center)
//                    .background(MaterialTheme.colorScheme.tertiary)
//                    .clip(RoundedCornerShape(8.dp))
//            ) {
//                Text(
//                    text = "${signInEmail}로 가입된 계정이 없습니다.\n회원 가입 화면으로 이동하시겠습니까?",
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
//                                signInViewModel.setShowGoToSignUpDialog(show = false)
//                            },
//                        text = "아니오",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//
//                    Text(
//                        modifier = Modifier
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
////                                authViewModel.setSignUpEmail(signInEmail)
////                                authViewModel.checkSignUpEmailValidity(signInEmail)
////                                authViewModel.setSignUpEmailEdited(true)
//
////                                authViewModel.resetSignInView()
//
////                                authenticationActivityNavController.popBackStack()
////                                onGoToSignUpButtonPressed(signInEmail)
//                            },
//                        text = "이동",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            /**
//             * 상단 바
//             */
//            Box(
//                modifier = Modifier
//                    .padding(horizontal = 16.dp)
//                    .fillMaxWidth()
//                    .height(56.dp)
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .size(24.dp)
//                        .align(Alignment.CenterStart)
//                        .clickable(
//                            interactionSource = remember { MutableInteractionSource() },
//                            indication = null
//                        ) {
//                            onBackButtonPressed()
//                        },
//                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
//                    contentDescription = "뒤로 가기",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//                    .padding(horizontal = 32.dp)
//                    .padding(top = screenHeight / 5),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                if (isSignInEmailLinkSent) {
//                    Text(
//                        text = "${signInEmail}에서 로그인 절차를 완료해 주세요.",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                } else {
//                    OutlinedTextField(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        value = signInEmail,
//                        onValueChange = {
//                            signInViewModel.setSignInEmail(it)
//                            signInViewModel.setSignInEmailValid(it)
//                            signInViewModel.setSignInEmailEdited(true)
//                        },
//                        placeholder = {
//                            Text(
//                                text = "이메일",
//                                color = MaterialTheme.colorScheme.primary,
//                                style = Typography.bodyMedium
//                            )
//                        },
//                        supportingText = {
//                            Text(
//                                text = if (!isSignInEmailValid && isSignInEmailEdited) {
//                                    "이메일 형식이 유효하지 않습니다."
//                                } else {
//                                    ""
//                                },
//                                color = if (!isSignInEmailValid && isSignInEmailEdited) {
//                                    OrangeRed
//                                } else {
//                                    MaterialTheme.colorScheme.primary
//                                },
//                                style = Typography.labelSmall
//                            )
//                        }
//                    )
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(
//                                if (isSignInEmailValid) {
//                                    MaterialTheme.colorScheme.surface
//                                } else {
//                                    DarkGray
//                                }
//                            )
//                            .padding(16.dp)
//                            .clickable(
//                                enabled = isSignInEmailValid,
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                signInViewModel.sendSignInLinkToEmail(signInEmail)
//                            },
//                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Image(
//                            modifier = Modifier
//                                .size(32.dp),
//                            painter = painterResource(id = R.mipmap.ic_main_foreground), // ic_main은 안되네?
//                            contentDescription = "앱 아이콘"
//                        )
//
//                        Text(
//                            text = "로그인",
//                            color = if (isSignInEmailValid) {
//                                MaterialTheme.colorScheme.inverseSurface
//                            } else {
//                                White
//                            },
//                            style = Typography.bodyMedium
//                        )
//                    }
//                }
//            }
//
//            if (isSignInEmailLinkSent) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Text(
//                        modifier = Modifier
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                signInViewModel.setSignInEmail("")
//                                signInViewModel.setSignInEmailLinkSent(false)
//                                signInViewModel.setSignInEmailEdited(false)
//                            },
//                        text = "다른 이메일로 로그인",
//                        style = Typography.bodyMedium,
//                        color = DeepSkyBlue
//                    )
//                }
//            }
//        }
//    }
//}

//            OutlinedTextField(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                value = password,
//                onValueChange = {
//                    signInViewModel.setPassword(it)
//                    signInViewModel.checkPasswordValidity()
//                },
//                label = {
//                    Text(
//                        text = "비밀번호",
//                        color = MaterialTheme.colorScheme.primary,
//                        style = Typography.bodyMedium
//                    )
//                }
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(
//                        if (isEmailValid && isPasswordValid) {
//                            MaterialTheme.colorScheme.surface
//                        } else {
//                            DarkGray
//                        }
//                    )
//                    .padding(16.dp)
//                    .clickable(
//                        enabled = isEmailValid,
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        signIn(email, password)
//                    },
//                horizontalArrangement = Arrangement.Center
//            ) {
//                // WiD 아이콘 넣기.
//
//                Text(
//                    text = "로그인",
//                    color = if (isEmailValid && isPasswordValid) {
//                        MaterialTheme.colorScheme.inverseSurface
//                    } else {
//                        White
//                    },
//                    style = Typography.bodyMedium
//                )
//            }