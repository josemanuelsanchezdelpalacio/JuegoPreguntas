package com.damjms.juegopreguntas

import com.damjms.juegopreguntas.ui.theme.JuegoPreguntasTheme

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.damjms.juegopreguntas.data.listaPreguntas
import com.damjms.juegopreguntas.data.preguntasRespuestas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JuegoPreguntasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                    preguntasAleatorias()
                }
            }
        }
    }
}

//metodo que recorre el hashMap y añade a la lista las preguntas. Despues las devuelve de forma aleatoria
fun preguntasAleatorias(): String {
    for (pregunta in preguntasRespuestas) {
        listaPreguntas.add(pregunta.key)
    }
    return listaPreguntas.shuffled().first()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    var respuesta by rememberSaveable { mutableStateOf("") }
    var numPartida by rememberSaveable { mutableStateOf(0) }
    var puntuacion by rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current

    //creo un scaffold para poner un CenterAlignedTopAppBar donde poner la puntuacion y un titulo
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFC107),
                    titleContentColor = Color.Black
                ),
                title = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "TITULO DEL JUEGO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "PUNTUACION: $puntuacion",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            )
        },
        //y un CenterAlignedTopAppBar para poner un boton que reiniciara el juego
        bottomBar = {
            CenterAlignedTopAppBar(
                title = {
                    //cuando se pulsa el boton reinicia los valores a su estado inicial
                    Button(onClick = {
                        numPartida = 0
                        puntuacion = 0
                    }) {
                        Text(
                            text = "REINICIAR JUEGO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Reiniciar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.pelis_interrogante),
                contentDescription = "interrogantePeliLogo",
                alignment = Alignment.Center
            )
            //si el numero de la partida es menor al tamaño de la lista continua el juego
            //cuando sea mayor saca un mensaje de FIN DEL JUEGO
            if (numPartida < listaPreguntas.size) {
                //muestro la pregunta a traves de la lista usando "numPartida" para sacar una de las preguntas
                Text(
                    text = listaPreguntas[numPartida],
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = respuesta,
                    onValueChange = { respuesta = it },
                    label = { Text(text = "Introduce tu respuesta") },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Button(
                    onClick = {
                        //convierto la respuesta tanto del usuario como del hashMap en minusculas para que se pueda acertar sin tener en cuenta las mayusculas
                        val respuestaUsuario = respuesta.lowercase()
                        val respuestaCorrecta = preguntasRespuestas[listaPreguntas[numPartida]]?.lowercase()

                        //si la respuesta coincide con el numero de la pregunta dentro de la lista saca mensaje de correcto, aumenta la puntuacion y elimina la pregunta ya mostrada.
                        if (respuestaUsuario == respuestaCorrecta) {
                            preguntasRespuestas.remove(listaPreguntas[numPartida])
                            Toast.makeText(context, "CORRECTO. Has acertado", Toast.LENGTH_SHORT).show()
                            puntuacion++
                        } else {
                            //si no es correcta muestro el mensaje de error con la respuesta correcta y reduce la puntuacion
                            Toast.makeText(context, "ERROR. La respuesta correcta es: ${preguntasRespuestas[listaPreguntas[numPartida]]}", Toast.LENGTH_SHORT).show()
                            puntuacion--
                        }

                        //cuando se pulsa el boton pasa a la siguiente pregunta y pone el edittext vacio
                        numPartida++
                        respuesta = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Validar")
                }
            } else {

            }
        }
    }
}

//todo lo comentado aqui seria para hacerlo a traves del metodo fun preguntasAleatorias() el cual directamente aleatoriza un HashMap
/*
fun preguntasAleatorias(): String {
    return preguntasRespuestas.keys.random()
}

@Composable
fun Greeting() {
    var pregunta by rememberSaveable { mutableStateOf("") }
    var respuesta by rememberSaveable { mutableStateOf("") }
    var puntuacion by rememberSaveable { mutableStateOf(0) }
    val context = LocalContext.current

    Column {
        Text(
            text = "PUNTUACION: $puntuacion",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Image(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.pelis_interrogante),
                contentDescription = "interrogantePeliLogo",
                alignment = Alignment.Center
            )

            Text(
                text = pregunta,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = respuesta,
                onValueChange = { respuesta = it },
                label = { Text(text = "Introduce tu respuesta") },
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Button(
                onClick = {
                    //si la respuesta coincide con la pregunta dentro del hashMap aumenta la puntuacion y vuelve a sacar una pregunta aleatoria
                    if (respuesta == preguntasRespuestas[pregunta]) {
                        Toast.makeText(
                            context,
                            "CORRECTO. Has acertado",
                            Toast.LENGTH_SHORT
                        ).show()
                        puntuacion++
                        preguntasRespuestas.remove(pregunta)
                    } else {
                        Toast.makeText(
                            context,
                            "ERROR. La respuesta correcta es: ${preguntasRespuestas[pregunta]}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    pregunta = preguntasAleatorias()
                    respuesta = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "Validar")
            }
        }
    }
}*/