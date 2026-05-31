package com.example.expensetracker.feature.auth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.expensetracker.R
import com.example.expensetracker.data.repository.Resource
import com.example.expensetracker.ui.theme.Zinc
import com.example.expensetracker.widget.ExpenseTextView

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBackToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    val activity = context as? Activity
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }
    val isTabletOrLandscape = windowSizeClass?.widthSizeClass != WindowWidthSizeClass.Compact

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordMatchError = stringResource(id = R.string.error_password_match)

    LaunchedEffect(key1 = authState) {
        when (authState) {
            is Resource.Success -> {
                viewModel.clearState()
                onNavigateToHome()
            }
            is Resource.Failure -> {
                val errorMsg = (authState as Resource.Failure).exception.localizedMessage ?: "Registration Failed"
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.clearState()
            }
            else -> {}
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (authState is Resource.Loading) {
                CircularProgressIndicator(color = Zinc)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(if (isTabletOrLandscape) 0.6f else 1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExpenseTextView(text = stringResource(id = R.string.create_account), fontSize = 28.sp)

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.email_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(id = R.string.password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(id = R.string.confirm_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            viewModel.signUpUser(email, password)
                        } else {
                            Toast.makeText(context, passwordMatchError, Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Zinc),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
                ) {
                    ExpenseTextView(text = stringResource(id = R.string.register_btn), color = Color.White)
                }

                ExpenseTextView(
                    text = stringResource(id = R.string.have_account_prompt),
                    modifier = Modifier.clickable { onNavigateBackToLogin() }
                )
            }
        }
    }
}