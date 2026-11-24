package com.banqueteria.usuarios.Controller;

import com.banqueteria.usuarios.Model.Usuarios;
import com.banqueteria.usuarios.Services.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    @Autowired
    private UsuariosService usuariosService;

    // 1. Obtener todos (GET http://localhost:8080/api/usuarios)
    @GetMapping
    public List<Usuarios> obtenerTodos() {
        return usuariosService.obtenerTodos();
    }

    // 2. Obtener por ID (GET http://localhost:8080/api/usuarios/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> obtenerPorId(@PathVariable Long id) {
        Optional<Usuarios> usuario = usuariosService.obtenerPorId(id);
        return usuario.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. Registrar un usuario (POST http://localhost:8080/api/usuarios/registro)
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuarios usuario) {
        try {
            Usuarios nuevoUsuario = usuariosService.registrarUsuario(usuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (RuntimeException e) {
            // Retorna un error 400 Bad Request si el correo ya existe
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Login (POST http://localhost:8080/api/usuarios/login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String correo = credenciales.get("correo");
            String password = credenciales.get("password");
            Usuarios usuario = usuariosService.login(correo, password);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            // Retorna un error 401 Unauthorized si las credenciales fallan
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 5. Eliminar un usuario (DELETE http://localhost:8080/api/usuarios/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        usuariosService.eliminarUsuario(id);
        return ResponseEntity.ok().body("Usuario eliminado correctamente");
    }
}