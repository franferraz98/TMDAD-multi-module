package com.example.fileupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.nio.channels.FileChannel;
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
