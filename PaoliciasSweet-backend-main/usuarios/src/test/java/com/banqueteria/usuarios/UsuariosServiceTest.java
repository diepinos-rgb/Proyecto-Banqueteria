package com.banqueteria.usuarios;

import com.banqueteria.usuarios.Model.Usuarios;
import com.banqueteria.usuarios.Repository.UsuariosRepository;
import com.banqueteria.usuarios.Services.UsuariosService;
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
public class UsuariosServiceTest {

    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private UsuariosService usuariosService;

    private Usuarios usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuarios();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setNombre("Paola");
        usuarioEjemplo.setCorreo("paola@paoliciassweet.cl");
        usuarioEjemplo.setPassword("123456");
    }

    @Test
    void testObtenerTodos() {
        Usuarios usuario2 = new Usuarios();
        usuario2.setId(2L);
        usuario2.setCorreo("cliente@correo.cl");

        when(usuariosRepository.findAll()).thenReturn(Arrays.asList(usuarioEjemplo, usuario2));

        List<Usuarios> resultado = usuariosService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(usuariosRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorId() {
        when(usuariosRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

        Optional<Usuarios> encontrado = usuariosService.obtenerPorId(1L);

        assertTrue(encontrado.isPresent());
        assertEquals("Paola", encontrado.get().getNombre());
        verify(usuariosRepository, times(1)).findById(1L);
    }

    @Test
    void testRegistrarUsuarioExitoso() {
        when(usuariosRepository.findByCorreo(usuarioEjemplo.getCorreo())).thenReturn(Optional.empty());
        when(usuariosRepository.save(any(Usuarios.class))).thenReturn(usuarioEjemplo);

        Usuarios guardado = usuariosService.registrarUsuario(usuarioEjemplo);

        assertNotNull(guardado);
        assertEquals("paola@paoliciassweet.cl", guardado.getCorreo());
        verify(usuariosRepository, times(1)).save(usuarioEjemplo);
    }

    @Test
    void testRegistrarUsuarioCorreoDuplicado() {
        // Simulamos que el correo ya existe en la base de datos
        when(usuariosRepository.findByCorreo(usuarioEjemplo.getCorreo())).thenReturn(Optional.of(usuarioEjemplo));

        // Verificamos que lance la excepción esperada y que NUNCA guarde
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuariosService.registrarUsuario(usuarioEjemplo);
        });

        assertEquals("El correo ya está registrado en el sistema", exception.getMessage());
        verify(usuariosRepository, never()).save(any(Usuarios.class));
    }

    @Test
    void testLoginExitoso() {
        when(usuariosRepository.findByCorreo("paola@paoliciassweet.cl")).thenReturn(Optional.of(usuarioEjemplo));

        Usuarios logueado = usuariosService.login("paola@paoliciassweet.cl", "123456");

        assertNotNull(logueado);
        assertEquals(1L, logueado.getId());
    }

    @Test
    void testLoginCredencialesInvalidas() {
        when(usuariosRepository.findByCorreo("paola@paoliciassweet.cl")).thenReturn(Optional.of(usuarioEjemplo));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuariosService.login("paola@paoliciassweet.cl", "claveIncorrecta");
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    void testEliminarUsuario() {
        doNothing().when(usuariosRepository).deleteById(1L);

        usuariosService.eliminarUsuario(1L);

        verify(usuariosRepository, times(1)).deleteById(1L);
    }
}
