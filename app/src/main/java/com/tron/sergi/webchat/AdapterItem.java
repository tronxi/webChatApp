package com.tron.sergi.webchat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterItem extends BaseAdapter
{
    protected Activity activity;
    protected ArrayList<Category> items;

    public AdapterItem (Activity activity, ArrayList<Category> items) {
        this.activity = activity;
        this.items = items;
    }

    public AdapterItem(Activity activity) {
        this.activity = activity;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<Category> category) {
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
            v = inf.inflate(R.layout.contactos_lista, viewGroup, false);
        }

        Category dir = items.get(i);

        TextView contacto = (TextView) v.findViewById(R.id.contacto);
        contacto.setText(dir.getContacto());

        TextView mensaje = (TextView) v.findViewById(R.id.numMensaje);
        mensaje.setText(dir.getMensaje());
        return v;
    }
}
