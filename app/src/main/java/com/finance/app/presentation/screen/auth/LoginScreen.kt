package com.finance.app.presentation.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.finance.app.presentation.theme.TitaniumColors
import com.finance.app.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = remember { AuthViewModel() }
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        loginState?.let { state ->
            if (state is com.finance.app.util.Resource.Success) {
                onLoginSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo 区域 - Titanium 风格
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = TitaniumColors.SurfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(1.dp, TitaniumColors.Border, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "¥",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = TitaniumColors.Positive
            )
        }
        Text(
            text = "记账助手",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = TitaniumColors.TextPrimary,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名", color = TitaniumColors.TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitaniumColors.BorderVariant,
                unfocusedBorderColor = TitaniumColors.Border,
                focusedLabelColor = TitaniumColors.TextMuted,
                unfocusedLabelColor = TitaniumColors.TextMuted,
                cursorColor = TitaniumColors.Positive,
                focusedTextColor = TitaniumColors.TextPrimary,
                unfocusedTextColor = TitaniumColors.TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码", color = TitaniumColors.TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitaniumColors.BorderVariant,
                unfocusedBorderColor = TitaniumColors.Border,
                focusedLabelColor = TitaniumColors.TextMuted,
                unfocusedLabelColor = TitaniumColors.TextMuted,
                cursorColor = TitaniumColors.Positive,
                focusedTextColor = TitaniumColors.TextPrimary,
                unfocusedTextColor = TitaniumColors.TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = { viewModel.login(username, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = username.isNotBlank() && password.isNotBlank() && loginState !is com.finance.app.util.Resource.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = TitaniumColors.Positive,
                contentColor = TitaniumColors.Background
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (loginState is com.finance.app.util.Resource.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = TitaniumColors.Background
                )
            } else {
                Text("登录", fontWeight = FontWeight.Medium)
            }
        }

        TextButton(onClick = onNavigateToRegister) {
            Text(
                "注册",
                color = TitaniumColors.TextMuted
            )
        }

        loginState?.let { state ->
            if (state is com.finance.app.util.Resource.Error) {
                Text(
                    text = state.message ?: "登录失败",
                    color = TitaniumColors.Error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
