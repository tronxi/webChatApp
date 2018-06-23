package com.tron.sergi.webchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity
{
    private static final String TAG = "MiW";

    private RequestQueue colaPeticiones;

    private EditText usuario;
    private EditText contraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SingleVolley volley;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        volley = SingleVolley.getInstance(getApplicationContext());
        colaPeticiones = volley.getColaPeticiones();


        usuario = (EditText) findViewById(R.id.usuario);
        contraseña = (EditText) findViewById(R.id.contraseña);
    }

    public void registrarse(View v)
    {
        if(usuario.getText().toString() == null
                || usuario.getText().toString().equals(""))
        {
            toast("introduce el usuario");
        }
        else if(contraseña.getText().toString() == null
                || contraseña.getText().toString().equals(""))
        {
            toast("introduce la contraseña");
        }
        else
        {
            StringRequest postRequest = new StringRequest(Request.Method.POST, RecursosServidor.registroApp,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            if(response.toString().equals("ok"))
                            {
                                toast("ok");
                                Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            else if (response.toString().equals("yaExisteUsuario"))
                            {
                                toast("yaExisteUsuario");
                            }
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
                    params.put("usuario", usuario.getText().toString());
                    params.put("password", contraseña.getText().toString());

                    return params;
                }
            };
            encolarPeticion(postRequest);
        }
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

}
