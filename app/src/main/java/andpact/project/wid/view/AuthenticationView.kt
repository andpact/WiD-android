package andpact.project.wid.view

import andpact.project.wid.R
import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.OrangeRed
import andpact.project.wid.ui.theme.SplashTheme
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.viewModel.AuthenticationViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationView(
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {
    val TAG = "AuthenticationView"

    val email = authenticationViewModel.email.value
    val emailModified = authenticationViewModel.emailModified.value
    val emailValid = authenticationViewModel.emailValid.value
    val authenticationLinkSentButtonClicked = authenticationViewModel.authenticationLinkSentButtonClicked.value
    val authenticationLinkSent = authenticationViewModel.authenticationLinkSent.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    SplashTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = email,
                    enabled = !authenticationLinkSentButtonClicked,
                    isError = emailModified && !emailValid,
                    onValueChange = {
                        if (!emailModified) {
                            authenticationViewModel.setEmailModified(true)
                        }

                        authenticationViewModel.setEmail(it)
                        authenticationViewModel.setEmailValid(it)
                    },
                    placeholder = {
                        Text(
                            text = "이메일",
                            style = Typography.bodyMedium
                        )
                    },
                    supportingText = {
                        Text(
                            text = if (emailModified && !emailValid) {
                                "이메일 형식이 유효하지 않습니다."
                            } else {
                                ""
                            },
                            style = Typography.labelSmall
                        )
                    }
                )

                FilledIconButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        authenticationViewModel.setAuthenticationLinkSentButtonClicked(true)
                        authenticationViewModel.sendAuthenticationLinkToEmail(email)
                    },
                    enabled = emailModified && emailValid && !authenticationLinkSentButtonClicked,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                    ) {
                        if (authenticationLinkSentButtonClicked) {
                            CircularProgressIndicator()
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.baseline_send_24),
                                contentDescription = "이메일 링크 전송",
                            )

                            Text(
                                text = "이메일 링크 전송",
                                style = Typography.bodyMedium,
                            )
                        }
                    }
                }
            }

            if (authenticationLinkSent) {
                AlertDialog(
                    onDismissRequest = {

                    }
                ) {
                    Text(
                        text = "${email}로 전송된 링크를 클릭하여 인증 절차를 완료하세요.\n링크를 클릭하면 기존 계정으로 로그인되거나,\n새로운 계정이 생성됩니다.",
                        style = Typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}