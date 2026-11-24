package com.example.appbanqueteria.ui.screen

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appbanqueteria.data.local.entity.ReservacionEntity
import com.example.appbanqueteria.ui.viewmodel.ReservacionViewModel
import java.io.File

@Composable
fun PanelAdmin(
    onLogout: () -> Unit = {},
    reservacionViewModel: ReservacionViewModel = viewModel()
) {
    val context = LocalContext.current

    // Estados observados desde el ViewModel conectado a Spring Boot
    val reservaciones by reservacionViewModel.reservaciones.collectAsState()
    val cargando by reservacionViewModel.cargando.collectAsState()
    val error by reservacionViewModel.error.collectAsState()

    // Carga inicial al abrir el panel
    LaunchedEffect(Unit) {
        reservacionViewModel.cargarReservaciones()
    }

    val colorGuinda = Color(0xFF7A1C3B)
    val totalIngresos = reservaciones.sumOf { it.totalEstimado }

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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Panel de Administrador",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorGuinda
                )
                Text(
                    text = "Gestión de Cotizaciones y Reservas",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { reservacionViewModel.cargarReservaciones() },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "↻",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorGuinda
                    )
                }

                OutlinedButton(
                    onClick = onLogout,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Salir",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorGuinda
                    )
                }
            }
        }

        // Tarjeta de Resumen General
        Card(
            colors = CardDefaults.cardColors(containerColor = colorGuinda),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL RESERVAS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF4EBE2)
                    )
                    Text(
                        text = "${reservaciones.size}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "MONTO ESTIMADO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF4EBE2)
                    )
                    Text(
                        text = "$${formatearPesos(totalIngresos)} CLP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Estado de carga, error o listado remoto
        when {
            cargando && reservaciones.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorGuinda)
                }
            }

            error != null && reservaciones.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = error ?: "Error desconocido",
                            fontSize = 14.sp,
                            color = Color(0xFFD32F2F),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { reservacionViewModel.cargarReservaciones() },
                            colors = ButtonDefaults.buttonColors(containerColor = colorGuinda)
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }
            }

            reservaciones.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no hay reservaciones registradas en el servidor.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = reservaciones,
                        key = { it.id }
                    ) { reserva ->
                        TarjetaReservaAdmin(
                            reserva = reserva,
                            colorGuinda = colorGuinda,
                            onLlamarCliente = { telefono ->
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefono"))
                                context.startActivity(intent)
                            },
                            onActualizarParametros = { nuevosTrabajadores, nuevosInsumos ->
                                val reservaActualizada = reserva.copy(
                                    trabajadoresNecesarios = nuevosTrabajadores,
                                    costoInsumos = nuevosInsumos
                                )
                                reservacionViewModel.actualizarReservacion(
                                    reservacion = reservaActualizada,
                                    onExito = {
                                        vibrar(context, 200)
                                        Toast.makeText(
                                            context,
                                            "Parámetros de Reserva #${reserva.id} actualizados",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onError = { mensajeError ->
                                        Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            onEliminarReserva = {
                                reservacionViewModel.eliminarReservacion(
                                    id = reserva.id,
                                    onExito = {
                                        if (reserva.rutaComprobante.isNotBlank()) {
                                            runCatching { File(reserva.rutaComprobante).delete() }
                                        }
                                        vibrar(context, 300)
                                        Toast.makeText(
                                            context,
                                            "Reserva #${reserva.id} eliminada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onError = { mensajeError ->
                                        Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaReservaAdmin(
    reserva: ReservacionEntity,
    colorGuinda: Color,
    onLlamarCliente: (String) -> Unit,
    onActualizarParametros: (Int, Int) -> Unit,
    onEliminarReserva: () -> Unit
) {
    val pagoCompleto = reserva.porcentajeAbono == 100
    val montoAbonado = if (pagoCompleto) reserva.totalEstimado else reserva.totalEstimado / 2
    val saldoRestante = reserva.totalEstimado - montoAbonado
    val etiquetaAbono = if (pagoCompleto) "100% - Total" else "50% - Mitad"

    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var mostrarDialogoComprobante by remember { mutableStateOf(false) }

    val bitmapComprobante = remember(reserva.rutaComprobante) {
        if (reserva.rutaComprobante.isNotBlank()) {
            val archivo = File(reserva.rutaComprobante)
            if (archivo.exists()) BitmapFactory.decodeFile(archivo.absolutePath) else null
        } else {
            null
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Reserva #${reserva.id}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorGuinda
                )

                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFD32F2F))
                            .clickable { mostrarDialogoEliminar = true }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${reserva.fecha} • ${reserva.hora}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = reserva.servicioReservado,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFF0F0F0)
            )

            DetalleFila("Tipo cliente:", reserva.tipoCliente.ifBlank { "Persona natural" })
            Spacer(modifier = Modifier.height(4.dp))
            DetalleFila("Cliente:", reserva.nombreCliente.ifBlank { "No especificado" })
            Spacer(modifier = Modifier.height(4.dp))
            DetalleFila("Correo:", reserva.correoCliente.ifBlank { "No especificado" })
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Teléfono:", color = Color.Gray, fontSize = 14.sp)
                if (reserva.telefonoCliente.isNotBlank()) {
                    Text(
                        text = "${reserva.telefonoCliente} (Llamar)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = colorGuinda,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            onLlamarCliente(reserva.telefonoCliente)
                        }
                    )
                } else {
                    Text(
                        text = "No especificado",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            DetalleFila("Invitados:", "${reserva.personas} personas")

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFF0F0F0)
            )

            Text(
                text = "Alérgenos y observaciones especiales:",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = reserva.observaciones.ifBlank { "Sin observaciones registradas" },
                fontSize = 13.sp,
                fontWeight = if (reserva.observaciones.isNotBlank()) FontWeight.SemiBold else FontWeight.Normal,
                color = if (reserva.observaciones.isNotBlank()) Color(0xFF5D4037) else Color.Gray
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFF0F0F0)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Estimado:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${formatearPesos(reserva.totalEstimado)} CLP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorGuinda
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFF0F0F0)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monto abonado ($etiquetaAbono):",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "$${formatearPesos(montoAbonado)} CLP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saldo pendiente:",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "$${formatearPesos(saldoRestante)} CLP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (saldoRestante == 0) Color(0xFF2E7D32) else Color(0xFFD84315)
                )
            }

            if (bitmapComprobante != null) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { mostrarDialogoComprobante = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Ver comprobante de pago",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorGuinda
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFF0F0F0)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Logística e Insumos",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorGuinda
                )

                TextButton(
                    onClick = { mostrarDialogoEditar = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text(
                        text = "Editar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorGuinda
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            DetalleFila(
                "Trabajadores necesarios:",
                "${reserva.trabajadoresNecesarios} personas"
            )
            Spacer(modifier = Modifier.height(4.dp))
            DetalleFila(
                "Costo en insumos:",
                "$${formatearPesos(reserva.costoInsumos)} CLP"
            )
        }
    }

    if (mostrarDialogoComprobante && bitmapComprobante != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoComprobante = false },
            title = {
                Text(
                    text = "Comprobante Reserva #${reserva.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorGuinda
                )
            },
            text = {
                Image(
                    bitmap = bitmapComprobante.asImageBitmap(),
                    contentDescription = "Comprobante de pago",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoComprobante = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colorGuinda)
                ) {
                    Text("Cerrar", color = Color.White)
                }
            }
        )
    }

    if (mostrarDialogoEditar) {
        var trabajadoresInput by remember { mutableStateOf(reserva.trabajadoresNecesarios.toString()) }
        var insumosInput by remember { mutableStateOf(reserva.costoInsumos.toString()) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoEditar = false },
            title = {
                Text(
                    text = "Editar Reserva #${reserva.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorGuinda
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = trabajadoresInput,
                        onValueChange = { trabajadoresInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Trabajadores necesarios") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = insumosInput,
                        onValueChange = { insumosInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Costo en insumos (CLP)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nuevosTrabajadores = trabajadoresInput.toIntOrNull() ?: reserva.trabajadoresNecesarios
                        val nuevosInsumos = insumosInput.toIntOrNull() ?: reserva.costoInsumos
                        onActualizarParametros(nuevosTrabajadores, nuevosInsumos)
                        mostrarDialogoEditar = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorGuinda)
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEditar = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = {
                Text(
                    text = "Cancelar Reserva #${reserva.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres eliminar esta reserva?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoEliminar = false
                        onEliminarReserva()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Sí", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("No", color = Color.Gray, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

private fun formatearPesos(monto: Int): String {
    return "$monto".reversed().chunked(3).joinToString(".").reversed()
}