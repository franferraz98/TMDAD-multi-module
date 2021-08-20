package com.example.multimodule.fileupload;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Usuarios")
@Table(name = "usuarios")
public class FileDbUsuarios {

    @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
        private Long id;

        private String name;

    public Set<FileDBGrupo> getGruposSet() {
        return gruposSet;
    }

    public void setGruposSet(Set<FileDBGrupo> gruposSet) {
        this.gruposSet = gruposSet;
    }

    @ManyToMany(cascade = {
                CascadeType.PERSIST,
                CascadeType.ALL
        })
    @JoinTable(name = "usuarios_grupos",
            joinColumns = @JoinColumn(name = "usuarios_id"),
            inverseJoinColumns = @JoinColumn(name = "grupos_id")
    )
        private Set<FileDBGrupo> gruposSet = new HashSet<>();

        private String Contraseña;

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
        return "{" + "\"id\":\"" + this.id + "\", \"name\":\"" + this.name + "\"" + ", \"Grupo\":\"" + "\"" + "}";
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


