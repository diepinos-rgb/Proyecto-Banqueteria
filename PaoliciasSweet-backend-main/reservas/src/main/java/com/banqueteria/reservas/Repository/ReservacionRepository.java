package com.banqueteria.reservas.Repository;

import com.banqueteria.reservas.Model.Reservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservacionRepository extends JpaRepository<Reservacion, Long> {
    
    // Método útil para que un cliente vea solo sus propias reservas
    List<Reservacion> findByCorreoCliente(String correoCliente);
}