package hackeps.aparcaya

import android.content.Intent
import android.health.connect.datatypes.units.Percentage
import android.net.Uri
import android.util.Log
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round
import kotlin.math.roundToInt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.DefaultSettingsProvider
import com.mapbox.maps.extension.compose.DefaultSettingsProvider.createDefault2DPuck
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import hackeps.aparcaya.core.MainViewModel
import hackeps.aparcaya.util.ApiResponse
import hackeps.aparcaya.util.Colors
import hackeps.aparcaya.util.Parking
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(context: MainActivity,
               mainViewModel: MainViewModel,
               navController: NavController) {
    val lightThemeMap = "mapbox://styles/mapbox/outdoors-v12"
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var resultsParking by remember { mutableStateOf(listOf<Parking>()) }
    var selectedParking by remember { mutableStateOf<Parking?>(null) }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var subscribedParkings by remember { mutableStateOf(listOf<String>()) }
    LaunchedEffect(Unit) {
        if(user != null) {
            val subscribeParkingUseCase = GetSubscribedParkingsUseCase()
            mainViewModel.setSubscribedParkings(subscribeParkingUseCase.invoke(user.uid))
        }
    }
    mainViewModel.subscribedParkings.observe(context) {
        subscribedParkings = it
    }
    mainViewModel.parkings.observe(context) {
        Log.d("HomeScreen", "Parkings: $it")
        resultsParking = it
    }
    mainViewModel.selectedParking.observe(context) {
        selectedParking = it
    }
    mainViewModel.showBottomSheet.observe(context) {
        showBottomSheet = it
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .size(70.dp)
            .zIndex(1f)
            .align(Alignment.TopEnd), contentAlignment = Alignment.Center){
            if(user == null) {
                IconButton(onClick = { navController.navigate(NavigationRoutes.LOGIN) }, modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, RoundedCornerShape(15.dp))
                    .align(Alignment.Center)) {
                    Icon(
                        Icons.Default.Person,
                        "person"
                        , tint = Color.Black,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                    )
                }
            } else {
                IconButton(onClick = { auth.signOut(); navController.navigate(NavigationRoutes.HOME) }, modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, RoundedCornerShape(15.dp))
                    .align(Alignment.Center)) {
                    Icon(
                        Icons.Default.ExitToApp,
                        "person"
                        , tint = Color.Red,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                    )
                }
            }
        }
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapInitOptionsFactory = { context ->
                MapInitOptions(
                    context = context,
                    styleUri = lightThemeMap,
                )
            },
            mapViewportState = MapViewportState().apply {
                setCameraOptions {
                    zoom(11.0)
                    center(Point.fromLngLat(0.6206, 41.6148))
                    pitch(0.0)
                    bearing(0.0)
                }
                style {
                    Style.DARK
                }
            },
            locationComponentSettings = DefaultSettingsProvider.defaultLocationComponentSettings(
                context = LocalContext.current
            ).toBuilder()
                .setLocationPuck(createDefault2DPuck(withBearing = false))
                .setEnabled(true)
                .build()
        ) {
            resultsParking.forEach { parking ->
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(
                            Point.fromLngLat(
                                parking.longitude.toDouble(),
                                parking.latitude.toDouble()
                            )
                        )
                        allowOverlap(true)
                        allowOverlapWithPuck(true)
                    }
                ) {
                    MapDataObject(
                        parking,
                        mainViewModel
                    )
                }
            }
        }
        if (showBottomSheet) {
            val totalCapacity = selectedParking?.total_capacity?.toDouble() ?: 1.0
            val occupiedPlaces = selectedParking?.occupied_places?.toDouble() ?: 0.0
            val percentFree = (1.0 - occupiedPlaces / totalCapacity).coerceIn(0.0, 1.0)
            val freePlaces = selectedParking?.total_capacity?.minus(selectedParking?.occupied_places ?: 0) ?: 0
            val user = FirebaseAuth.getInstance().currentUser
            val subsribeParking = SubscribeParkingUseCase()
            val predictResult = runBlocking {
                PredictUseCase().invoke("2024-11-24 20:00:00", "2024-11-27 20:00:00")
            }
            Log.d("HomeScreen", "Predict: $predictResult")
            fun subscribeParking() {
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                            return@addOnCompleteListener
                        }
                        val token = task.result
                        Log.d("FCM", "Token: $token")
                        mainViewModel.viewModelScope.launch {
                            if (!subscribedParkings.contains(selectedParking?.id)) {
                                subsribeParking.invoke(
                                    parkingId = selectedParking?.id ?: "",
                                    userId = user?.uid ?: "",
                                    notificationId = token
                                )
                                val subscribeParkingUseCase = GetSubscribedParkingsUseCase()
                                mainViewModel.setSubscribedParkings(
                                    subscribeParkingUseCase.invoke(
                                        user?.uid ?: ""
                                    )
                                )
                            }
                        }
                    }
            }
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth() ) {
                        Text(text = "Parking ${selectedParking?.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        if(user != null) {
                            Box(modifier = Modifier
                                .clickable { subscribeParking() }
                                .background(
                                    if (!subscribedParkings.contains(selectedParking?.id)) Color.Gray else Color.LightGray,
                                    RoundedCornerShape(10.dp)
                                )
                                .width(130.dp)
                                .height(40.dp),
                                contentAlignment = Alignment.Center) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
                                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "icon notifications", modifier = Modifier.size(30.dp), tint = Color.White)
                                    Text(text = "Subscriure", fontSize = 15.sp, fontWeight = FontWeight.Normal, color = Color.White)
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(shape = RoundedCornerShape(5.dp))
                            .background(Color.Gray.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(percentFree.toFloat())
                                .background(Color.Green),
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth((1.0 - percentFree).toFloat())
                                .background(Color.Red)
                                .align(Alignment.CenterEnd),
                        )
                    }
                    Text(text = "Places: $freePlaces de ${selectedParking?.total_capacity}", fontSize = 15.sp, fontWeight = FontWeight.Normal, color = Color.Gray)
                    OpenMapsButton(latitude = selectedParking?.latitude?.toDouble(), longitude = selectedParking?.longitude?.toDouble())

                    LineChart(data = transformApiResponseToPairList(predictResult).takeLast(5), modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp))
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

private fun getValuePercentageForRange(value: Float, max: Float, min: Float) =
    (value - min) / (max - min)

@RequiresApi(Build.VERSION_CODES.O)
fun transformApiResponseToPairList(apiResponse: ApiResponse): List<Pair<LocalDate, Double>> {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return apiResponse.data.map { (timestamp, timeData) ->
        val localDate = LocalDate.parse(timestamp, dateFormatter)
        localDate to timeData.occupacy.toDouble()
    }
}

@Composable
fun LineChart(
    data: List<Pair<LocalDate, Double>> = emptyList(),
    modifier: Modifier = Modifier
) {
    val spacing = 100f
    val graphColor = Color.Cyan
    val transparentGraphColor = remember { graphColor.copy(alpha = 0.5f) }
    val upperValue = remember { (data.maxOfOrNull { it.second }?.plus(1))?.roundToInt() ?: 0 }
    val lowerValue = remember { (data.minOfOrNull { it.second }?.toInt() ?: 0) }
    val density = LocalDensity.current

    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }

    Canvas(modifier = modifier) {
        val spacePerDate = (size.width - spacing) / (data.size - 1).coerceAtLeast(1)

        // Draw X-axis labels (dates)
        data.forEachIndexed { index, entry ->
            val date = entry.first
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    date.toString(), // Format date as needed
                    spacing + index * spacePerDate,
                    size.height,
                    textPaint
                )
            }
        }

        // Calculate price steps for Y-axis
        val priceStep = (upperValue - lowerValue) / 5f
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * i).toString(),
                    30f,
                    size.height - spacing - i * size.height / 5f,
                    textPaint
                )
            }
        }

        // Draw graph line
        val strokePath = Path().apply {
            val height = size.height
            data.forEachIndexed { index, entry ->
                val value = entry.second
                val ratio = (value - lowerValue) / (upperValue - lowerValue)

                val x = spacing + index * spacePerDate
                val y = height - spacing - (ratio * (height - spacing)).toFloat()

                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }

        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // Draw gradient fill
        val fillPath = android.graphics.Path(strokePath.asAndroidPath()).asComposePath().apply {
            lineTo(spacing + (data.size - 1) * spacePerDate, size.height - spacing)
            lineTo(spacing, size.height - spacing)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            )
        )
    }
}


@Composable
fun MapDataObject(
    data: Parking,
    viewModel: MainViewModel
) {
    val percentFree = data.occupied_places.toDouble() / data.total_capacity.toDouble()
    val color = when(percentFree) {
        in 0.0..0.5 -> Color.Blue
        in 0.5..0.9999 -> Color.Yellow
        else -> Color.Red
    }
    Box(modifier = Modifier
        .size(40.dp)
        .clickable {
            viewModel.setSelectedParking(data)
        }
        .background(color, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center) {
        Text(
            text = "P",
            fontSize = 25.sp,
            color = if (color!=Color.Yellow) Color.White else Color.Black,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun OpenMapsButton(latitude: Double?, longitude: Double?) {
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(150.dp)
            .background(Colors.lightGrey, RoundedCornerShape(10.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
            .height(50.dp)
            .fillMaxWidth()
            .clickable {
                if (latitude == null || longitude == null) return@clickable
                val mapUri = Uri.parse("https://maps.google.com/maps/search/$latitude,$longitude")
                val intent = Intent(Intent.ACTION_VIEW, mapUri)
                context.startActivity(intent)
            }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Google Maps",
                fontSize = 15.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 6.dp))
            Image(painterResource(id = R.drawable.maps), contentDescription = "Location", modifier= Modifier.size(40.dp))
        }
    }
}