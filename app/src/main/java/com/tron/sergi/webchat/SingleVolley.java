package com.tron.sergi.webchat;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingleVolley
{
    private static SingleVolley instanciaVolley = null;

    private RequestQueue colaPeticiones;

    private SingleVolley(Context context)
    {
        colaPeticiones = Volley.newRequestQueue(context);
    }

    public static SingleVolley getInstance(Context context)
    {
        if(instanciaVolley == null)
        {
            instanciaVolley = new SingleVolley(context);
        }
        return instanciaVolley;
    }

    public RequestQueue getColaPeticiones()
    {
        return colaPeticiones;
    }

    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }
}
