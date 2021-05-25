package com.example.multimodule.filesupload;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Usuarios")
public class FileDbUsuarios {
        @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
        private Long id;

        private String name;


        @ElementCollection(fetch = FetchType.EAGER)
        private Set<String> grupo = new HashSet<String>();

        private String Contraseña;

        private String Cola;

        public FileDbUsuarios() {
        }

        public FileDbUsuarios(String name, String newGrupo, String Contraseña, String Cola) {
            this.name = name;
            this.grupo.add(newGrupo);
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

        public Set<String> getGrupo() {
            return grupo;
        }

        public void setGrupo(Set<String> Grupo) {
            this.grupo = Grupo;
        }
    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof FileDbUsuarios))
            return false;
        FileDbUsuarios fileDbUsuarios = (FileDbUsuarios) o;
        return Objects.equals(this.id, fileDbUsuarios.id) && Objects.equals(this.name, fileDbUsuarios.name)
                && Objects.equals(this.grupo, fileDbUsuarios.grupo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.grupo);
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + this.id + ", name='" + this.name + '\'' + ", Grupo='" + this.grupo + '\'' + '}';
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

    public void addGroup(String newGrupo){
        this.grupo.add(newGrupo);
    }
    public void removeGroup(String deleteGrupo){ this.grupo.remove(deleteGrupo) ;}
}


