package com.example.multimodule.filesupload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Transactional
@Repository
public interface FileDBGrupoRepository extends JpaRepository<FileDBGrupo, Long> {

    List<FileDbUsuarios> findByNameAndPerteneceIn(String name, Set<String> pertenece);
    List<FileDbUsuarios> findByName(String name);
    Long deleteByName(String name);

}