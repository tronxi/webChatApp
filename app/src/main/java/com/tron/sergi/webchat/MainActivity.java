package com.tron.sergi.webchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MiW";

    private RequestQueue colaPeticiones;

    private EditText usuario;
    private EditText contraseña;

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SingleVolley volley;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volley = SingleVolley.getInstance(getApplicationContext());
        colaPeticiones = volley.getColaPeticiones();

        gson = new Gson();

        usuario = (EditText) findViewById(R.id.usuario);
        contraseña = (EditText) findViewById(R.id.contraseña);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                Log.e("Token",mToken);
            }
        });
    }



    public void iniciarSesion(View v)
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
            StringRequest postRequest = new StringRequest(Request.Method.POST, RecursosServidor.entrarApp,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            if(response.toString().equals("ok"))
                            {
                                Intent intent = new Intent (getApplicationContext(), Conversacion.class);
                                intent.putExtra("usuario", usuario.getText().toString());
                                startActivity(intent);
                            }
                            else if (response.toString().equals("contraseñaIncorrecta"))
                            {
                                toast("contraseñaIncorreta");
                            }
                            else if (response.toString().equals("noExisteUsuario"))
                            {
                                toast("noExiste usuario");
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
                    params.put("usuario", usuario.getText().toString());
                    params.put("password", contraseña.getText().toString());

                    return params;
                }
            };
           encolarPeticion(postRequest);
        }
    }

    public void registrarse(View v)
    {
        Intent intent = new Intent (getApplicationContext(), Registro.class);
        startActivity(intent);
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
