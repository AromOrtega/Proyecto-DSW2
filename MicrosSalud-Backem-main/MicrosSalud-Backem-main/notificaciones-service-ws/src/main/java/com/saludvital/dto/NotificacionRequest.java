package com.saludvital.dto;

public class NotificacionRequest {

    private String destinatario;
    private String mensaje;
    private String tipo;

    public NotificacionRequest() {}

    public NotificacionRequest(String destinatario, String mensaje, String tipo) {
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}