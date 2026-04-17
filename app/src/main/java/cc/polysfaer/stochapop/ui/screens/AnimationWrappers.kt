package cc.polysfaer.stochapop.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import cc.polysfaer.stochapop.ui.theme.SpaceCrabbyRed
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@Composable
fun ElasticSlideItem(
    startingOffset: Float = 17f,
    content: @Composable () -> Unit
) {
    val animatableOffset = remember { Animatable(startingOffset) }

    LaunchedEffect(Unit) {
        animatableOffset.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(modifier = Modifier.graphicsLayer(translationY = animatableOffset.value)) {
        content()
    }
}

@Composable
fun ElasticScrollWrapper(
    maxScroll: Float = 55f,
    scaleFactor: Float = 4e-4f,
    content: @Composable (Modifier) -> Unit
) {
    val overscrollOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return if (overscrollOffset.value > 0f && available.y < 0f) {
                    val consumed = available.y.coerceAtLeast(-overscrollOffset.value)
                    scope.launch { overscrollOffset.snapTo(overscrollOffset.value + consumed) }
                    Offset(0f, consumed)
                } else Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y > 0f && source == NestedScrollSource.UserInput) {
                    scope.launch {
                        val logResistance = 0.5f * (1f - (overscrollOffset.value / maxScroll))
                        overscrollOffset.snapTo(overscrollOffset.value + (available.y * logResistance))
                    }
                    return available
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                scope.launch {
                    overscrollOffset.animateTo(
                        targetValue = 0f,
                        initialVelocity = available.y.coerceIn(0f, maxScroll),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    Box(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .graphicsLayer {
                translationY = overscrollOffset.value
                val scale = 1f + (overscrollOffset.value * scaleFactor)
                scaleX = scale
                scaleY = scale
            }
    ) {
        content(Modifier)
    }
}

@Composable
fun SwipeBox(
    modifier: Modifier = Modifier,
    onSwipLeft: (() -> Unit)? = null,
    onSwipRight: (() -> Unit)? = null,
    content: @Composable (ColumnScope.(Modifier) -> Unit)
) {
    val validationOffset = 0.25f
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        positionalThreshold = { validationOffset * it },
    )
    val scope = rememberCoroutineScope()

    var maxWidthPx by remember { mutableFloatStateOf(1f) }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromEndToStart = onSwipLeft != null,
        enableDismissFromStartToEnd = onSwipRight != null,
        backgroundContent = {
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> {
                    val offset = dismissState.requireOffset()
                    val colorProgress by remember(offset, maxWidthPx) {
                        derivedStateOf {
                            (offset.absoluteValue / maxWidthPx).coerceIn(0f, 1f)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = colorProgress }
                            .background(SpaceCrabbyRed) // lerp(PastelRed, ErrorRed, colorProgress))
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete icon",
                            tint = Color.White
                        )
                    }
                }

                SwipeToDismissBoxValue.StartToEnd -> {}

                SwipeToDismissBoxValue.Settled -> {}
            }
        },
        onDismiss = { direction ->
            when (direction) {
                SwipeToDismissBoxValue.EndToStart -> {
                    scope.launch {
                        if (onSwipLeft != null) onSwipLeft()
                    }
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    scope.launch {
                        if (onSwipRight != null) onSwipRight()
                        dismissState.reset()
                    }
                }
                SwipeToDismissBoxValue.Settled -> {}
            }
        },
        modifier = modifier.onGloballyPositioned { coordinates ->
            maxWidthPx = coordinates.size.width.toFloat()
        }
    ) {
        Column { content(Modifier) }
    }
}
