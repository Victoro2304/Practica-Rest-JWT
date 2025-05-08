/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.io.Serializable;

/**
 *
 * @author victo
 */
public class Cliente implements Serializable {
    private int id;
    private String user;
    private String contrasena;

    public Cliente(){}

    @Override
    public String toString() {
        return "Cliente{" + "id=" + id + ", user=" + user + ", Contreseña=" + contrasena + '}';
    }

    public Cliente(int id, String user, String contrasena) {
        this.id = id;
        this.user = user;
        this.contrasena = contrasena;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return user;
    }

    public void setNombre(String user) {
        this.user = user;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    
}
