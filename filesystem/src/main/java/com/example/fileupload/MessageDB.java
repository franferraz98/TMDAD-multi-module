package com.example.fileupload;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Mensajes")
public class MessageDB {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    private String texto;

    private String fechaHora;


    public MessageDB() {
    }

    public MessageDB(String name, String texto, String fecha) {
        this.name = name;
        this.texto = texto;
        this.fechaHora = fecha;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}