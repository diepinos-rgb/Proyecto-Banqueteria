package com.example.appbanqueteria.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appbanqueteria.ui.screen.ConfigurarEvento
import com.example.appbanqueteria.ui.screen.Login
import com.example.appbanqueteria.ui.screen.LoginAdmin
import com.example.appbanqueteria.ui.screen.PanelAdmin
import com.example.appbanqueteria.ui.screen.Principal
import com.example.appbanqueteria.ui.screen.Register
import com.example.appbanqueteria.ui.screen.ResumenCotizacion
import com.example.appbanqueteria.ui.screen.ScreenBanqueteria

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Variables de sesión para conservar los datos del usuario autenticado
    var usuarioNombre by rememberSaveable { mutableStateOf("") }
    var usuarioCorreo by rememberSaveable { mutableStateOf("") }
    var usuarioTelefono by rememberSaveable { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = "screenBanqueteria"
    ) {
        // Pantalla de Bienvenida Inicial
        composable("screenBanqueteria") {
            ScreenBanqueteria(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("screenBanqueteria") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Login de Clientes (con enlace a LoginAdmin)
        composable("login") {
            Login(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToAdminLogin = { navController.navigate("login_admin") },
                onLoginSuccess = { nombreUsuario, correoUsuario, telefonoUsuario ->
                    usuarioNombre = nombreUsuario
                    usuarioCorreo = correoUsuario
                    usuarioTelefono = telefonoUsuario
                    navController.navigate("principal/$nombreUsuario") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Login Exclusiva para Administrador
        composable("login_admin") {
            LoginAdmin(
                onBack = { navController.popBackStack() },
                onAdminLoginSuccess = {
                    navController.navigate("admin_panel") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Panel de Administrador conectado
        composable("admin_panel") {
            PanelAdmin(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("admin_panel") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Registro
        composable("register") {
            Register(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        // Catálogo Unificado (recibe nombreUsuario como argumento)
        composable(
            route = "principal/{nombreUsuario}",
            arguments = listOf(navArgument("nombreUsuario") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombreUsuario = backStackEntry.arguments?.getString("nombreUsuario")
                ?: usuarioNombre.ifBlank { "Usuario" }

            Principal(
                nombreUsuario = nombreUsuario,
                onNavigateToConfigurar = { servicioId ->
                    navController.navigate("configurar_evento/$servicioId")
                },
                onLogout = {
                    usuarioNombre = ""
                    usuarioCorreo = ""
                    usuarioTelefono = ""
                    navController.navigate("login") {
                        popUpTo("principal/{nombreUsuario}") { inclusive = true }
                    }
                }
            )
        }

        // Configurar Evento (recibe servicioId de Catering o Pastelería)
        composable(
            route = "configurar_evento/{servicioId}",
            arguments = listOf(navArgument("servicioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val servicioId = backStackEntry.arguments?.getString("servicioId")
            ConfigurarEvento(
                servicioId = servicioId,
                onBack = { navController.popBackStack() },
                onVerCotizacion = { id, fecha, hora, personas, tipoCliente, observaciones ->
                    val observacionesCodificadas = Uri.encode(observaciones)
                    navController.navigate(
                        "resumen_cotizacion/$id/$fecha/$hora/$personas/$tipoCliente?observaciones=$observacionesCodificadas"
                    )
                }
            )
        }

        // Resumen de Cotización (recibe los datos de sesión, tipo de cliente y observaciones)
        composable(
            route = "resumen_cotizacion/{servicioId}/{fecha}/{hora}/{personas}/{tipoCliente}?observaciones={observaciones}",
            arguments = listOf(
                navArgument("servicioId") { type = NavType.StringType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.StringType },
                navArgument("personas") { type = NavType.IntType },
                navArgument("tipoCliente") { type = NavType.StringType },
                navArgument("observaciones") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val servicioId = backStackEntry.arguments?.getString("servicioId")
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val hora = backStackEntry.arguments?.getString("hora") ?: ""
            val personas = backStackEntry.arguments?.getInt("personas") ?: 20
            val tipoCliente = backStackEntry.arguments?.getString("tipoCliente") ?: "Persona natural"
            val observaciones = backStackEntry.arguments?.getString("observaciones") ?: ""

            ResumenCotizacion(
                servicioId = servicioId,
                fecha = fecha,
                hora = hora,
                personas = personas,
                tipoCliente = tipoCliente,
                observaciones = observaciones,
                nombreCliente = usuarioNombre,
                correoCliente = usuarioCorreo,
                telefonoCliente = usuarioTelefono,
                onBack = { navController.popBackStack() },
                onConfirmarExito = {
                    val nombreDestino = usuarioNombre.ifBlank { "Usuario" }
                    navController.navigate("principal/$nombreDestino") {
                        popUpTo("principal/{nombreUsuario}") { inclusive = true }
                    }
                }
            )
        }
    }
}