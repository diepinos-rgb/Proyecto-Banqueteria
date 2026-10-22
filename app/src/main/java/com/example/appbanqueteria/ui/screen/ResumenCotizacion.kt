package com.example.appbanqueteria.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appbanqueteria.data.local.CateringRepository
import com.example.appbanqueteria.data.local.PasteleriaRepository

@Composable
fun ResumenCotizacion(
    servicioId: String?,
    fecha: String,
    hora: String,
    personas: Int,
    onBack: () -> Unit = {},
    onConfirmarExito: () -> Unit = {}
) {
    val context = LocalContext.current

    // Búsqueda del servicio en Catering o Pastelería
    val itemCatering = CateringRepository.getCateringItems().find { it.id == servicioId }
    val itemPasteleria = PasteleriaRepository.getPasteleriaItems().find { it.id.toString() == servicioId }

    // Obtención dinámica de título y precio unitario
    val tituloServicio = itemCatering?.titulo
        ?: itemPasteleria?.title
        ?: "Servicio Paolicias.Sweet"

    val precioUnitario = itemCatering?.precioPorPersona
        ?: itemPasteleria?.pricePerPerson
        ?: 0

    val totalEstimado = precioUnitario * personas

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
    ) {
        // Barra Superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text("Resumen de Cotización", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(tituloServicio, fontSize = 12.sp, color = Color.Gray)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Detalles del Evento
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Detalles del Evento",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7A1C3B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetalleFila("Servicio:", tituloServicio)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    DetalleFila("Fecha:", if (fecha.isBlank()) "Por confirmar" else fecha)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    DetalleFila("Horario:", hora)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    DetalleFila("Invitados:", "$personas personas")
                }
            }

            // Tarjeta de Desglose de Costos
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Desglose Estimado",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7A1C3B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetalleFila("Valor por persona:", "$${"$precioUnitario".reversed().chunked(3).joinToString(".").reversed()} CLP")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    DetalleFila("Cantidad de personas:", "x $personas")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Estimado:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "$${"$totalEstimado".reversed().chunked(3).joinToString(".").reversed()} CLP",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A1C3B)
                        )
                    }
                }
            }

            // Nota Aclaratoria
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Esta cotización es referencial. Un ejecutivo de Paolicias.Sweet te contactará para confirmar disponibilidad y agenda.",
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Confirmar Reserva
            Button(
                onClick = {
                    Toast.makeText(context, "¡Solicitud enviada con éxito!", Toast.LENGTH_LONG).show()
                    onConfirmarExito()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A1C3B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Confirmar y Enviar Solicitud ✓", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun DetalleFila(titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = titulo, color = Color.Gray, fontSize = 14.sp)
        Text(text = valor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}