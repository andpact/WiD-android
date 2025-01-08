package andpact.project.wid.view

import andpact.project.wid.viewModel.AuthenticationViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationView(authenticationViewModel: AuthenticationViewModel = hiltViewModel()) {
    val TAG = "AuthenticationView"

    val email = authenticationViewModel.email.value
    val emailModified = authenticationViewModel.emailModified.value
    val emailValid = authenticationViewModel.emailValid.value
    val authenticationLinkSentButtonClicked = authenticationViewModel.authenticationLinkSentButtonClicked.value
    val authenticationLinkSent = authenticationViewModel.authenticationLinkSent.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")
        onDispose { Log.d(TAG, "disposed") }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        content = { contentPadding: PaddingValues ->
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
                        Text(text = "이메일")
                    },
                    supportingText = {
                        Text(
                            text = if (emailModified && !emailValid) {
                                "이메일 형식이 유효하지 않습니다."
                            } else {
                                ""
                            },
                        )
                    }
                )

                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        authenticationViewModel.setAuthenticationLinkSentButtonClicked(true)
                        authenticationViewModel.sendAuthenticationLinkToEmail(email)
                    },
                    enabled = emailModified && emailValid && !authenticationLinkSentButtonClicked,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    if (authenticationLinkSentButtonClicked) {
                        CircularProgressIndicator()
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "이메일 링크 전송",
                        )

                        Text(text = "이메일 링크 전송")
                    }
                }
            }

            if (authenticationLinkSent) {
                AlertDialog(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    onDismissRequest = {

                    },
                    content = {
                        Text(
                            text = "${email}로 전송된 링크를 클릭하여 인증 절차를 완료하세요.\n링크를 클릭하면 기존 계정으로 로그인되거나,\n새로운 계정이 생성됩니다.",
//                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        }
    )
}