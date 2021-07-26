package com.example.multimodule.fileupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadUsersDB {

    private static final Logger log = LoggerFactory.getLogger(LoadUsersDB.class);

    @Bean
    CommandLineRunner initDatabase(FileDBUsuariosRepository repository) {

        return args -> {
            log.info("Preloading " + repository.save(new FileDbUsuarios("BilboBaggins",
                    "Contraseña", "BilboBaggins")));
            log.info("Preloading " + repository.save(new FileDbUsuarios("FrodoBaggins",
                    "Contraseña", "FrodoBaggins")));
        };
    }
}
