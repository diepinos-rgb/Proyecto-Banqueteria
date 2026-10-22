package com.example.appbanqueteria.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appbanqueteria.data.local.CateringRepository
import com.example.appbanqueteria.data.local.PasteleriaRepository
import com.example.appbanqueteria.model.PasteleriaItem
import com.example.appbanqueteria.model.Servicio

val BurgundyHeader = Color(0xFF7A1C3B)
val GoldBadge = Color(0xFFC7922E)
val LightBg = Color(0xFFF9F6F0)

@Composable
fun Principal(
    nombreUsuario: String = "Usuario",
    onNavigateToConfigurar: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var categoriaSeleccionada by remember { mutableStateOf("Catering") }

    val cateringItems = remember { CateringRepository.getCateringItems() }
    val pasteleriaItems = remember { PasteleriaRepository.getPasteleriaItems() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        // Cabecera superior institucional
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BurgundyHeader)
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)) {
                                append("Paolicias")
                            }
                            withStyle(SpanStyle(color = Color(0xFFE5A93C), fontWeight = FontWeight.Bold, fontSize = 22.sp)) {
                                append(".Sweet")
                            }
                        }
                    )
                    Text(
                        text = "Hola, $nombreUsuario",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = "Salir",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtros Catering / Pastelería
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { categoriaSeleccionada = "Catering" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (categoriaSeleccionada == "Catering") Color.White else BurgundyHeader.copy(alpha = 0.5f),
                        contentColor = if (categoriaSeleccionada == "Catering") BurgundyHeader else Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Catering", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { categoriaSeleccionada = "Pastelería" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (categoriaSeleccionada == "Pastelería") Color.White else BurgundyHeader.copy(alpha = 0.5f),
                        contentColor = if (categoriaSeleccionada == "Pastelería") BurgundyHeader else Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Pastelería", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Subtítulo con total de servicios según la categoría activa
        val cantidadServicios = if (categoriaSeleccionada == "Catering") cateringItems.size else pasteleriaItems.size
        Text(
            text = "$cantidadServicios servicios disponibles · máx. 100 personas",
            color = Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )

        // Lista de tarjetas según categoría
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (categoriaSeleccionada == "Catering") {
                items(cateringItems) { servicio ->
                    ItemCateringCard(
                        servicio = servicio,
                        onCotizarClick = { onNavigateToConfigurar(servicio.id) }
                    )
                }
            } else {
                items(pasteleriaItems) { item ->
                    ItemPasteleriaCard(
                        item = item,
                        onCotizarClick = { onNavigateToConfigurar(item.id.toString()) }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCateringCard(
    servicio: Servicio,
    onCotizarClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = servicio.imagenRes),
                    contentDescription = servicio.titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Catering",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = GoldBadge,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = "desde \$${servicio.precioPorPersona}/pers.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = servicio.titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = servicio.descripcion,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(servicio.etiquetas) { tag ->
                        Surface(
                            color = Color(0xFFF2ECE4),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = servicio.rangoPersonas,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Button(
                        onClick = onCotizarClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyHeader),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Cotizar ➔", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemPasteleriaCard(
    item: PasteleriaItem,
    onCotizarClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Pastelería",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = GoldBadge,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = "desde \$${item.pricePerPerson}/pers.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(item.tags) { tag ->
                        Surface(
                            color = Color(0xFFF2ECE4),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.minPersons}–${item.maxPersons} personas",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Button(
                        onClick = onCotizarClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyHeader),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Cotizar ➔", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}