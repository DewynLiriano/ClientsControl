package com.liriano.dewyn.clientscontrol.Clases;

import java.io.Serializable;

/**
 * Created by dewyn on 9/26/2016.
 */

public class Producto implements Serializable {
    private String _id;
    private String _titulo;
    private String _precio;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_titulo() {
        return _titulo;
    }

    public void set_titulo(String _nombre) {
        this._titulo = _nombre;
    }

    public String get_precio() {
        return _precio;
    }

    public void set_precio(String _precio) {
        this._precio = _precio;
    }

    public Producto(String id, String nombre, String precio) {
        set_id(id);
        set_titulo(nombre);
        set_precio(precio);
    }

    public Producto(){}

    @Override
    public String toString() {
        return this.get_titulo();
    }

}
