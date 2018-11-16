package com.tron.sergi.webchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterItemMensaje extends BaseAdapter
{
    protected Activity activity;
    protected ArrayList<MensajeCategory> items;

    public AdapterItemMensaje (Activity activity, ArrayList<MensajeCategory> items) {
        this.activity = activity;
        this.items = items;
    }

    public AdapterItemMensaje(Activity activity) {
        this.activity = activity;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<MensajeCategory> category) {
        for (int i = 0; i < category.size(); i++) {
            items.add(category.get(i));
        }
    }
    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (view == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.mensajes_vista, viewGroup, false);
        }

        MensajeCategory dir = items.get(i);

        TextView nombre = (TextView) v.findViewById(R.id.nombre);
        if(dir.getNombre().equals(dir.getUsuario()))
        {
            nombre.setTextColor(Color.rgb(63, 247, 63));
        }
        nombre.setText(dir.getNombre());

        TextView mensaje = (TextView) v.findViewById(R.id.mensaje);
        mensaje.setText(dir.getMensaje());

        TextView fecha = (TextView) v.findViewById(R.id.fecha);
        fecha.setText(dir.getFecha());
        return v;
    }
}
