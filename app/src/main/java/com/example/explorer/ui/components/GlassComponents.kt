package com.example.explorer.ui.components

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val NeonCyan = Color(0xFF00E5FF)
val NeonCyanSoft = Color(0xFF38BDF8)

/**
 * Fondo ambiental suave para que las tarjetas de cristal tengan profundidad real
 * tanto en Modo Claro (pastel luminoso) como en Modo Oscuro (aurora nocturna).
 */
@Composable
fun GlassAmbientBackground(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val baseGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF070B14),
                Color(0xFF0B1324),
                Color(0xFF09101D)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE7F2FF),
                Color(0xFFF3EEFF),
                Color(0xFFE6FAF6)
            )
        )
    }

    val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.blur(85.dp)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseGradient)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(blurModifier)
        ) {
            // Orbe superior izquierdo
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .offset(x = (-60).dp, y = (-40).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFF0284C7).copy(alpha = 0.28f), Color.Transparent)
                            } else {
                                listOf(Color(0xFF7DD3FC).copy(alpha = 0.55f), Color.Transparent)
                            }
                        )
                    )
            )

            // Orbe central derecho
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .offset(x = 160.dp, y = 180.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFF6366F1).copy(alpha = 0.24f), Color.Transparent)
                            } else {
                                listOf(Color(0xFFC4B5FD).copy(alpha = 0.50f), Color.Transparent)
                            }
                        )
                    )
            )

            // Orbe inferior
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = (-30).dp, y = 480.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFF0D9488).copy(alpha = 0.22f), Color.Transparent)
                            } else {
                                listOf(Color(0xFF99F6E4).copy(alpha = 0.52f), Color.Transparent)
                            }
                        )
                    )
            )
        }
    }
}

/**
 * Superficie estilo cristal esmerilado con brillo neón únicamente cuando está seleccionada o presionada.
 */
@Composable
fun GlassCard(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    shape: Shape = RoundedCornerShape(22.dp),
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val showNeon = isSelected || isPressed

    val borderColor by animateColorAsState(
        targetValue = when {
            showNeon -> NeonCyan
            isDark -> Color.White.copy(alpha = 0.16f)
            else -> Color.White.copy(alpha = 0.85f)
        },
        animationSpec = tween(durationMillis = 180),
        label = "glassBorder"
    )

    val borderWidth = if (showNeon) 1.8.dp else 1.dp

    val fillBrush = if (isDark) {
        Brush.verticalGradient(
            colors = if (showNeon) {
                listOf(
                    Color(0xFF192B42).copy(alpha = 0.78f),
                    Color(0xFF0F1A2B).copy(alpha = 0.65f)
                )
            } else {
                listOf(
                    Color(0xFF1C2538).copy(alpha = 0.70f),
                    Color(0xFF111827).copy(alpha = 0.55f)
                )
            }
        )
    } else {
        Brush.verticalGradient(
            colors = if (showNeon) {
                listOf(
                    Color.White.copy(alpha = 0.85f),
                    Color(0xFFE0F7FA).copy(alpha = 0.72f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.76f),
                    Color.White.copy(alpha = 0.52f)
                )
            }
        )
    }

    val shadowColor = when {
        showNeon -> NeonCyan
        isDark -> Color.Black.copy(alpha = 0.45f)
        else -> Color(0xFF64748B).copy(alpha = 0.18f)
    }

    val baseModifier = modifier
        .shadow(
            elevation = if (showNeon) 12.dp else elevation,
            shape = shape,
            ambientColor = shadowColor,
            spotColor = shadowColor
        )
        .clip(shape)
        .background(fillBrush)
        .border(BorderStroke(borderWidth, borderColor), shape)

    val clickableModifier = if (onClick != null) {
        baseModifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        baseModifier
    }

    Box(
        modifier = clickableModifier,
        content = content
    )
}
