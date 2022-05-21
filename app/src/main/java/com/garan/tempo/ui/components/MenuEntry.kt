package com.garan.tempo.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun MenuEntry(menuItem: MenuItem) = when (menuItem) {
    is MenuItem.MenuHeading -> Text(
        modifier = Modifier
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = menuItem.label,
        style = MaterialTheme.typography.title2
    )
    is MenuItem.MenuButton -> Chip(
        onClick = menuItem.onClick,
        label = { Text(menuItem.label) },
        icon = {
            Icon(
                imageVector = menuItem.imageVector,
                menuItem.label
            )
        }
    )
}

sealed class MenuItem {
    data class MenuButton(
        val label: String,
        val imageVector: ImageVector,
        val onClick: () -> Unit
    ) :
        MenuItem()

    data class MenuHeading(val label: String) : MenuItem()
}