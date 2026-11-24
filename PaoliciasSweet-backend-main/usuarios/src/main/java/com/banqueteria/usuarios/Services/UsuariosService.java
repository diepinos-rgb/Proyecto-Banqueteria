package com.banqueteria.usuarios.Services;

import com.banqueteria.usuarios.Model.Usuarios;
import com.banqueteria.usuarios.Repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuariosService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    // 1. Obtener todos los usuarios (Útil para el PanelAdmin)
    public List<Usuarios> obtenerTodos() {
        return usuariosRepository.findAll();
    }

    // 2. Obtener un usuario por ID
    public Optional<Usuarios> obtenerPorId(Long id) {
        return usuariosRepository.findById(id);
    }

    // 3. Registrar un nuevo usuario (Validando que el correo no exista)
    public Usuarios registrarUsuario(Usuarios usuario) {
        Optional<Usuarios> usuarioExistente = usuariosRepository.findByCorreo(usuario.getCorreo());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("El correo ya está registrado en el sistema");
        }
        return usuariosRepository.save(usuario);
    }

    // 4. Autenticación / Login
    public Usuarios login(String correo, String password) {
        Optional<Usuarios> usuario = usuariosRepository.findByCorreo(correo);
        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            return usuario.get();
        }
        throw new RuntimeException("Credenciales inválidas");
    }

    // 5. Eliminar un usuario
    public void eliminarUsuario(Long id) {
        usuariosRepository.deleteById(id);
    }
}