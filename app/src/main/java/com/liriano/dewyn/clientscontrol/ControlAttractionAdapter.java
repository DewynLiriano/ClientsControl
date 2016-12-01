package com.liriano.dewyn.clientscontrol;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.liriano.dewyn.clientscontrol.Clases.Atraccion;
import com.liriano.dewyn.clientscontrol.Clases.Cliente;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dewyn on 11/29/2016.
 */
public class ControlAttractionAdapter extends RecyclerView.Adapter<ClientControlViewHolder> {

    private List<ControlAtraccion> list;
    private Context context;

    private List<Cliente> clientes;
    private List<Atraccion> atracciones;

    public ControlAttractionAdapter(Context context, List<ControlAtraccion> controlAtracciones, List<Cliente> clientes, List<Atraccion> atracciones){
        this.context = context;
        this.list = controlAtracciones;
        this.clientes = clientes;
        this.atracciones = atracciones;
    }

    @Override
    public ClientControlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_attractions_control_list_item, parent, false);
        return new ClientControlViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ClientControlViewHolder holder, final int position) {

        final String[] url = {""};
        FirebaseStorage.getInstance().getReference(context.getString(R.string.FOTOS_CLIENTES)).child(list.get(position).get_clientID()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Descarga Completa", uri.toString());
                        url[0] = uri.toString();
                        Picasso.with(context).load(uri.toString()).into(holder.fotoCliente);}
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error de descarga", e.getMessage());
                    }
        });

        //<editor-fold desc="Getting actual objects">
        Cliente cl = new Cliente();
        for (Cliente c : clientes){
            if (c.get_id().equals(list.get(position).get_clientID())){
                cl = c;
            }
        }
        final Cliente cliente = cl;

        Atraccion atr = new Atraccion();
        for (Atraccion a : atracciones){
            if (a.get_id().equals(list.get(position).get_attractionsID())){
                atr = a;
            }
        }
        final Atraccion atraccion = atr;
        //</editor-fold>

        holder.nombreTV.setText(cliente.get_nombre());
        holder.atraccionTV.setText(atraccion.get_titulo());
        holder.horaSalidaTV.setText(list.get(position).get_horaSalida());
        holder.horaeEntradaTV.setText(list.get(position).get_horaEntrada());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void OnItemClickListener(View v, int pos) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.selected_controller_dialog);

                TextView client_idTV = (TextView)dialog.findViewById(R.id.check_client_dialog_id);
                client_idTV.setText(cliente.get_id());
                TextView client_nameTV = (TextView)dialog.findViewById(R.id.check_client_dialog_name);
                client_nameTV.setText(cliente.get_nombre() + " " + cliente.get_apellido());
                final ImageView selectedClientPhoto = (ImageView)dialog.findViewById(R.id.check_client_dialog_foto);

                Picasso.with(context).load(url[0]).into(selectedClientPhoto);

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
