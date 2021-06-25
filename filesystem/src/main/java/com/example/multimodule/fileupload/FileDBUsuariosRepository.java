package com.example.multimodule.fileupload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileDBUsuariosRepository extends JpaRepository<FileDbUsuarios, Long> {
    List<FileDbUsuarios> findByName(String name);
}
