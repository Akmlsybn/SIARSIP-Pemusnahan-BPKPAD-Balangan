package com.example.arsipbpkpad.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
fun RetentionStatusBadge(isExpired: Boolean) {
    val backgroundColor = if (isExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val textColor = if (isExpired) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
    val text = if (isExpired) "EXPIRED" else "AKTIF"
    StatusBadge(text = text, backgroundColor = backgroundColor, textColor = textColor)
}

@Composable
fun ConditionBadge(condition: String) {
    val (backgroundColor, textColor, text) = when (condition.uppercase()) {
        "GOOD" -> Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, "BAIK")
        "DAMAGED" -> Triple(Color(0xFFFFA000), Color.Black, "RUSAK") // Amber
        "LOST" -> Triple(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError, "HILANG")
        else -> Triple(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary, condition)
    }
    StatusBadge(text = text, backgroundColor = backgroundColor, textColor = textColor)
}

@Composable
fun DocStatusBadge(status: String) {
    val (backgroundColor, textColor, text) = when (status.uppercase()) {
        "AVAILABLE" -> Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, "TERSEDIA")
        "BORROWED" -> Triple(Color(0xFF1976D2), Color.White, "DIPINJAM") // Blue
        "DISPOSED" -> Triple(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), MaterialTheme.colorScheme.surface, "DIMUSNAHKAN") 
        "UNVERIFIED" -> Triple(Color(0xFFFFA000), Color.Black, "BELUM VERIFIKASI") // Amber
        else -> Triple(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary, status)
    }
    StatusBadge(text = text, backgroundColor = backgroundColor, textColor = textColor)
}
