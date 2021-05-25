    package com.example.fileupload;

    import net.minidev.json.JSONArray;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.hateoas.CollectionModel;
    import org.springframework.hateoas.EntityModel;
    import org.springframework.hateoas.IanaLinkRelations;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

    import java.util.List;
    import java.util.Date;
    import java.util.Optional;
    import java.util.stream.Collectors;

    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

    @RestController
    public class FileController {

        @Autowired
        private FileStorageService storageService;

        @Autowired
        private MessageDBRepository Messagerepository;

        @Autowired
        private FileDBUsuariosRepository repository;

        @Autowired
        private FileDBGrupoRepository repositoryGrupo;

        @Autowired
        private UserModelAssembler assembler;

        @Autowired
        private GroupModelAssembler assemblerGrupo;

        @PostMapping("/upload")
        public ResponseEntity<com.example.fileupload.ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
            String message = "";
            try {
                FileDB a = storageService.store(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename() + ". Share via id: " + a.getId();
                return ResponseEntity.status(HttpStatus.OK).body(new com.example.fileupload.ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new com.example.fileupload.ResponseMessage(message));
            }
        }

        @PostMapping("/message")
        public ResponseEntity<ResponseMessage> storeMessage(@RequestParam("message") String message) {
            String ResponseMs = "";
            try {
                MessageDB Ms = new MessageDB("Pedro", message, new Date().toString());
                Messagerepository.save(Ms);

                ResponseMs = "Message store successfully at: " + Ms.getFechaHora();
                return ResponseEntity.status(HttpStatus.OK).body(new com.example.fileupload.ResponseMessage(ResponseMs));
            } catch (Exception e) {
                ResponseMs = "Could not store the message: ";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new com.example.fileupload.ResponseMessage(ResponseMs));
            }
        }

        @GetMapping("/message")
        public ResponseEntity<String> getMessage() {
            List<MessageDB> messages = Messagerepository.findAll();

            String jsonStr = JSONArray.toJSONString(messages);

            return ResponseEntity.status(HttpStatus.OK).body(jsonStr);
        }

        @GetMapping("/files")
        public ResponseEntity<List<com.example.fileupload.ResponseFile>> getListFiles() {
            List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
                String fileDownloadUri = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/files/")
                        .path(dbFile.getId())
                        .toUriString();

                return new com.example.fileupload.ResponseFile(
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

        private static final Logger log = LoggerFactory.getLogger(FileController.class);

        @PostMapping("/Usuarios")
        ResponseEntity<ResponseMessage> newUser(@RequestBody String newUser) {
            String message = "";
            List<FileDbUsuarios> User;
            try {
                User = repository.findByName(newUser);
                if(User.isEmpty()) {
                    repository.save(new FileDbUsuarios(newUser.substring(5), "Cualquiera", "Contraseña", "Cola"));
                    message = "User sing in successfully: " + newUser;
                    return ResponseEntity.status(HttpStatus.OK).body(new com.example.fileupload.ResponseMessage(message));
                }
                else {
                    message = "User already in the DB: ";
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new com.example.fileupload.ResponseMessage(message));}
            } catch (Exception e) {
                message = "Could not sign in: " + newUser;
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new com.example.fileupload.ResponseMessage(message));
            }
        }

        @GetMapping("/Usuarios/{id}")
        EntityModel<FileDbUsuarios> one(@PathVariable("id") Long id) {
            System.out.println(id);
            FileDbUsuarios Usuario = repository.findById(id)
                    .orElseThrow(() -> new com.example.fileupload.EmployeeNotFoundException(id));

            return assembler.toModel(Usuario);
        }

        @PutMapping("/Usuarios/{id}")
        @ResponseStatus(HttpStatus.CREATED)
        ResponseEntity<?> replaceUser(@RequestBody FileDbUsuarios Usuario, @PathVariable Long id) {

            FileDbUsuarios updatedUsuario = repository.findById(id) //
                    .map(employee -> {
                        employee.setName(Usuario.getName());
                        employee.setGrupo(Usuario.getGrupo());
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
        ResponseEntity<ResponseMessage> newGroup(@RequestBody String newGroup) {
            String message = "";
            try {
                repositoryGrupo.save(new FileDBGrupo(newGroup.substring(5), "Lista", "Exchange", "Miembros"));
                return ResponseEntity.status(HttpStatus.OK).body(new com.example.fileupload.ResponseMessage(message));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        @PostMapping("/Grupo/{id}")
        @ResponseStatus(HttpStatus.CREATED)
        ResponseEntity<ResponseMessage> AddUser(@RequestBody Long MiembroID, @PathVariable Long id) {
            try
            {
                Optional<FileDBGrupo> grupo = repositoryGrupo.findById(id);
                Optional<FileDbUsuarios> miembro = repository.findById(MiembroID);

                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Miembro añadido al grupro"));
            }
            catch (Exception e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("No se ha podido realizar la peticion"));
            }

        }
    }
