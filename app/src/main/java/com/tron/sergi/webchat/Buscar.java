package com.tron.sergi.webchat;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Buscar extends AppCompatActivity
{
    private static final String TAG = "MiW";

    private RequestQueue colaPeticiones;
    private String usuario;
    private Gson gson;
    private int estado;
    private Activity act;
    private ActualizarConversaciones actualizarConversaciones;

    private ConversacionJson[] cj;

    private ListView listaConversaciones;
    private SingleVolley volley;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        usuario = getIntent().getExtras().getString("usuario");
        act = this;

        listaConversaciones = (ListView) findViewById(R.id.listaConversaciones);
        listaConversaciones.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView parent, View view, final int position, long id)
            {
                StringRequest postRequest = new StringRequest(Request.Method.POST, RecursosServidor.crearConversacionApp,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                try
                                {
                                    JSONArray j = new JSONArray(response);

                                    ConversacionNueva cn = gson.fromJson(
                                            j.getJSONObject(0).toString(), ConversacionNueva.class);
                                    estado = cn.getEstado().intValue();


                                    if(colaPeticiones != null)
                                    {
                                        colaPeticiones.cancelAll(TAG);
                                    }
                                    actualizarConversaciones.cancel(true);
                                    Intent intent = new Intent (getApplicationContext(), MostrarMensajes.class);
                                    intent.putExtra("idConversacion", String.valueOf(estado));
                                    intent.putExtra("usuario", usuario);
                                    startActivity(intent);
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
                        params.put("persona", cj[position].getNombre());
                        return params;
                    }
                };
                encolarPeticion(postRequest);
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

    private class ActualizarConversaciones extends AsyncTask<Void, ArrayList<Category>, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            while(!isCancelled())
            {
                StringRequest postRequest = new StringRequest(Request.Method.POST, RecursosServidor.buscarPersonasApp,
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
                                    //String [] conversacionesAR = new String[j.length()];
                                    ArrayList<Category> category = new ArrayList<Category>();
                                    for(int i = 0; i < j.length(); i++)
                                    {
                                        cj[i] = gson.fromJson(
                                                j.getJSONObject(i).toString(), ConversacionJson.class);
                                        //conversacionesAR[i] = cj[i].getNombre();
                                        Category cat = new Category();
                                        cat.setContacto(cj[i].getNombre());
                                        cat.setMensaje("");
                                        category.add(cat);
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
        protected void onProgressUpdate(ArrayList<Category>... params)
        {
            Parcelable estado = listaConversaciones.onSaveInstanceState();
            //ArrayAdapter<String> itemsAdapter =
                    //new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, params);
            AdapterItem itemsAdapter = new AdapterItem(act, params[0]);
            listaConversaciones.setAdapter(itemsAdapter);
            listaConversaciones.onRestoreInstanceState(estado);
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
