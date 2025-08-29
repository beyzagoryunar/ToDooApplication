package com.example.todoapplication.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun     VerticalScreen(){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp)
            .background(Color.White),

    ){
        TextComponent(value="Hello",
            colorValue = Color.Black,
            size = 24.sp,
            fontWeightValue = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        TextFieldComponent()
        Spacer(modifier = Modifier.height(10.dp))
        SimpleButton()
        ImageComponent()
    }
}
@Preview(showBackground = true,showSystemUi = true)
@Composable
fun VerticalScreenPreview(){
    VerticalScreen()
}
