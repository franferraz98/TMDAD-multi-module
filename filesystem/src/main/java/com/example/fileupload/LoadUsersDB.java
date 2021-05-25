package com.example.fileupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
class LoadUsersDB {

    private static final Logger log = LoggerFactory.getLogger(LoadUsersDB.class);

    @Bean
    CommandLineRunner initDatabase(FileDBUsuariosRepository repository) {

        FileDbUsuarios a = new FileDbUsuarios("Frodo Baggins", "thief", "Contraseña", "Cola");
        return args -> {
            log.info("Preloading " + repository.save(new FileDbUsuarios("Bilbo Baggins", "burglar", "Contraseña", "Cola")));
            log.info("Preloading " + repository.save(a));
            log.info("Preloading " + repository.findByName("Frodo Baggins").get(0).getGrupo());

            final FileDbUsuarios[] b = new FileDbUsuarios[1];
            b[0] = repository.findByName("Frodo Baggins").get(0);
            b[0].addGroup("perro");
            log.info("Preloading " + b[0].getGrupo());
            b[0].removeGroup("perro");
            log.info("Preloading " + b[0].getGrupo());
            b[0].addGroup("test");
            log.info("Preloading " + b[0]);
            repository.deleteByName("Frodo Baggins");
            log.info("Preloading " + repository.findAll());
            log.info("Preloading " + repository.save(b[0]));

            if(repository.findByNameAndGrupoIn("Frodo Baggins", Collections.singleton("test")).isEmpty())
              {
                  log.info("Preloading " +"Nadie tiene ese grupo"); }
            else log.info(repository.findByNameAndGrupoIn("Frodo Baggins", Collections.singleton("test")).get(0).toString());
        };
    }
}
