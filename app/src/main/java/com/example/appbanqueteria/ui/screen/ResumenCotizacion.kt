package com.example.appbanqueteria.ui.screen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appbanqueteria.data.local.CateringRepository
import com.example.appbanqueteria.data.local.PasteleriaRepository
import com.example.appbanqueteria.data.local.entity.ReservacionEntity
import com.example.appbanqueteria.ui.viewmodel.ReservacionViewModel
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PORCENTAJE_GASTO_INSUMOS = 0.25
private const val MINIMO_TRABAJADORES = 2
private const val PERSONAS_POR_TRABAJADOR_EXTRA = 10

@Composable
fun ResumenCotizacion(
    servicioId: String?,
    fecha: String,
    hora: String,
    personas: Int,
    tipoCliente: String = "Persona natural",
    observaciones: String = "",
    nombreCliente: String = "",
    correoCliente: String = "",
    telefonoCliente: String = "",
    onBack: () -> Unit = {},
    onConfirmarExito: () -> Unit = {},
    reservacionViewModel: ReservacionViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cargando by reservacionViewModel.cargando.collectAsState()

    val opcionesAbono = listOf("50%", "100%")
    var porcentajeAbonoSeleccionado by remember { mutableStateOf("50%") }
    var fotoComprobante by remember { mutableStateOf<Bitmap?>(null) }
    var comprobanteEnviado by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoComprobante = bitmap
            comprobanteEnviado = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(
                context,
                "Se requiere permiso de cámara para tomar la foto",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun abrirCamara() {
        try {
            cameraLauncher.launch(null)
        } catch (_: SecurityException) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val itemCatering = CateringRepository.getCateringItems().find { it.id == servicioId }
    val itemPasteleria = PasteleriaRepository.getPasteleriaItems().find { it.id.toString() == servicioId }

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
            // Tarjeta de Datos de Contacto del Cliente
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Datos de Contacto",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7A1C3B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val tipoClienteTexto = if (tipoCliente.contains("natural", ignoreCase = true)) {
                        "Natural"
                    } else {
                        tipoCliente.ifBlank { "Natural" }
                    }

                    DetalleFila("Tipo cliente:", tipoClienteTexto)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    DetalleFila("Cliente:", nombreCliente.ifBlank { "No especificado" })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    DetalleFila("Correo:", correoCliente.ifBlank { "No especificado" })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

                    DetalleFila("Teléfono:", telefonoCliente.ifBlank { "No especificado" })
                }
            }

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

                    if (observaciones.isNotBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))
                        DetalleFila("Observaciones:", observaciones)
                    }
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

                    DetalleFila(
                        "Valor por persona:",
                        "$${"$precioUnitario".reversed().chunked(3).joinToString(".").reversed()} CLP"
                    )
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
                        text = "Para finalizar su solicitud debe realizar un abono del 50% o 100% del total y adjunta aquí tu comprobante de pago.",
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037)
                    )
                }
            }

            // Selector de monto a abonar (50% o 100%)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Monto a abonar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7A1C3B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        opcionesAbono.forEach { opcion ->
                            val selected = opcion == porcentajeAbonoSeleccionado
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) Color(0xFF7A1C3B) else Color.White)
                                    .border(
                                        1.dp,
                                        if (selected) Color(0xFF7A1C3B) else Color(0xFFE0E0E0),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { porcentajeAbonoSeleccionado = opcion }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = opcion,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selected) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // Tarjeta para tomar y enviar foto del comprobante del abono de pago
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enviar foto del comprobante pago",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7A1C3B),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (fotoComprobante != null) {
                        Image(
                            bitmap = fotoComprobante!!.asImageBitmap(),
                            contentDescription = "Foto del comprobante",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (comprobanteEnviado) {
                            Text(
                                text = "✓ Foto del comprobante adjuntada",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { abrirCamara() },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Volver a tomar foto", color = Color(0xFF7A1C3B))
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { abrirCamara() },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tomar otra", color = Color(0xFF7A1C3B))
                                }

                                Button(
                                    onClick = {
                                        comprobanteEnviado = true
                                        Toast.makeText(
                                            context,
                                            "Fotografia adjuntada correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A1C3B)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Adjuntar", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { abrirCamara() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Tomar Foto",
                                color = Color(0xFF7A1C3B),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Confirmar Reserva y Enviar por Retrofit a Spring Boot
            Button(
                onClick = {
                    scope.launch {
                        val porcentajeNumerico = if (porcentajeAbonoSeleccionado == "100%") 100 else 50
                        val insumosEstimados = (totalEstimado * PORCENTAJE_GASTO_INSUMOS).toInt()
                        val trabajadoresCalculados =
                            MINIMO_TRABAJADORES + (personas / PERSONAS_POR_TRABAJADOR_EXTRA)

                        val rutaFoto = fotoComprobante?.let { bitmap ->
                            withContext(Dispatchers.IO) {
                                guardarComprobanteEnAlmacenamiento(context, bitmap)
                            }
                        } ?: ""

                        val nuevaReservacion = ReservacionEntity(
                            tipoCliente = tipoCliente.ifBlank { "Persona natural" },
                            nombreCliente = nombreCliente,
                            correoCliente = correoCliente,
                            telefonoCliente = telefonoCliente,
                            servicioReservado = tituloServicio,
                            fecha = if (fecha.isBlank()) "Por confirmar" else fecha,
                            hora = hora,
                            personas = personas,
                            totalEstimado = totalEstimado,
                            porcentajeAbono = porcentajeNumerico,
                            observaciones = observaciones.trim(),
                            costoInsumos = insumosEstimados,
                            trabajadoresNecesarios = trabajadoresCalculados,
                            rutaComprobante = rutaFoto
                        )

                        reservacionViewModel.crearReservacion(
                            reservacion = nuevaReservacion,
                            onExito = { idCorrelativo ->
                                Toast.makeText(
                                    context,
                                    "¡Reservación #$idCorrelativo enviada con éxito!",
                                    Toast.LENGTH_LONG
                                ).show()
                                onConfirmarExito()
                            },
                            onError = { mensajeError ->
                                Toast.makeText(
                                    context,
                                    mensajeError,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                },
                enabled = comprobanteEnviado && !cargando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7A1C3B),
                    disabledContainerColor = Color(0xFFBDBDBD),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFF616161)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text("Confirmar Cotizacion✓", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (!comprobanteEnviado) {
                Text(
                    text = "Debes tomar y enviar la foto del comprobante para confirmar.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun guardarComprobanteEnAlmacenamiento(context: Context, bitmap: Bitmap): String {
    return try {
        val nombreArchivo = "comprobante_${System.currentTimeMillis()}.jpg"
        val archivo = File(context.filesDir, nombreArchivo)
        FileOutputStream(archivo).use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, salida)
        }
        archivo.absolutePath
    } catch (_: Exception) {
        ""
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