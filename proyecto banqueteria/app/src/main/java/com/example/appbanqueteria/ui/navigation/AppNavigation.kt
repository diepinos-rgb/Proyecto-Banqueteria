package com.example.appbanqueteria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appbanqueteria.ui.screen.ConfigurarEvento
import com.example.appbanqueteria.ui.screen.Login
import com.example.appbanqueteria.ui.screen.Principal
import com.example.appbanqueteria.ui.screen.Register
import com.example.appbanqueteria.ui.screen.ResumenCotizacion
import com.example.appbanqueteria.ui.screen.ScreenBanqueteria

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

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

        // Pantalla de Login (recibe el nombre en el callback de éxito)
        composable("login") {
            Login(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { nombreUsuario ->
                    navController.navigate("principal/$nombreUsuario") {
                        popUpTo("login") { inclusive = true }
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
            val nombreUsuario = backStackEntry.arguments?.getString("nombreUsuario") ?: "Usuario"

            Principal(
                nombreUsuario = nombreUsuario,
                onNavigateToConfigurar = { servicioId ->
                    navController.navigate("configurar_evento/$servicioId")
                },
                onLogout = {
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
                onVerCotizacion = { id, fecha, hora, personas ->
                    navController.navigate("resumen_cotizacion/$id/$fecha/$hora/$personas")
                }
            )
        }

        // Resumen de Cotización
        composable(
            route = "resumen_cotizacion/{servicioId}/{fecha}/{hora}/{personas}",
            arguments = listOf(
                navArgument("servicioId") { type = NavType.StringType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.StringType },
                navArgument("personas") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val servicioId = backStackEntry.arguments?.getString("servicioId")
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val hora = backStackEntry.arguments?.getString("hora") ?: ""
            val personas = backStackEntry.arguments?.getInt("personas") ?: 20

            ResumenCotizacion(
                servicioId = servicioId,
                fecha = fecha,
                hora = hora,
                personas = personas,
                onBack = { navController.popBackStack() },
                onConfirmarExito = {
                    navController.navigate("principal/Usuario") {
                        popUpTo("principal/{nombreUsuario}") { inclusive = true }
                    }
                }
            )
        }
    }
}