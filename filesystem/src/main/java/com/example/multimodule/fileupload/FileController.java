    package com.example.multimodule.fileupload;

    import com.fasterxml.jackson.annotation.JsonView;
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

    import java.io.File;
    import java.lang.reflect.Array;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.*;
    import java.util.stream.Collectors;

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
        private MensajesRepository repositoryMensajes;

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

        @JsonView(Views.Summary.class)
        @GetMapping("/Usuarios")
        List<FileDbUsuarios> all() {

            List<FileDbUsuarios> usuarios = repository.findAll();
            return usuarios;
        }

        /*
        @GetMapping("/Usuarios")
        CollectionModel<EntityModel<FileDbUsuarios>> all() {

            List<EntityModel<FileDbUsuarios>> usuarios = repository.findAll().stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());

            return CollectionModel.of(usuarios, linkTo(methodOn(FileController.class).all()).withSelfRel());
        }
         */

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
                        System.out.println("Password correct");
                        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
                    } else {
                        message = "Password not correct";
                        System.out.println("Password not correct");
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

        public int loginSpring(String body) {
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
                        System.out.println("Password correct");
                        return 0;
                    } else {
                        message = "Password not correct";
                        System.out.println("Password not correct");
                        return 1;
                    }
                } else {
                    message = newUser + "doesn't exist";
                    return 2;
                }
            } catch (Exception e) {
                message = "Could not sign in: " + newUser;
                return 3;
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
                    message = "User sign in successfully: " + newUser;
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

        public int newUserSpring(String body) {
            String parts[] = body.split("&");
            String newUser = parts[0];
            String contrasena = parts[1];
            String message = "";
            try {
                if (repository.findByName(newUser).isEmpty()) {
                    repository.save(new FileDbUsuarios(newUser, contrasena, newUser));
                    message = "User sign in successfully: " + newUser;
                    return 0;
                } else {
                    message = newUser + "does already exist";
                    return 1;
                }
            } catch (Exception e) {
                message = "Could not sign in: " + newUser;
                return 2;
            }
        }

        @RequestMapping(value = "/Usuarios/get/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
        public @ResponseBody
        String getUsuario(@PathVariable("name") String name) {
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

        public int newUserinGroupSpring(String body) {
            String parts[] = body.split("&");
            String groupname = parts[0];
            String newmemberName = parts[1];
            String from = parts[2];

            System.out.println(groupname);
            List<FileDbUsuarios> Usuario = repository.findByName(groupname);

            String message = "";
            FileDBGrupo G = null;
            try {
                if (repository.findByName(groupname).isEmpty()) {
                    if (repositoryGrupo.findByName(newmemberName).isEmpty()) {
                        List<FileDBGrupo> all = repositoryGrupo.findByName(groupname);
                        FileDBGrupo group = all.get(0);
                        Set<FileDbUsuarios> usuarios = group.getPertenece();
                        Iterator<FileDbUsuarios> iter = usuarios.iterator();
                        FileDbUsuarios usuario = iter.next();
                        System.out.println(usuario);
                        if (usuario.getName().equals(from)) {
                            G = repositoryGrupo.findByName(groupname).get(0);
                            G.addMember(repository.findByName(newmemberName).get(0));
                            repositoryGrupo.save(G);
                            return 0;
                        } else {
                            message = "Only the administrator can add users";
                            return 1;
                        }
                    } else {
                        message = "Group " + newmemberName + "does already exist";
                        return 2;
                    }
                } else {
                    message = "User " + groupname + "does not exist";
                    return 3;
                }
            } catch (Exception e) {
                return 4;
            }
        }

        @PostMapping("/Grupos/addToGroup")
        ResponseEntity<ResponseMessage> newUserinGroup(@RequestBody String body) {
            String parts[] = body.split("&");
            String groupname = parts[0];
            String newmemberName = parts[1];
            String from = parts[2];

            System.out.println(groupname);
            List<FileDbUsuarios> Usuario = repository.findByName(groupname);

            String message = "";
            FileDBGrupo G = null;
            try {
                if (repository.findByName(groupname).isEmpty()) {
                    if (repositoryGrupo.findByName(newmemberName).isEmpty()) {
                        List<FileDBGrupo> all = repositoryGrupo.findByName(groupname);
                        FileDBGrupo group = all.get(0);
                        Set<FileDbUsuarios> usuarios = group.getPertenece();
                        Iterator<FileDbUsuarios> iter = usuarios.iterator();
                        FileDbUsuarios usuario = iter.next();
                        System.out.println(usuario);
                        if (usuario.getName().equals(from)) {
                            G = repositoryGrupo.findByName(groupname).get(0);
                            G.addMember(repository.findByName(newmemberName).get(0));
                            repositoryGrupo.save(G);
                            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
                        } else {
                            message = "Only the administrator can add users";
                            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                        }
                    } else {
                        message = "Group " + newmemberName + "does already exist";
                        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                    }
                } else {
                    message = "User " + groupname + "does not exist";
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

        @JsonView(Views.Summary.class)
        @GetMapping("/Grupos")
        List<FileDBGrupo> allGroup() {
            List<FileDBGrupo> l = repositoryGrupo.findAll();
            return l;
        }

        public int newGroupSpring(String body){
            String parts[] = body.split("&");
            String username = parts[0];
            String groupName = parts[1];

            String message = "";
            ArrayList<FileDbUsuarios> ListaMiembros = null;
            FileDbUsuarios U;
            FileDBGrupo G;
            try {
                if (!repository.findByName(username).isEmpty()) {
                    if (repositoryGrupo.findByName(groupName).isEmpty()) {
                        G = repositoryGrupo.save(new FileDBGrupo(groupName, "", groupName));
                        U = repository.findByName(username).get(0);
                        G.getPertenece().add(U);
                        repositoryGrupo.save(G);
                        return 0;
                    } else {
                        message = "Group " + groupName + "does already exist";
                        return 1;
                    }
                } else {
                    message = "User " + username + "does not exist";
                    return 2;
                }
            } catch (Exception e) {
                return 3;
            }
        }

        @PostMapping("/Grupos")
        ResponseEntity<ResponseMessage> newGroup(@RequestBody String body) {
            String parts[] = body.split("&");
            String username = parts[0];
            String groupName = parts[1];

            String message = "";
            ArrayList<FileDbUsuarios> ListaMiembros = null;
            FileDbUsuarios U;
            FileDBGrupo G;
            try {
                if (!repository.findByName(username).isEmpty()) {
                    if (repositoryGrupo.findByName(groupName).isEmpty()) {
                        G = repositoryGrupo.save(new FileDBGrupo(groupName, "", groupName));
                        U = repository.findByName(username).get(0);
                        G.getPertenece().add(U);
                        U.getGruposSet().add(G);
                        repository.save(U);
                        repositoryGrupo.save(G);
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

        @JsonView(Views.Groups.class)
        @GetMapping("/getGroups/{username}")
        public Set<FileDBGrupo> getGroups(@PathVariable String username) {

            FileDbUsuarios usuario = repository.findByName(username).get(0);
            Set<FileDBGrupo> grupos = usuario.getGruposSet();

            return grupos;
        }

        @JsonView(Views.Groups.class)
        public String getGroupsSpring(String username) {

            FileDbUsuarios usuario = repository.findByName(username).get(0);

            Set<FileDBGrupo> grupos = usuario.getGruposSet();
            Iterator<FileDBGrupo> iter = grupos.iterator();
            String result = "";

            for(int i = 0; i<grupos.size(); i++){
                FileDBGrupo g = iter.next();
                result = result.concat(g.getName() + ";");
            }

            return result;
        }

        @RequestMapping("/Grupos/{name}/{name2}")
        @ResponseStatus(HttpStatus.ACCEPTED)
        ResponseEntity<?> deleteUserFromGroup(@PathVariable String name, @PathVariable String name2) {
            try {
                FileDBGrupo g = repositoryGrupo.findByName(name).get(0);
                g.deleteMember(repository.findByName(name2).get(0));
                repositoryGrupo.save(g);
            } catch (Exception e) {
                System.err.println("Error when deleting " + e);
            }
            return ResponseEntity.noContent().build();
        }

        @PostMapping("/Mensajes")
        ResponseEntity<ResponseMessage> newMensaje(@RequestBody String body) {
            String parts[] = body.split("&");
            String userName = parts[0];
            String groupName = parts[1];
            String Content = parts[2];

            String message = "";
            FileDBGrupo G;
            try {
                if (!repository.findByName(userName).isEmpty()) {
                    if (!repositoryGrupo.findByName(groupName).isEmpty()) {
                        G = repositoryGrupo.findByName(groupName).get(0);
                        G.addMensaje(new Mensajes(userName, groupName, Content));
                        repositoryGrupo.save(G);
                        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
                    } else {
                        message = "Group " + Content + "does already exist";
                        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                    }
                } else {
                    message = "User " + userName + "does not exist";
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
                }

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        @JsonView(Views.Summary.class)
        @GetMapping("/Mensajes/{groupname}")
        List<Mensajes> MensajeEnGrupo(@PathVariable String groupname) {
            List<Mensajes> m = repositoryMensajes.findByGruposMensaje(repositoryGrupo.findByName(groupname).get(0));
            return m;
        }
    }