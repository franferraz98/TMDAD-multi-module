package com.example.multimodule.fileupload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDBUsuariosRepository extends JpaRepository<FileDbUsuarios, Long> {

}
