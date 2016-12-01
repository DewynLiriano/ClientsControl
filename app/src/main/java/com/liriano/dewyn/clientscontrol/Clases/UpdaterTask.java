package com.liriano.dewyn.clientscontrol.Clases;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;

/**
 * Created by dewyn on 11/30/2016.
 */

public class UpdaterTask extends TimerTask {
    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
        final String strDate = simpleDateFormat.format(calendar.getTime());
    }
}
