package com.example.fileupload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FileDBUsuariosRepository extends JpaRepository<FileDbUsuarios, Long> {

}
