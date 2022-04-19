package com.example.firstcompose

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import kotlin.math.round
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    var points=0
    var currentLife=3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Column(modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0.294f, 0.149f, 0.152f))) {

                Box(modifier = Modifier.fillMaxSize())
                {

                    //Creates the List of bars
                    CreateList(modifier=Modifier.fillMaxSize())

                    //Indicators
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(Color.Red)
                            .width(7.dp)
                    )
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(Color.Yellow)
                                .width(7.dp)
                        )
                    }
                }
            }
        }
    }

    //Model class for bars
    data class bar(var id:Int, var direction:String)

    //Display the bar
    @Composable
    fun createBar(item: bar)
    {
        if(item.direction=="LEFT")
        {
            Box(modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .padding(vertical = 7.dp, horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(color = Color.Red))
        }
        else if(item.direction=="RIGHT")
        {
            Box(modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .padding(vertical = 7.dp, horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(color = Color.Yellow))
        }
    }

    @SuppressLint("RememberReturnType")
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun CreateList(modifier: Modifier=Modifier.fillMaxSize())
    {
        val score= remember {
            mutableStateOf(points)
        }
        val life= remember {
            mutableStateOf(currentLife)
        }

        val barsList= remember {
            mutableStateListOf<bar>()
        }
        var listofbars= remember {
            for(i in 1..25)
            {
                val random = Random.nextInt()
                if (random % 2 == 0)
                {
                    barsList.add(bar(i,"LEFT"))
                }
                else
                {
                    barsList.add(bar(i,"RIGHT"))
                }
            }
        }
        var showDialog by remember { mutableStateOf(false) }

        if(currentLife==0)
            showDialog=true
        if(barsList.size==0)
            showDialog=true
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {showDialog=false},
                title = {Text("Play Again!")},
                text = {Text("Press Restart to start the game again!")},
                confirmButton = {TextButton(onClick = {showDialog=false

                    this.startActivity(Intent(this, MainActivity::class.java))
                    this.finish()
                }
                ){ Text(text ="Restart")}},
                dismissButton ={},
            )
        }



        Column()
        {
            Row(modifier = Modifier
                .height(55.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically){

                Text(text = "Score ${score.value}", style = TextStyle(color = Color.White, fontSize = 24.sp, fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Bold))
                Text(text = "Life ${life.value}", style = TextStyle(color = Color.White, fontSize = 24.sp, fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Bold))

            }

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .background(Color(0.294f, 0.149f, 0.152f)))
            {
                items(barsList,{barsList: bar ->barsList.id})
                { items->
                    val dismissSate= rememberDismissState()
                    if (dismissSate.isDismissed(DismissDirection.EndToStart)&&items.direction=="LEFT") {
                        barsList.remove(items)
                        points++;
                        score.value=points
                    }
                    else if (dismissSate.isDismissed(DismissDirection.StartToEnd)&&items.direction=="RIGHT")
                    {
                        barsList.remove(items)
                        points++;
                        score.value=points
                    }
                    else if(dismissSate.isDismissed(DismissDirection.StartToEnd)||dismissSate.isDismissed(DismissDirection.EndToStart)){
                        Toast.makeText(this@MainActivity,"Oops Wrong side!",Toast.LENGTH_SHORT).show()
                        barsList.remove(items)
                        currentLife--;
                        life.value=currentLife
                    }
                    SwipeToDismiss(
                        state = dismissSate,
                        dismissContent = {createBar(items)},
                        background = {}, dismissThresholds = {FractionalThreshold(0.7f)}
                    )
                }
            }
        }
    }
}