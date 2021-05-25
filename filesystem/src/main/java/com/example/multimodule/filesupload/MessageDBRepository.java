package com.example.multimodule.filesupload;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface MessageDBRepository extends JpaRepository<MessageDB, Long> {

    List<FileDbUsuarios> findByName(String name);

}
