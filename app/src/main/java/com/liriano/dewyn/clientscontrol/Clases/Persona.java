package com.liriano.dewyn.clientscontrol.Clases;

import java.io.Serializable;

/**
 * Created by dewyn on 10/11/2016.
 */

public class Persona implements Serializable {
    private String _id;
    private String _nombre;
    private String _apellido;
    private String _sexo;

    public Persona(String _id, String _nombre, String _apellido, String _sexo) {
        set_id(_id);
        set_nombre(_nombre);
        set_apellido(_apellido);
        set_sexo(_sexo);
    }

    public Persona() {}

    public String get_id() {
        return String.valueOf(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_nombre() {
        return _nombre;
    }

    public void set_nombre(String _nombre) {
        this._nombre = _nombre;
    }

    public String get_apellido() {
        return _apellido;
    }

    public void set_apellido(String _apellido) {
        this._apellido = _apellido;
    }

    public String get_sexo() {
        return _sexo;
    }

    public void set_sexo(String _sexo) {
        this._sexo = _sexo;
    }
}
