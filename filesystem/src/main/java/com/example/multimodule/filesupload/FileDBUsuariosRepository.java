package com.example.multimodule.filesupload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Transactional
@Repository
public
interface FileDBUsuariosRepository extends JpaRepository<FileDbUsuarios, Long> {

    List<FileDbUsuarios> findByName(String name);

    List<FileDbUsuarios> findByNameAndGrupoIn(String name, Set<String> grupo);

    Long deleteByName(String name);

}
