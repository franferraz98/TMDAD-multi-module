package com.example.multimodule.fileupload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDBGrupoRepository extends JpaRepository<FileDBGrupo, Long> {

}