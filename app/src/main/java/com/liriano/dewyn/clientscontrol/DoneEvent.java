package com.liriano.dewyn.clientscontrol;

/**
 * Created by dewyn on 11/30/2016.
 */
public class DoneEvent {
    private boolean areClientsDone = false;
    private boolean areAttractionsDone = false;
    private boolean areControlsDone = false;

    public DoneEvent (boolean areClientsDone, boolean areAttractionsDone, boolean areControlsDone){
        this.areClientsDone = areClientsDone;
        this.areAttractionsDone = areAttractionsDone;
        this.areControlsDone = areControlsDone;
    }

    public DoneEvent() {}

    public boolean AreClientsDone() {
        return areClientsDone;
    }

    public boolean AreAttractionsDone() {
        return areAttractionsDone;
    }

    public void setAreClientsDone(boolean areClientsDone) {
        this.areClientsDone = areClientsDone;
    }

    public void setAreAttractionsDone(boolean areAttractionsDone) {
        this.areAttractionsDone = areAttractionsDone;
    }

    public boolean AreControlsDone() {
        return areControlsDone;
    }

    public void setAreControlsDone(boolean areControlsDone) {
        this.areControlsDone = areControlsDone;
    }
}
