package com.janisschanbacher.springmicroservicesdemo.usermanagementservice.config;

import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.document.User;
import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@Configuration
public class MongoDBConfig {

    /**
     * Seed the database using the commandline interface.
     *
     * @param userRepository userRepository that is used to add users to the database
     * @return CommandLineRunner that will seed the database
     */
    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return strings -> {
            userRepository.save(new User("mmuster", "Maxime", "Muster", "pass1234", "USER"));
            userRepository.save(new User("eschuler", "Elena", "Schuler", "pass1234", "USER"));
            userRepository.save(new User("admin", "Admin", "Admin", "pass1234", "ADMIN"));
        };
    }

}
