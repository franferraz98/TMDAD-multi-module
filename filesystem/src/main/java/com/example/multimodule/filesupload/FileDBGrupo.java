package com.example.multimodule.filesupload;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Grupos")
public class FileDBGrupo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    private String listaColas;

    private String exchage;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> pertenece = new HashSet<String>();;

    public FileDBGrupo() {
    }

    public FileDBGrupo(String Grupo, String listaColas, String exchage, String ListaUsuarios) {
        this.name = Grupo;
        this.listaColas = listaColas;
        this.exchage= exchage;
        this.pertenece.add(ListaUsuarios);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrupo() {
        return name;
    }

    public void setGrupo(String Grupo) {
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
        return listaColas;
    }

    public void setListaColas(String listaColas) {
        this.listaColas = listaColas;
    }

    public String getExchage() {
        return exchage;
    }

    public void setExchage(String exchage) {
        this.exchage = exchage;
    }

    public Set<String> getPertenece() {
        return pertenece;
    }

    public void setPertenece(Set<String> pertenece) {
        this.pertenece = pertenece;
    }

    public void addMember(String miembro){
        pertenece.add(miembro);
    }
}
