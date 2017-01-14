package com.liriano.dewyn.clientscontrol.CrearClientes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;


import com.liriano.dewyn.clientscontrol.R;

import java.util.Calendar;

/**
 * Created by dewyn on 9/28/2016.
 */

public class ClientDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //INICIALIZA EL CALENDARIO CON LA FECHA ACTUAL
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.style_date_picker_dialog, this, year, month, day);

        DatePicker picker = datePickerDialog.getDatePicker();
        picker.setMaxDate(c.getTimeInMillis());

        // CREA INSTANCIA DEL DIALOGO Y SE DEVUELVE
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        EditText et = (EditText)getActivity().findViewById(R.id.cumpleET);
        //CUANDO SE SELECCIONA UNA FECHA, SE ACTUALIZA EN EL EDITTEXT.
        String FECHA = dayOfMonth + "/" + String.valueOf((month+1)) + "/" + year;
        et.setText(FECHA);
    }
}
