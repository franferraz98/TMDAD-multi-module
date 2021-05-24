package com.example.fileupload;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Usuarios")
public class FileDbUsuarios {
        @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
        private Long id;

        private String name;

        private String Grupo;

        private String Contraseña;

        private String Cola;

        public FileDbUsuarios() {
        }

        public FileDbUsuarios(String name, String Grupo, String Contraseña, String Cola) {
            this.name = name;
            this.Grupo = Grupo;
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
        FileDbUsuarios fileDbUsuarios = (FileDbUsuarios) o;
        return Objects.equals(this.id, fileDbUsuarios.id) && Objects.equals(this.name, fileDbUsuarios.name)
                && Objects.equals(this.Grupo, fileDbUsuarios.Grupo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.Grupo);
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + this.id + ", name='" + this.name + '\'' + ", Grupo='" + this.Grupo + '\'' + '}';
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


