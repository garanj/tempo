package com.garan.tempo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import com.garan.tempo.R
import com.garan.tempo.mapping.LatLng
import com.garan.tempo.mapping.RouteMap
import com.garan.tempo.ui.theme.TempoTheme
import java.nio.ByteBuffer
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

@Composable
fun WorkoutMap(pathData: ByteArray) {
    val vectorPainter = rememberVectorPainter(
        defaultWidth = 100.dp,
        defaultHeight = 100.dp,
        autoMirror = false,
    ) { viewPortX, viewPortY ->
        val strokeWidth = 3f
        val routeMap = remember { RouteMap.fromByteArray(pathData) }
        val nodes = remember {
            val paddedVx = viewPortX.toDouble() - strokeWidth * 2
            val paddedVy = viewPortY.toDouble() - strokeWidth * 2
            routeMap.toPathNodeList(paddedVx, paddedVy)
        }
        Group(
            name = "Map",
            translationX = strokeWidth,
            translationY = strokeWidth,
        ) {
            Path(
                pathData = nodes,
                strokeLineWidth = 3f,
                stroke = SolidColor(MaterialTheme.colors.onSurface),
                strokeLineJoin = StrokeJoin.Round,
                strokeLineCap = StrokeCap.Round,
                fillAlpha = 1f
            )
        }
    }
    TitleCard(
        onClick = { },
        title = {
            Text(
                text = stringResource(id = R.string.map_title),
                style = MaterialTheme.typography.caption3
            )
        },
        titleColor = MaterialTheme.colors.onSurface
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            alignment = Alignment.Center,
            contentDescription = "A map",
            painter = vectorPainter
        )
    }
}

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun WorkoutMapPreview() {
    TempoTheme {
        val data = remember { mapPreviewData() }
        WorkoutMap(pathData = data)
    }
}

val locData = listOf(
    LatLng(lng = -2.603288, lat = 51.458447), // Bristol
    LatLng(lng = -0.116414, lat = 51.511448), // London
    LatLng(lng = 0.113818, lat = 52.204311), // Cambridge
    LatLng(lng = -1.254449, lat = 51.754845), // Oxford
)

val lData = locData.map {
    LatLng(
        lng = xAxisProjection(it.lng),
        lat = yAxisProjection(it.lat)
    )
}

fun yAxisProjection(value: Double): Double {
    val RADIUS_MAJOR = 6378137.0
    val RADIUS_MINOR = 6356752.3142

    val input = value.coerceIn(-89.5, 89.5)
    val earthDimensionalRateNormalized = 1.0 - (RADIUS_MINOR / RADIUS_MAJOR).pow(2.0)
    var inputOnEarthProj = sqrt(earthDimensionalRateNormalized) *
            sin(Math.toRadians(input))
    inputOnEarthProj = ((1.0 - inputOnEarthProj) / (1.0 + inputOnEarthProj)).pow(
        0.5 * sqrt(earthDimensionalRateNormalized)
    )
    val inputOnEarthProjNormalized =
        tan(0.5 * (Math.PI * 0.5 - Math.toRadians(input))) / inputOnEarthProj
    return -1 * RADIUS_MAJOR * ln(inputOnEarthProjNormalized)
}

/**
 * Projects a longitude using Mercator projecction.
 */
fun xAxisProjection(input: Double): Double {
    val RADIUS_MAJOR = 6378137.0
    return RADIUS_MAJOR * Math.toRadians(input)
}

fun mapPreviewData(): ByteArray {
    val buffer = ByteBuffer.allocate(locData.size * 2 * Double.SIZE_BYTES)
    lData.forEach {
        buffer.putDouble(it.lng)
        buffer.putDouble(it.lat)
    }
    return buffer.array()
}