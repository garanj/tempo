package com.garan.tempo.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    mainColor: Color = Color.Unspecified,
    unitColor: Color = mainColor,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    sizingPlaceholder: String,
    unitText: String,
    unitSize: TextUnit = 6.sp
) {
    BoxWithConstraints {
        var shrunkFontSize by rememberSaveable{ mutableStateOf(100.0) }
        var ready by rememberSaveable{ mutableStateOf(false) }
        if (!ready) {
            var fontSize = shrunkFontSize
            var annotated = createAnnotatedString(
                sizingPlaceholder,
                fontSize.sp,
                mainColor,
                unitText,
                unitSize,
                unitColor
            )
            val calculateIntrinsics = @Composable {
                ParagraphIntrinsics(
                    annotated.text, TextStyle(
                        color = mainColor,
                        fontWeight = fontWeight,
                        textAlign = textAlign,
                        lineHeight = lineHeight,
                        fontFamily = fontFamily,
                        textDecoration = textDecoration,
                        fontStyle = fontStyle,
                        letterSpacing = letterSpacing
                    ),
                    annotated.spanStyles, listOf(), LocalDensity.current,
                    fontFamilyResolver = LocalFontFamilyResolver.current
                )
            }

            var intrinsics = calculateIntrinsics()
            with(LocalDensity.current) {
                while (intrinsics.maxIntrinsicWidth > maxWidth.toPx()) {
                    fontSize *= 0.9
                    annotated = createAnnotatedString(
                        sizingPlaceholder,
                        fontSize.sp,
                        mainColor,
                        unitText,
                        unitSize,
                        unitColor
                    )
                    intrinsics = calculateIntrinsics()
                }
            }
            ready = true
            shrunkFontSize = fontSize
        }
        // TODO remembr styl?
        Text(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            text = createAnnotatedString(
                text,
                shrunkFontSize.sp,
                mainColor,
                unitText,
                unitSize,
                unitColor
            ),
            textAlign = textAlign
        )
    }
}

fun createAnnotatedString(
    mainText: String,
    mainSize: TextUnit,
    mainColor: Color,
    unitText: String,
    unitSize: TextUnit,
    unitColor: Color
) = buildAnnotatedString {
        withStyle(SpanStyle(fontSize = mainSize, color = mainColor)) {
            append(mainText)
        }
        withStyle(SpanStyle(fontSize = unitSize, color = unitColor)) {
            append(unitText)
        }
    }