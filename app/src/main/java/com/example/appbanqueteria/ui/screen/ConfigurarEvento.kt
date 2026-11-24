package com.example.appbanqueteria.ui.screen

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appbanqueteria.data.local.CateringRepository
import com.example.appbanqueteria.data.local.PasteleriaRepository
import com.example.appbanqueteria.ui.viewmodel.ReservacionViewModel
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
    onVerCotizacion: (String, String, String, Int, String, String) -> Unit = { _, _, _, _, _, _ -> },
    reservacionViewModel: ReservacionViewModel = viewModel()
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

    val personasInicial = (kotlin.math.round(minPersonas / 10f) * 10f).coerceIn(10f, 100f)

    var tipoCotizacionSeleccionado by remember { mutableStateOf("Persona natural") }
    var fechaEvento by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var horaSeleccionada by remember { mutableStateOf("13:00") }
    var cantidadPersonas by remember { mutableFloatStateOf(personasInicial) }
    var observaciones by remember { mutableStateOf("") }

    // Lista de horas ya reservadas obtenida desde Spring Boot vía ViewModel
    val horasOcupadas by reservacionViewModel.horasOcupadas.collectAsState()

    val tiposCotizacion = listOf("Persona natural", "Empresa")
    val horarios = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00")
    val restricciones = listOf("Vegetariano", "Vegano", "Sin gluten / Celiaquía", "Sin lácteos", "Sin mariscos", "Sin frutos secos", "Menú halal", "Bajo sodio (hipertenso)")
    val restriccionesSeleccionadas = remember { mutableStateListOf<String>() }

    // Consulta en el servidor cada vez que cambia la fecha seleccionada
    LaunchedEffect(fechaEvento) {
        if (fechaEvento.isNotBlank()) {
            val fechaFormatoBD = fechaEvento.replace("/", "-")
            reservacionViewModel.cargarHorasOcupadasPorFecha(fechaFormatoBD)
        } else {
            reservacionViewModel.cargarHorasOcupadasPorFecha("")
        }
    }

    // Si la hora seleccionada está ocupada al llegar la respuesta del servidor, elige la primera libre
    LaunchedEffect(horasOcupadas) {
        if (horaSeleccionada in horasOcupadas) {
            horaSeleccionada = horarios.firstOrNull { it !in horasOcupadas } ?: ""
        }
    }

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

            // Tipo de Cotización
            Column {
                Text("TIPO DE COTIZACIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tiposCotizacion.forEach { tipo ->
                        val selected = tipo == tipoCotizacionSeleccionado
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) Color(0xFF7A1C3B) else Color.White)
                                .border(
                                    1.dp,
                                    if (selected) Color(0xFF7A1C3B) else Color(0xFFE0E0E0),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { tipoCotizacionSeleccionado = tipo }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tipo,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selected) Color.White else Color.Black
                            )
                        }
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

            // Horario (bloquea visualmente las horas ya reservadas en esa fecha)
            Column {
                Text("HORARIO DE INICIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    horarios.forEach { hora ->
                        val estaOcupada = hora in horasOcupadas
                        val selected = !estaOcupada && (hora == horaSeleccionada)

                        val fondoChip = when {
                            estaOcupada -> Color(0xFFEAEAEA)
                            selected -> Color(0xFF7A1C3B)
                            else -> Color.White
                        }

                        val bordeChip = when {
                            estaOcupada -> Color(0xFFD0D0D0)
                            selected -> Color(0xFF7A1C3B)
                            else -> Color(0xFFE0E0E0)
                        }

                        val colorTexto = when {
                            estaOcupada -> Color.Gray
                            selected -> Color.White
                            else -> Color.Black
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(fondoChip)
                                .border(1.dp, bordeChip, RoundedCornerShape(10.dp))
                                .clickable(enabled = !estaOcupada) {
                                    horaSeleccionada = hora
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (estaOcupada) "$hora (Reservado)" else hora,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorTexto
                            )
                        }
                    }
                }
            }

            // Cantidad de personas (intervalos de 10 en 10, de 10 a 100)
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
                    onValueChange = { valor ->
                        cantidadPersonas = (kotlin.math.round(valor / 10f) * 10f)
                    },
                    valueRange = 10f..100f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF7A1C3B),
                        activeTrackColor = Color(0xFF7A1C3B)
                    )
                )
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
                    if (horaSeleccionada.isBlank() || horaSeleccionada in horasOcupadas) {
                        Toast.makeText(context, "Selecciona un horario disponible para esta fecha", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    vibrar(context, 300)
                    val fechaValida = fechaEvento.replace("/", "-")
                    val idFinal = servicioId ?: "1"

                    val restriccionesTexto = if (restriccionesSeleccionadas.isNotEmpty()) {
                        "Alérgenos/Restricciones: ${restriccionesSeleccionadas.joinToString(", ")}"
                    } else ""
                    val notasTexto = observaciones.trim()
                    val observacionesCompletas = listOf(restriccionesTexto, notasTexto)
                        .filter { it.isNotBlank() }
                        .joinToString(" | ")

                    onVerCotizacion(
                        idFinal,
                        fechaValida,
                        horaSeleccionada,
                        cantidadPersonas.toInt(),
                        tipoCotizacionSeleccionado,
                        observacionesCompletas
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