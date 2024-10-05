package andpact.project.wid.tmp

import andpact.project.wid.R
import andpact.project.wid.ui.theme.*
import andpact.project.wid.viewModel.HomeViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
 * 스플래쉬 화면 이후, 처음 표시되는 화면
 * 회원 가입은 무조건 이메일 링크를 사용하도록 함.
 * 추후에 비밀 번호 설정이나 구글 연동 등을 할 수 있도록 제공함.
 */
//@Composable
//fun SignUpView(
//    onSignInButtonPressed: () -> Unit,
////    onGoToSignInButtonPressed: (String) -> Unit,
////    signUpEmailFromSignInView: String,
//    signUpViewModel: SignUpViewModel = hiltViewModel()
//) {
//    val TAG = "SignUpView"
//
//    // 화면
//    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp.dp
//
//    // 인증
//    val signUpEmail = signUpViewModel.signUpEmail.value
//    val isSignUpEmailEdited = signUpViewModel.isSignUpEmailEdited.value
//    val isEmailValid = signUpViewModel.isSignUpEmailValid.value
//    val isSignUpEmailLinkSent = signUpViewModel.isSignUpEmailLinkSent.value
//    val showGoToSignInDialog = signUpViewModel.showGoToSignInDialog.value
//
////    val password = signUpViewModel.password.value
////    val isPasswordEdited = signUpViewModel.isPasswordEdited.value
////    val isPasswordValid = signUpViewModel.isPasswordValid.value
////
////    val confirmPassword = signUpViewModel.confirmPassword.value
////    val isConfirmPasswordEdited = signUpViewModel.isConfirmPasswordEdited.value
////    val isConfirmPasswordValid = signUpViewModel.isConfirmPasswordValid.value
//
////    val actionCodeSettings = signUpViewModel.actionCodeSettings
//
//    DisposableEffect(Unit) {
//        Log.d(TAG, "composed")
//
////        signUpViewModel.setSignUpEmail(signUpEmailFromSignInView)
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
//                if (showGoToSignInDialog) {
//                    signUpViewModel.setShowGoToSignInDialog(show = false)
//                }
//            }
//    ) {
//        if (showGoToSignInDialog) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .align(Alignment.Center)
//                    .background(MaterialTheme.colorScheme.tertiary)
//                    .clip(RoundedCornerShape(8.dp))
//            ) {
//                Text(
//                    modifier = Modifier
//                        .padding(16.dp),
//                    text = "이미 ${signUpEmail}로 가입된 계정이 있습니다.\n로그인 화면으로 이동하시겠습니까?",
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
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                signUpViewModel.setShowGoToSignInDialog(show = false)
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
//                                // SignUpViewModel의 SignUpEmail을 어떻게 SignInViewModel로 가져갈 것인가?
////                                authViewModel.setSignInEmail(signUpEmail)
////                                authViewModel.checkSignInEmailValidity(signUpEmail)
////                                authViewModel.setSignInEmailEdited(true)
////
////                                authViewModel.resetSignUpView()
//
////                                authenticationActivityNavController.navigate(SignInActivityDestinations.SignInViewDestination.route)
////                                onGoToSignInButtonPressed(signUpEmail)
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
//
//            }
//
//            /**
//             * 회원 가입 버튼 누르면 이메일과 비밀번호, 확인 비밀번호 수정하지 못하도록.
//             */
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//                    .padding(horizontal = 32.dp)
//                    .padding(top = screenHeight / 5),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                if (isSignUpEmailLinkSent) {
//                    Text(
//                        text = "${signUpEmail}에서 회원 가입 절차를 완료해 주세요.",
//                        style = Typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                } else {
//                    OutlinedTextField(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        value = signUpEmail,
//    //                    enabled = !isSignUpEmailLinkSent,
//    //                    readOnly = isSignUpEmailLinkSent,
//                        onValueChange = {
//                            signUpViewModel.setSignUpEmail(it)
//                            signUpViewModel.setSignUpEmailValid(it)
//                            signUpViewModel.setSignUpEmailEdited(true)
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
//                                text = if (!isEmailValid && isSignUpEmailEdited) {
//                                    "이메일 형식이 유효하지 않습니다."
//                                } else {
//                                    ""
//                                },
//                                color = if (!isEmailValid && isSignUpEmailEdited) {
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
//                                if (isEmailValid) {
//                                    MaterialTheme.colorScheme.surface
//                                } else {
//                                    DarkGray
//                                }
//                            )
//                            .padding(16.dp)
//                            .clickable(
//                                enabled = isEmailValid,
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                signUpViewModel.sendSignUpLinkToEmail(signUpEmail)
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
//                            text = "회원 가입",
//                            color = if (isEmailValid) {
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
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                if (isSignUpEmailLinkSent) {
//                    Text(
//                        modifier = Modifier
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                signUpViewModel.setSignUpEmail("")
//                                signUpViewModel.setSignUpEmailLinkSent(false)
//                                signUpViewModel.setSignUpEmailEdited(false)
//                            },
//                        text = "다른 이메일로 회원가입",
//                        style = Typography.bodyMedium,
//                        color = DeepSkyBlue
//                    )
//                } else {
////                    Text(
////                        modifier = Modifier
////                            .clickable(
////                                interactionSource = remember { MutableInteractionSource() },
////                                indication = null
////                            ) {
//////                                authenticationActivityNavController.navigate(SignInActivityDestinations.SignInAnonymouslyViewDestination.route)
////                            },
////                        text = "비 회원으로 시작",
////                        color = DeepSkyBlue,
////                        style = Typography.bodyMedium
////                    )
//
//                    Text(
//                        modifier = Modifier
//                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null
//                            ) {
//                                onSignInButtonPressed()
////                                authenticationActivityNavController.navigate(SignInActivityDestinations.SignInViewDestination.route)
//                            },
//                        text = "로그인 화면",
//                        color = DeepSkyBlue,
//                        style = Typography.bodyMedium
//                    )
//                }
//            }
//        }
//    }
//}

//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth(),
//            value = password,
//            onValueChange = {
//                signUpViewModel.setPassword(it)
//                signUpViewModel.checkPasswordValidity()
//                signUpViewModel.setPasswordEdited(edited = true)
//
//                // 비밀 번호 입력, 학인 비밀 번호 입력 후
//                // 비밀 번호를 다시 수정했을 때, 확인 비밀번호의 유효성도 체크함.
//                signUpViewModel.checkConfirmPasswordValidity()
//            },
//            label = {
//                Text(
//                    text = "비밀번호",
//                    color = MaterialTheme.colorScheme.primary,
//                    style = Typography.bodyMedium
//                )
//            },
//            supportingText = {
//                Text(
//                    text = if (!isPasswordValid && isPasswordEdited) {
////                        "비밀번호는 최소 8자 이상이어야 하며, 대문자, 소문자, 숫자가 모두 포함되어야 합니다."
//                        "영어 소문자, 대문자, 숫자를 포함 8자 이상을 사용해 주세요."
//                    } else {
//                        ""
//                    },
//                    color = if (!isPasswordValid && isPasswordEdited) {
//                        OrangeRed
//                    } else {
//                        MaterialTheme.colorScheme.primary
//                    },
//                    style = Typography.labelSmall
//                )
//            }
//        )
//
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth(),
//            value = confirmPassword,
//            onValueChange = {
//                signUpViewModel.setConfirmPassword(it)
//                signUpViewModel.checkConfirmPasswordValidity()
//                signUpViewModel.setConfirmPasswordEdited(edited = true)
//            },
//            label = {
//                Text(
//                    text = "비밀번호 확인",
//                    color = MaterialTheme.colorScheme.primary,
//                    style = Typography.bodyMedium
//                )
//            },
//            supportingText = {
//                Text(
//                    text = if (!isConfirmPasswordValid && isConfirmPasswordEdited) {
//                        "비밀번호가 일치하지 않습니다."
//                    } else {
//                        ""
//                    },
//                    color = if (!isConfirmPasswordValid && isConfirmPasswordEdited) {
//                        OrangeRed
//                    } else {
//                        MaterialTheme.colorScheme.primary
//                    },
//                    style = Typography.labelSmall
//                )
//            }
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(8.dp))
//                .background(
//                    if (isEmailValid && isPasswordValid && isConfirmPasswordValid) {
//                        MaterialTheme.colorScheme.surface
//                    } else {
//                        DarkGray
//                    }
//                )
//                .padding(16.dp)
//                .clickable(
//                    enabled = isEmailValid && isPasswordValid && isConfirmPasswordValid,
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null
//                ) {
//                    sendSignInLinkToEmail(email, actionCodeSettings)
//                },
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "회원 가입",
//                color = if (isEmailValid && isPasswordValid && isConfirmPasswordValid) {
//                    MaterialTheme.colorScheme.inverseSurface
//                } else {
//                    White
//                },
//                style = Typography.bodyMedium
//            )
//        }