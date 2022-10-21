package com.garan.tempo.mapping

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.garan.tempo.data.FitDecoder
import com.garan.tempo.data.SavedExerciseDao
import com.garan.tempo.data.SavedExerciseUpdate
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan


const val WORKOUT_UUID = "workout_uuid"

class RouteMapCreator @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun createMap(uuid: UUID) {
        val inputData = Data.Builder()
            .putString(WORKOUT_UUID, uuid.toString())
            .build()
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<MapCreatorWorker>()
                .setInputData(inputData)
                .build()
        WorkManager
            .getInstance(context)
            .enqueue(uploadWorkRequest)
    }
}

@HiltWorker
class MapCreatorWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val dao: SavedExerciseDao,
) :
    CoroutineWorker(appContext, workerParams) {
    //    val locData = listOf(
//        Pair(-0.347978,51.43285),
//        Pair(-0.346646,51.432732),
//        Pair(-0.345851,51.432264),
//        Pair(-0.346887,51.432185),
//        Pair(-0.347393,51.431685),
//        Pair(-0.348693,51.431808),
//        Pair(-0.349171,51.431146),
//        Pair(-0.349677,51.430531),
//        Pair(-0.349987,51.429749),
//        Pair(-0.350224,51.428967),
//        Pair(-0.350538,51.428202),
//        Pair(-0.350475,51.427441),
//        Pair(-0.350506,51.42664),
//        Pair(-0.349634,51.426313),
//        Pair(-0.348323,51.426035),
//        Pair(-0.347151,51.425692),
//        Pair(-0.346171,51.425136),
//        Pair(-0.345741,51.424361),
//        Pair(-0.345466,51.423514),
//        Pair(-0.345386,51.422691),
//        Pair(-0.346304,51.422042),
//        Pair(-0.347292,51.421457),
//        Pair(-0.347892,51.420777),
//        Pair(-0.347541,51.42006),
//        Pair(-0.346398,51.419621),
//        Pair(-0.345175,51.419297),
//        Pair(-0.343948,51.418978),
//        Pair(-0.342682,51.418655),
//        Pair(-0.341413,51.418372),
//        Pair(-0.340165,51.418054),
//        Pair(-0.338941,51.417735),
//        Pair(-0.337596,51.417613),
//        Pair(-0.336311,51.417526),
//        Pair(-0.33536,51.41787),
//        Pair(-0.335218,51.418702),
//        Pair(-0.334976,51.419516),
//        Pair(-0.334569,51.420263),
//        Pair(-0.333429,51.420595),
//        Pair(-0.33216,51.420397),
//        Pair(-0.330831,51.42027),
//        Pair(-0.329518,51.420154),
//        Pair(-0.3283,51.419861),
//        Pair(-0.327209,51.419502),
//        Pair(-0.326056,51.419131),
//        Pair(-0.324829,51.418796),
//        Pair(-0.323686,51.418393),
//        Pair(-0.322561,51.417976),
//        Pair(-0.321478,51.417552),
//        Pair(-0.320549,51.417028),
//        Pair(-0.319487,51.416489),
//        Pair(-0.318892,51.415819),
//        Pair(-0.318609,51.41496),
//        Pair(-0.317864,51.414317),
//        Pair(-0.317599,51.413536),
//        Pair(-0.317596,51.412728),
//        Pair(-0.318816,51.412544),
//        Pair(-0.319195,51.411937),
//        Pair(-0.319041,51.411113),
//        Pair(-0.320061,51.410704),
//        Pair(-0.321346,51.410548),
//        Pair(-0.322656,51.41046),
//        Pair(-0.323936,51.410258),
//        Pair(-0.325174,51.409983),
//        Pair(-0.326472,51.409769),
//        Pair(-0.327758,51.409617),
//        Pair(-0.329031,51.409346),
//        Pair(-0.330331,51.409211),
//        Pair(-0.33164,51.409049),
//        Pair(-0.332943,51.408844),
//        Pair(-0.334251,51.408613),
//        Pair(-0.335545,51.408474),
//        Pair(-0.336043,51.407776),
//        Pair(-0.336722,51.407109),
//        Pair(-0.336766,51.406469),
//        Pair(-0.335325,51.406378),
//        Pair(-0.334029,51.406397),
//        Pair(-0.332968,51.406443),
//        Pair(-0.332537,51.405863),
//        Pair(-0.332398,51.405049),
//        Pair(-0.332466,51.404367),
//        Pair(-0.331567,51.404638),
//        Pair(-0.332296,51.404386),
//        Pair(-0.333004,51.404187),
//        Pair(-0.332177,51.404518),
//        Pair(-0.332477,51.405349),
//        Pair(-0.332607,51.406166),
//        Pair(-0.332874,51.406571),
//        Pair(-0.331553,51.406698),
//        Pair(-0.330238,51.406845),
//        Pair(-0.328862,51.406957),
//        Pair(-0.327512,51.407126),
//        Pair(-0.326281,51.407208),
//        Pair(-0.324942,51.407351),
//        Pair(-0.323721,51.407532),
//        Pair(-0.322329,51.407669),
//        Pair(-0.321112,51.408004),
//        Pair(-0.320002,51.408434),
//        Pair(-0.318821,51.408825),
//        Pair(-0.317724,51.409288),
//        Pair(-0.316619,51.409725),
//        Pair(-0.315429,51.410099),
//        Pair(-0.314186,51.410419),
//        Pair(-0.31293,51.410753),
//        Pair(-0.311949,51.411121),
//        Pair(-0.311066,51.411211),
//        Pair(-0.310197,51.410834),
//        Pair(-0.309964,51.41015),
//        Pair(-0.30986,51.409294),
//        Pair(-0.309976,51.408531),
//        Pair(-0.309759,51.407624),
//        Pair(-0.309632,51.406766),
//        Pair(-0.309722,51.405955),
//        Pair(-0.309624,51.405121),
//        Pair(-0.309449,51.404265),
//        Pair(-0.309297,51.403502),
//        Pair(-0.309321,51.402694),
//        Pair(-0.309521,51.40179),
//        Pair(-0.309721,51.400964),
//        Pair(-0.310148,51.400145),
//        Pair(-0.3109,51.399521),
//        Pair(-0.311704,51.398905),
//        Pair(-0.312494,51.398242),
//        Pair(-0.313184,51.397534),
//        Pair(-0.314041,51.396956),
//        Pair(-0.314878,51.396355),
//        Pair(-0.315755,51.395766),
//        Pair(-0.316671,51.39514),
//        Pair(-0.317509,51.394543),
//        Pair(-0.31836,51.393908),
//        Pair(-0.319233,51.393308),
//        Pair(-0.320227,51.392895),
//        Pair(-0.321599,51.392649),
//        Pair(-0.322837,51.392413),
//        Pair(-0.32415,51.392202),
//        Pair(-0.325411,51.392085),
//        Pair(-0.32674,51.392149),
//        Pair(-0.327846,51.392553),
//        Pair(-0.328781,51.393098),
//        Pair(-0.329448,51.393735),
//        Pair(-0.330224,51.394455),
//        Pair(-0.330911,51.395177),
//        Pair(-0.331362,51.395943),
//        Pair(-0.33214,51.396628),
//        Pair(-0.333005,51.397267),
//        Pair(-0.333857,51.397917),
//        Pair(-0.334644,51.398571),
//        Pair(-0.335497,51.399222),
//        Pair(-0.336429,51.399839),
//        Pair(-0.337334,51.400423),
//        Pair(-0.337982,51.40113),
//        Pair(-0.338395,51.401863),
//        Pair(-0.339104,51.402601),
//        Pair(-0.33986,51.403255),
//        Pair(-0.340814,51.403801),
//        Pair(-0.341789,51.404301),
//        Pair(-0.341163,51.405019),
//        Pair(-0.340616,51.405784),
//        Pair(-0.340153,51.406546),
//        Pair(-0.339219,51.406794),
//        Pair(-0.337881,51.406555),
//        Pair(-0.337126,51.406969),
//        Pair(-0.337604,51.407755),
//        Pair(-0.33803,51.408552),
//        Pair(-0.338369,51.409367),
//        Pair(-0.338864,51.410009),
//        Pair(-0.340223,51.410137),
//        Pair(-0.341486,51.410231),
//        Pair(-0.342812,51.410396),
//        Pair(-0.344106,51.410567),
//        Pair(-0.345443,51.41067),
//        Pair(-0.346293,51.410981),
//        Pair(-0.346032,51.411801),
//        Pair(-0.346283,51.412641),
//        Pair(-0.346699,51.4134),
//        Pair(-0.347155,51.414146),
//        Pair(-0.348525,51.414345),
//        Pair(-0.349725,51.414706),
//        Pair(-0.350598,51.415198),
//        Pair(-0.350768,51.41599),
//        Pair(-0.350931,51.416808),
//        Pair(-0.35079,51.417499),
//        Pair(-0.34961,51.417768),
//        Pair(-0.348418,51.418073),
//        Pair(-0.347528,51.418691),
//        Pair(-0.346675,51.419339),
//        Pair(-0.346904,51.419915),
//        Pair(-0.348004,51.420396),
//        Pair(-0.348917,51.421013),
//        Pair(-0.34962,51.421698),
//        Pair(-0.350322,51.422423),
//        Pair(-0.351137,51.423052),
//        Pair(-0.351354,51.423744),
//        Pair(-0.352017,51.424484),
//        Pair(-0.353082,51.424982),
//        Pair(-0.354124,51.425491),
//        Pair(-0.355201,51.426018),
//        Pair(-0.35607,51.425775),
//        Pair(-0.356884,51.425067),
//        Pair(-0.357614,51.424338),
//        Pair(-0.357586,51.42379),
//        Pair(-0.356236,51.423935),
//        Pair(-0.354915,51.424072),
//        Pair(-0.353577,51.424222),
//        Pair(-0.352218,51.424326),
//        Pair(-0.351318,51.424849),
//        Pair(-0.350968,51.425636),
//        Pair(-0.350541,51.426486),
//        Pair(-0.350531,51.427489),
//        Pair(-0.350355,51.428388),
//        Pair(-0.350158,51.429203),
//        Pair(-0.349826,51.429973),
//        Pair(-0.349574,51.430779),
//        Pair(-0.348824,51.431248),
//        Pair(-0.348089,51.43171),
//        Pair(-0.347097,51.431907),
//        Pair(-0.346279,51.432176)
//    )
    private val MIN_DATA_POINTS = 50
    private val MAP_SIZE_PX = 512
    private val MAP_LINE_WIDTH = 6.0f
    private val RADIUS_MAJOR = 6378137.0
    private val RADIUS_MINOR = 6356752.3142

    /**
     * Draws and save a bitmap from lat, long points if there are sufficient data points for the
     * workout.
     */
    override suspend fun doWork(): Result {
        val id = workerParams.inputData.getString(WORKOUT_UUID)
        id?.let {
            val fitFile = FitDecoder(appContext)
            val latLongs = fitFile.latLongsForId(id)
            if (latLongs.size > MIN_DATA_POINTS) {
                val coords = createCartesianCoords(latLongs)
                val bitmap = drawMap(coords)
                try {
                    appContext.openFileOutput("${id}.png", Context.MODE_PRIVATE).use { out ->
                        bitmap.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            out
                        )
                    }
                    dao.update(SavedExerciseUpdate(id, true))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return Result.success()
    }

    /**
     * Draws a map of the workout from a list of lat, long points.
     */
    private fun drawMap(points: List<Pair<Double, Double>>): Bitmap {
        val conf = Bitmap.Config.ARGB_8888
        val width = points.maxOf { it.first }.toInt() + MAP_LINE_WIDTH.toInt()
        val height = points.maxOf { it.second }.toInt() + MAP_LINE_WIDTH.toInt()
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            conf
        )

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)
        val paint = Paint()
        paint.color = Color.parseColor("#03A9F4")
        paint.strokeWidth = MAP_LINE_WIDTH
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND

        for (i in (1 until points.size)) {
            canvas.drawLine(
                points[i - 1].first.toFloat() + MAP_LINE_WIDTH / 2,
                height - (points[i - 1].second.toFloat() + MAP_LINE_WIDTH / 2) - 1,
                points[i].first.toFloat() + MAP_LINE_WIDTH / 2,
                height - (points[i].second.toFloat() + MAP_LINE_WIDTH / 2) - 1,
                paint
            )
        }
        return bitmap
    }

    /**
     * Projects a latitude using Mercator projection.
     */
    private fun yAxisProjection(value: Double): Double {
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
    private fun xAxisProjection(input: Double): Double {
        return RADIUS_MAJOR * Math.toRadians(input)
    }

    /**
     * Projects coordinates using Mercator projection then scales it to fit within bitmap bounds.
     */
    private fun createCartesianCoords(data: List<Pair<Double, Double>>): List<Pair<Double, Double>> {
        var minX = Double.MAX_VALUE
        var maxX = -Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxY = -Double.MAX_VALUE

        val coords = mutableListOf<Pair<Double, Double>>()

        data.forEach {
            val x = xAxisProjection(it.first)
            val y = yAxisProjection(it.second)

            if (y > maxY) {
                maxY = y
            }
            if (y < minY) {
                minY = y
            }
            if (x > maxX) {
                maxX = x
            }
            if (x < minX) {
                minX = x
            }
            coords.add(Pair(x, y))
        }

        val xRange = maxX - minX
        val yRange = maxY - minY
        val xScale = MAP_SIZE_PX / xRange
        val yScale = MAP_SIZE_PX / yRange
        val scale = min(xScale, yScale)

        return coords.map {
            Pair(
                (it.first - minX) * scale,
                (it.second - minY) * scale
            )
        }
    }
}