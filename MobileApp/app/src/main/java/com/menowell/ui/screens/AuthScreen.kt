package com.menowell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menowell.ui.components.MenoPrimaryButton
import com.menowell.ui.components.menoOutlinedTextFieldColors
import com.menowell.ui.theme.BlushPink
import com.menowell.ui.theme.Cream
import com.menowell.ui.theme.DustyRose
import com.menowell.ui.theme.SoftWhite
import com.menowell.ui.theme.TextMid
import com.menowell.ui.theme.TextSoft
import com.menowell.viewmodel.AuthUiState
import com.menowell.viewmodel.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel, onSuccess: () -> Unit) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        if (state is AuthUiState.Success) onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.34f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DustyRose, BlushPink)
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "MenoWell",
                    style = MaterialTheme.typography.displayLarge,
                    color = SoftWhite,
                )
                Text(
                    text = "Your companion through the change.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftWhite.copy(alpha = 0.8f),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.66f)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            AuthTabRow(
                isLogin = isLogin,
                onSelectLogin = {
                    isLogin = true
                    localError = null
                    viewModel.resetState()
                },
                onSelectRegister = {
                    isLogin = false
                    localError = null
                    viewModel.resetState()
                },
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email", color = TextMid) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = menoOutlinedTextFieldColors(),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
            )
            PasswordField(
                value = password,
                onValueChange = { password = it },
                visible = passwordVisible,
                onToggle = { passwordVisible = !passwordVisible },
                label = "Password",
            )
            if (!isLogin) {
                PasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    visible = confirmVisible,
                    onToggle = { confirmVisible = !confirmVisible },
                    label = "Confirm password",
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name", color = TextMid) },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = menoOutlinedTextFieldColors(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    singleLine = true,
                )
            }

            MenoPrimaryButton(
                text = if (isLogin) "Sign in" else "Create account",
                onClick = {
                    localError = null
                    if (!isLogin && confirmPassword != password) {
                        localError = "Passwords do not match."
                    } else if (isLogin) {
                        viewModel.login(email.trim(), password)
                    } else {
                        viewModel.register(email.trim(), password, fullName.ifBlank { null })
                    }
                },
            )

            val remoteError = (state as? AuthUiState.Error)?.message
            val visibleError = localError ?: remoteError
            if (!visibleError.isNullOrBlank()) {
                Text(
                    text = visibleError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (state is AuthUiState.Loading) {
                CircularProgressIndicator(color = DustyRose)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    isLogin = !isLogin
                    localError = null
                    viewModel.resetState()
                }
            ) {
                Text(
                    text = if (isLogin) "Need an account? Register" else "Already have an account? Sign in",
                    color = DustyRose,
                )
            }
        }
    }
}

@Composable
private fun AuthTabRow(
    isLogin: Boolean,
    onSelectLogin: () -> Unit,
    onSelectRegister: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        AuthTab(
            title = "Sign in",
            selected = isLogin,
            onClick = onSelectLogin,
            modifier = Modifier.weight(1f),
        )
        AuthTab(
            title = "Create account",
            selected = !isLogin,
            onClick = onSelectRegister,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AuthTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = if (selected) DustyRose else TextSoft,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(if (selected) DustyRose else Color.Transparent)
        )
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggle: () -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = TextMid) },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = menoOutlinedTextFieldColors(),
        textStyle = MaterialTheme.typography.bodyLarge,
        visualTransformation = if (visible) VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Hide password" else "Show password",
                    tint = TextSoft,
                )
            }
        },
    )
}
