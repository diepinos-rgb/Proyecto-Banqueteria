package com.banqueteria.usuarios.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String numberPhone;

    @Column(nullable = false)
    private String password;

    private String rol;

    // Constructor vacío exigido por JPA
    public Usuarios() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return name; }
    public void setNombre(String nombre) { this.name = nombre; }

    public String getCorreo() { return email; }
    public void setCorreo(String correo) { this.email = correo; }

    public String getTelefono() { return numberPhone; }
    public void setTelefono(String telefono) { this.numberPhone = telefono; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}