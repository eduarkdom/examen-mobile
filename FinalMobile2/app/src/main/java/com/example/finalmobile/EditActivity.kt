package com.example.finalmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalmobile.db.AppDatabase
import com.example.finalmobile.db.Lugar
import com.example.finalmobile.db.LugarDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lugarId = intent.getIntExtra("LUGAR_ID", -1)
        val dao = AppDatabase.getInstance(this).lugarDao()
        val lugarToEdit = dao.getById(lugarId)

        lugarToEdit?.let { lugar ->
            setContent {
                EditFormUI(
                    lugar = lugar,
                    lugarDao = dao,
                    finishActivity = { finish() }
                )
            }
        } ?: finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFormUI(
    lugar: Lugar,
    lugarDao: LugarDao,
    finishActivity: () -> Unit
) {
    var placeName by remember { mutableStateOf(lugar.nombre) }
    var imageRef by remember { mutableStateOf(lugar.imagenReferencial) }
    var latLong by remember { mutableStateOf(lugar.latitud) }
    var orderText by remember { mutableStateOf(lugar.orden.toString()) }
    var lodgingCostText by remember { mutableStateOf(lugar.costoNoche.toString()) }
    var travelCostText by remember { mutableStateOf(lugar.costoTraslado.toString()) }
    var comments by remember { mutableStateOf(lugar.comentarios) }
    // Convertir los campos de texto a enteros cuando sea necesario
    var order by remember { mutableStateOf(0) }
    var lodgingCost by remember { mutableStateOf(0) }
    var travelCost by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textFieldModifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)

        Text(
            text = "Lugar",
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
        TextField(
            value = placeName,
            onValueChange = { placeName = it },
            label = { Text("Lugar") },
            modifier = textFieldModifier,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        Text(
            text = "Imagen Ref.",
            textAlign = TextAlign.Center,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )
        TextField(
            value = imageRef,
            onValueChange = { imageRef = it },
            label = { Text("Imagen Referencial") },
            modifier = textFieldModifier,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        Text(
            text = "Lat, Long",
            textAlign = TextAlign.Center,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )
        TextField(
            value = latLong,
            onValueChange = { latLong = it },
            label = { Text("Lat, Long") },
            modifier = textFieldModifier,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        Text(
            text = "Orden",
            textAlign = TextAlign.Center,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )
        TextField(
            value = orderText,
            onValueChange = {
                orderText = it
                order = it.toIntOrNull() ?: 0
            },
            label = { Text("Orden") },
            modifier = textFieldModifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next)
        )

        Text(
            text = "Costo Alojamiento",
            textAlign = TextAlign.Center,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )
        TextField(
            value = lodgingCostText,
            onValueChange = {
                lodgingCostText = it
                lodgingCost = it.toIntOrNull() ?: 0
            },
            label = { Text("Costo Alojamiento") },
            modifier = textFieldModifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next)
        )

        Text(
            text = "Costo Traslados",
            textAlign = TextAlign.Center,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )
        TextField(
            value = travelCostText,
            onValueChange = {
                travelCostText = it
                travelCost = it.toIntOrNull() ?: 0
            },
            label = { Text("Costo Traslados") },
            modifier = textFieldModifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next)
        )

        Text(
            text = "Comentarios",
            textAlign = TextAlign.Center,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )
        TextField(
            value = comments,
            onValueChange = { comments = it },
            label = { Text("Comentarios") },
            modifier = textFieldModifier,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.weight(1f))
        EditFormButtonSavePlace(
            modifier = Modifier.padding(bottom = 30.dp),
            placeName = placeName,
            imageRef = imageRef,
            latLong = latLong,
            order = order,
            lodgingCost = lodgingCost,
            travelCost = travelCost,
            comments = comments,
            lugar = lugar,
            lugarDao = lugarDao,
            finishActivity = finishActivity
        )
    }
}

@Composable
fun EditFormButtonSavePlace(
    modifier: Modifier = Modifier,
    placeName: String,
    imageRef: String,
    latLong: String,
    order: Int,
    lodgingCost: Int,
    travelCost: Int,
    comments: String,
    lugar: Lugar,
    lugarDao: LugarDao,
    finishActivity: () -> Unit
) {
    val contexto = LocalContext.current
    val alcanceCorrutina = rememberCoroutineScope()

    Button(
        onClick = {
            lugar.nombre = placeName
            lugar.imagenReferencial = imageRef
            lugar.latitud = latLong
            lugar.orden = order
            lugar.costoNoche = lodgingCost
            lugar.costoTraslado = travelCost
            lugar.comentarios = comments

            alcanceCorrutina.launch(Dispatchers.IO) {
                lugarDao.actualizar(lugar)
                finishActivity()
            }
        },
        modifier = modifier
    ) {
        Text(contexto.getString(R.string.guardar))
    }
}