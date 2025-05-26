package it.fast4x.rigallery.feature_node.presentation.statistics

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.util.isLandscape
import it.fast4x.rigallery.core.util.randomColor
import it.fast4x.rigallery.feature_node.domain.util.isImage
import it.fast4x.rigallery.feature_node.presentation.common.components.MediaImage
import it.fast4x.rigallery.feature_node.presentation.common.components.TwoLinedDateToolbarTitle
import it.fast4x.rigallery.feature_node.presentation.util.Screen
import it.fast4x.rigallery.feature_node.presentation.util.mediaSharedElement
import it.fast4x.rigallery.feature_node.presentation.util.rememberFeedbackManager
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navigateUp: () -> Unit,
    navigate: (String) -> Unit,
) {

    val viewModelMedia = hiltViewModel<StatisticsViewModel>()
    val favoriteCount by viewModelMedia.favoriteCount.collectAsStateWithLifecycle()
    val trashedCount by viewModelMedia.trashedCount.collectAsStateWithLifecycle()
    val ignoredCount by viewModelMedia.ignoredCount.collectAsStateWithLifecycle()
    val withLocationCount by viewModelMedia.withLocationCount.collectAsStateWithLifecycle()
    val mediaTypeCountByYears by viewModelMedia.mediaTypeCountByYears.collectAsStateWithLifecycle()
    val mediaTypeCount by viewModelMedia.mediaTypeCount.collectAsStateWithLifecycle()
    val mediaTypesCount by viewModelMedia.mediaTypesCount.collectAsStateWithLifecycle()
    val topMedia by viewModelMedia.topMedia.collectAsStateWithLifecycle()

    var canScroll by rememberSaveable { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { canScroll }
    )

    val textFavorited = stringResource(R.string.tag_favorite)
    val textTrashed = stringResource(R.string.trash)
    val textIgnored = stringResource(R.string.ignored)
    val textImage = stringResource(R.string.tag_image)
    val textVideo = stringResource(R.string.tag_video)
    val textWithLocation = stringResource(R.string.tag_withlocation)
    val imagesColor = Color(0xFF8454DA)
    val videosColor = Color(0xFFCB8566)
    val favoritedColor = Color(0xFF70B1E5)
    val trashedColor = Color(0xFFC7876B)
    val ignoredColor = Color(0xFFE5DA79)
    val withLocationColor = Color(0xFF68E86C)

    Box {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TwoLinedDateToolbarTitle(
                            albumName = "Statistics",
                            dateHeader = "----"
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_cd)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddings ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
                    .padding(paddings)
                    .padding(horizontal = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var media = snapshotFlow { mediaTypeCount }
                    val dataList = remember { mutableStateOf(emptyList<Pie>()) }
                    val selectedColor = MaterialTheme.colorScheme.primary //Color.Green.copy(alpha = 0.5f)
                    var pieIndex by remember { mutableIntStateOf(0) }
                    var clicked by remember { mutableStateOf(false) }


                    LaunchedEffect(key1 = mediaTypeCount) {
                        media.collectLatest {
                            dataList.value = listOf(
                                Pie(
                                    label = textImage,
                                    data = it.images.toDouble(),
                                    color = imagesColor,
                                    selectedColor = selectedColor
                                ),
                                Pie(
                                    label = textVideo,
                                    data = it.videos.toDouble(),
                                    color = videosColor,
                                    selectedColor = selectedColor
                                )
                            )
                        }
                    }


                    Column {
                        Text(text = "${stringResource(R.string.tag_image)}: ${mediaTypeCount.images}", color = if (clicked && pieIndex == 0) selectedColor else imagesColor)
                        Text(text = "${stringResource(R.string.tag_video)}: ${mediaTypeCount.videos}", color = if (clicked && pieIndex == 1) selectedColor else videosColor)
                    }

                    Column {
                        PieChart(
                            modifier = Modifier.size(150.dp),
                            data = dataList.value,
                            onPieClick = {
                                println("${it.label} Clicked")
                                pieIndex = dataList.value.indexOf(it)
                                dataList.value =
                                    dataList.value.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                                clicked = true
                            },
                            //selectedScale = 1.2f,
                            scaleAnimEnterSpec = spring<Float>(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            colorAnimEnterSpec = tween(300),
                            colorAnimExitSpec = tween(300),
                            scaleAnimExitSpec = tween(300),
                            spaceDegreeAnimExitSpec = tween(300),
                            style = Pie.Style.Fill
                        )
                    }

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var media = snapshotFlow { mediaTypesCount }
                    val dataList = remember { mutableStateOf(emptyList<Pie>()) }
                    val dataListColor = remember { mutableStateOf(emptyList<Color>()) }
                    val selectedColor = MaterialTheme.colorScheme.primary
                    var pieIndex by remember { mutableIntStateOf(0) }
                    var clicked by remember { mutableStateOf(false) }


                    LaunchedEffect(key1 = mediaTypesCount) {
                        media.collectLatest {
                            mediaTypesCount.forEach {
                                val color = Color(randomColor())
                                dataList.value += Pie(
                                    label = it.mimeType,
                                    data = it.value.toDouble(),
                                    color = color,
                                    selectedColor = selectedColor
                                )
                                dataListColor.value += color
                            }
                        }
                    }


                    Column {
                        PieChart(
                            modifier = Modifier.size(150.dp),
                            data = dataList.value,
                            onPieClick = {
                                println("${it.label} Clicked")
                                pieIndex = dataList.value.indexOf(it)
                                dataList.value =
                                    dataList.value.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                                clicked = true
                            },
                            //selectedScale = 1.2f,
                            scaleAnimEnterSpec = spring<Float>(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            colorAnimEnterSpec = tween(300),
                            colorAnimExitSpec = tween(300),
                            scaleAnimExitSpec = tween(300),
                            spaceDegreeAnimExitSpec = tween(300),
                            style = Pie.Style.Fill
                        )
                    }
                    Column {
                        mediaTypesCount.forEachIndexed { index, it ->
                            Text(text = "${it.mimeType}: ${it.value}", color = if (clicked && pieIndex == 0) selectedColor else
                                try {
                                    dataListColor.value[index]
                                } catch (e: Exception) {
                                    Color.Transparent
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var media = snapshotFlow { mediaTypeCount }
                    val dataList = remember { mutableStateOf(emptyList<Pie>()) }
                    val selectedColor = MaterialTheme.colorScheme.primary //Color.Green.copy(alpha = 0.5f)
                    var pieIndex by remember { mutableIntStateOf(0) }
                    var clicked by remember { mutableStateOf(false) }


                    LaunchedEffect(key1 = mediaTypeCount, withLocationCount) {
                        media.collectLatest {
                            dataList.value = listOf(
                                Pie(
                                    label = textFavorited,
                                    data = favoriteCount.toDouble(),
                                    color = favoritedColor,
                                    selectedColor = selectedColor
                                ),
                                Pie(
                                    label = textTrashed,
                                    data = trashedCount.toDouble(),
                                    color = trashedColor,
                                    selectedColor = selectedColor
                                ),
                                Pie(
                                    label = textTrashed,
                                    data = ignoredCount.toDouble(),
                                    color = ignoredColor,
                                    selectedColor = selectedColor
                                ),
                                Pie(
                                    label = textWithLocation,
                                    data = withLocationCount.toDouble(),
                                    color = withLocationColor,
                                    selectedColor = selectedColor
                                )
                            )
                        }
                    }

                    Column {
                        Text(text = "${textFavorited}: ${favoriteCount}", color = if (clicked && pieIndex == 0) selectedColor else favoritedColor)
                        Text(text = "${textTrashed}: ${trashedCount}", color = if (clicked && pieIndex == 1) selectedColor else trashedColor)
                        Text(text = "${textIgnored}: ${ignoredCount}", color = if (clicked && pieIndex == 2) selectedColor else ignoredColor)
                        Text(text = "${textWithLocation}: ${withLocationCount}", color = if (clicked && pieIndex == 3) selectedColor else withLocationColor)
                    }

                    Column {
                        PieChart(
                            modifier = Modifier.size(150.dp),
                            data = dataList.value,
                            onPieClick = {
                                println("${it.label} Clicked")
                                pieIndex = dataList.value.indexOf(it)
                                dataList.value =
                                    dataList.value.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                                clicked = true
                            },
                            //selectedScale = 1.2f,
                            scaleAnimEnterSpec = spring<Float>(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            colorAnimEnterSpec = tween(300),
                            colorAnimExitSpec = tween(300),
                            scaleAnimExitSpec = tween(300),
                            spaceDegreeAnimExitSpec = tween(300),
                            style = Pie.Style.Stroke()
                        )
                    }
                }

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val barsList = remember { mutableListOf<Bars>() }
                        mediaTypeCountByYears.take(if (isLandscape) mediaTypeCountByYears.size else 5)
                        .forEach { year ->
                            barsList.add(
                                Bars(
                                    label = year.year.toString(),
                                    values = listOf(
                                        Bars.Data(label = textImage , value = year.images.toDouble(), color = SolidColor(imagesColor)),
                                        Bars.Data(label = textVideo , value = year.videos.toDouble()+100, color = SolidColor(videosColor))
                                    )
                                )
                            )
                        }

                    if (barsList.isNotEmpty())
                        ColumnChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 22.dp),
                            data = remember {
                                barsList.toMutableStateList()
                            },
                            onBarClick = {
                                println("Bar clicked: ${it.bar.label}")
                            },
                            barProperties = BarProperties(
                                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
                                spacing = 3.dp,
                                style = DrawStyle.Fill
                            ),
                            labelHelperProperties = LabelHelperProperties(
                                enabled = true,
                                textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            ),
                            labelProperties = LabelProperties(
                                enabled = true,
                                textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            ),
                            indicatorProperties = HorizontalIndicatorProperties(
                                enabled = true,
                                textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            ),
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                        )
                }

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val valuesImagesList = remember { mutableListOf<Double>() }
                    mediaTypeCountByYears.take(if (isLandscape) mediaTypeCountByYears.size else 5)
                        .also { valuesImagesList.clear() }
                        .map { it.images.toDouble() }.toCollection(valuesImagesList)
                    val valuesVideosList = remember { mutableListOf<Double>() }
                    mediaTypeCountByYears.take(if (isLandscape) mediaTypeCountByYears.size else 5)
                        .also { valuesVideosList.clear() }
                        .map { it.videos.toDouble() }.toCollection(valuesVideosList)
                    val labelsList = remember { mutableListOf<String>() }
                    mediaTypeCountByYears.take(if (isLandscape) mediaTypeCountByYears.size else 5)
                        .also { labelsList.clear() }
                        .map { it.year.toString() }.toCollection(labelsList)
//                    var rangeOfYears = remember { "" }
//                    rangeOfYears = mediaTypeCountByYears.take(if (isLandscape) mediaTypeCountByYears.size else 5)
//                        .firstOrNull()?.year.toString()

                    if (valuesImagesList.isNotEmpty())
                        LineChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 22.dp),
                            data = remember {
                                listOf(
                                    Line(
                                        label = textImage,
                                        values = valuesImagesList,
                                        color = SolidColor(imagesColor),
                                        firstGradientFillColor = imagesColor.copy(alpha = .5f),
                                        secondGradientFillColor = Color.Transparent,
                                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                        gradientAnimationDelay = 1000,
                                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                                    ),
                                    Line(
                                        label = textVideo,
                                        values = valuesVideosList,
                                        color = SolidColor(videosColor),
                                        firstGradientFillColor = videosColor.copy(alpha = .5f),
                                        secondGradientFillColor = Color.Transparent,
                                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                        gradientAnimationDelay = 1000,
                                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                                    )
                                )
                            },
                            animationMode = AnimationMode.Together(delayBuilder = {
                                it * 500L
                            }),
                            labelProperties = LabelProperties(
                                enabled = true,
                                labels = labelsList,
                                textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            ),
                            labelHelperProperties = LabelHelperProperties(
                                enabled = true,
                                textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            ),
                            indicatorProperties = HorizontalIndicatorProperties(
                                enabled = true,
                                textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            ),
                        )
                }

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    LazyVerticalGrid(
                        state = rememberLazyGridState(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp),
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(16.dp),
                        userScrollEnabled = canScroll,
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {

                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) { Text(
                            text = "Your top 10",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) }

                        itemsIndexed(
                            items = topMedia,
                            key = { _, item -> item.toString() },
                            contentType = { _, item -> item.isImage }
                        ) { index, media ->
                            Box() {
                                MediaImage(
                                    modifier = Modifier
                                        .animateItem(),
                                    media = media,
                                    canClick = canScroll,
                                    onItemClick = {
                                        navigate(Screen.MediaViewScreen.route + "?mediaId=${it.id}&query=false")
                                    },
                                    onItemLongClick = {},
                                    selectionState = remember { mutableStateOf(false) },
                                    selectedMedia = SnapshotStateList()
                                )
                                Text(
                                    text = (index+1).toString(),
                                    color = Color.Red,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                        }
                    }
                }

                Row {
                    Spacer(modifier = Modifier.height(100.dp))
                }

            }

        }
    }
}
