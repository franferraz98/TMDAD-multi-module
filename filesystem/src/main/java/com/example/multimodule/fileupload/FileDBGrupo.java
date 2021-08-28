package com.example.multimodule.fileupload;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.util.Lazy;

import javax.persistence.*;
import java.util.*;

@Entity(name = "Grupos")
@Table(name = "grupos")
public class FileDBGrupo {

    @JsonView({Views.Summary.class, Views.Groups.class})
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @JsonView({Views.Summary.class, Views.Groups.class})
    private String name;

    @JsonView({Views.Summary.class, Views.Groups.class})
    private String ListaColas;

    @JsonView({Views.Summary.class, Views.Groups.class})
    private String Exchage;

    public Set<FileDbUsuarios> getPertenece() {
        return Pertenece;
    }

    @JsonView(Views.Summary.class)
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.ALL
    }, fetch = FetchType.LAZY)
    @JoinTable(name = "usuarios_grupos",
            joinColumns = @JoinColumn(name = "grupos_id"),
            inverseJoinColumns = @JoinColumn(name = "usuarios_id")
    )
    private Set<FileDbUsuarios> Pertenece = new HashSet<>();


    @JsonView(Views.Summary.class)
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.ALL
    }, fetch = FetchType.LAZY)
    @JoinTable(name = "mensajes_grupos",
            joinColumns = @JoinColumn(name = "grupos_id"),
            inverseJoinColumns = @JoinColumn(name = "mensajes_id")
    )
    private Set<Mensajes> MensajesGrupo = new HashSet<>();

    public FileDBGrupo() {
    }

    public FileDBGrupo(String name, String listaColas, String exchage) {
        this.name = name;
        this.ListaColas = listaColas;
        this.Exchage= exchage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String Grupo) {
        this.name = Grupo;
    }
    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof FileDbUsuarios))
            return false;
        FileDbUsuarios fileDBGrupo = (FileDbUsuarios) o;
        return Objects.equals(this.id, fileDBGrupo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + this.id + ", name='" + this.name + '\'' + ", Grupo='" + this.name + '\'' + '}';
    }


    public String getListaColas() {
        return ListaColas;
    }

    public void setListaColas(String listaColas) {
        ListaColas = listaColas;
    }

    public String getExchage() {
        return Exchage;
    }

    public void setExchage(String exchage) {
        Exchage = exchage;
    }

    public void addMember(FileDbUsuarios miembro){
        Pertenece.add(miembro);
    }

    public void addMensaje(Mensajes mensaje){
        MensajesGrupo.add(mensaje);
    }

    public void deleteMember(FileDbUsuarios miembro){
        Pertenece.remove(miembro);
    }
}
