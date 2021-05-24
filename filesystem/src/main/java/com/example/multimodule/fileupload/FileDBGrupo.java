package com.example.multimodule.fileupload;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Grupos")
public class FileDBGrupo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String Grupo;

    private String ListaColas;

    private String Exchage;

    private ArrayList<FileDbUsuarios> Pertenece;

    public FileDBGrupo() {
    }

    public FileDBGrupo(String Grupo, String listaColas, String exchage, ArrayList<FileDbUsuarios> ListaUsuarios) {
        this.Grupo = Grupo;
        this.ListaColas = listaColas;
        this.Exchage= exchage;
        this.Pertenece = ListaUsuarios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrupo() {
        return Grupo;
    }

    public void setGrupo(String Grupo) {
        this.Grupo = Grupo;
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
        return Objects.hash(this.id, this.Grupo);
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + this.id + ", name='" + this.Grupo + '\'' + ", Grupo='" + this.Grupo + '\'' + '}';
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

    public List<FileDbUsuarios> getPertenece() {
        return Pertenece;
    }

    public void setPertenece(ArrayList<FileDbUsuarios> pertenece) {
        Pertenece = pertenece;
    }
    public void addMember(FileDbUsuarios miembro){
        Pertenece.add(miembro);
    }
}
