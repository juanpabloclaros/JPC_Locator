package com.project.juan_.jpc_locator;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.project.juan_.jpc_locator.Entidades.Usuario;

import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.MensajeViewHolder> {
    private List<Mensajes> listaMensajes;
    private Usuario usuario = new Usuario();

    public MensajesAdapter (List<Mensajes> listaMensajes){
        this.listaMensajes = listaMensajes;
    }

    public class MensajeViewHolder extends RecyclerView.ViewHolder{

        public TextView mensajeEnviadoText, mensajeRecibidoText;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);

            mensajeEnviadoText = (TextView) itemView.findViewById(R.id.mensajeEnviadoTV);
            mensajeRecibidoText = (TextView) itemView.findViewById(R.id.mensajeRecibidoTV);
        }
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages, viewGroup, false);

        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder mensajeViewHolder, int i) {
        String mensajeEnviadoId = usuario.getUsuario();
        Mensajes mensajes = listaMensajes.get(i);

        String fromUsuarioId = mensajes.getFrom();

        mensajeViewHolder.mensajeRecibidoText.setVisibility(View.INVISIBLE);
        mensajeViewHolder.mensajeEnviadoText.setVisibility(View.INVISIBLE);

        if (fromUsuarioId.equals(mensajeEnviadoId)){
            mensajeViewHolder.mensajeEnviadoText.setVisibility(View.VISIBLE);
            mensajeViewHolder.mensajeEnviadoText.setBackgroundResource(R.drawable.mensajes_enviados);
            mensajeViewHolder.mensajeEnviadoText.setTextColor(Color.BLACK);
            mensajeViewHolder.mensajeEnviadoText.setText(mensajes.getMensaje() + "\n \n" + mensajes.getHora() + " - " + mensajes.getFecha());
        } else{
            mensajeViewHolder.mensajeEnviadoText.setVisibility(View.INVISIBLE);

            mensajeViewHolder.mensajeRecibidoText.setVisibility(View.VISIBLE);

            mensajeViewHolder.mensajeRecibidoText.setBackgroundResource(R.drawable.mensajes_recibidos);
            mensajeViewHolder.mensajeRecibidoText.setTextColor(Color.BLACK);
            mensajeViewHolder.mensajeRecibidoText.setText(mensajes.getMensaje() + "\n \n" + mensajes.getHora() + " - " + mensajes.getFecha());
        }
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
