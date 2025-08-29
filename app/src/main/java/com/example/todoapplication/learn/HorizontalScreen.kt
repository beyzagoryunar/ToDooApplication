package com.example.todoapplication.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun HorizontalScreen(){
    Scaffold (

    ){ paddingValues ->
        Row (modifier = Modifier.fillMaxWidth()
            .height(200.dp)
            .background(Color.Black)
            .padding(paddingValues),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically

            ){
            TextComponent(value="Test 1",)
            TextComponent(value="Test 2",)
            TextComponent(value="Test 3",)
            TextComponent(value="Test 4",)
        }
    }

}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HorizontalScreenPreview(){
    HorizontalScreen()
}

