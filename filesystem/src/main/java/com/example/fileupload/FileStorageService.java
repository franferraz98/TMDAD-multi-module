package com.example.fileupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Persistent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    @Autowired
    private FileDBRepository fileDBRepository;

    public FileDB store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FileDB FileDB = new FileDB(fileName, file.getContentType(), file.getBytes());

        return fileDBRepository.save(FileDB);
    }

    public FileDB getFile(String id) {
        return fileDBRepository.findById(id).get();
    }

    public Stream<FileDB> getAllFiles() {
        return fileDBRepository.findAll().stream();
    }

    @Autowired
    @Persistent
    private com.example.fileupload.FileDBUsuariosRepository fileDBusuariosRepository;

    public com.example.fileupload.FileDbUsuarios storeUsers(String User) throws IOException {
        String userName = StringUtils.cleanPath(User);
        com.example.fileupload.FileDbUsuarios FileDbUsuarios = new com.example.fileupload.FileDbUsuarios(userName, "Cualquiera", "Contraseña", "Cola");

        return fileDBusuariosRepository.save(FileDbUsuarios);
    }

    public com.example.fileupload.FileDbUsuarios getUsers(Long id) {
        return fileDBusuariosRepository.findById(id).get();
    }

    public Stream<com.example.fileupload.FileDbUsuarios> getAllUsers() {
        return fileDBusuariosRepository.findAll().stream();
    }
}
