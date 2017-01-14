package com.liriano.dewyn.clientscontrol.CrearClientes;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.liriano.dewyn.clientscontrol.Clases.Cliente;
import com.liriano.dewyn.clientscontrol.CloseActivityEvent;
import com.liriano.dewyn.clientscontrol.R;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;
import java.util.Objects;

public class CrearCliente extends AppCompatActivity {

    private static final String TAG = "Mensaje";
    private EditText nombreET, apellidoET, correoET, cumpleET, numeroET;

    private ImageView fotoIV;
    private Uri uriFoto = null;
    private ClientFirebaseHelper clientFirebaseHelper;

    private String DATEFORMAT = "dd/MM/yyyy";
    private String sexo = "";
    private View focusView = null;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.client_toolbar);
        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        EventBus.getDefault().post(new CloseActivityEvent());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Guardando Cliente");
        progressDialog.setMessage("Guardando Cliente");
        progressDialog.setCancelable(true);

        clientFirebaseHelper = new ClientFirebaseHelper();

        fotoIV = (ImageView) findViewById(R.id.foto);
        nombreET = (EditText) findViewById(R.id.nombreET);
        apellidoET = (EditText) findViewById(R.id.apellidoET);
        correoET = (EditText) findViewById(R.id.correoET);
        cumpleET = (EditText) findViewById(R.id.cumpleET);
        numeroET = (EditText)findViewById(R.id.numeroET);

    }

    @Subscribe
    public void onEvent(SuccessEvent event){
        Log.d(TAG, "Event triggered");
        if (event.isOK()){
            progressDialog.dismiss();
            Toast.makeText(this, getString(R.string.clienteAgregado), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getBirthday(View v) {
        DialogFragment fragment = new ClientDatePickerFragment();
        fragment.show(getFragmentManager(), "Date Picker");
    }

    //MANEJO DEL CLICK DEL IMAGEVIEW DE LA FOTO.
    public void fotoOnClick(View v) {
        //INTENT PARA LLAMAR LA CAMARAPA
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //LANZA UNA ACTIVIDAD ESPERANDO UN RESULTADO (EN ESTE CASO UN BITMAP).
        startActivityForResult(intentCamera, 0);
    }

    //FUNCION QUE RECIBE LA FOTO DESDE LA CAMARA Y LA MUESTRA EN LA ACTIVIDAD.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            uriFoto = data.getData();
            fotoIV.setImageURI(uriFoto);
        }
    }

    public void registrarCliente(View v) {
        boolean cancel = false;

        //CONTROL DE ENTRADA DE DATOS
        /*if (!DateValidator.isDateValid(cumpleET.getText().toString(), DATEFORMAT)) {
            //NOTIFICACION ERROR
            cumpleET.setError(getString(R.string.fechaInvalida));
            focusView = cumpleET;
            cancel = true;
        } else*/ if(Objects.equals(nombreET.getText().toString(), "")){
            nombreET.setError(getString(R.string.vacio) + getString(R.string.NOMBRE));
            focusView = nombreET;
            cancel = true;
        } else if (Objects.equals(apellidoET.getText().toString(), "")) {
            apellidoET.setError(getString(R.string.vacio) + getString(R.string.APELLIDO));
            focusView = apellidoET;
            cancel = true;
        } else if (Objects.equals(sexo, "")) {
            sex_alertBuilder().show();
            cancel = true;
        } else if (uriFoto == null){
            photo_alertBuilder().show();
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            progressDialog.show();
            String nombre = nombreET.getText().toString();
            String apellido = apellidoET.getText().toString();
            String correo = correoET.getText().toString();
            String cumple = cumpleET.getText().toString();
            String numero = numeroET.getText().toString();

            Cliente cliente = new Cliente(formatID() , nombre, apellido,
                    sexo, correo, cumple, numero);

            clientFirebaseHelper.addClient(cliente, uriFoto);
        }
    }

    public void getSexo(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch (v.getId()) {
            case R.id.radioVaron:
                if (checked)
                    sexo = "M";
                break;
            case R.id.radioHembra:
                if (checked)
                    sexo = "F";
                break;
        }
    }

    public AlertDialog.Builder sex_alertBuilder(){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CrearCliente.this)
                .setTitle(getString(R.string.ERROR))
                .setMessage(getString(R.string.sexo_invalido));

        alertBuilder.setNeutralButton(getString(R.string.ok), null);

        return alertBuilder;
    }

    public AlertDialog.Builder photo_alertBuilder(){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.ERROR))
                .setMessage(getString(R.string.foto_invalido));

        alertBuilder.setNeutralButton(getString(R.string.ok), null);

        return alertBuilder;
    }

    private String formatID(){
        String s = String.format(Locale.getDefault(), "%04d", clientFirebaseHelper.count + 1);
        return "KPC" + String.valueOf(s);
    }
}
