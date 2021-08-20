    package com.example.multimodule.fileupload;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.hateoas.CollectionModel;
    import org.springframework.hateoas.EntityModel;
    import org.springframework.hateoas.IanaLinkRelations;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

    import java.lang.reflect.Array;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;
    import java.util.Set;

    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

    @RestController
    public class FileController {

        @Autowired
        private FileStorageService storageService;

        @Autowired
        private FileDBUsuariosRepository repository;

        @Autowired
        private FileDBGrupoRepository repositoryGrupo;

        @Autowired
        private UserModelAssembler assembler;

        @Autowired
        private GroupModelAssembler assemblerGrupo;

        @PostMapping("/upload")
        public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
            String message = "";
            try {
                FileDB a = storageService.store(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename() + ". Share via id: " + a.getId();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        @GetMapping("/files")
        public ResponseEntity<List<ResponseFile>> getListFiles() {
            List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
                String fileDownloadUri = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/files/")
                        .path(dbFile.getId())
                        .toUriString();

                return new ResponseFile(
                        dbFile.getName(),
                        fileDownloadUri,
                        dbFile.getType(),
                        dbFile.getData().length);
            }).collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(files);
        }


        @GetMapping("/files/{id}")
        public ResponseEntity<byte[]> getFile(@PathVariable String id) {
            FileDB fileDB = storageService.getFile(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                    .body(fileDB.getData());
        }

        @GetMapping("/Usuarios")
        CollectionModel<EntityModel<FileDbUsuarios>> all() {

            List<EntityModel<FileDbUsuarios>> usuarios = repository.findAll().stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());

            return CollectionModel.of(usuarios, linkTo(methodOn(FileController.class).all()).withSelfRel());
        }

        @PostMapping("/Usuarios/login")
        ResponseEntity<ResponseMessage> login(@RequestBody String body) {
            String parts[] = body.split("&");
            String newUser = parts[0];
            String contrasena = parts[1];
            // System.out.println(body);
            // System.out.println(contrasena.toString());
            String message = "";
            try {
                List<FileDbUsuarios> lista = repository.findByName(newUser);
                if (!lista.isEmpty()) {
                    if (lista.get(0).getContraseña().equals(contrasena)) {
                        message = "Password correct";
                        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
                    } else {
                        message = "Password not correct";
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseMessage(message));
                    }
                } else {
                    message = newUser + "doesn't exist";
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message));
                }
            } catch (Exception e) {
                message = "Could not sign in: " + newUser;
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        @PostMapping("/Usuarios")
        ResponseEntity<ResponseMessage> newUser(@RequestBody String body) {
            String parts[] = body.split("&");
            String newUser = parts[0];
            String contrasena = parts[1];
            String message = "";
            try {
                if (repository.findByName(newUser).isEmpty()) {
                    repository.save(new FileDbUsuarios(newUser, contrasena, newUser));
                    message = "User sing in successfully: " + newUser;
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
                } else {
                    message = newUser + "does already exist";
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                }
            } catch (Exception e) {
                message = "Could not sign in: " + newUser;
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        @RequestMapping(value = "/Usuarios/get/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
        public @ResponseBody String getUsuario(@PathVariable("name") String name) {
            // System.out.println(name);
            List<FileDbUsuarios> Usuario = repository.findByName(name);
            System.out.println(Usuario.get(0));

            return Usuario.get(0).toString();
        }

        @GetMapping("/Usuarios/{id}")
        EntityModel<FileDbUsuarios> one(@PathVariable("id") Long id) {
            System.out.println(id);
            FileDbUsuarios Usuario = repository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException(id));

            return assembler.toModel(Usuario);
        }

        @GetMapping("/Usuarios/{name}/gruposet")
        CollectionModel<EntityModel<FileDBGrupo>> grupospertenece(@PathVariable("name") String name) {
            System.out.println(name);
            List<FileDbUsuarios> Usuario = repository.findByName(name);

            List<EntityModel<FileDBGrupo>> grupos = Usuario.get(0).getGruposSet().stream()
                    .map(assemblerGrupo::toModel)
                    .collect(Collectors.toList());

            System.out.println(CollectionModel.of(grupos, linkTo(methodOn(FileController.class).all()).withSelfRel()).getContent().toString());

            return CollectionModel.of(grupos, linkTo(methodOn(FileController.class).all()).withSelfRel());
        }
        @PostMapping("/Usuarios/{id}/gruposet")
        ResponseEntity<ResponseMessage> newUserinGroup(@PathVariable("id") Long id, @RequestBody String body) {
            String parts[] = body.split("&");
            String username = parts[0];
            String groupName = parts[1];

            System.out.println(username);
            List<FileDbUsuarios> Usuario = repository.findByName(username);

            String message = "";
            ArrayList<FileDbUsuarios> ListaMiembros = null;
            try {
                if (!repository.findByName(username).isEmpty()) {
                    if(repositoryGrupo.findByName(groupName).isEmpty()){
                        repositoryGrupo.save(new FileDBGrupo(groupName, "", groupName));
                        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
                    } else {
                        message = "Group " + groupName + "does already exist";
                        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                    }
                } else {
                    message = "User " + username + "does not exist";
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        @PutMapping("/Usuarios/{id}")
        @ResponseStatus(HttpStatus.CREATED)
        ResponseEntity<?> replaceUser(@RequestBody FileDbUsuarios Usuario, @PathVariable Long id) {

            FileDbUsuarios updatedUsuario = repository.findById(id) //
                    .map(employee -> {
                        employee.setName(Usuario.getName());
                        return repository.save(employee);
                    }) //
                    .orElseGet(() -> {
                        Usuario.setId(id);
                        return repository.save(Usuario);
                    });

            EntityModel<FileDbUsuarios> entityModel = assembler.toModel(updatedUsuario);

            return ResponseEntity //
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(entityModel);
        }

        @DeleteMapping("/Usuarios/{id}")
        @ResponseStatus(HttpStatus.ACCEPTED)
        ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
            repository.deleteById(id);

            return ResponseEntity.noContent().build();
        }

        @GetMapping("/Grupos")
        CollectionModel<EntityModel<FileDBGrupo>> allGroup() {

            List<EntityModel<FileDBGrupo>> grupos = repositoryGrupo.findAll().stream()
                    .map(assemblerGrupo::toModel)
                    .collect(Collectors.toList());

            return CollectionModel.of(grupos, linkTo(methodOn(FileController.class).all()).withSelfRel());
        }

        @PostMapping("/Grupos")
        ResponseEntity<ResponseMessage> newGroup(@RequestBody String body) {
            String parts[] = body.split("&");
            String username = parts[0];
            String groupName = parts[1];

            String message = "";
            ArrayList<FileDbUsuarios> ListaMiembros = null;
            FileDbUsuarios U;
            try {
                if (!repository.findByName(username).isEmpty()) {
                    if(repositoryGrupo.findByName(groupName).isEmpty()){
                        //repositoryGrupo.save(new FileDBGrupo(groupName, "", groupName));
                        U=repository.findByName(username).get(0);
                        EntityModel<FileDBGrupo> entityModelGrupo = assemblerGrupo.toModel(new FileDBGrupo(groupName, "", groupName));
                        U.getGruposSet().add(entityModelGrupo.getContent());
                        replaceUser(U,U.getId());
                        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
                    } else {
                        message = "Group " + groupName + "does already exist";
                        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                    }
                } else {
                    message = "User " + username + "does not exist";
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
    }
