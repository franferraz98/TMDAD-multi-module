package com.example.multimodule.fileupload;

import org.springframework.data.util.Lazy;

import javax.persistence.*;
import java.util.*;

@Entity(name = "Grupos")
@Table(name = "grupos")
public class FileDBGrupo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    private String ListaColas;

    private String Exchage;

    //public Set<FileDbUsuarios> getPertenece() {
       // return Pertenece;
    //}

    //@ManyToMany(targetEntity = FileDbUsuarios.class, fetch = FetchType.LAZY, cascade=CascadeType.ALL, mappedBy = "gruposSet")
    //private Set<FileDbUsuarios> Pertenece = new HashSet<>();


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

    //public void addMember(FileDbUsuarios miembro){
        //Pertenece.add(miembro);
    //}
}
