package com.liriano.dewyn.clientscontrol;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by dewyn on 11/29/2016.
 */
public class ClientControlViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView nombreTV, atraccionTV, horaSalidaTV, horaeEntradaTV;
    ImageView fotoCliente;
    ItemClickListener itemClickListener;

    public ClientControlViewHolder(View view) {
        super(view);
        nombreTV = (TextView)view.findViewById(R.id.control_client_name);
        atraccionTV = (TextView)view.findViewById(R.id.control_atraccion_name);
        horaSalidaTV = (TextView)view.findViewById(R.id.control_hora_salida);
        horaeEntradaTV = (TextView)view.findViewById(R.id.control_hora_entrada);
        fotoCliente = (ImageView)view.findViewById(R.id.cotrol_foto_cliente);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.OnItemClickListener(v, getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
