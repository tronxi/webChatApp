package com.tron.sergi.webchat;

public class MensajeCategory
{
    private String nombre;
    private String mensaje;
    private String fecha;
    private String usuario;

    public MensajeCategory(String usuario)
    {
        this.usuario = usuario;
    }
    public MensajeCategory(String nombre, String mensaje, String fecha)
    {
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
    public void setFecha(String fecha)
    {
        this.fecha = fecha;
    }
    public void setMensaje(String mensaje)
    {
        this.mensaje = mensaje;
    }

    public String getNombre()
    {
        return nombre;
    }
    public String getFecha()
    {
        return fecha;
    }
    public String getMensaje()
    {
        return mensaje;
    }
    public String getUsuario()
    {
        return usuario;
    }

}
