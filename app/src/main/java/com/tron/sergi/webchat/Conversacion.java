package com.tron.sergi.webchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Conversacion extends AppCompatActivity
{
    private static final String TAG = "MiW";

    private RequestQueue colaPeticiones;
    private String usuario;
    private Gson gson;
    private ActualizarConversaciones actualizarConversaciones;

    private ConversacionJson[] cj;

    private ListView listaConversaciones;
    private  SingleVolley volley;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion);
        usuario = getIntent().getExtras().getString("usuario");

        listaConversaciones = (ListView) findViewById(R.id.listaConversaciones);
        listaConversaciones.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id)
            {
                if(colaPeticiones != null)
                {
                    colaPeticiones.cancelAll(TAG);
                }
                actualizarConversaciones.cancel(true);
                toast(cj[position].getNombre());
                Intent intent = new Intent (getApplicationContext(), MostrarMensajes.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("idConversacion", cj[position].getIdConversacion());
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onStop()
    {
        super.onStop();
        if(colaPeticiones != null)
        {
            colaPeticiones.cancelAll(TAG);
        }
        actualizarConversaciones.cancel(true);
    }

    @Override
    protected  void onResume()
    {
        super.onResume();

        volley = SingleVolley.getInstance(getApplicationContext());
        colaPeticiones = volley.getColaPeticiones();
        gson = new Gson();
        actualizarConversaciones = new ActualizarConversaciones();
        actualizarConversaciones.execute();
    }

    private void toast(String mensaje)
    {
        Toast.makeText(getApplicationContext(),
                mensaje, Toast.LENGTH_SHORT).show();
    }

    public void buscar(View v)
    {
        if(colaPeticiones != null)
        {
            colaPeticiones.cancelAll(TAG);
        }
        actualizarConversaciones.cancel(true);
        Intent intent = new Intent (getApplicationContext(), Buscar.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
    }

    public void cerrarSesion(View v)
    {
        Intent intent = new Intent (getApplicationContext(), MainActivity.class);
        startActivity(intent);
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

    private class ActualizarConversaciones extends AsyncTask<Void, String, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            while(!isCancelled())
            {
                StringRequest postRequest = new StringRequest(Request.Method.POST, RecursosServidor.buscarConversacionApp,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                String conversacionTexto = "";
                                try
                                {
                                    JSONArray j = new JSONArray(response);
                                    cj = new ConversacionJson[j.length()];
                                    String [] conversacionesAR = new String[j.length()];
                                    for(int i = 0; i < j.length(); i++)
                                    {
                                        cj[i] = gson.fromJson(
                                                j.getJSONObject(i).toString(), ConversacionJson.class);
                                        conversacionTexto +=
                                                cj[i].getNombre() +
                                                        "- " + cj[i].getIdConversacion() +
                                                        ": " + cj[i].getEstado() + "\n";
                                        conversacionesAR[i] = cj[i].getNombre() + " - " + cj[i].getEstado();
                                    }
                                    publishProgress(conversacionesAR);

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
                        return params;
                    }
                };
                encolarPeticion(postRequest);
                try {

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... params)
        {
            Parcelable state = listaConversaciones.onSaveInstanceState();
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, params);
            listaConversaciones.setAdapter(itemsAdapter);
            listaConversaciones.onRestoreInstanceState(state);
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
