# PaoliciasSweet - Backend & App Móvil de Banquetería

Aplicación móvil y arquitectura de microservicios desarrollada en equipo para la gestión integral de reservas y clientes de una banquetería y pastelería (capacidad máxima de 100 personas).

## Integrantes
* Andrea Mascareño
* Diego Pino

---

## Funcionalidades Principales
* **Gestión de Usuarios (`usuarios`):** Registro de clientes con validación de correo único, inicio de sesión (autenticación), consulta de perfil y administración de usuarios.
* **Gestión de Reservas (`reservas`):** Creación, consulta de historial por cliente, listado general para administración y cancelación de eventos de banquetería.
* **API Gateway (`integracion`):** Enrutamiento centralizado de peticiones desde la aplicación móvil hacia los microservicios internos mediante Spring Cloud Gateway.
* **Consumo de API Externa:** Integración de servicio externo vía Retrofit en la aplicación móvil sin interferir con el flujo de datos locales.
* **Pruebas Unitarias:** Cobertura de lógica de negocio mediante JUnit 5 y Mockito en los servicios del backend y ViewModels/Repositories en Kotlin.

---

## Endpoints Utilizados

**URL Base (API Gateway):** `http://<IP_PUBLICA_AWS>:9000` *(o `http://localhost:9000` en entorno local)*

### Microservicio de Usuarios (`/api/usuarios`)
* `GET /api/usuarios` - Obtiene el listado completo de usuarios registrados.
* `GET /api/usuarios/{id}` - Obtiene los datos de un usuario específico por su ID.
* `POST /api/usuarios/registro` - Registra un nuevo usuario en el sistema.
* `POST /api/usuarios/login` - Valida credenciales e inicia sesión.
* `DELETE /api/usuarios/{id}` - Elimina un usuario por su ID.

### Microservicio de Reservas (`/api/reservas`)
* `GET /api/reservas` - Obtiene todas las reservaciones agendadas.
* `GET /api/reservas/{id}` - Consulta una reservación específica por ID.
* `GET /api/reservas/cliente/{correo}` - Obtiene el historial de reservas de un cliente.
* `POST /api/reservas` - Agenda una nueva reservación.
* `DELETE /api/reservas/{id}` - Cancela o elimina una reservación existente.

### API Externa (App Móvil)
* `[URL Base de la API Externa]` - `[Endpoint específico utilizado, ej. GET /api/v1/productos]`

---

## Pasos para Ejecutar

### 1. Backend (Con Docker / AWS)
1. Clonar el repositorio:
   ```bash
   git clone [https://github.com/dreakjj/PaoliciasSweet-backend.git](https://github.com/dreakjj/PaoliciasSweet-backend.git)
   cd PaoliciasSweet-backend
