package com.example.multimodule.fileupload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajesRepository extends JpaRepository<Mensajes, Long> {

    // @Query("select m from Mensajes m where m.gruposMensaje = ?1")
    List<Mensajes> findByGruposMensaje(FileDBGrupo grupo);

    // @Query("select m from Mensajes m where m.gruposMensaje = ?1 and m.userName = ?2")
    List<Mensajes> findByGruposMensajeAndUserName(FileDBGrupo grupo, String userName);

}
