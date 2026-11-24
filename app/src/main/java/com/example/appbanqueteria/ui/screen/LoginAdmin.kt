package com.example.appbanqueteria.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appbanqueteria.data.database.AppDatabase
import kotlinx.coroutines.launch

@Composable
fun LoginAdmin(
    onBack: () -> Unit = {},
    onAdminLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val colorGuinda = Color(0xFF7A1C3B)

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorGuinda)
    ) {
        // Barra superior para volver al login de clientes
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
                Text("Acceso Administrador", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Paolicias.Sweet", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Iniciar sesión como Administrador",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorGuinda
                    )

                    // Campo Correo Administrador
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorGuinda,
                            focusedLabelColor = colorGuinda
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo Contraseña Administrador
                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorGuinda,
                            focusedLabelColor = colorGuinda
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Botón Ingresar Administrador validando en SQLite/Room
                    Button(
                        onClick = {
                            if (correo.isBlank() || contrasena.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Completa el correo y la contraseña",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            scope.launch {
                                val admin = db.usuarioDao().getUserByEmailAndRol(
                                    email = correo.trim(),
                                    rol = "ADMIN"
                                )

                                if (admin != null && admin.password == contrasena) {
                                    Toast.makeText(
                                        context,
                                        "Bienvenido, ${admin.name}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onAdminLoginSuccess()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Correo o contraseña de administrador incorrectos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorGuinda),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Ingresar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}