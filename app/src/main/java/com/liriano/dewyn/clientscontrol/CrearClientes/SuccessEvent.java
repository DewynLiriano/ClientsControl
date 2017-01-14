package com.liriano.dewyn.clientscontrol.CrearClientes;

/**
 * Created by dewyn on 11/13/2016.
 */
public class SuccessEvent {
    private boolean OK;

    public boolean isOK() {
        return OK;
    }

    public void setOK(boolean OK) {
        this.OK = OK;
    }

    public SuccessEvent(boolean OK) {
        setOK(OK);
    }
}
