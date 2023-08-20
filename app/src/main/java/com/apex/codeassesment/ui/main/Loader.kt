package com.apex.codeassesment.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.apex.codeassesment.ui.main.ui.theme.Pink40
import com.apex.codeassesment.ui.main.ui.theme.Purple40
import com.apex.codeassesment.ui.main.ui.theme.PurpleGrey40
import com.apex.codeassesment.ui.main.ui.theme.PurpleGrey80
import com.apex.codeassesment.ui.main.ui.theme.PurpleGreyTransparent

@Composable
fun Loader() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = PurpleGreyTransparent)
            .clickable { },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
        ) {
            val gradient = Brush.verticalGradient(0f to PurpleGrey40, 1000f to PurpleGrey80)
            CircularProgressIndicator(
                color = Pink40,
                modifier = Modifier
                    .background(brush = gradient)
                    .padding(25.dp)
            )
        }
    }
}