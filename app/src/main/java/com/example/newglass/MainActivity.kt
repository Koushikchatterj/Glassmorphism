package com.example.newglass

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.newglass.ui.theme.NewGlassTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewGlassTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val hazeState = remember { HazeState() }
    val clicked = remember { mutableStateOf(false) }
    val offset_X_CD: Dp by animateDpAsState(
        targetValue = if (clicked.value) 90.dp else 0.dp,
        animationSpec = tween(durationMillis = 1000), // Slower shift animation (2 seconds)
        label = "CD Shift"
    )
    // No movement on card for clicks
    val offset_X_Card: Dp = 0.dp

    var rotationAngle by remember { mutableStateOf(0f) }
    var isPositiveRotation by remember { mutableStateOf(true) } // State to toggle rotation direction

    // Animate the rotation angle
    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 1500), // Adjust for slow, smooth animation
        label = "CD Rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // CD Box
        Box(
            modifier = Modifier
                .offset(x = offset_X_CD)
                .size(175.dp),

            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.layer1),
                contentDescription = "CD",
                modifier = Modifier
                    .haze(
                        hazeState,
                        backgroundColor = MaterialTheme.colorScheme.background,
                        tint = Color.Black.copy(alpha = .2f),
                        blurRadius = 30.dp,
                    )
                    .graphicsLayer {
                        // Apply rotation animation when clicked
                        rotationZ = animatedRotation
                    },
            )
        }

        // GlassCard for Music Logo
        GlassCard(
            hazeState = hazeState,
            clicked = clicked,
            offsetX = offset_X_Card, // Static, no offset movement on click
            onCardClick = {
                clicked.value = !clicked.value
                // Toggle between 360 and -360 degrees on each click
                rotationAngle += if (isPositiveRotation) 360f else -360f
                isPositiveRotation = !isPositiveRotation // Toggle rotation direction
            }
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun GlassCard(hazeState: HazeState, clicked: MutableState<Boolean>, offsetX: Dp, onCardClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(175.dp)
            .clickable {
                onCardClick() // Trigger the CD rotation when the card is clicked
            }
            // No offset applied, card doesn't move on click
            .hazeChild(state = hazeState, shape = RoundedCornerShape(12.dp))
            .border(
                width = Dp.Hairline,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = .8f),
                        Color.White.copy(alpha = .2f),
                    ),
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.music_logo),
                contentDescription = "Music",
                modifier = Modifier.size(165.dp)
            )
        }
    }
}

@Preview
@Composable
private fun show () {
    App()
}