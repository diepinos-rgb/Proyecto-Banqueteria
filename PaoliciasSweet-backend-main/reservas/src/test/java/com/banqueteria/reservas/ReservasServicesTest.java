package com.banqueteria.reservas;

import com.banqueteria.reservas.Model.Reservacion;
import com.banqueteria.reservas.Repository.ReservacionRepository;
import com.banqueteria.reservas.Services.ReservasServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservasServicesTest {

    @Mock
    private ReservacionRepository reservacionRepository;

    @InjectMocks
    private ReservasServices reservasServices;

    private Reservacion reservaEjemplo;

    @BeforeEach
    void setUp() {
        // Preparamos una reserva de prueba antes de cada test
        reservaEjemplo = new Reservacion();
        reservaEjemplo.setId(1L);
        reservaEjemplo.setCorreoCliente("cliente@paoliciassweet.cl");
        // Si tu modelo tiene más campos obligatorios (ej. cantidadPersonas, fecha), puedes agregarlos aquí
    }

    @Test
    void testCrearReservacion() {
        // Simulamos que al guardar, el repositorio devuelve nuestra reserva de ejemplo
        when(reservacionRepository.save(any(Reservacion.class))).thenReturn(reservaEjemplo);

        Reservacion resultado = reservasServices.crearReservacion(reservaEjemplo);

        assertNotNull(resultado);
        assertEquals("cliente@paoliciassweet.cl", resultado.getCorreoCliente());
        verify(reservacionRepository, times(1)).save(reservaEjemplo);
    }

    @Test
    void testObtenerTodas() {
        Reservacion reserva2 = new Reservacion();
        reserva2.setId(2L);
        reserva2.setCorreoCliente("otro@correo.cl");

        when(reservacionRepository.findAll()).thenReturn(Arrays.asList(reservaEjemplo, reserva2));

        List<Reservacion> lista = reservasServices.obtenerTodas();

        assertEquals(2, lista.size());
        verify(reservacionRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorCorreoCliente() {
        String correo = "cliente@paoliciassweet.cl";
        when(reservacionRepository.findByCorreoCliente(correo)).thenReturn(List.of(reservaEjemplo));

        List<Reservacion> historial = reservasServices.obtenerPorCorreoCliente(correo);

        assertFalse(historial.isEmpty());
        assertEquals(1, historial.size());
        assertEquals(correo, historial.get(0).getCorreoCliente());
        verify(reservacionRepository, times(1)).findByCorreoCliente(correo);
    }

    @Test
    void testObtenerPorId() {
        when(reservacionRepository.findById(1L)).thenReturn(Optional.of(reservaEjemplo));

        Optional<Reservacion> encontrada = reservasServices.obtenerPorId(1L);

        assertTrue(encontrada.isPresent());
        assertEquals(1L, encontrada.get().getId());
        verify(reservacionRepository, times(1)).findById(1L);
    }

    @Test
    void testEliminarReservacion() {
        doNothing().when(reservacionRepository).deleteById(1L);

        reservasServices.eliminarReservacion(1L);

        verify(reservacionRepository, times(1)).deleteById(1L);
    }
}