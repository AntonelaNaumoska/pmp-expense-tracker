package com.example.expensetracker.feature.auth

import android.app.Activity
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.credentials.CustomCredential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.expensetracker.R
import com.example.expensetracker.data.repository.Resource
import com.example.expensetracker.feature.auth.components.FacebookSignInButton
import com.example.expensetracker.feature.auth.components.GoogleSignInButton
import com.example.expensetracker.localization.LocaleManager
import com.example.expensetracker.ui.theme.Zinc
import com.example.expensetracker.widget.ExpenseTextView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import java.security.SecureRandom

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val lang = LocaleManager.currentLanguage.value

    val activity = context as? Activity
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }
    val isTabletOrLandscape = windowSizeClass?.widthSizeClass != WindowWidthSizeClass.Compact

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val credentialManager = remember { CredentialManager.create(context) }

    val resId = remember(context) {
        context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    }
    val webClientId = if (resId != 0) stringResource(id = resId) else ""

    fun generateSecureRandomNonce(): String {
        val rawNonce = ByteArray(16)
        SecureRandom().nextBytes(rawNonce)
        return Base64.encodeToString(rawNonce, Base64.NO_WRAP or Base64.URL_SAFE)
    }

    val callbackManager = remember { CallbackManager.Factory.create() }
    val facebookAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callbackManager.onActivityResult(Activity.RESULT_OK, result.resultCode, result.data)
    }

    DisposableEffect(Unit) {
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                viewModel.loginWithSocialCredential(credential)
            }
            override fun onCancel() {}
            override fun onError(error: FacebookException) {
                Toast.makeText(context, "Facebook Login Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
        onDispose { LoginManager.getInstance().unregisterCallback(callbackManager) }
    }

    LaunchedEffect(key1 = authState) {
        when (authState) {
            is Resource.Success -> {
                viewModel.clearState()
                onNavigateToHome()
            }
            is Resource.Failure -> {
                val errorMsg = (authState as Resource.Failure).exception.localizedMessage ?: "Auth Failed"
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 16.dp, end = 24.dp)
                    .size(40.dp)
                    .background(Zinc.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    .clickable {
                        val newLang = if (lang == "en") "mk" else "en"
                        LocaleManager.setLanguage(context, newLang)
                        (context as Activity).recreate()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lang.uppercase(),
                    color = Zinc,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            if (authState is Resource.Loading) {
                CircularProgressIndicator(color = Zinc)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(if (isTabletOrLandscape) 0.6f else 1f)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExpenseTextView(text = stringResource(id = R.string.welcome_back), fontSize = 28.sp)

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

                Button(
                    onClick = { viewModel.loginUser(email, password) },
                    colors = ButtonDefaults.buttonColors(containerColor = Zinc),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = email.isNotBlank() && password.isNotBlank()
                ) {
                    ExpenseTextView(text = stringResource(id = R.string.login_btn), color = Color.White)
                }

                GoogleSignInButton(
                    onClick = {
                        if (webClientId.isBlank()) {
                            Toast.makeText(context, "Google Services identity file processing... Rebuild project.", Toast.LENGTH_LONG).show()
                            return@GoogleSignInButton
                        }

                        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                            serverClientId = webClientId
                        )
                            .setNonce(generateSecureRandomNonce())
                            .build()

                        val credentialRequest = GetCredentialRequest.Builder()
                            .addCredentialOption(signInWithGoogleOption)
                            .build()

                        coroutineScope.launch {
                            try {
                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = credentialRequest
                                )

                                val credential = result.credential
                                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                                    viewModel.loginWithSocialCredential(firebaseCredential)
                                }
                            } catch (e: GetCredentialException) {
                                e.printStackTrace()
                                Toast.makeText(context, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                FacebookSignInButton(
                    onClick = {
                        val registryOwner = context as? ComponentActivity

                        if (registryOwner != null) {
                            LoginManager.getInstance().logInWithReadPermissions(
                                registryOwner,
                                callbackManager,
                                listOf("email", "public_profile")
                            )
                        } else {
                            Toast.makeText(context, "Context is not a ComponentActivity", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExpenseTextView(
                    text = stringResource(id = R.string.login_anonymous),
                    color = Zinc,
                    modifier = Modifier
                        .clickable { viewModel.loginAnonymously() }
                        .padding(8.dp)
                )

                ExpenseTextView(
                    text = stringResource(id = R.string.need_account_prompt),
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}