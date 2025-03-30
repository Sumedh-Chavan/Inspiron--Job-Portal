package com.example.csiapp_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import com.example.csiapp_2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkoutAppUI()
        }
    }
}

@Composable
fun WorkoutAppUI() {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2C5364), Color(0xFF203A43), Color(0xFF0F2027))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTitle()
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar()
        Spacer(modifier = Modifier.height(16.dp))
        ChoiceButtons(scrollState,coroutineScope)
        Spacer(modifier = Modifier.height(16.dp))
        WorkoutHeading()
        Spacer(modifier = Modifier.height(16.dp))
        WorkoutGrid()
        Spacer(modifier = Modifier.height(24.dp))
        DietPlanHeading()
        Spacer(modifier = Modifier.height(16.dp))
        DietPlanGrid()
        Spacer(modifier = Modifier.height(24.dp))
        YogaHeading()
        Spacer(modifier = Modifier.height(16.dp))
        YogaGrid()
    }
}

@Composable
fun AppTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC))))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Kotlin Fitness Hub",
            color = Color.White,
            fontSize = 28.sp
        )
    }
}

@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    BasicTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(16.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, color = Color.White)
    )
}

@Composable
fun ChoiceButtons(scrollState: androidx.compose.foundation.ScrollState, coroutineScope: CoroutineScope) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Workout" to 600, "Diet Plan" to 1600, "Yoga" to 2600).forEach { (label, scrollTo) ->
            Button(
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(scrollTo)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC))))
                    .padding(8.dp)
            ) {
                Text(label, color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun WorkoutHeading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC))))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Workouts",
            color = Color.White,
            fontSize = 24.sp
        )
    }
}

@Composable
fun WorkoutGrid() {
    val workouts = listOf(
        Pair("Legs", R.drawable.leg),
        Pair("Shoulders", R.drawable.shoulder),
        Pair("Arms", R.drawable.arms),
        Pair("Back", R.drawable.back),
        Pair("Chest", R.drawable.chest),
        Pair("Abs", R.drawable.abs)
    )

    Column {
        for (i in workouts.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WorkoutCard(workouts[i].first, workouts[i].second, Modifier.weight(1f))
                if (i + 1 < workouts.size) {
                    WorkoutCard(workouts[i + 1].first, workouts[i + 1].second, Modifier.weight(1f))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DietPlanHeading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC))))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Diet Plans",
            color = Color.White,
            fontSize = 24.sp
        )
    }
}

@Composable
fun DietPlanGrid() {
    val dietPlans = listOf(
        Pair("Keto", R.drawable.keto),
        Pair("Vegan", R.drawable.vegan),
        Pair("Paleo", R.drawable.paleo),
        Pair("Mediterranean", R.drawable.mediterranean),
        Pair("Low Carb", R.drawable.lowcarb),
        Pair("High Protein", R.drawable.highprotein)
    )

    Column {
        for (i in dietPlans.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WorkoutCard(dietPlans[i].first, dietPlans[i].second, Modifier.weight(1f))
                if (i + 1 < dietPlans.size) {
                    WorkoutCard(dietPlans[i + 1].first, dietPlans[i + 1].second, Modifier.weight(1f))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)
@Composable
fun WorkoutCard(title: String, imageRes: Int, modifier: Modifier) {
    var isHovered by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isHovered) 0.2f else 0.5f,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Card(
        modifier = modifier
            .height(160.dp)
            .padding(8.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        isHovered = event.type == PointerEventType.Enter
                    }
                }
            }
            .pointerHoverIcon(PointerIconDefaults.Hand),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)))
            )
            Text(
                text = title,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 22.sp
            )
            Button(
                onClick = {},
                modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
            ) {
                Text("Start")
            }
        }
    }
}

@Composable
fun YogaHeading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC))))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Yoga",
            color = Color.White,
            fontSize = 24.sp
        )
    }
}

@Composable
fun YogaGrid() {
    val yogaExercises = listOf(
        Pair("Hatha", R.drawable.hatha),
        Pair("Vinyasa", R.drawable.vinyasa),
        Pair("Ashtanga", R.drawable.ashtanga),
        Pair("Iyengar", R.drawable.iyengar),
        Pair("Kundalini", R.drawable.kundalini),
        Pair("Bikram", R.drawable.bikram)
    )

    Column {
        for (i in yogaExercises.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WorkoutCard(yogaExercises[i].first, yogaExercises[i].second, Modifier.weight(1f))
                if (i + 1 < yogaExercises.size) {
                    WorkoutCard(yogaExercises[i + 1].first, yogaExercises[i + 1].second, Modifier.weight(1f))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}