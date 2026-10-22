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
fun Header(
    title:String,
    backGroundColor: Color = Color(0xFF090909),
    textColor:Color=Color.White
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(backGroundColor),
        contentAlignment = Alignment.Center
    ){
        Text(
            text=title,
            color=textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }


}