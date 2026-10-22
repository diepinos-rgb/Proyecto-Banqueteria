# Banquetería - Paolicias.Sweet 

Aplicación móvil nativa para Android desarrollada como parte de la entrega técnica del proyecto de Banquetería y Pastelería **Paolicias.Sweet**. 
La aplicación permite a los usuarios explorar el catálogo de servicios de catering y repostería, registrarse, iniciar sesión y generar cotizaciones personalizadas
de eventos con validaciones en tiempo real y persistencia de datos local.

---

Integrantes del Equipo

Andrea Mascareño

Diego Pino

---

Tecnologías y Arquitectura

Lenguaje:Kotlin
UI Framework: Jetpack Compose (Material 3)
Arquitectura: MVVM (Model-View-ViewModel)
Base de Datos Local: Room Persistence Library (SQLite)
Navegación: Jetpack Navigation Compose
Recursos de Hardware: Cámara nativa (`ActivityResultLauncher`) y Vibración (`Vibrator` / `VibratorManager`)

---

Funcionalidades Implementadas

1. Inicio y Bienvenida (`ScreenBanqueteria`)
Pantalla de entrada con la identidad visual corporativa (color guinda de marca).
Botón de ingreso directo que inicia el flujo hacia el control de acceso.

2. Autenticación y Registro (`Login` & `Register`)
Validación en tiempo real en `RegisterViewModel`:
   Nombre: Mínimo de 3 caracteres obligatorios.
   Correo electrónico: Validación de formato que exige `@` y dominio `.com` o `.cl`.
   Contraseña: Mínimo 6 caracteres y comprobación de coincidencia con la confirmación.
Persistencia y verificación de usuarios existentes en la base de datos local Room.
Flujo de navegación seguro que gestiona la pila de actividades (`popUpTo`).

3. Catálogo Unificado (`Principal`)
Visualización limpia de servicios categorizados entre Catering y Pastelería.
Interfaz sobria libre de emojis decorativos.
Botón de cierre de sesión "Salir" que limpia la pila de navegación y redirige al inicio.

4. Configuración de Eventos y Cotizaciones (`ConfigurarEvento`)
Formulario interactivo para estimación de presupuestos (fecha exploratoria, horario y selector de asistentes).
Integración de Cámara Nativa: Permite adjuntar una fotografía de referencia utilizando el hardware de la cámara del dispositivo.
Selección múltiple de restricciones alimentarias (Vegetariano, Vegano, Celiaco, etc.).

5. Resumen de Cotización (`ResumenCotizacion`)
Desglose detallado del evento antes del envío.
Registro del requerimiento y retroalimentación mediante respuesta háptica (vibración del dispositivo).

---

Pasos para Ejecutar el Proyecto

Requisitos Previos
Android Studio: Ladybug (2024.2.1) o superior.
JDK: versión 17 o superior.
SDK Mínimo: API 26 (Android 8.0 Oreo) o superior.
Dispositivo físico o emulador con servicios de cámara habilitados.

Instalación
1. Clonar o descargar el repositorio:
   ```bash
   git clone <URL_DEL_REPOSOTORIO>
