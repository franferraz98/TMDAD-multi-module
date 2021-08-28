package com.example.multimodule.fileupload;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Usuarios")
@Table(name = "usuarios")
public class FileDbUsuarios {

    @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
        @JsonView({Views.Summary.class, Views.Groups.class})
        private Long id;

        @JsonView({Views.Summary.class, Views.Groups.class})
        private String name;

    public Set<FileDBGrupo> getGruposSet() {
        return gruposSet;
    }

    public void setGruposSet(Set<FileDBGrupo> gruposSet) {
        this.gruposSet = gruposSet;
    }

    @JsonView(Views.Groups.class)
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "Pertenece")
    private Set<FileDBGrupo> gruposSet = new HashSet<>();

        private String Contraseña;

        @JsonView(Views.Summary.class)
        private String Cola;

        public FileDbUsuarios() {
        }

        public FileDbUsuarios(String name, String Contraseña, String Cola) {
            this.name = name;
            this.Contraseña = Contraseña;
            this.Cola= Cola;
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

        public void setName(String name) {
            this.name = name;
        }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof FileDbUsuarios))
            return false;
        FileDbUsuarios fileDbUsuarios = (FileDbUsuarios) o;
        return Objects.equals(this.id, fileDbUsuarios.id) && Objects.equals(this.name, fileDbUsuarios.name)
               ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }

    @Override
    public String toString() {
        return "id: " + id;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public void setContraseña(String contraseña) {
        this.Contraseña = contraseña;
    }

    public String getCola() {
        return Cola;
    }

    public void setCola(String cola) {
        this.Cola = cola;
    }
}


