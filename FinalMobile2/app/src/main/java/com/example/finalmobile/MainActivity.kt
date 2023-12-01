package com.example.finalmobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.finalmobile.db.AppDatabase
import com.example.finalmobile.db.Lugar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            val lugarDao = AppDatabase.getInstance(this@MainActivity).lugarDao()
            val cantRegistros = lugarDao.contar()

        }

        setContent {
            PlacesListUI()
        }
    }
}

@Composable
fun PlacesListUI() {
    val contexto = LocalContext.current
    val (lugares, setLugares) = remember { mutableStateOf(emptyList<Lugar>()) }
    var selectedLugar by remember { mutableStateOf<Lugar?>(null) }

    LaunchedEffect(lugares) {
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(contexto).lugarDao()
            setLugares(dao.getAll())
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(lugares) { lugar ->
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                LugarItemUI(lugar = lugar, onSave = { /* Acción onSave */ }) {
                    selectedLugar = lugar
                }
            }
        }
    }

    if (selectedLugar != null) {
        DetalleLugarUI(
            lugar = selectedLugar!!,
            onSave = {
                selectedLugar = null
            },
            onBack = {
                selectedLugar = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddPlacesButton()
    }
}

@Composable
fun LugarItemUI(lugar: Lugar, onSave: () -> Unit, onLocationClick: () -> Unit) {
    val contexto = LocalContext.current
    val alcanceCorrutina = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .background(Color(240, 240, 240, 255))
            .padding(vertical = 5.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .verticalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna izquierda - Imagen
        Box(
            modifier = Modifier
                .weight(0.45f),

            ) {
            AsyncImage(model = lugar.imagenReferencial, contentDescription = lugar.nombre )
        }

        // Columna derecha - Textos
        Column(
            modifier = Modifier
                .weight(0.55f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = lugar.nombre,
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.padding(vertical = 5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = contexto.getString(R.string.costoxnoche),
                        textAlign = TextAlign.Start,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(text = "$${lugar.costoNoche}")
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = contexto.getString(R.string.costotraslado),
                        textAlign = TextAlign.Start,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(text = "$${lugar.costoTraslado}")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_view_location),
                    contentDescription = "Icono de Ubicación",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onLocationClick()
                        },
                    tint = Color.Unspecified
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit_place),
                    contentDescription = "Icono de Edicion",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            val intent = Intent(contexto, EditActivity::class.java)
                            intent.putExtra("LUGAR_ID", lugar.id)
                            contexto.startActivity(intent)
                        },
                    tint = Color.Unspecified
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_delete_place),
                    contentDescription = "Icono de Eliminar",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            alcanceCorrutina.launch(Dispatchers.IO) {
                                val dao = AppDatabase.getInstance(contexto).lugarDao()
                                dao.eliminar(lugar)
                                onSave()
                            }

                        },
                    tint = Color.Unspecified
                )
            }
        }
    }
}


@Composable
fun AddPlacesButton() {
    val contexto = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(contexto, FormActivity::class.java)
            contexto.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = "Plus Icon",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = contexto.getString(R.string.agregar_lugar),
                color = Color.Black,
                style = TextStyle(
                    fontSize = 18.sp
                )
            )
        }
    }
}

fun tomarFoto(context: Context) {
    val REQUEST_IMAGE_CAPTURE = 1

    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (takePictureIntent.resolveActivity(context.packageManager) != null) {
        (context as Activity).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }
}

@Composable
fun DetalleLugarUI(lugar: Lugar, onSave: () -> Unit, onBack: () -> Unit) {
    val contexto = LocalContext.current
    val alcanceCorrutina = rememberCoroutineScope()
    val imagenReferencial = lugar.imagenReferencial
    val costoNoche = lugar.costoNoche
    val costoTraslado = lugar.costoTraslado
    val comentarios = lugar.comentarios
    val latitud = lugar.latitud

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {

            AsyncImage(model = imagenReferencial, contentDescription = "Imagen referencial")
        }

        Text(text = "Costo por Noche: $$costoNoche")
        Text(text = "Costo de Traslado: $$costoTraslado")
        Text(text = "Comentarios: $comentarios")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "Icono de camara",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        tomarFoto(contexto)
                    },
                tint = Color.Unspecified
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_edit_place),
                contentDescription = "Icono de Edicion",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val intent = Intent(contexto, EditActivity::class.java)
                        intent.putExtra("LUGAR_ID", lugar.id)
                        contexto.startActivity(intent)
                    },
                tint = Color.Unspecified
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_delete_place),
                contentDescription = "Icono de Eliminar",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        alcanceCorrutina.launch(Dispatchers.IO) {
                            val dao = AppDatabase.getInstance(contexto).lugarDao()
                            dao.eliminar(lugar)
                            onSave()
                        }

                    },
                tint = Color.Unspecified
            )
        }

        Button(
            onClick = onBack, // Llamar a la función onBack al hacer clic en el botón
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
        ) {
            Text("Volver a la pantalla principal")
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.LightGray)
        ) {
            AndroidView(
                factory = { context ->
                    val mapView = MapView(context).apply {
                        onCreate(null)
                        getMapAsync { googleMap ->
                            val latitud = lugar.latitud.toDouble()
                            val longitud = lugar.latitud.toDouble()
                            val ubicacion = LatLng(latitud, longitud)
                            googleMap.addMarker(MarkerOptions().position(ubicacion).title("Marcador"))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15f))
                        }
                    }
                    mapView
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun DetalleItem(texto: String, valor: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(texto, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(valor)
    }
}