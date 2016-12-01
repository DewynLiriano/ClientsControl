package com.liriano.dewyn.clientscontrol;

/**
 * Created by dewyn on 11/29/2016.
 */
public class ControlAtraccion {
    private String _id;
    private String _clientID;
    private String _attractionsID;
    private String _horaSalida;
    private String _horaEntrada;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_clientID() {
        return _clientID;
    }

    public void set_clientID(String _clientID) {
        this._clientID = _clientID;
    }

    public String get_attractionsID() {
        return _attractionsID;
    }

    public void set_attractionsID(String _attractionsID) {
        this._attractionsID = _attractionsID;
    }

    public String get_horaSalida() {
        return _horaSalida;
    }

    public void set_horaSalida(String _horaSalida) {
        this._horaSalida = _horaSalida;
    }

    public String get_horaEntrada() {
        return _horaEntrada;
    }

    public void set_horaEntrada(String _horaEntrada) {
        this._horaEntrada = _horaEntrada;
    }

    public ControlAtraccion(String _id, String _clientID, String _attractionsID, String _horaSalida, String _horaEntrada) {
        set_id(_id);
        set_clientID(_clientID);
        set_attractionsID(_attractionsID);
        set_horaSalida(_horaSalida);
        set_horaEntrada(_horaEntrada);
    }

    public ControlAtraccion(){}

}
