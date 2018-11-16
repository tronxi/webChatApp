package com.tron.sergi.webchat;

import java.util.HashMap;
import java.util.Map;

public class ConversacionJson
{

    private String nombre;
    private String idConversacion;
    private String estado;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getIdConversacion()
    {
        return idConversacion;
    }

    public void setIdConversacion(String idConversacion)
    {
        this.idConversacion = idConversacion;
    }

    public String getEstado()
    {
        if(estado.equals("0"))
        {
            return "";
        }
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    public Map<String, Object> getAdditionalProperties()
    {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value)
    {
        this.additionalProperties.put(name, value);
    }

}