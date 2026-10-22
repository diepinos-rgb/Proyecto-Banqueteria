package com.example.appbanqueteria.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appbanqueteria.data.local.PasteleriaRepository
import com.example.appbanqueteria.model.PasteleriaItem

// Colores del tema
val ColorGuinda = Color(0xFF6B1D35)
val ColorDorado = Color(0xFFC7852B)
val ColorFondoCrema = Color(0xFFF9F5F1)
val ColorEtiquetaBg = Color(0xFFEFE6E2)

@Composable
fun PasteleriaScreen(
    userName: String = "Diego",
    onCategoryChange: (String) -> Unit = {},
    onCotizarClick: (PasteleriaItem) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("Pastelería") }
    val items = remember { PasteleriaRepository.getPasteleriaItems() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondoCrema)
    ) {
        // --- HEADER SUPERIOR GUINDA ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorGuinda)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Paolicias.Sweet",
                        color = Color(0xFFE89A3C),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hola, $userName",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Text("Salir", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Categorías (Catering / Pastelería)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        selectedCategory = "Catering"
                        onCategoryChange("Catering")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory == "Catering") Color.White else Color.White.copy(alpha = 0.2f),
                        contentColor = if (selectedCategory == "Catering") ColorGuinda else Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("⚔Catering", fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = {
                        selectedCategory = "Pastelería"
                        onCategoryChange("Pastelería")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory == "Pastelería") Color.White else Color.White.copy(alpha = 0.2f),
                        contentColor = if (selectedCategory == "Pastelería") ColorGuinda else Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Pastelería", fontWeight = FontWeight.Medium)
                }
            }
        }

        // --- SUBTÍTULO DE SERVICIOS DISPONIBLES ---
        Text(
            text = "3 servicios disponibles · máx. 100 personas",
            color = Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 12.dp)
        )

        // --- LISTA DE TARJETAS DE PASTELERÍA ---
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                PasteleriaCard(item = item, onCotizarClick = { onCotizarClick(item) })
            }
        }
    }
}

@Composable
fun PasteleriaCard(
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
            // IMAGEN CON BADGES EN SUPERPOSICIÓN
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

                // Badge Categoria (Arriba Izquierda)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Pastelería",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Badge Precio (Abajo Derecha)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ColorDorado,
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

            // DETALLES DEL PRODUCTO
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2424)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ETIQUETAS / TAGS
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ColorEtiquetaBg
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = Color(0xFF6B5A55),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ALÉRGENOS
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Alérgenos: ${item.allergens}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // RANGO PERSONAS Y BOTÓN COTIZAR
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
                        colors = ButtonDefaults.buttonColors(containerColor = ColorGuinda),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Cotizar", fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("→", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}