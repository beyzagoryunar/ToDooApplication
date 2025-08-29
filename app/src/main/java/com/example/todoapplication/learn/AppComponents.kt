package com.example.todoapplication.learn

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapplication.R


@Composable
fun TextComponent(
    value: String,
    size: TextUnit = 18.sp,
    colorValue: Color = Color.Magenta,
    fontStyleValue: FontStyle = FontStyle.Normal,
    maxLinesValue: Int? = null,
    backgroundColor: Color = Color.White,
    fontWeightValue: FontWeight= FontWeight.Normal
)
{

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .background(backgroundColor)
            .padding(18.dp),
        text = value,
        fontSize = size,
        color = colorValue,
        fontWeight=fontWeightValue,
        fontStyle = fontStyleValue,
        maxLines = maxLinesValue ?:Int.MAX_VALUE,
        overflow = TextOverflow.Ellipsis)

}

@Preview
@Composable
fun TextComponentPreview(){
    TextComponent(
        value = "Hello",
        size=28.sp,
        Color.Magenta,
        maxLinesValue=4,
        fontWeightValue =  FontWeight.Normal
    )
}

@Composable
fun SimpleButton(){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),

        onClick = {
            Log.d("Simple Button", "Button Clicked")
        })
    {
        NormalTextForButtons(value = "Click here",TextAlign.Center)
    }
}

@Preview
@Composable
fun SimpleButtonPreview(){
    SimpleButton()
}

@Composable
fun NormalTextForButtons(value: String,alignment: TextAlign=TextAlign.Start){
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),

        text = value,
        textAlign = alignment,
        fontSize = 16.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldComponent(){
    var text by remember {
       mutableStateOf("")
    }
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        onValueChange = { newText ->
        text = newText
    },
        textStyle = TextStyle(
            fontSize = 21.sp,
            fontStyle = FontStyle.Italic,
        ),
        label = {
            NormalTextForButtons(value = "Your Name")
        },
        placeholder = {
            NormalTextForButtons(value = "Please enter your name")
        },
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Preview
@Composable
fun TextFieldComponentPreview(){
    TextFieldComponent()
}

@Composable
fun ImageComponent(){
    Image(
        modifier = Modifier.wrapContentHeight()
            .fillMaxSize()
            .alpha(0.5f),

        painter = painterResource(id= R.drawable.noface) ,
        contentDescription = "Halloween Logo",
        )
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ImageComponentPreview(){
    ImageComponent()
}