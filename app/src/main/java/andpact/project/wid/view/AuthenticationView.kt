package andpact.project.wid.view

import andpact.project.wid.ui.theme.DarkGray
import andpact.project.wid.ui.theme.OrangeRed
import andpact.project.wid.ui.theme.Typography
import andpact.project.wid.viewModel.AuthenticationViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthenticationView(
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {
    val TAG = "AuthenticationView"

    val email = authenticationViewModel.email.value
    val isEmailEdited = authenticationViewModel.isEmailEdited.value
    val isEmailValid = authenticationViewModel.isEmailValid.value
    val isAuthenticationLinkSentButtonClicked = authenticationViewModel.isAuthenticationLinkSentButtonClicked.value
    val isAuthenticationLinkSent = authenticationViewModel.isAuthenticationLinkSent.value

    DisposableEffect(Unit) {
        Log.d(TAG, "composed")

        onDispose {
            Log.d(TAG, "disposed")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isAuthenticationLinkSent) {
            Text(
                text = "${email}로 전송된 링크를 클릭하여 인증 절차를 완료하세요.\n링크를 클릭하면 기존 계정으로 로그인되거나,\n새로운 계정이 생성됩니다.",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = email,
//                enabled = !isEmailLinkSent && !isEmailLinkSentButtonClicked,
//                readOnly = isEmailLinkSent && isEmailLinkSentButtonClicked,
                onValueChange = {
                    authenticationViewModel.setEmail(it)
                    authenticationViewModel.setEmailValid(it)
                    authenticationViewModel.setEmailEdited(true)
                },
                placeholder = {
                    Text(
                        text = "이메일",
                        color = MaterialTheme.colorScheme.primary,
                        style = Typography.bodyMedium
                    )
                },
                supportingText = {
                    Text(
                        text = if (!isEmailValid && isEmailEdited) {
                            "이메일 형식이 유효하지 않습니다."
                        } else {
                            ""
                        },
                        color = if (!isEmailValid && isEmailEdited) {
                            OrangeRed
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        style = Typography.labelSmall
                    )
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isEmailEdited && isEmailValid) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            DarkGray
                        }
                    )
                    .padding(16.dp)
                    .clickable(
                        enabled = isEmailEdited && isEmailValid && !isAuthenticationLinkSentButtonClicked,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        authenticationViewModel.setAuthenticationLinkSentButtonClicked(true)
                        authenticationViewModel.sendAuthenticationLinkToEmail(email)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isAuthenticationLinkSentButtonClicked) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = "이메일 링크 전송",
                        color = MaterialTheme.colorScheme.primary,
                        style = Typography.bodyMedium
                    )
                }
            }
        }
    }
}