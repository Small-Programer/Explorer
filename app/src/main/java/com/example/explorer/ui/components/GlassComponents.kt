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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
 * Fondo ambiental adaptable a teléfono y tablet (vertical u horizontal),
 * con orbes luminosos suaves para dar profundidad real al cristal.
 */
@Composable
fun GlassAmbientBackground(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val baseGradient = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF070C18),
                Color(0xFF0D172B),
                Color(0xFF08121E)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFE3F0FF),
                Color(0xFFF1EAFF),
                Color(0xFFE2F9F4)
            )
        )
    }

    val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.blur(95.dp)
    } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(baseGradient)
    ) {
        val w = maxWidth
        val h = maxHeight

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(blurModifier)
        ) {
            // Orbe superior izquierdo
            Box(
                modifier = Modifier
                    .size(w * 0.55f)
                    .align(Alignment.TopStart)
                    .offset(x = -(w * 0.12f), y = -(h * 0.12f))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFF0EA5E9).copy(alpha = 0.34f), Color.Transparent)
                            } else {
                                listOf(Color(0xFF7DD3FC).copy(alpha = 0.65f), Color.Transparent)
                            }
                        )
                    )
            )

            // Orbe derecho central
            Box(
                modifier = Modifier
                    .size(w * 0.55f)
                    .align(Alignment.CenterEnd)
                    .offset(x = w * 0.14f, y = -(h * 0.05f))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFF6366F1).copy(alpha = 0.30f), Color.Transparent)
                            } else {
                                listOf(Color(0xFFC4B5FD).copy(alpha = 0.60f), Color.Transparent)
                            }
                        )
                    )
            )

            // Orbe inferior izquierdo
            Box(
                modifier = Modifier
                    .size(w * 0.50f)
                    .align(Alignment.BottomStart)
                    .offset(x = -(w * 0.08f), y = h * 0.15f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFF14B8A6).copy(alpha = 0.26f), Color.Transparent)
                            } else {
                                listOf(Color(0xFF99F6E4).copy(alpha = 0.58f), Color.Transparent)
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
            isDark -> Color.White.copy(alpha = 0.20f)
            else -> Color.White.copy(alpha = 0.90f)
        },
        animationSpec = tween(durationMillis = 180),
        label = "glassBorder"
    )

    val borderWidth = if (showNeon) 2.dp else 1.dp

    val fillBrush = if (isDark) {
        Brush.verticalGradient(
            colors = if (showNeon) {
                listOf(
                    Color(0xFF1D3554).copy(alpha = 0.82f),
                    Color(0xFF0F1D32).copy(alpha = 0.68f)
                )
            } else {
                listOf(
                    Color(0xFF24314A).copy(alpha = 0.58f),
                    Color(0xFF141D2F).copy(alpha = 0.44f)
                )
            }
        )
    } else {
        Brush.verticalGradient(
            colors = if (showNeon) {
                listOf(
                    Color.White.copy(alpha = 0.88f),
                    Color(0xFFE0F7FA).copy(alpha = 0.74f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.78f),
                    Color.White.copy(alpha = 0.54f)
                )
            }
        )
    }

    val shadowColor = when {
        showNeon -> NeonCyan
        isDark -> Color.Black.copy(alpha = 0.45f)
        else -> Color(0xFF64748B).copy(alpha = 0.16f)
    }

    val baseModifier = modifier
        .shadow(
            elevation = if (showNeon) 14.dp else elevation,
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
