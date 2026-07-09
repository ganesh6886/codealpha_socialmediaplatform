package com.example.project.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.project.ui.theme.ElectricPurple
import com.example.project.ui.theme.CyberCyan
import com.example.project.ui.theme.HotPink

@Composable
fun AppLogo(size: Dp = 100.dp) {
    val gradient = Brush.linearGradient(
        colors = listOf(ElectricPurple, CyberCyan, HotPink)
    )

    Canvas(modifier = Modifier.size(size)) {
        val width = size.toPx()
        val height = size.toPx()
        val strokeWidth = width * 0.12f

        val path = Path().apply {
            val centerX = width / 2
            val centerY = height / 2
            val radius = (width - strokeWidth) / 4

            moveTo(centerX, centerY)
            cubicTo(
                centerX - radius * 2, centerY + radius * 2,
                centerX - radius * 2, centerY - radius * 2,
                centerX, centerY
            )
            cubicTo(
                centerX + radius * 2, centerY + radius * 2,
                centerX + radius * 2, centerY - radius * 2,
                centerX, centerY
            )
        }

        drawPath(
            path = path,
            brush = gradient,
            style = Stroke(width = strokeWidth)
        )
    }
}
