package com.example.multimodule.fileupload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileDBGrupoRepository extends JpaRepository<FileDBGrupo, Long> {
    List<FileDBGrupo> findByName(String name);
}