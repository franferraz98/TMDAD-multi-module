package com.example.multimodule.filesupload;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class GroupModelAssembler implements RepresentationModelAssembler<FileDBGrupo, EntityModel<FileDBGrupo>> {

    @Override
    public EntityModel<FileDBGrupo> toModel(FileDBGrupo Grupo) {

        return EntityModel.of(Grupo, //
                linkTo(methodOn(FileController.class).one(Grupo.getId())).withSelfRel(),
                linkTo(methodOn(FileController.class).allGroup()).withRel("Grupos"));
    }
}
