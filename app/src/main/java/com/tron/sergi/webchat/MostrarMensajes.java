package com.tron.sergi.webchat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MostrarMensajes extends AppCompatActivity
{
    private static final String TAG = "MiW";

    private RequestQueue colaPeticiones;

    private ListView mensajesLista;
    private Activity act;
    private TextView mensajeText;
    private ActualizarMensajes actualizarMensajes;

    private String usuario;
    private String idConversacion;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SingleVolley volley;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_mensajes);

        volley = SingleVolley.getInstance(getApplicationContext());
        colaPeticiones = volley.getColaPeticiones();

        mensajeText = (TextView) findViewById(R.id.mensajeText);
        mensajesLista = (ListView) findViewById(R.id.mensajesLista);
        act = this;

        gson = new Gson();

        usuario = getIntent().getExtras().getString("usuario");
        idConversacion = getIntent().getExtras().getString("idConversacion");

        actualizarMensajes = new ActualizarMensajes();
        actualizarMensajes.execute();
    }


    private void encolarPeticion(Request peticion)
    {
        if(peticion != null)
        {
            peticion.setTag(TAG);
            peticion.setRetryPolicy(
                    new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                            3,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            colaPeticiones.add(peticion);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(colaPeticiones != null)
        {
            colaPeticiones.cancelAll(TAG);
        }
        actualizarMensajes.cancel(true);
    }

    public void enviar(View v)
    {
        if(mensajeText.getText().toString().equals(""))
        {
            return ;
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, RecursosServidor.enviarMensajeApp,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        mensajeText.setText("");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("usuario", usuario);
                params.put("mensaje", mensajeText.getText().toString());
                params.put("conversacion", idConversacion);

                return params;
            }
        };
        encolarPeticion(postRequest);
    }
    private void toast(String mensaje)
    {
        Toast.makeText(getApplicationContext(),
                mensaje, Toast.LENGTH_SHORT).show();
    }

    private class ActualizarMensajes extends AsyncTask<Void, ArrayList<MensajeCategory>, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            while(!isCancelled())
            {
                StringRequest peticion = new StringRequest(Request.Method.POST, RecursosServidor.mostrarMensajeApp,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                String mensajeTexto = "";
                                try
                                {
                                    JSONArray j = new JSONArray(response);
                                    ArrayList<MensajeCategory> category = new ArrayList<MensajeCategory>();
                                    for(int i = 0; i < j.length(); i++)
                                    {
                                       Mensaje mensaje = gson.fromJson(
                                                j.getJSONObject(i).toString(), Mensaje.class);
                                        mensajeTexto +=
                                                mensaje.getNombre() +
                                                        "- " + mensaje.getFecha() +
                                                        ": " + mensaje.getTexto() + "\n";
                                        MensajeCategory men = new MensajeCategory(usuario);
                                        men.setFecha(mensaje.getFecha());
                                        men.setMensaje(mensaje.getTexto());
                                        men.setNombre(mensaje.getNombre());
                                        category.add(men);
                                    }
                                    publishProgress(category);

                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                toast(error.toString());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("usuario", usuario);
                        params.put("conversacion", idConversacion);
                        return params;
                    }
                };
                encolarPeticion(peticion);
                try {

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ArrayList<MensajeCategory>... params)
        {
            Parcelable state = mensajesLista.onSaveInstanceState();
            AdapterItemMensaje itemsAdapter = new AdapterItemMensaje(act, params[0]);
            mensajesLista.setAdapter(itemsAdapter);
            mensajesLista.setSelection(mensajesLista.getHeight());
            //mensajesLista.onRestoreInstanceState(state);
        }
        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected void onCancelled()
        {

        }
    }

}
