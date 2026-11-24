package com.banqueteria.usuarios.Repository;

import com.banqueteria.usuarios.Model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    
    // Método personalizado para buscar un usuario por su correo (ideal para el Login)
    Optional<Usuarios> findByCorreo(String correo);
}