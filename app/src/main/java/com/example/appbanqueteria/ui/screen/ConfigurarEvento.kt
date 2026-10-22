package com.example.appbanqueteria.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.appbanqueteria.data.local.CateringRepository
import com.example.appbanqueteria.data.local.PasteleriaRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConfigurarEvento(
    servicioId: String?,
    onBack: () -> Unit = {},
    onVerCotizacion: (String, String, String, Int) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current

    val itemCatering = CateringRepository.getCateringItems().find { it.id == servicioId }
    val itemPasteleria = PasteleriaRepository.getPasteleriaItems().find { it.id.toString() == servicioId }

    val tituloServicio = itemCatering?.titulo
        ?: itemPasteleria?.title
        ?: "Configuración de Evento"

    val rangoTexto = itemCatering?.rangoPersonas
        ?: itemPasteleria?.let { "${it.minPersons}–${it.maxPersons} personas" }
        ?: "10–100 personas"

    val minPersonas = itemCatering?.minPersonas
        ?: itemPasteleria?.minPersons?.toFloat()
        ?: 10f

    val maxPersonas = itemCatering?.maxPersonas
        ?: itemPasteleria?.maxPersons?.toFloat()
        ?: 100f

    var fechaEvento by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var horaSeleccionada by remember { mutableStateOf("13:00") }
    var cantidadPersonas by remember { mutableFloatStateOf(minPersonas) }
    var observaciones by remember { mutableStateOf("") }

    // --- RECURSO NATIVO: CÁMARA Y PERMISOS ---
    var fotoCamaraBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 1. Launcher para capturar la foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            fotoCamaraBitmap = bitmap
            vibrar(context, 150)
            Toast.makeText(context, "Foto tomada con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Launcher para solicitar el permiso de cámara al usuario
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al abrir la cámara", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Debes aceptar el permiso para usar la cámara", Toast.LENGTH_LONG).show()
        }
    }

    val horarios = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00")
    val restricciones = listOf("Vegetariano", "Vegano", "Sin gluten / Celiaquía", "Sin lácteos", "Sin mariscos", "Sin frutos secos", "Menú halal", "Bajo sodio (hipertenso)")
    val restriccionesSeleccionadas = remember { mutableStateListOf<String>() }

    // --- DIÁLOGO DE CALENDARIO COMPATIBLE DESDE API 23 ---
    if (mostrarDatePicker) {
        val hoyMillis = remember {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        }

        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= hoyMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            fechaEvento = sdf.format(Date(selectedMillis))
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = Color(0xFF7A1C3B), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF7A1C3B),
                    todayDateBorderColor = Color(0xFF7A1C3B),
                    todayContentColor = Color(0xFF7A1C3B)
                )
            )
        }
    }

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
                Text("Configurar evento", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(tituloServicio, fontSize = 12.sp, color = Color.Gray)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF4EBE2)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(tituloServicio, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(rangoTexto, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // Fecha con Selector de Calendario
            Column {
                Text("FECHA DEL EVENTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Box {
                    OutlinedTextField(
                        value = fechaEvento,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Seleccionar fecha (dd/mm/aaaa)") },
                        trailingIcon = { Text("📅", fontSize = 16.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { mostrarDatePicker = true }
                    )
                }
            }

            // Horario
            Column {
                Text("HORARIO DE INICIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    horarios.forEach { hora ->
                        val selected = hora == horaSeleccionada
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) Color(0xFF7A1C3B) else Color.White)
                                .border(1.dp, if (selected) Color(0xFF7A1C3B) else Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                                .clickable { horaSeleccionada = hora }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = hora,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            // Cantidad de personas
            Column {
                Text("NÚMERO DE PERSONAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("${cantidadPersonas.toInt()}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7A1C3B))
                    Text("personas", fontSize = 14.sp, color = Color.Gray)
                }
                Slider(
                    value = cantidadPersonas,
                    onValueChange = { cantidadPersonas = it },
                    valueRange = minPersonas..maxPersonas,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF7A1C3B), activeTrackColor = Color(0xFF7A1C3B))
                )
            }

            // --- BOTÓN CÁMARA NATIVA CON VERIFICACIÓN DE PERMISOS ---
            Column {
                Text("FOTO DE REFERENCIA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))

                if (fotoCamaraBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF7A1C3B), RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            bitmap = fotoCamaraBitmap!!.asImageBitmap(),
                            contentDescription = "Foto capturada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { fotoCamaraBitmap = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        ) {
                            Text("✕", color = Color.White, fontSize = 12.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            val estadoPermiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (estadoPermiso == PackageManager.PERMISSION_GRANTED) {
                                try {
                                    cameraLauncher.launch(null)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al abrir cámara en el emulador", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Abrir Cámara", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Restricciones Alimentarias
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("RESTRICCIONES ALIMENTARIAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    restricciones.forEach { item ->
                        val isChecked = restriccionesSeleccionadas.contains(item)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                if (isChecked) restriccionesSeleccionadas.remove(item)
                                else restriccionesSeleccionadas.add(item)
                            },
                            label = { Text(item, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF7A1C3B),
                                selectedLabelColor = Color.White,
                                containerColor = Color.White
                            )
                        )
                    }
                }
            }

            // Observaciones
            Column {
                Text("OBSERVACIONES ESPECIALES (OPCIONAL)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    placeholder = { Text("Ej: decoración personalizada...", fontSize = 13.sp, color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                )
            }

            // Botón Ver Cotización
            Button(
                onClick = {
                    if (fechaEvento.isBlank()) {
                        Toast.makeText(context, "Por favor selecciona una fecha para el evento", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    vibrar(context, 300)
                    val fechaValida = fechaEvento.replace("/", "-")
                    val idFinal = servicioId ?: "1"
                    onVerCotizacion(
                        idFinal,
                        fechaValida,
                        horaSeleccionada,
                        cantidadPersonas.toInt()
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A1C3B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Ver cotización ➔", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

fun vibrar(context: Context, milisegundos: Long) {
    val vibrador = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrador.vibrate(VibrationEffect.createOneShot(milisegundos, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrador.vibrate(milisegundos)
    }
}