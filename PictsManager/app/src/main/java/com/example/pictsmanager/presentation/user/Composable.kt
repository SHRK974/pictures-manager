package com.example.pictsmanager.presentation.user

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pictsmanager.presentation.album.AlbumViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoginView(
    modifier: Modifier,
    userViewModel: UserViewModel,
    context: Context
)  {
    val focusManager = LocalFocusManager.current
    var email by rememberSaveable { mutableStateOf("baptiste2@mail.fr") }
    var password by rememberSaveable { mutableStateOf("Password974") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .clickable { focusManager.clearFocus() }
    ) {
        if (!userViewModel.state.isLoggedIn) {
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            LoginFields(
                email,
                password,
                context = context,
                onLoginClick = { email, password ->
                    userViewModel.login(email, password)
                },
                onEmailChange = { email = it },
                onPasswordChange = { password = it }
            )

            if (userViewModel.state.error?.isNotEmpty() == true) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        "Login error: ${userViewModel.state.error}",
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
            }

            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Composable
fun LoginFields(
    email: String,
    password: String,
    context: Context,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: (email: String, password:String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Please login", color = Color.White)

        OutlinedTextField(
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
            value = email,
            placeholder = { Text(text = "user@email.com") },
            label = { Text(text = "email", color = Color.White) },
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        OutlinedTextField(
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
            value = password,
            placeholder = { Text(text = "password") },
            label = { Text(text = "password", color = Color.White) },
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    onLoginClick(email, password)
                    focusManager.clearFocus()
                } else {
                    Toast.makeText(
                        context,
                        "Please enter an email and password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            Text("Login")
        }
    }
}