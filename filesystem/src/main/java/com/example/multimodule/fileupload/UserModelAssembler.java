package com.example.multimodule.fileupload;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
class UserModelAssembler implements RepresentationModelAssembler<FileDbUsuarios, EntityModel<FileDbUsuarios>> {

    @Override
    public EntityModel<FileDbUsuarios> toModel(FileDbUsuarios Usuario) {

        return EntityModel.of(Usuario, //
                linkTo(methodOn(FileController.class).one(Usuario.getId())).withSelfRel(),
                linkTo(methodOn(FileController.class).all()).withRel("usuarios"));
    }
}
