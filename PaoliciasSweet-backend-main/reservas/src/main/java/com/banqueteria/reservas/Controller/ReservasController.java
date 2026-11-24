package com.banqueteria.reservas.Controller;

import java.util.List;
import java.util.Optional; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banqueteria.reservas.Model.Reservacion;
import com.banqueteria.reservas.Services.ReservasServices;

@RestController
@RequestMapping("/api/reservaciones")
@CrossOrigin(origins = "*") // Permite que tu app Android se conecte sin bloqueos de seguridad
public class ReservasController {

    @Autowired
    private ReservasServices reservasServices;

    // POST: Guardar una nueva reserva (desde la app móvil)
    @PostMapping
    public Reservacion crearReservacion(@RequestBody Reservacion reservacion) {
        return reservasServices.crearReservacion(reservacion);
    }

    // GET: Ver todas las reservas (para tu panel de administrador)
    @GetMapping
    public List<Reservacion> obtenerTodas() {
        return reservasServices.obtenerTodas();
    }

    // GET: Ver solo las reservas de un cliente específico según su correo
    @GetMapping("/cliente/{correo}")
    public List<Reservacion> obtenerPorCorreo(@PathVariable String correo) {
        return reservasServices.obtenerPorCorreoCliente(correo);
    }

    // GET: Ver los detalles de una sola reserva por su ID
    @GetMapping("/{id}")
    public Optional<Reservacion> obtenerPorId(@PathVariable Long id) {
        return reservasServices.obtenerPorId(id);
    }

    // DELETE: Cancelar o borrar una reserva
    @DeleteMapping("/{id}")
    public void eliminarReservacion(@PathVariable Long id) {
        reservasServices.eliminarReservacion(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Reservacion> actualizarReservacion(@PathVariable Long id, @RequestBody Reservacion detalles) {
        return reservasServices.actualizarReservacion(id, detalles)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}