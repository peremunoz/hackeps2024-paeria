package hackeps.aparcaya


import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import hackeps.aparcaya.core.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController, mainViewModel: MainViewModel) {
    val auth: FirebaseAuth = Firebase.auth
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var passwordVisible1 by rememberSaveable { mutableStateOf(false) }
    var wifiOnly by remember {
        mutableStateOf(false)
    }
    var isConnectedToWifi by remember {
        mutableStateOf(false)
    }
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(context.getString(R.string.web_client_id))
        .requestProfile()
        .build()

    val googleSignInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (wifiOnly && !isConnectedToWifi) {
                Toast.makeText(context, "Please connect to Wi-Fi to login", Toast.LENGTH_SHORT)
                    .show()
                return@rememberLauncherForActivityResult
            }
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign Up was successful, authenticate with Firebase
                    task.result?.idToken?.let { idToken ->
                        val credential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "signInWithCredential:success")

                                    val user = auth.currentUser!!
                                    if (user.displayName == null) {
                                        val profileUpdates = userProfileChangeRequest {
                                            displayName = user.email!!.substringBefore('@')
                                        }
                                        user.updateProfile(profileUpdates).addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Welcome ${user.displayName}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigate(NavigationRoutes.HOME)
                                        }
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    Toast.makeText(
                                        context,
                                        "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                    Toast.makeText(
                        context,
                        "Google sign in failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Tempusky Logo"
                    )
                    Spacer(modifier = Modifier.fillMaxSize(0.1f))
                    Text(text = "TEMPUSKY", fontSize = 40.sp, fontWeight = FontWeight.Black)
                }
            }

            Column(
                modifier = Modifier
                    .weight(2.5f)
                    .fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 10.dp),
                    text = "Signup with your credentials:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        selectionColors = LocalTextSelectionColors.current,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    value = email,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "emailIcon"
                        )
                    },
                    onValueChange = {
                        email = it
                        isButtonEnabled =
                            email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
                    },
                    label = { Text("Email address") },
                    placeholder = { Text(text = "Enter your e-mail") },
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
                )
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        selectionColors = LocalTextSelectionColors.current,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    value = userName,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "emailIcon"
                        )
                    },
                    onValueChange = {
                        userName = it
                        isButtonEnabled =
                            email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
                    },
                    label = { Text("Username") },
                    placeholder = { Text(text = "Enter your Username") },
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
                )
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        selectionColors = LocalTextSelectionColors.current,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    visualTransformation = if (passwordVisible1) VisualTransformation.None else PasswordVisualTransformation(),
                    value = password,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "passwordIcon"
                        )
                    },
                    onValueChange = {
                        password = it
                        isButtonEnabled =
                            email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
                    },
                    label = { Text(text = "Password") },
                    placeholder = { Text(text = "Enter your password") },
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
                )
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        selectionColors = LocalTextSelectionColors.current,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    value = confirmPassword,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "passwordIcon"
                        )
                    },
                    onValueChange = {
                        confirmPassword = it
                        isButtonEnabled =
                            email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
                    },
                    label = { Text(text = "Confirm Password") },
                    placeholder = { Text("Confirm your password") },
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate(NavigationRoutes.LOGIN) },
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                containerColor = Color.Transparent
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = "LOGIN", fontSize = 20.sp)
                        }

                        Button(
                            onClick = {
                                if (wifiOnly && !isConnectedToWifi) {
                                    Toast.makeText(
                                        context,
                                        "Please connect to Wi-Fi to login",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { registerTask ->
                                            if (registerTask.isSuccessful) {
                                                // Sign up success, save user username and navigate to home screen
                                                val user = auth.currentUser

                                                val profileUpdates = userProfileChangeRequest {
                                                    displayName = userName
                                                }

                                                user!!.updateProfile(profileUpdates)
                                                    .addOnCompleteListener { updateTask ->
                                                        if (updateTask.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Welcome ${user.displayName}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            navController.navigate(NavigationRoutes.HOME)
                                                            Log.d(TAG, "User profile updated.")
                                                        } else {
                                                            Log.d(
                                                                TAG,
                                                                "User profile update failed."
                                                            )
                                                            Toast.makeText(
                                                                context,
                                                                "User profile update failed.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                            } else {
                                                // If sign up fails, display a message to the user.
                                                Toast.makeText(
                                                    context,
                                                    "Authentication failed, try again later.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }

                            },
                            enabled = isButtonEnabled,
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                        ) {
                            Text(text = "SIGN UP", fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

private const val TAG = "SignupScreen"