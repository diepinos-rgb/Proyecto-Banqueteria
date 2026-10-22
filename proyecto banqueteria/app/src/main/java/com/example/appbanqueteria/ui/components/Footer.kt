package com.example.appbanqueteria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Footer(
    copyrightText: String ="© 2026 App de Paolicias.Sweet. Todos los derechos reservados",
    backGroundColor: Color = Color(0xFF0B0B0B),
    textColor:Color=Color.White
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(backGroundColor),
        contentAlignment = Alignment.Center
    ){
        Text(
            text=copyrightText,
            color=textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}