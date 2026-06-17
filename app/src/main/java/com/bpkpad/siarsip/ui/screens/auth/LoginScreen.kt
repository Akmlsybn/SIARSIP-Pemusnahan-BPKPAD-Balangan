package com.bpkpad.siarsip.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: (username: String, password: String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {}
) {
    var username   by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var pwVisible  by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {

        // ── Top Bar ──────────────────────────────────────────
        LoginTopBar()

        // ── Konten utama ─────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        GreenPrimary.copy(alpha = 0.1f),
                        RoundedCornerShape(48.dp)
                    )
                    .border(
                        2.dp,
                        GreenPrimary.copy(alpha = 0.3f),
                        RoundedCornerShape(48.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏛️", fontSize = 40.sp)
            }

            Spacer(Modifier.height(20.dp))

            // Judul
            Text(
                text = "Sistem Pengarsipan\nBPKPAD Balangan",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextHead,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )

            Spacer(Modifier.height(36.dp))

            // Username field
            LoginField(
                label = "Username",
                value = username,
                onValueChange = { username = it },
                placeholder = "Masukkan username"
            )

            Spacer(Modifier.height(14.dp))

            // Password field
            LoginField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "Masukkan password",
                isPassword = true,
                passwordVisible = pwVisible,
                onTogglePassword = { pwVisible = !pwVisible }
            )

            Spacer(Modifier.height(12.dp))

            // Remember me + Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor   = GreenPrimary,
                        uncheckedColor = TextHint,
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "Remember me",
                    fontSize = 13.sp,
                    color = TextBody,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Forgot Password ?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = GreenPrimary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onForgotPasswordClick() }
                )
            }

            Spacer(Modifier.height(24.dp))

            // Tombol Login
            Button(
                onClick = { onLoginClick(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = Color.White
                )
            ) {
                Text(
                    text = "Log In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────────────────────
@Composable
private fun LoginTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(GreenPrimary, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("B", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = "BPKPAD Balangan",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )
    }
    HorizontalDivider(color = BorderGray, thickness = 0.5.dp)
}

// ─────────────────────────────────────────────────────────────
//  Reusable TextField
// ─────────────────────────────────────────────────────────────
@Composable
private fun LoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextBody,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, color = TextHint, fontSize = 14.sp)
            },
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword && onTogglePassword != null) {
                {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = TextHint
                        )
                    }
                }
            } else null,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = GreenPrimary,
                unfocusedBorderColor    = BorderGray,
                focusedContainerColor   = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedTextColor        = TextHead,
                unfocusedTextColor      = TextHead,
                cursorColor             = GreenPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}