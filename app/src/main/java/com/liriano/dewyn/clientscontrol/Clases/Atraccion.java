package com.liriano.dewyn.clientscontrol.Clases;

import java.io.Serializable;

/**
 * Created by dewyn on 10/26/2016.
 */

public class Atraccion extends Producto implements Serializable {

    private String _tiempo;

    public String get_tiempo() {
        return _tiempo;
    }

    public void set_tiempo(String _tiempo) {
        this._tiempo = _tiempo;
    }

    public Atraccion(String id, String nombre, String precio, String _tiempo) {
        super(id, nombre, precio);
        this._tiempo = _tiempo;
    }

    public Atraccion(){}

    @Override
    public String toString(){
        return this.get_titulo() + " - " + this.get_tiempo() + " mins.";
    }
}
