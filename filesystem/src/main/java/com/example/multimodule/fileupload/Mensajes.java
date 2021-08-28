package com.example.multimodule.fileupload;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.util.Lazy;

import javax.persistence.*;
import java.util.*;

@Entity(name = "Mensajes")
@Table(name = "mensajes")
public class Mensajes {

    public Mensajes(String username, String recivername, String content) {
        this.userName = username;
        this.reciverName = recivername;
        this.Content = content;
    }

    public Mensajes() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Id
    @JsonView({Views.Summary.class, Views.Groups.class})
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonView({Views.Summary.class, Views.Groups.class})
    private String userName;

    public String getReciverName() {
        return reciverName;
    }

    public void setReciverName(String reciverName) {
        this.reciverName = reciverName;
    }

    @JsonView({Views.Summary.class, Views.Groups.class})
    private String reciverName;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    @JsonView({Views.Summary.class, Views.Groups.class})
    private String Content;

    public Set<FileDBGrupo> getGruposMensaje() {
        return gruposMensaje;
    }

    public void setGruposMensaje(Set<FileDBGrupo> gruposMensaje) {
        this.gruposMensaje = gruposMensaje;
    }

    @JsonView(Views.Mensajes.class)
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "MensajesGrupo")
    private Set<FileDBGrupo> gruposMensaje = new HashSet<>();


}