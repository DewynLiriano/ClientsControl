package com.liriano.dewyn.clientscontrol;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.liriano.dewyn.clientscontrol.Clases.Atraccion;
import com.liriano.dewyn.clientscontrol.Clases.Cliente;
import com.liriano.dewyn.clientscontrol.Clases.ControlAtraccion;
import com.liriano.dewyn.clientscontrol.CrearClientes.CrearCliente;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ControlAttractionAdapter adapter;
    private List<ControlAtraccion> controles, passed_controls;
    private DatabaseReference controlsRef;

    private List<Cliente> clientes;
    private List<Atraccion> atracciones;
    private DoneEvent doneEvent;

    private TimerTask timerTask;
    private Timer timer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        //<editor-fold desc="Authenticate App">
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("facturacion.kanquipark@gmail.com", "kanquipark1")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Sign in Completed", task.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Sign in error", e.getMessage());
            }
        });
        //</editor-fold>

        controles = new ArrayList<>();
        passed_controls = new ArrayList<>();
        clientes = new ArrayList<>();
        atracciones = new ArrayList<>();

        //<editor-fold desc="Set Firebase References">
        controlsRef = FirebaseDatabase.getInstance().getReference(getString(R.string.CONTROL_ATRACCIONES));
        controlsRef.limitToFirst(10).addValueEventListener(getControls);
        controlsRef.keepSynced(true);

        DatabaseReference clientsRef = FirebaseDatabase.getInstance().getReference(getString(R.string.CLIENTES));
        clientsRef.addValueEventListener(getClients);
        clientsRef.keepSynced(true);
        DatabaseReference attractionsRef = FirebaseDatabase.getInstance().getReference(getString(R.string.ATRACCIONES));
        attractionsRef.addValueEventListener(getAtr);
        attractionsRef.keepSynced(true);
        //</editor-fold>

        //<editor-fold desc="Setup recyclerview">
        adapter = new ControlAttractionAdapter(MainActivity.this, passed_controls, clientes, atracciones);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_control_atracciones);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setUpItemTouchHelper();
        //</editor-fold>
    }
    @Override
    protected void onStart(){
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        doneEvent = new DoneEvent();
        startFilter();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        stopTimerTask();
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopTimerTask();
    }

    //<editor-fold desc="Menu">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.create_client_settings) {
            Intent intent = new Intent(getApplicationContext(), CrearCliente.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Subscribe
    public void onEvent(DoneEvent event){
        if (event.AreClientsDone() && event.AreAttractionsDone() && event.AreControlsDone()){
            recyclerView.setAdapter(adapter);
            Log.e("EVENTO", "DONE");
        } else {
            Log.e("EVENTO", "Not donde yet");
        }
    }

    //<editor-fold desc="Firebase events">
    private ValueEventListener getControls = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            controles.clear();
            HashMap rootMap = (HashMap) dataSnapshot.getValue();
            if (rootMap != null){
                Collection<Object> objects = rootMap.values();
                for (Object o : objects){
                    if (o instanceof Map){
                        HashMap<String, Object> map = (HashMap<String, Object>) o;
                        ControlAtraccion c = new ControlAtraccion();
                        c.set_id( (String) map.get(getString(R.string.ID)));
                        c.set_clientID((String)map.get(getString(R.string.CLIENTE_ID)));
                        c.set_attractionsID((String)map.get(getString(R.string.ATRACCION_ID)));
                        c.set_horaSalida((String)map.get(getString(R.string.HORA_SALIDA)));
                        c.set_horaEntrada((String)map.get(getString(R.string.HORA_ENTRADA)));
                        controles.add(c);
                    }
                }
            }

            if (dataSnapshot.getChildrenCount() == controles.size()) {
                doneEvent.setAreControlsDone(true);
                EventBus.getDefault().post(doneEvent);
            }

            sortControles();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(getString(R.string.ERROR), databaseError.getMessage());
        }
    };

    private ValueEventListener getClients = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            clientes.clear();
            HashMap rootMap = (HashMap) dataSnapshot.getValue();

            if (rootMap != null){
                Collection<Object> objects = rootMap.values();
                for (Object o : objects){
                    if (o instanceof Map){
                        HashMap<String, Object> map = (HashMap<String, Object>) o;
                        final Cliente c = new Cliente();
                        c.set_id( (String) map.get(getString(R.string.ID)));
                        c.set_nombre((String) map.get(getString(R.string.NOMBRE)));
                        c.set_apellido((String) map.get(getString(R.string.APELLIDO)));
                        c.set_fechaCumpleAnos((String) map.get(getString(R.string.FECHA_CUMPLEANOS)));
                        c.set_sexo((String) map.get(getString(R.string.SEXO)));
                        c.set_numero((String) map.get(getString(R.string.NUMERO)));
                        c.set_correo((String) map.get(getString(R.string.CORREO)));
                        clientes.add(c);
                    }
                }
            }
            if (dataSnapshot.getChildrenCount() == clientes.size()) {
                doneEvent.setAreClientsDone(true);
                EventBus.getDefault().post(doneEvent);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(getString(R.string.ERROR), databaseError.getMessage());
        }
    };

    private ValueEventListener getAtr = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            GenericTypeIndicator<Map<String, Map<String, String>>> genin = new GenericTypeIndicator<Map<String, Map<String, String>>>() {};
            Map<String, Map<String,String>> map = dataSnapshot.getValue(genin);
            atracciones.clear();

            if (map != null){
                for (Map.Entry<String, Map<String, String>> entry : map.entrySet()){
                    if (entry != null){
                        HashMap value = (HashMap) entry.getValue();
                        Atraccion a = new Atraccion();
                        a.set_id((String)value.get(getString(R.string.ID)));
                        a.set_titulo((String)value.get(getString(R.string.NOMBRE)));
                        a.set_precio((String) value.get(getString(R.string.PRECIO)));
                        a.set_tiempo((String) value.get(getString(R.string.TIEMPO)));
                        atracciones.add(a);
                    }
                }
            }

            if (atracciones.size() == dataSnapshot.getChildrenCount()){
                doneEvent.setAreAttractionsDone(true);
                EventBus.getDefault().post(doneEvent);
            }

        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(getString(R.string.ERROR), databaseError.getMessage());
        }
    };
    //</editor-fold>

    private void sortControles() {
        Collections.sort(controles, new Comparator<ControlAtraccion>() {
            @Override
            public int compare(ControlAtraccion o1, ControlAtraccion o2) {
                return o1.get_id().compareTo(o2.get_id());
            }
        });
    }

    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_close_circle_white_48dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) MainActivity.this.getResources().getDimension(R.dimen.activity_horizontal_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ControlAttractionAdapter testAdapter = (ControlAttractionAdapter)recyclerView.getAdapter();
                /*if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }*/
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int swipedPosition = viewHolder.getAdapterPosition();
                ControlAttractionAdapter testAdapter = (ControlAttractionAdapter)recyclerView.getAdapter();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.BORRAR));
                builder.setMessage(getString(R.string.DESEA_ELIMINAR));
                builder.setCancelable(false);
                builder.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recyclerView.setAdapter(adapter);
                    }
                });

                builder.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ControlAtraccion control = passed_controls.get(swipedPosition);
                        DatabaseReference controlsRef = FirebaseDatabase.getInstance().getReference(getString(R.string.CONTROL_ATRACCIONES));
                        controlsRef.child(control.get_id()).removeValue();
                        passed_controls.remove(swipedPosition);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Cliente retirado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }
                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void startFilter() {
        handler = new Handler();
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 60000); //
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        int cantNuevos = 0;
                        for (ControlAtraccion control : controles) {
                            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.DATEFORMAT), Locale.getDefault());
                            try {
                                Date horaSalida = sdf.parse(control.get_horaSalida());
                                boolean there = false;
                                if (horaSalida.before(Calendar.getInstance().getTime())){
                                    for (ControlAtraccion con : passed_controls){
                                        if (con.get_id().equals(control.get_id())){
                                            there = true;
                                        }
                                    }
                                    if (!there){
                                        cantNuevos++;
                                        passed_controls.add(control);
                                        //adapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (ParseException e) {
                                Log.e("Catch", e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        if  (cantNuevos > 0){
                            adapter.notifyDataSetChanged();
                            notifyNewPassedControls(cantNuevos);
                        }
                    }
                });
            }
        };
    }

    private void notifyNewPassedControls(int cantNuevos) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle(getString(R.string.app_name));
        notificationBuilder.setContentText("Hay " + String.valueOf(cantNuevos) + " nuevos tickets vencidos");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
        stackBuilder.addParentStack(MainActivity.class);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, notificationBuilder.build());
    }
}
